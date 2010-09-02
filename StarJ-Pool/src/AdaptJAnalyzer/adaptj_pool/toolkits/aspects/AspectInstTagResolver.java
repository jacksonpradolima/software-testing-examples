package adaptj_pool.toolkits.aspects;

import adaptj_pool.Scene;
import adaptj_pool.toolkits.*;
import adaptj_pool.toolkits.analyses.IDResolver;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.JVMPI.*;

import java.io.*;
import java.util.*;
import it.unimi.dsi.fastUtil.*;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;

public class AspectInstTagResolver extends EventOperation {
    private static final boolean DEBUG_TRACE_STACKS = false;
    private static final boolean DEBUG_SHOW_STACK_SUMMARY = true;
    private static final boolean DEBUG_SHOW_INV_MISMATCH = false;

    //public static final String ASPECT_TAG_ATTRIBUTE_NAME = "ca.mcgill.sable.InstructionKind";
    public static final String ASPECT_KIND_ATTRIBUTE_NAME = "ca.mcgill.sable.InstructionKind";
    public static final String ASPECT_SOURCE_ATTRIBUTE_NAME = "ca.mcgill.sable.InstructionSource";
    public static final String ASPECT_SHADOW_ATTRIBUTE_NAME = "ca.mcgill.sable.InstructionShadow";
    
    static final AspectInstTagResolver instance = new AspectInstTagResolver();
    //private Integer[] tag_objects = null;
    Int2ObjectOpenHashMap envIDtoIntStack;
    Int2ObjectOpenHashMap envIDtoCallStack;
    Int2ObjectOpenHashMap envIDtoPendingInvokeStack;
    long propagations;
    
    private Map shadowSourceTags;

    private AspectInstTagResolver() {
        super("AspectInstTagResolver", "Adds the bytecode number to the event corresponding to an Instruction Start event");

        /*
        tag_objects = new Integer[AspectTagConstants.ASPECT_TAG_COUNT];
        for (int i = 0; i < tag_objects.length; i++) {
            tag_objects[i] = new Integer(i);
        }
        */
    }

    public static AspectInstTagResolver v() {
        return instance;
    }

    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_LOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHODS),
            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OFFSET
                                | AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID),
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY,
                                AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID),
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_EXIT,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.INSTRUCTION_RESOLVER};
        return deps;
    }

    public void doInit() {
        propagations = 0L;
        envIDtoCallStack = new Int2ObjectOpenHashMap();
        envIDtoIntStack = new Int2ObjectOpenHashMap();
        envIDtoPendingInvokeStack = new Int2ObjectOpenHashMap();
        shadowSourceTags = new HashMap();
    }

    public void doDone() {
        if (DEBUG_SHOW_STACK_SUMMARY) {
            Iterator it;
            int i;
            
            System.err.println("===============================");
            System.err.println("Post-analysis info for tag propagation\n");
            System.err.println("  Dumping object stack heights:");
            it = envIDtoCallStack.keySet().iterator();
            i = 0;
            while (it.hasNext()) {
                Integer key = (Integer) it.next();
                ObjectStack s = (ObjectStack) envIDtoCallStack.get(key);
                System.err.println("    [" + i + "] " + key + ": " + s.size());
                if (s.size() > 0) {
                    System.err.println("      " + s);
                }
                i++;
            }
            System.err.println("\n  Dumping int stack heights:");
            it = envIDtoIntStack.keySet().iterator();
            i = 0;
            while (it.hasNext()) {
                Integer key = (Integer) it.next();
                IntStack s = (IntStack) envIDtoIntStack.get(key);
                System.err.println("    [" + i + "] " + key + ": " + s.size());
                if (s.size() > 0) {
                    System.err.println("      " + s);
                }
                i++;
            }
            System.err.println("===============================");
        }
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        ObjectStack stack;
        BoolStack pendingInvokeStack;

        int env_id = event.getEnvID();
        if (envIDtoCallStack.containsKey(env_id)) {
            stack = (ObjectStack) envIDtoCallStack.get(env_id);
        } else {
            stack = new ObjectStack();
            envIDtoCallStack.put(env_id, stack);
        }
        if (envIDtoPendingInvokeStack.containsKey(env_id)) {
            pendingInvokeStack = (BoolStack) envIDtoPendingInvokeStack.get(env_id);
        } else {
            pendingInvokeStack = new BoolStack();
            envIDtoPendingInvokeStack.put(env_id, pendingInvokeStack);
        }

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                {
                    ClassLoadEvent e = (ClassLoadEvent) event;
                    
                    /* Look at each method in the class, and tag the bytecodes
                     * appropriately */
                    for (int i = 0; i < e.getNumMethods(); i++) {
                        JVMPIMethod m = e.getMethod(i);
                        Bytecode bc = BytecodeResolver.v().getBytecode(m.getMethodID());
                        AspectTagAttribute kindTagAttrib = getAspectTagAttribute(bc, ASPECT_KIND_ATTRIBUTE_NAME);
                        AspectTagAttribute sourceTagAttrib = getAspectTagAttribute(bc, ASPECT_SOURCE_ATTRIBUTE_NAME);
                        AspectTagAttribute shadowTagAttrib = getAspectTagAttribute(bc, ASPECT_SHADOW_ATTRIBUTE_NAME);

                        if (bc != null) {
                            InstructionHandle[] handles = bc.getInstructionHandles();

                            if (kindTagAttrib != null) {
                                for (int j = 0; j < handles.length; j++) {
                                    InstructionHandle h = handles[j];
                                    int tag = kindTagAttrib.getTag(h.getPosition());
                                    /*
                                    if (tag == AspectTagConstants.ASPECT_TAG_INVALID) {
                                        tag = AspectTagConstants.ASPECT_TAG_REGULAR;
                                    }
                                    */
                                    h.pushKindTag(tag);
                                }
                            } else {
                                for (int j = 0; j < handles.length; j++) {
                                    InstructionHandle h = handles[j];
                                    h.pushKindTag(AspectTagConstants.ASPECT_TAG_INVALID);
                                }
                            }
                            
                            if (sourceTagAttrib != null && shadowTagAttrib != null) {
                            	for (int j = 0; j < handles.length; j++) {
                            		InstructionHandle h = handles[j];
                            		int sourceTag = sourceTagAttrib.getTag(h.getPosition());
                            		int shadowTag = shadowTagAttrib.getTag(h.getPosition());
                            		ShadowSourcePair ssp = new ShadowSourcePair(shadowTag, sourceTag);
                            		ShadowSourceTag tag = (ShadowSourceTag)shadowSourceTags.get(ssp);
                            		if(tag == null) {
                            			tag = new ShadowSourceTag(shadowTag, sourceTag);
                            			shadowSourceTags.put(ssp, tag);
                            		}
                            		h.setShadowSourceTag(tag);
                            	}
                            } else {
                            	/*
                            	for (int j = 0; j < handles.length; j++) {
                            		InstructionHandle h = handles[j];
                            		h.setShadowSourceTag(null);
                            	}
                            	*/
                            }
                        }
                    }
                }
                break;
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                {
                    InstructionStartEvent e = (InstructionStartEvent) event;
                    int method_id = e.getMethodID();
                    //InstructionHandle ih = BytecodeResolver.v().getInstructionHandle(method_id, e.getOffset());
                    InstructionHandle ih = e.getInstructionHandle();
                    MethodEntity me = IDResolver.v().getMethodEntity(method_id);
                    IntStack istack = (IntStack) envIDtoIntStack.get(env_id);
                    if (istack == null) {
                        istack = new IntStack();
                        envIDtoIntStack.put(env_id, istack);
                    }

                    if (pendingInvokeStack.peek(false)) {
                        /* Whatever was invoked has been optimized away */
                        if (!stack.empty()) {
                            stack.pop();
                        }
                        if (!istack.empty()) {
                            if (DEBUG_TRACE_STACKS) {
                                System.err.println("POPPING TAG (opt): " + istack.pop());
                            } else {
                                istack.pop();
                            }
                        }
                    }

                    // Handle tag
                    int tag = AspectTagConstants.ASPECT_TAG_INVALID;
                    if (ih != null) {
                        tag = ih.getKindTag();
                        int new_tag;

                        if (DEBUG_TRACE_STACKS) {
                            System.err.println("BEGIN INSTRUCTION TAGGED: " + tag);
                        }
                        // Instruction is not tagged
                        // Check if we have a propagated tag
                        if ((istack != null) && !istack.empty()) {
                            // We do
                            if (DEBUG_TRACE_STACKS) {
                                System.err.println("PROPAGATING STACKED TAG: " + istack.top());
                            }

                            int prop_tag = istack.top();
                            if (tag == AspectTagConstants.ASPECT_TAG_INVALID) {
                                new_tag = prop_tag;
                            } else if (prop_tag != AspectTagConstants.ASPECT_TAG_INVALID) {
                                // Stack contains a propagated tag. 
                                new_tag = AspectTagConstants.REPLACEMENT_TABLE[tag][prop_tag];
                            } else {
                                new_tag = tag;
                            }
                        } else {
                            // This is where we would add a default tag.
                            // We don't do this since the metric analysis will
                            // handle this case.

                            // This code will add a default tag:
                            //tag = AspectTagConstants.ASPECT_TAG_REGULAR;
                            new_tag = tag;
                        }

                        ih.setKindTag(new_tag);
                        tag = new_tag;
                    }
                    
                    
                    switch (e.getCode()) {
                        case Constants.INVOKEVIRTUAL:
                        case Constants.INVOKEINTERFACE:
                        case Constants.INVOKESTATIC:
                        case Constants.INVOKESPECIAL:
                            pendingInvokeStack.pushOrReplace(true);
                            if (ih != null) {
                                InvokeInstruction invInst = (InvokeInstruction) ih.getInstruction();
                                MethodEntity stat_me = invInst.getMethodEntity();
                                if (stat_me == null) {
                                    String className = me.getClassName();
                                    JavaClass jclass = ClassPathExplorer.v().getJavaClass(className);
                                    ConstantPool cp = jclass.getConstantPool();
                                    stat_me = new MethodEntity(invInst.getClassName(cp),
                                            invInst.getMethodName(cp),
                                            org.apache.bcel.generic.Type.getMethodSignature(invInst.getReturnType(cp),
                                                invInst.getArgumentTypes(cp)),
                                            false // FIXME
                                    );
                                    invInst.setMethodEntity(stat_me);
                                }
                                stack.push(stat_me);
                                if (tag != AspectTagConstants.ASPECT_TAG_INVALID) {
                                    if (DEBUG_TRACE_STACKS) {
                                        System.err.println("TAG PROPAGATION: " + tag + " -> " + AspectTagConstants.PROPAGATION_TABLE[tag]);
                                    }
                                    tag = AspectTagConstants.PROPAGATION_TABLE[tag];
                                }
                                if (DEBUG_TRACE_STACKS) {
                                    System.err.println("PUSHING TAG (Inv): " + tag + " [" + stat_me.getFullName() + "]");
                                }
                                istack.push(tag);
                            }
                            break;
                        default:
                            pendingInvokeStack.pushOrReplace(false);
                            break;
                    }
                }
                break;
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                {
                    MethodEntryEvent e = (MethodEntryEvent) event;
                    int method_id = e.getMethodID();
                    MethodEntity me = IDResolver.v().getMethodEntity(method_id);
                    Bytecode bc = BytecodeResolver.v().getBytecode(method_id);

                    me.enter(event.getEnvID()); // Remember that we entered this
                                                // method (fixes bug with
                                                // missing events)

                    if (bc != null) {
                        InstructionHandle[] handles = bc.getInstructionHandles();

                        for (int i = 0; i < handles.length; i++) {
                            InstructionHandle h = handles[i];
                            h.dupKindTag();
                        }
                    } 

                    // START DEBUGGING
                    //String currentMethodName = me.getClassName() + "." + me.getMethodName() + me.getMethodSignature();
                    if (DEBUG_TRACE_STACKS) {
                        System.err.println("+: " + me.getFullName());
                    }
                    // END DEBUGGING
                    
                    IntStack istack = (IntStack) envIDtoIntStack.get(env_id);
                    if (istack == null) {
                        istack = new IntStack();
                        envIDtoIntStack.put(env_id, istack);
                    }


                    int tag = istack.top(AspectTagConstants.ASPECT_TAG_INVALID);
                    
                    if (!stack.empty()) {
                        MethodEntity stat_me = (MethodEntity) stack.top();
                        //String invokedMethodName = (String) stack.top();
                        //String currentMethodName = me.getClassName() + "." + me.getMethodName() + me.getMethodSignature();

                        // System.err.println("Checking for match between");
                        // System.err.println("   1. " + currentMethodName);
                        // System.err.println("   2. " + invokedMethodName);
                        // System.err.println("   --> " + invokeMatch(currentMethodName, invokedMethodName));
                        //if (invokeMatch(currentMethodName, invokedMethodName)) {
                        if (me.matchesInvocation(stat_me)) {
                            // We have a match!!
                            pendingInvokeStack.pushOrReplace(false); // Clear previous value
                            stack.pop(); // remove this call site from the call site stack

                            /*
                            last_tag = envIDtoTags.get(env_id);
                            if (last_tag == AspectTagConstants.ASPECT_TAG_ADV_EXECUTION) {
                                last_tag = AspectTagConstants.ASPECT_TAG_REGULAR;
                            }
                            istack.push(last_tag);
                            */
                            pendingInvokeStack.push(false); // Push a new value for the current method
                            return;
                        } else {
                            // The executed method is not the invoked method
                            // This should mean that were are executing part
                            // of the Class Loader now.
                            
                            // Just skip this method, since we have no corresponding
                            // receiver
                            
                            // DEBUGGING
                            if (DEBUG_SHOW_INV_MISMATCH) {
                                System.err.println("Invocation mismatch:");
                                System.err.println("  Declared: " + stat_me.getFullName());
                                System.err.println("  Invoked : " + me.getFullName());
                            }
                            //Scene.v().showDebug("Mismatch:" +  + " <> " + currentMethodName);
                            // END DEBUGGING
                            ////last_tag = AspectTagConstants.ASPECT_TAG_INVALID;
                        }
                    } else {
                        // DEBUGGING
                        //Scene.v().showDebug("Empty Stack");
                        // END DEBUGGING
                        ////last_tag = AspectTagConstants.ASPECT_TAG_INVALID;
                    }
                    
                    pendingInvokeStack.push(false); // Push a new value for the current method

                    //---------------------------------
                    // The executed method was not the result of the previous invoke
                    // instruction. Push an invalid tag onto the stack so that we
                    // don't propagate the previous tag.

                    int last_tag = AspectTagConstants.ASPECT_TAG_INVALID;
                    /* Push the propagated tag on all instruction stacks */
                    //---------------------------------

                    istack.push(last_tag);
                    // START DEBUGGING
                    if (DEBUG_TRACE_STACKS) {
                        System.err.println("PUSHING TAG: -1 (INVALID)");
                    }
                    // END DEBUGGING
                }
                break;
            case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                {
                    MethodExitEvent e = (MethodExitEvent) event;
                    int method_id = e.getMethodID();
                    MethodEntity me = IDResolver.v().getMethodEntity(method_id);

                    if (!me.exit(event.getEnvID())) {
                        break; // We have never entered this method...
                                // Continuing would cause serious problems
                                // with the stacks.
                    }
                    
                    if (!pendingInvokeStack.empty()) {
                        pendingInvokeStack.pop();
                    }

                    IntStack istack = (IntStack) envIDtoIntStack.get(e.getEnvID());
                    if (istack != null && !istack.empty()) {
                        if (DEBUG_TRACE_STACKS) {
                            System.err.println("POPPING TAG: " + istack.pop());
                        } else {
                            istack.pop();
                        }
                    }

                    /* Pop all instruction stacks in this method */
                    Bytecode bc = BytecodeResolver.v().getBytecode(e.getMethodID());
                    if (bc != null) {
                        InstructionHandle[] handles = bc.getInstructionHandles();
                        for (int j = 0; j < handles.length; j++) {
                            handles[j].popKindTag();
                        }

                        // START DEBUGGING
                        if (DEBUG_TRACE_STACKS) {
                            System.err.println("-: " + me.getFullName());
                        }
                        // END DEBUGGING
                    }

                }
                break;
        }
    }

    /*
    private boolean invokeMatch(String dyn, String stat) {
        // Sanity check
        if (dyn == null || stat == null) {
            return false;
        }

        // Check the easy case first -- exact match
        if (dyn == stat || dyn.equals(stat)) {
            return true;
        }

        int paren_index;
        int last_dot_index;
        
        //Extract the static class name
        paren_index = stat.indexOf('(');
        if (paren_index < 0) {
            throw new RuntimeException("Failed to find paren in stat \'" + stat + "\'");
        }
        last_dot_index = stat.lastIndexOf('.', paren_index - 1);
        if (last_dot_index < 0) {
            throw new RuntimeException("Failed to find last dot in stat \'" + stat + "\'");
        }
        String stat_class_name = stat.substring(0, last_dot_index);
        String method = stat.substring(last_dot_index + 1);
        // Extract the dynamic class name
        paren_index = dyn.indexOf('(');
        if (paren_index < 0) {
            throw new RuntimeException("Failed to find paren in dyn \'" + dyn + "\'");
        }
        last_dot_index = dyn.lastIndexOf('.', paren_index - 1);
        if (last_dot_index < 0) {
            throw new RuntimeException("Failed to find last dot in dyn \'" + dyn + "\'");
        }
        String dyn_class_name = dyn.substring(0, last_dot_index);
        String dyn_method = dyn.substring(last_dot_index + 1);

        // Quickly check if the method names match.
        // If they don't, we're done
        if (!dyn_method.equals(method)) {
            return false;
        }

        // Method names match. We need to look at the class hierarchy
        JavaClass sc = ClassPathExplorer.v().getJavaClass(stat_class_name);
        JavaClass dc = ClassPathExplorer.v().getJavaClass(dyn_class_name);

        if (sc.isInterface()) {
            // Make sure that dc implements the given interface
            return isImplementation(dc, sc) && definesMethod(dc, method);
        }

        //if (stat.indexOf("Stack.size()I") >= 0) {
        //    System.err.println("  Stack-specific info follows");
        //    System.err.println("    stat class - " + stat_class_name);
        //    System.err.println("    stat method - " + method);
        //    System.err.println("    dyn class - " + dyn_class_name);
        //    System.err.println("    dyn method - " + dyn_method);
        //    System.err.println("    sc defines m - " + definesMethod(sc, method));
        //    System.err.println("    dc defines m - " + definesMethod(dc, method));
        //    System.err.println("    sc instanceof dc - " + isSubclass(sc, dc));
        //    System.err.println("    dc instanceof sc - " + isSubclass(dc, sc));
        //}
        // We have an actual class, not interface
        if (definesMethod(sc, method)) {
            // sc does define the method, so dyn must be a matching
            // method in a subclass of c
            return (isSubclass(dc, sc) && definesMethod(dc, method));
        } else {
            // sc does not define the method, dyn must be a matching
            // method in a superclass of c
            return (isSubclass(sc, dc) && definesMethod(dc, method));
        }

        // We can never reach this point
    }

    private boolean definesMethod(JavaClass c, String method) {
        Method[] class_methods = c.getMethods();
        for (int i = 0; i < class_methods.length; i++) {
            Method m = class_methods[i];
            String name = m.getName() + m.getSignature();
            if (name.equals(method)) {
                return true;
            }
        }

        return false;
    }

    private boolean isImplementation(JavaClass c, JavaClass inter) {
        if (c == null) {
            return false;
        }

        String inter_name = inter.getClassName();
        JavaClass[] ifaces = c.getInterfaces();
        if (ifaces != null) {
            for (int i = 0; i < ifaces.length; i++) {
                if (inter_name.equals(ifaces[i].getClassName())) {
                    return true;
                }
            }
        }

        return isImplementation(c.getSuperClass(), inter); 
    }

    private boolean isSubclass(JavaClass sub, JavaClass sup) {
        if (sub == null || sup == null) {
            return false;
        }
        String sup_name = sup.getClassName();
        JavaClass tmp = sub.getSuperClass();
        while (tmp != null) {
            if (sup_name.equals(tmp.getClassName())) {
                return true;
            }
            tmp = tmp.getSuperClass();
        }

        return false;
    }
    */

    private AspectTagAttribute getAspectTagAttribute(Bytecode bc, String tagName) {
        if (bc != null) {
            Attribute attribs[] = bc.getAttributes();
            if (attribs != null) {
                for (int i = 0; i < attribs.length; i++) {
                    if (attribs[i] instanceof Unknown) {
                        Unknown unknown = (Unknown) attribs[i];
                        if (unknown.getName().equals(tagName)) {
                            return new AspectKindTagAttribute(unknown.getBytes());
                        }
                    }
                }
            }
        } 

        return null;
    }
    
    public Map getShadowSourceTags() {
    	return shadowSourceTags;
    }

    class OffsetTagPair implements Comparable {
        private int offset;
        private int tag;

        public OffsetTagPair(int offset, int tag) {
            this.offset = offset;
            this.tag = tag;
        }

        public int getOffset() {
            return offset;
        }

        public int getTag() {
            return tag;
        }

        public int compareTo(Object obj) {
            return offset - ((OffsetTagPair) obj).getOffset();
        }

        public String toString() {
            return "(" + getOffset() + ", " + getTag() + ")";
        }
    }
    
    class ShadowSourcePair {
    	private int shadowId;
    	private int sourceId;
    	
    	public ShadowSourcePair(int shadowId, int sourceId) {
    		this.shadowId = shadowId;
    		this.sourceId = sourceId;
    	}
    	
    	public int getShadowId() {
    		return shadowId;
    	}
    	
    	public int getSourceId() {
    		return sourceId;
    	}
    	
    	public boolean equals(Object o) {
    		if(o == null) {
    			return false;
    		}
    		ShadowSourcePair ssp;
    		try {
    			ssp = (ShadowSourcePair)o;
    		} catch(ClassCastException e) {
    			return false;
    		}
    		if(ssp.getShadowId() == shadowId
    		   && ssp.getSourceId() == sourceId)
    		{
    			return true;
    		}
    		return false;
    	}
    	
    	public int hashCode() {
    		return shadowId ^ sourceId;
    	}
    }
    
    abstract class AspectTagAttribute {
        protected List l;
        
        public AspectTagAttribute(byte[] data) {
            DataInputStream input = new DataInputStream(
                    new ByteArrayInputStream(data));

            l = new ArrayList();
            try {
                while(input.available() > 0) {
                    int offset = input.readUnsignedShort();
                    int kind = input.readInt();
                    l.add(new OffsetTagPair(offset, kind));
                }
            } catch (IOException e) {
                throw new RuntimeException("Reading from byte array threw an IOException!");
            }

            Collections.sort(l);
        }

        protected int getTag(int offset, int defaultTag) {
        	int tag = defaultTag;

            for (int i = 0; i < l.size(); i++) {
                OffsetTagPair pair = (OffsetTagPair) l.get(i);
                
                if (offset < pair.getOffset()) {
                    return tag;
                }

                tag = pair.getTag();
            }
            
            return tag;
        }
        
        abstract protected int getTag(int offset);
    }
    
    class AspectKindTagAttribute extends AspectTagAttribute {
    	
    	public AspectKindTagAttribute(byte[] data) {
    		super(data);
    	}
    	
    	protected int getTag(int offset) {
    		return getTag(offset, AspectTagConstants.ASPECT_TAG_REGULAR);
    	}
    }

    class AspectShadowTagAttribute extends AspectTagAttribute {
    	
    	public AspectShadowTagAttribute(byte[] data) {
    		super(data);
    	}
    	
    	protected int getTag(int offset) {
    		return getTag(offset, AspectTagConstants.ASPECT_TAG_INVALID);
    	}
    }

    class AspectSourceTagAttribute extends AspectTagAttribute {
    	
    	public AspectSourceTagAttribute(byte[] data) {
    		super(data);
    	}
    	
    	protected int getTag(int offset) {
    		return getTag(offset, AspectTagConstants.ASPECT_TAG_INVALID);
    	}
    }
    
    class InvokeInfo {
        public String invokedMethodName;
        public int tag;
    }
}
