/*
 * Created on Feb 28, 2005
 */
package util.tracing;

/**
 * @author Ramnivas Laddad
 * @author Ron Bodkin
 */
public aspect Tracing percflow(topLevelTracedOps()) {
    // this is a natural to set through JMX
    private static boolean enabled = Boolean.getBoolean("trace");

    private int callDepth;
    
    // our pointcut now limits tracing to when tracing is enabled
    pointcut tracedOps() : 
        execution(* *.*(..)) && !within(Tracing) &&
        !cflow(execution(String Tracing.traceStringFor(*)))&& 
        if(enabled);
    
    pointcut topLevelTracedOps() :
        tracedOps() && !cflowbelow(tracedOps());

    before(Object current) : tracedOps() && this(current) {
        printIndent();
        System.out.println("Executing: " + thisJoinPointStaticPart
                           +" for "+traceStringFor(current));
        addToCallDepth(+1);
    }

    before() : tracedOps() && execution(static * *.*(..)) {
        printIndent();
        System.out.println("Executing: " + thisJoinPointStaticPart);
        addToCallDepth(+1);
    }
    
    after() throwing (Throwable t): tracedOps() {
        addToCallDepth(-1);
        printIndent();
        System.out.println("Threw: " + traceStringFor(t));        
    }

    after() returning: tracedOps() && execution(void *(..)){
        addToCallDepth(-1);
        printIndent();
        System.out.println("Returned");        
    }
    
    after() returning (Object o): tracedOps() 
                                  && execution(!void *(..)){
        addToCallDepth(-1);
        printIndent();
        System.out.println("Returned: " + traceStringFor(o));        
    }

    protected String traceStringFor(Object o) {
        if (o==null) {
            return "null";
        } else if (o instanceof Traceable) {
            return ((Traceable)o).toTraceString();
        } else {
            return o.toString();
        }
    }
    
    public String Traceable.toTraceString() {
        return toString();
    }

    private void addToCallDepth(int delta) {
        callDepth+=delta;
    }
    
    private void printIndent() {
        for(int i = 0; i < callDepth; i++) {
            System.out.print("    ");
        }
    }
    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean anEnabled) {
        enabled = anEnabled;
    }
}
