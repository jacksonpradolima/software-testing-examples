/* ========================================================================== *
 *                                   AdaptJ                                   *
 *              A Dynamic Application Profiling Toolkit for Java              *
 *                                                                            *
 *  Copyright (C) 2003-2004 Bruno Dufour                                      *
 *                                                                            *
 *  This software is under (heavy) development. Please send bug reports,      *
 *  comments or suggestions to bdufou1@sable.mcgill.ca.                       *
 *                                                                            *
 *  This library is free software; you can redistribute it and/or             *
 *  modify it under the terms of the GNU Library General Public               *
 *  License as published by the Free Software Foundation; either              *
 *  version 2 of the License, or (at your option) any later version.          *
 *                                                                            *
 *  This library is distributed in the hope that it will be useful,           *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU         *
 *  Library General Public License for more details.                          *
 *                                                                            *
 *  You should have received a copy of the GNU Library General Public         *
 *  License along with this library; if not, write to the                     *
 *  Free Software Foundation, Inc., 59 Temple Place - Suite 330,              *
 *  Boston, MA 02111-1307, USA.                                               *
 * ========================================================================== */

package adaptj_pool.toolkits.analyses.metrics;

import adaptj_pool.Scene;
import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.util.xml.*;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.toolkits.transformers.*;
import adaptj_pool.toolkits.types.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.util.text.HelpDisplayManager;
import adaptj_pool.util.text.OptionStringParser;

import java.text.*;
import java.util.Iterator;

import it.unimi.dsi.fastUtil.*;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;

public class PolymorphismMetric extends MetricAnalysis {
    private final static int CALL_SITE_KIND_UNKNOWN = -1;
    private final static int CALL_SITE_KIND_VIRTUAL = 0;
    private final static int CALL_SITE_KIND_STATIC = 1;
    private final static int CALL_SITE_KIND_SPECIAL = 2;
        
    private static final int ARITY_BIN_SIZE = 3;

    private final static int CACHE_SIZE = 32; // NB: This has to be a power of 2!
    private final static int CACHE_MASK = 0x0000001F; // Keep 5 bits (2^5 = 32)

    private Object2ObjectOpenHashMap callSiteToInfo;
    private Int2ObjectOpenHashMap envIDtoStack;
    private Int2BooleanOpenHashMap lastInstWasInvoke;
    
    /* Results */
    private long instCount, appInstCount;
    private long totalInvokes, totalAppInvokes;
    private long totalCallSites, totalAppCallSites;
    private long totalCalls, totalAppCalls;
    private long totalReceiverPolyCallSites, totalReceiverAppPolyCallSites;
    private long totalTargetPolyCallSites, totalTargetAppPolyCallSites;
    private long totalReceiverCacheMiss, totalReceiverAppCacheMiss;
    private long totalTargetCacheMiss, totalTargetAppCacheMiss;
    private double typesPerSite, typesPerAppSite;
    private double typesPerPolySite, typesPerAppPolySite;
    private double targetsPerSite, targetsPerAppSite;
    private double targetsPerPolySite, targetsPerAppPolySite;
    long[] receiverTypeBin, appReceiverTypeBin;
    long[] receiverTypeCallsBin, appReceiverTypeCallsBin;
    long[] targetBin, appTargetBin;
    long[] targetCallsBin, appTargetCallsBin;

    private int totalNullTypes;
    private int totalNullMethods;

    //private boolean includeAppMethods = false;

    /* Avoid creating a new object each time */
    private InstructionEntity instEntity = new InstructionEntity();

    private int[] cachedMethodIDs;
    private MethodEntity[] cachedMethodEntities;
    private CallSiteInfo[] cachedCallSiteInfos;
    private InstructionHandle[] cachedInstructionHandles;

    /**
     * Sums up all values in the map.
     * 
     * @param m The map for which the values are to be summed
     * @return The sum of all values in the map <code>m</code>
     */
    private int getCount(Object2IntOpenHashMap m) {
        int sum = 0;
        Iterator it = m.keySet().iterator();
        while (it.hasNext()) {
            sum += m.getInt(it.next());
        }

        return sum;
    }

    private void getBinRange(int index, int[] tuple) {
        switch (index) {
            case 0:
                tuple[0] = tuple[1] = 1;
                break;
            case 1:
                tuple[0] = tuple[1] = 2;
                break;
            case 2:
                tuple[0] = 3;
                tuple[1] = Integer.MAX_VALUE;
                break;
            default:
                throw new RuntimeException("Invalid bin index");
        }
    }

    public PolymorphismMetric(String name) {
        super(name,
              "Polymorphism Metrics",
              "Measure the level of polymorphism in a program by looking " +
                  "at \"invokevirtual\" instructions");
    }

    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OFFSET
                                | AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID),

            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OBJ_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER, Scene.INSTRUCTION_RESOLVER};
        return deps;
    }

    public void doInit() {
        callSiteToInfo = new Object2ObjectOpenHashMap();
        envIDtoStack = new Int2ObjectOpenHashMap();
        lastInstWasInvoke = new Int2BooleanOpenHashMap();
        lastInstWasInvoke.setDefRetValue(false);

        
        receiverTypeBin = new long[ARITY_BIN_SIZE];
        targetBin = new long[ARITY_BIN_SIZE];
        receiverTypeCallsBin = new long[ARITY_BIN_SIZE];
        targetCallsBin = new long[ARITY_BIN_SIZE];
        
        appReceiverTypeBin = new long[ARITY_BIN_SIZE];
        appTargetBin = new long[ARITY_BIN_SIZE];
        appReceiverTypeCallsBin = new long[ARITY_BIN_SIZE];
        appTargetCallsBin = new long[ARITY_BIN_SIZE];
        
        instCount = 0L;
        appInstCount = 0L;
        totalInvokes = 0L;
        totalAppInvokes = 0L;
        
        totalReceiverCacheMiss = 0;
        totalTargetCacheMiss = 0;
        totalCalls = 0;

        totalReceiverAppCacheMiss = 0;
        totalTargetAppCacheMiss = 0;
        totalAppCalls = 0;
        
        totalNullTypes = 0;
        totalNullMethods = 0;

        cachedMethodIDs = new int[CACHE_SIZE];
        cachedMethodEntities = new MethodEntity[CACHE_SIZE];
        cachedCallSiteInfos = new CallSiteInfo[CACHE_SIZE];
        cachedInstructionHandles = new InstructionHandle[CACHE_SIZE];
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        ObjectStack stack;

        int env_id = event.getEnvID();
        if (envIDtoStack.containsKey(env_id)) {
            stack = (ObjectStack) envIDtoStack.get(env_id);
        } else {
            stack = new ObjectStack();
            envIDtoStack.put(env_id, stack);
        }

        int method_id = ((MethodEvent) event).getMethodID();
        MethodEntity me;
        int key = (method_id >> 4) & CACHE_MASK;
        if (method_id == cachedMethodIDs[key] && method_id != 0) {
            me = cachedMethodEntities[key];
        } else {
            me = IDResolver.v().getMethodEntity(method_id);
            cachedMethodEntities[key] = me;
            cachedMethodIDs[key] = method_id;
        }

        boolean app = (me == null || !me.isStandardLib());

        if (me == null) {
            totalNullMethods++;
            return;
        }
        
        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START: {
                    InstructionStartEvent e = (InstructionStartEvent) event;
                    int kind = CALL_SITE_KIND_UNKNOWN;
                    instCount++;
                    if (app) {
                        appInstCount++;
                    }
                    switch (e.getCode()) {
                        case Constants.INVOKEVIRTUAL:
                        case Constants.INVOKEINTERFACE:
                            kind = CALL_SITE_KIND_VIRTUAL;
                            
                            totalInvokes++;
                            if (app) {
                                totalAppInvokes++;
                            }
                            break;
                        case Constants.INVOKESTATIC:
                            kind = CALL_SITE_KIND_STATIC;
                            break;
                        case Constants.INVOKESPECIAL:
                            kind = CALL_SITE_KIND_SPECIAL;
                            break;
                        default:
                            if (lastInstWasInvoke.get(env_id)) {
                                /* Whatever was invoked has been optimized away */
                                if (!stack.empty()) {
                                    stack.pop();
                                }
                            }
                            lastInstWasInvoke.put(env_id, false);
                            return;
                    }
                    lastInstWasInvoke.put(env_id, true);
                    processCallSite(me, method_id, e.getOffset(), stack, kind);
                }
                break;
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2: {
                    MethodEntry2Event e = (MethodEntry2Event) event;
                    
                    lastInstWasInvoke.put(env_id, false);
                    
                    if (!stack.empty()) {
                        CallSiteInfo lastCallSiteInfo = (CallSiteInfo) stack.top();
                        if (lastCallSiteInfo == null) {
                            // Just skip this method, since we have no corresponding
                            // receiver
                            return;
                        }
                        
                        String invokedMethodName = lastCallSiteInfo.invokedMethodName;
                        String currentMethodName = me.getMethodName() + me.getMethodSignature();

                        if (currentMethodName.equals(invokedMethodName)) {
                            // We have a match!!
                            stack.pop(); // remove this call site from the call site stack
                            if (lastCallSiteInfo.kind == CALL_SITE_KIND_VIRTUAL) {
                                processMethodEntry(me, e.getObjID(), lastCallSiteInfo);
                            }
                        } else {
                            // The executed method is not the invoked method
                            // This should mean that were are executing part
                            // of the Class Loader now.
                            
                            // Just skip this method, since we have no corresponding
                            // receiver
                            
                            // DEBUGGING
                            //Scene.v().showDebug("Mismatch:" + invokedMethodName + " <> " + currentMethodName);
                            // END DEBUGGING
                            return;
                        }
                    } else {
                        //String currentMethodName = me.getMethodName() + me.getMethodSignature();

                        // DEBUGGING
                        //Scene.v().showDebug("No call site for " + currentMethodName);
                        // END DEBUGGING
                        return; 
                    }
                }
                break;
            default:
                break;
        }
    }

    private void processCallSite(MethodEntity me, int method_id, int offset, ObjectStack stack, int kind) {
        InstructionHandle ih = BytecodeResolver.v().getInstructionHandle(method_id, offset);
        if (ih != null) {
            String className = me.getClassName();
            JavaClass jclass = ClassPathExplorer.v().getJavaClass(className);
            InvokeInstruction invInst = (InvokeInstruction) ih.getInstruction();
            ConstantPool cp = jclass.getConstantPool();
            String invokedMethodName = invInst.getMethodName(cp)
                    + org.apache.bcel.generic.Type.getMethodSignature(invInst.getReturnType(cp),
                            invInst.getArgumentTypes(cp));

            CallSiteInfo lastCallSiteInfo;
            instEntity.setInternalValues(ih, me);
            if (callSiteToInfo.containsKey(instEntity)) {
                lastCallSiteInfo = (CallSiteInfo) callSiteToInfo.get(instEntity);
            } else {
                lastCallSiteInfo = new CallSiteInfo();
                lastCallSiteInfo.appCallSite = !me.isStandardLib();
                InstructionEntity entity = new InstructionEntity(instEntity);
                callSiteToInfo.put(entity, lastCallSiteInfo);
            }
            lastCallSiteInfo.invokedMethodName = invokedMethodName;
            lastCallSiteInfo.kind = kind;
            stack.push(lastCallSiteInfo);
        }
    }

    private void processMethodEntry(MethodEntity me, int obj_id, CallSiteInfo lastCallSiteInfo) {
        boolean appSite = lastCallSiteInfo.appCallSite;
        
        totalCalls += 1;
        if (lastCallSiteInfo.appCallSite) {
            totalAppCalls += 1;
        }

        // Receiver
        Type t = TypeRepository.v().getType(obj_id);
        if (t != null) {
            if (lastCallSiteInfo.lastReceiverType == null
                    || !lastCallSiteInfo.lastReceiverType.equals(t)) {
                totalReceiverCacheMiss += 1;
                if (appSite) {
                    totalReceiverAppCacheMiss += 1;
                } else {
                    lastCallSiteInfo.receiverTypeCacheMiss += 1;
                }
            }
            lastCallSiteInfo.lastReceiverType = t;
            Object2IntOpenHashMap m = lastCallSiteInfo.receiverTypes;
            m.put(t, m.getInt(t) + 1);
        } else {
            totalNullTypes++;
        }

        // Target
        if (me != null) {
            if (lastCallSiteInfo.lastTarget == null
                    || !lastCallSiteInfo.lastTarget.equals(me)) {
                totalTargetCacheMiss += 1;
                if (appSite) {
                    totalTargetAppCacheMiss += 1;
                } else {
                    lastCallSiteInfo.targetCacheMiss += 1;
                }
            }
            lastCallSiteInfo.lastTarget = me;
            Object2IntOpenHashMap m = lastCallSiteInfo.targets;
            m.put(me, m.getInt(me) + 1);
        } else {
            totalNullMethods++;
        }
    }

    public void computeResults() {
        totalCallSites = totalAppCallSites = 0;
        totalReceiverPolyCallSites = totalReceiverAppPolyCallSites = 0;
        totalTargetPolyCallSites = totalTargetAppPolyCallSites = 0;
        typesPerSite = typesPerAppSite = 0.0;
        targetsPerSite = targetsPerAppSite = 0.0;
        typesPerPolySite = typesPerAppPolySite = 0.0;
        targetsPerPolySite = targetsPerAppPolySite = 0.0;

        
        /*
        { // DEBUG
            Iterator callSitesIt = callSiteToInfo.keySet().iterator();
            while (callSitesIt.hasNext()) {
                Object key = callSitesIt.next();
                CallSiteInfo csinfo = (CallSiteInfo) callSiteToInfo.get(key);
                if (csinfo.appCallSite && csinfo.targets.size() > 1) {
                    System.err.println("Poly site: " + key);
                }
            }
        }
        */
        
        /* Compute Results */
        Iterator it = callSiteToInfo.values().iterator();
        while (it.hasNext()) {
            CallSiteInfo info = (CallSiteInfo) it.next();
            int numTypes = info.receiverTypes.size();
            int numTargets = info.targets.size();
            typesPerSite += numTypes;
            targetsPerSite += numTargets;

            
            switch (numTypes) {
                case 0:
                    /* Ignore this call site. Empirical studies
                       show that this is (most likely) the result
                       of some optimization which removes
                       invokevirtuals which point to an
                       empty method. */
                    break;
                case 1:
                    receiverTypeBin[0] += 1;
                    receiverTypeCallsBin[0] += getCount(info.receiverTypes);
                    totalCallSites += 1;
                    break;
                case 2:
                    receiverTypeBin[1] += 1;
                    receiverTypeCallsBin[1] += getCount(info.receiverTypes);
                    totalCallSites += 1;
                    typesPerPolySite += 2;
                    totalReceiverPolyCallSites += 1;
                    break;
                default:
                    receiverTypeBin[2] += 1;
                    receiverTypeCallsBin[2] += getCount(info.receiverTypes);
                    totalCallSites += 1;
                    typesPerPolySite += numTypes;
                    totalReceiverPolyCallSites += 1;
                    break;
            }

            switch (numTargets) {
                case 0:
                    break;
                case 1:
                    targetBin[0] += 1;
                    targetCallsBin[0] += getCount(info.targets);
                    break;
                case 2:
                    targetBin[1] += 1;
                    targetCallsBin[1] += getCount(info.targets);
                    targetsPerPolySite += 2;
                    totalTargetPolyCallSites += 1;
                    break;
                default:
                    targetBin[2] += 1;
                    targetCallsBin[2] += getCount(info.targets);
                    targetsPerPolySite += numTargets;
                    totalTargetPolyCallSites += 1;
                    break;
                    
            }

            if (info.appCallSite) {
                typesPerAppSite += numTypes;
                targetsPerAppSite += numTargets;
                
                /**
                 * DEBUGGING
                 **/
                /*
                System.err.println("----------------------------------------------------");
                Iterator debugIt = info.targets.keySet().iterator();
                while (debugIt.hasNext()) {
                    Object o = debugIt.next();
                    System.err.println("Target (" + info.targets.getInt(o) + ") : " + o);
                }
                System.err.println("----------------------------------------------------");
                */

                switch (numTypes) {
                    case 0:
                        /* Ignore this call site. Empirical studies
                           show that this is (most likely) the result
                           of some optimization which removes
                           invokevirtuals which point to an
                           empty method. */
                        break;
                    case 1:
                        appReceiverTypeBin[0] += 1;
                        appReceiverTypeCallsBin[0] += getCount(info.receiverTypes);
                        totalAppCallSites += 1;
                        break;
                    case 2:
                        appReceiverTypeBin[1] += 1;
                        appReceiverTypeCallsBin[1] += getCount(info.receiverTypes);
                        totalAppCallSites += 1;
                        typesPerAppPolySite += 2;
                        totalReceiverAppPolyCallSites += 1;
                        break;
                    default:
                        appReceiverTypeBin[2] += 1;
                        appReceiverTypeCallsBin[2] += getCount(info.receiverTypes);
                        totalAppCallSites += 1;
                        typesPerAppPolySite += numTypes;
                        totalReceiverAppPolyCallSites += 1;
                        break;
                }

                switch (numTargets) {
                    case 0:
                        break;
                    case 1:
                        appTargetBin[0] += 1;
                        appTargetCallsBin[0] += getCount(info.targets);
                        break;
                    case 2:
                        appTargetBin[1] += 1;
                        appTargetCallsBin[1] += getCount(info.targets);
                        targetsPerAppPolySite += 2;
                        totalTargetAppPolyCallSites += 1;
                        break;
                    default:
                        appTargetBin[2] += 1;
                        appTargetCallsBin[2] += getCount(info.targets);
                        targetsPerAppPolySite += numTargets;
                        totalTargetAppPolyCallSites += 1;
                        break;
                        
                }
            }
        }

        typesPerSite = typesPerSite / totalCallSites;
        typesPerAppSite = typesPerAppSite / totalAppCallSites;
        
        typesPerPolySite = typesPerPolySite / totalReceiverPolyCallSites;
        typesPerAppPolySite = typesPerAppPolySite / totalReceiverAppPolyCallSites;

        targetsPerSite = targetsPerSite / totalCallSites;
        targetsPerPolySite = targetsPerPolySite / totalTargetPolyCallSites;
        
        targetsPerAppSite = targetsPerAppSite / totalCallSites;
        targetsPerAppPolySite = targetsPerAppPolySite / totalTargetPolyCallSites;
        
        if (totalNullTypes > 0) {
            Scene.v().showDebug("Total null types: " + totalNullTypes);
        }

        if (totalNullMethods > 0) {
            Scene.v().showDebug("Total null methods: " + totalNullMethods);
        }
    }
    
    public void outputXML(XMLMetricPrinter xmlPrinter) {
        double dblVal;
        int[] binBounds = new int[2];

        // polymorphism.callSites.value
        xmlPrinter.addValue("polymorphism", "callSites", totalCallSites);

        // polymorphism.receiverArity.bin
        xmlPrinter.addBin("polymorphism", "receiverArity");
        for (int i = 0; i < ARITY_BIN_SIZE; i++) {
            dblVal = ((double) receiverTypeBin[i]) / totalCallSites;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("polymorphism", "receiverArity", binBounds[0], binBounds[1], dblVal);
        }

        // polymorphism.targetArity.bin
        xmlPrinter.addBin("polymorphism", "targetArity");
        for (int i = 0; i < ARITY_BIN_SIZE; i++) {
            dblVal = ((double) targetBin[i]) / totalCallSites;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("polymorphism", "targetArity", binBounds[0], binBounds[1], dblVal);
        }

        // polymorphism.receiverArityCalls.bin
        xmlPrinter.addBin("polymorphism", "receiverArityCalls");
        for (int i = 0; i < ARITY_BIN_SIZE; i++) {
            dblVal = ((double) receiverTypeCallsBin[i]) / totalCalls;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("polymorphism", "receiverArityCalls", binBounds[0], binBounds[1], dblVal);
        }

        // polymorphism.targetArityCalls.bin
        xmlPrinter.addBin("polymorphism", "targetArityCalls");
        for (int i = 0; i < ARITY_BIN_SIZE; i++) {
            dblVal = ((double) targetCallsBin[i]) / totalCalls;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("polymorphism", "targetArityCalls", binBounds[0], binBounds[1], dblVal);
        }
        
        // polymorphism.receiverPolyDensity.value
        dblVal = ((double) (receiverTypeBin[1] + receiverTypeBin[2])) / totalCallSites;
        xmlPrinter.addValue("polymorphism", "receiverPolyDensity", dblVal);

        // polymorphism.targetPolyDensityCalls.value
        dblVal = ((double) (targetBin[1] + targetBin[2])) / totalCallSites;
        xmlPrinter.addValue("polymorphism", "targetPolyDensity", dblVal);
        
        // polymorphism.receiverPolyDensityCalls.value
        dblVal = ((double) (receiverTypeCallsBin[1] + receiverTypeCallsBin[2])) / totalCalls;
        xmlPrinter.addValue("polymorphism", "receiverPolyDensityCalls", dblVal);

        // polymorphism.targetPolyDensity.value
        dblVal = ((double) (targetCallsBin[1] + targetCallsBin[2])) / totalCalls;
        xmlPrinter.addValue("polymorphism", "targetPolyDensityCalls", dblVal);

        // polymorphism.receiverCacheMissRate.value
        dblVal = ((double) totalReceiverCacheMiss) / totalCalls;
        xmlPrinter.addValue("polymorphism", "receiverCacheMissRate", dblVal);
        
        // polymorphism.targetCacheMissRate.value
        dblVal = ((double) totalTargetCacheMiss) / totalCalls;
        xmlPrinter.addValue("polymorphism", "targetCacheMissRate", dblVal);
          

        /* Samething, but for the application only (no libs) */


        // polymorphism.appCallSites.value
        xmlPrinter.addValue("polymorphism", "appCallSites", totalAppCallSites);

        // polymorphism.appReceiverArity.bin
        xmlPrinter.addBin("polymorphism", "appReceiverArity");
        for (int i = 0; i < ARITY_BIN_SIZE; i++) {
            dblVal = ((double) appReceiverTypeBin[i]) / totalAppCallSites;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("polymorphism", "appReceiverArity", binBounds[0], binBounds[1], dblVal);
        }

        // polymorphism.appTargetArity.bin
        xmlPrinter.addBin("polymorphism", "appTargetArity");
        for (int i = 0; i < ARITY_BIN_SIZE; i++) {
            dblVal = ((double) appTargetBin[i]) / totalAppCallSites;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("polymorphism", "appTargetArity", binBounds[0], binBounds[1], dblVal);
        }

        // polymorphism.appReceiverArityCalls.bin
        xmlPrinter.addBin("polymorphism", "appReceiverArityCalls");
        for (int i = 0; i < ARITY_BIN_SIZE; i++) {
            dblVal = ((double) appReceiverTypeCallsBin[i]) / totalAppCalls;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("polymorphism", "appReceiverArityCalls", binBounds[0], binBounds[1], dblVal);
        }

        // polymorphism.appTargetArityCalls.bin
        xmlPrinter.addBin("polymorphism", "appTargetArityCalls");
        for (int i = 0; i < ARITY_BIN_SIZE; i++) {
            dblVal = ((double) appTargetCallsBin[i]) / totalAppCalls;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("polymorphism", "appTargetArityCalls", binBounds[0], binBounds[1], dblVal);
        }
        
        // polymorphism.appReceiverPolyDensity.value
        dblVal = ((double) (appReceiverTypeBin[1] + appReceiverTypeBin[2])) / totalAppCallSites;
        xmlPrinter.addValue("polymorphism", "appReceiverPolyDensity", dblVal);

        // polymorphism.appTargetPolyDensityCalls.value
        dblVal = ((double) (appTargetBin[1] + appTargetBin[2])) / totalAppCallSites;
        xmlPrinter.addValue("polymorphism", "appTargetPolyDensity", dblVal);
        
        // polymorphism.appReceiverPolyDensityCalls.value
        dblVal = ((double) (appReceiverTypeCallsBin[1] + appReceiverTypeCallsBin[2])) / totalAppCalls;
        xmlPrinter.addValue("polymorphism", "appReceiverPolyDensityCalls", dblVal);

        // polymorphism.appTargetPolyDensity.value
        dblVal = ((double) (appTargetCallsBin[1] + appTargetCallsBin[2])) / totalAppCalls;
        xmlPrinter.addValue("polymorphism", "appTargetPolyDensityCalls", dblVal);

        // polymorphism.appReceiverCacheMissRate.value
        dblVal = ((double) totalReceiverAppCacheMiss) / totalAppCalls;
        xmlPrinter.addValue("polymorphism", "appReceiverCacheMissRate", dblVal);
        
        // polymorphism.appTargetCacheMissRate.value
        dblVal = ((double) totalTargetAppCacheMiss) / totalAppCalls;
        xmlPrinter.addValue("polymorphism", "appTargetCacheMissRate", dblVal);

        // polymorphism.invokeDensity.value
        dblVal = 1000.0 * ((double) totalInvokes) / instCount;
        xmlPrinter.addValue("polymorphism", "invokeDensity", dblVal);
        
        // polymorphism.appInvokeDensity.value
        dblVal = 1000.0 * ((double) totalAppInvokes) / appInstCount;
        xmlPrinter.addValue("polymorphism", "appInvokeDensity", dblVal);
    }

    /*
    public boolean getIncludeAppMethods() {
        return includeAppMethods;
    }
    
    public void setIncludeAppMethods(boolean includeAppMethods) {
        this.includeAppMethods = includeAppMethods;
    }

    public void setOption(String name, String value) {
        if (name.equals("includeAppMethods")) {
            includeAppMethods = OptionStringParser.parseBoolean(value);
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) {
        if (name.equals("includeAppMethods")) {
            if (includeAppMethods) {
                return "true";
            } else {
                return "false";
            }
        }

        return super.getOption(name);
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);

        manager.displayOptionHelp("includeAppMethods[:boolean]", "Specifies whether invoking an application method from a library method is considered in the application statistics");
    }
    */

    class CallSiteInfo {
        public int receiverTypeCacheMiss;
        public int targetCacheMiss;
        public boolean appCallSite = false;
        public Type lastReceiverType = null;
        public MethodEntity lastTarget = null;
        public Object2IntOpenHashMap receiverTypes;
        public Object2IntOpenHashMap targets;
        public String invokedMethodName = null;
        public int kind = CALL_SITE_KIND_UNKNOWN;

        public CallSiteInfo() {
            receiverTypeCacheMiss = 0;
            targetCacheMiss = 0;
            receiverTypes = new Object2IntOpenHashMap();
            receiverTypes.setDefRetValue(0);
            targets = new Object2IntOpenHashMap();
            targets.setDefRetValue(0);
        }

        public String toString() {
            return "<" + invokedMethodName + (appCallSite ? " lib" : " app") + ">";
        }
    }
}
