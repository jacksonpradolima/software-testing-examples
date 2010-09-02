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

package adaptj_pool;

import adaptj_pool.event.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.toolkits.*;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.toolkits.analyses.metrics.*;
import adaptj_pool.toolkits.aspects.*;
import adaptj_pool.toolkits.transformers.*;
import adaptj_pool.toolkits.printers.*;
import adaptj_pool.toolkits.graph.*;

import java.io.*;
import java.util.*;

public class Scene {
    /* Quiet modes */
    public static final int HIDE_MESSAGES = 1;
    public static final int HIDE_WARNINGS = 2;
    public static final int HIDE_ERRORS   = 4;
    private static final int SUPPORTED_VERSION = 0;

    public static final String ID_RESOLVER = "tp.IDResolver";
    public static final String INSTRUCTION_RESOLVER = "tp.InstructionResolver";
    
    private static Scene instance = new Scene();
    private int version;
    private short eventInfo[];
    private long counters[];
    private IAEFReader reader = null;
    private IEventBoxApplicable eventToOps[][] = null;
    private int quietMode = 0;
    private boolean showTimes = false;
    private boolean showProgress = false;
    private boolean pipedMode = false;
    private int refreshRate = 1000;

    /* Event processing */
    private boolean isProcessing = false; // true if processing is in progress
    private long eventCount = -1L; // the number of events to process, -1 if unknown
    private long currentEvent = -1L; // the number of the event being currently processed
    
    /* Claspath-specific fields */
    private String pathSeparator = System.getProperty("path.separator");
    private char fileSeparator = System.getProperty("file.separator").charAt(0);
    private String userName = System.getProperty("user.name");
    private String userHome = System.getProperty("user.home");
    private String userDir  = System.getProperty("user.dir");
    boolean isRunningUnderWindows = System.getProperty("os.name").startsWith("Windows");
    List classpath;

    private Scene() {
        setClassPath(System.getProperty("java.class.path"));

        addDefaultOperations();
    }

    public static String getEventNameFromID(int eventID) {
        switch (eventID) {
            case AdaptJEvent.ADAPTJ_ARENA_DELETE:
                return "Arena Delete";
            case AdaptJEvent.ADAPTJ_ARENA_NEW:
                return "Arena New";
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                return "Class Load";
            case AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK:
                return "Class Load Hook";
            case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                return "Class Unload";
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD:
                return "Compiled Method Load";
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD:
                return "Compiled Method Unload";
            case AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST:
                return "Data Dump Request";
            case AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST:
                return "Data Reset Request";
            case AdaptJEvent.ADAPTJ_GC_FINISH:
                return "GC Finish";
            case AdaptJEvent.ADAPTJ_GC_START:
                return "GC Start";
            case AdaptJEvent.ADAPTJ_HEAP_DUMP:
                return "Heap Dump";
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC:
                return "JNI Globalref Alloc";
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC:
                return "JNI Weak Globalref Alloc";
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE:
                return "JNI Globalref Free";
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE:
                return "JNI Weak Globalref Free";
            case AdaptJEvent.ADAPTJ_JVM_INIT_DONE:
                return "JVM Init Done";
            case AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN:
                return "JVM Shut Down";
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
                return "Method Entry";
            case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                return "Method Exit";
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                return "Method Entry 2";
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER:
                return "Monitor Contended Enter";
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED:
                return "Monitor Contended Entered";
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT:
                return "Monitor Contended Exit";
            case AdaptJEvent.ADAPTJ_MONITOR_DUMP:
                return "Monitor Dump";
            case AdaptJEvent.ADAPTJ_MONITOR_WAIT:
                return "Monitor Wait";
            case AdaptJEvent.ADAPTJ_MONITOR_WAITED:
                return "Monitor Waited";
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                return "Object Alloc";
            case AdaptJEvent.ADAPTJ_OBJECT_DUMP:
                return "Object Dump";
            case AdaptJEvent.ADAPTJ_OBJECT_FREE:
                return "Object Free";
            case AdaptJEvent.ADAPTJ_OBJECT_MOVE:
                return "Object Move";
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER:
                return "Raw Monitor Contended Enter";
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED:
                return "Raw Monitor Contended Entered";
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT:
                return "Raw Monitor Contended Exit";
            case AdaptJEvent.ADAPTJ_THREAD_END:
                return "Thread End";
            case AdaptJEvent.ADAPTJ_THREAD_START:
                return "Thread Start";
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                return "Instruction Start";
            case AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE:
                return "Thread Status Change";
            default:
                throw new RuntimeException("Unknown event ID: " + eventID);
        }
    }
    
    /* Output methods */
    public void showMessage(String msg) {
        if ((quietMode & HIDE_MESSAGES) == 0) {
            System.out.println("AdaptJ> " + msg);
        }
    }

    public void showWarning(String msg) {
        if ((quietMode & HIDE_WARNINGS) == 0) {
            System.err.println("AdaptJ Warning> " + msg);
        }
    }

    public void reportError(String msg) {
        if ((quietMode & HIDE_ERRORS) == 0) {
            System.err.println("AdaptJ Error> " + msg );
        }
    }

    public void showDebug(String msg) {
        System.err.println(">>>> " + msg + " <<<<");
    }

    public void reportFileNotFoundError(String filename) {
        reportError("Failed to find file: \"" + filename + "\"");
    }

    public void reportReadError(String filename) {
        reportError("Error reading file: \"" + filename + "\"");
    }

    public void reportWriteError(String filename) {
        reportError("Error writing file: \"" + filename + "\"");
    }

    public void reportFileOpenError(String filename) {
        reportError("Could not open file: \"" + filename + "\"");
    }

    public int getQuietMode() {
        return quietMode;
    }

    public void setQuietMode(int quietMode) {
        this.quietMode = quietMode;
    }

    public void addQuietMode(int quietMode) {
        this.quietMode |= quietMode;
    }

    public void removeQuietMode(int quietMode) {
        this.quietMode &= (~quietMode);
    }

    public int getSupportedVersion() {
        return SUPPORTED_VERSION;
    }

    public boolean supports(int version) {
        return (version >= 0) && (version <= SUPPORTED_VERSION);
    }

    public int getVersion() {
        return version;
    }

    public int checkVersion(int magic, int magic_type) throws AEFFormatException {
        int v = magic & AdaptJSpecConstants.VERSION_MASK;
        int m = magic ^ v;

        if (m != magic_type) {
            throw new AEFFormatException("Invalid magic number");
        }

        return v;
    }

    public long getCounterValue(int eventTypeID) {
        if (eventTypeID < 0 || eventTypeID >= AdaptJEvent.ADAPTJ_EVENT_COUNT) {
            return -1;
        }
        
        return counters[eventTypeID];
    }

    public long getCounterValue(AdaptJEvent event) {
         return getCounterValue(event.getTypeID());
    }

    public boolean isTypeDumped(int eventTypeID) {
        return ((eventInfo[eventTypeID] & AdaptJSpecConstants.ADAPTJ_FIELD_RECORDED) != 0);
    }

    public boolean isTypeDumped(AdaptJEvent event) {
         return isTypeDumped(event.getTypeID());
    }

    public void setRefreshRate(int rate) {
        refreshRate = rate;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void reset() {
        Scene.instance = new Scene();
    }

    public static Scene v() {
        return instance;
    }

    public void addDefaultOperations() {
        /* Initial Pack */
        Pack initialPack = new Pack("init", "Contains operations that are executed before any other pack is executed");
        RestrictedEventPrinter debugPrn = new RestrictedEventPrinter("prn", "Prints events as they enter the processing chain");
        debugPrn.setEnabled(false);
        initialPack.add(debugPrn);
        add(initialPack);
        
        /* Transformer Pack */
        Pack transformerPack = new Pack("tp", "Contains utility operations and transformers");
        transformerPack.add(IDResolver.v());
        transformerPack.add(ClassNameResolver.v());
        transformerPack.add(InstructionResolver.v());
        transformerPack.add(new EventDistiller("ED", "Filters events based on the name of their associated class"));
        add(transformerPack);
        
        /* Metrics Pack */
        MetricPack metricPack = new MetricPack("mp", "Metrics", "Contains dynamic software metrics applications");
        
        /* Add the metrics */
        metricPack.add(new BaseMetric("BM"));
        // -- Laurie's Metrics --
        metricPack.add(new MemoryMetric("MM"));
        metricPack.add(new PointerMetric("PTRM"));
        
        // -- Karel's Metrics --
        metricPack.add(new PolymorphismMetric("PM"));
        //metricPack.add(new ObjectOrientationMetric("OOM"));
        metricPack.add(new ProgramSizeMetric("PSM"));
          
        // -- Clark's Metrics --
        metricPack.add(new SynchronizationMetric("SM"));
        metricPack.add(new FloatingPointMetric("FPM"));
        //metricPack.add(new ConcurrencyMetric("CM"));
        
        // -- Bruno's Metrics --
        metricPack.add(new ArrayMetric("AM"));
        //metricPack.add(new RecursionMetric("RM"));
        
        InstructionMixMetric imm = new InstructionMixMetric("IMM");
        imm.setEnabled(false);
        metricPack.add(imm);
        
        metricPack.setEnabled(false);
        add(metricPack);

        /* Aspects Pack */
        Pack aspectsPack = new MetricPack("aspects", "Aspects", "Contains AspectJ-related operations");
        aspectsPack.add(AspectInstTagResolver.v());
        aspectsPack.add(new AspectMetrics("am"));
        aspectsPack.add(new AspectSizeMetrics("asm"));
        aspectsPack.setEnabled(false);
        add(aspectsPack);

        /* Analysis Pack */
        Pack analysisPack = new Pack("ap", "Contains user-defined analyses and operations"); // Custom analyses should be added here
        InvokeCounter invokeCounter = new InvokeCounter("ic", "Counts the number of executed invoke bytecodes");
        invokeCounter.setEnabled(false);
        analysisPack.add(invokeCounter);
        add(analysisPack);

        /* Printers */
        Pack prnPack = new Pack("pp", "Contains printers that are executed at the end of the processing chain");
        RestrictedEventPrinter prn = new RestrictedEventPrinter("prn", "A simple event printer");
        prn.setEnabled(false);
        prnPack.add(prn);
        DefaultEventPrinter vprn = new DefaultEventPrinter("vprn", "A more verbose event printer");
        vprn.setEnabled(false);
        prnPack.add(vprn);
        ExtendedEventPrinter eprn = new ExtendedEventPrinter("eprn", "A variation on the simple printer that also shows associated class and method names");
        eprn.setEnabled(false);
        prnPack.add(eprn);
        MethodPrinter mprn = new MethodPrinter("mprn", "Prints a list of invoked methods order by frequency");
        mprn.setEnabled(false);
        prnPack.add(mprn);
        
        /* Call Graph Printer */
        CallGraphPrinter cgprn = new CallGraphPrinter("cgprn", "Prints a method call graph");
        cgprn.setEnabled(false);
        prnPack.add(cgprn);

        AspectPrinter aprn = new AspectPrinter("aprn", "Prints aspect tags");
        aprn.setEnabled(false);
        prnPack.add(aprn);
        
        add(prnPack);
    }

    public void add(ICustomizable eba) {
        RootPack.v().add(eba);
    }

    public ICustomizable getByName(String name) {
        return RootPack.v().getByName(name);
    }

    public void disableAllOps() {
        RootPack.v().disableAllOps();
    }

    public void displayHierarchy(PrintStream out) {
        RootPack.v().displayHierarchy(out);
    }

    public void setOption(String name, String value) {
        RootPack.v().setOption(name, value);
    }

    public String getOption(String name) {
        return RootPack.v().getOption(name);
    }

    public void setVerbose(boolean verbose) {
        RootPack.v().setVerbose(verbose);
    }
    
    public boolean isVerbose() {
        return RootPack.v().isVerbose();
    }

    public void setShowTimes(boolean showTimes) {
        this.showTimes = showTimes;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public void setPipedMode(boolean pipedMode) {
        this.pipedMode = pipedMode;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public long getEventCount() {
        return eventCount;
    }

    public long getCurrentEvent() {
        return currentEvent;
    }

    public void loadFile(String fileName) throws IOException, FileNotFoundException,
            AEFFormatException {
        reader = new AEFReader(fileName, pipedMode);
        version = reader.getVersion();
        eventInfo = reader.getEventInfo();

        /* Get the counters right away, in case an analysis wants to look at them
         * to determine whether it should run. This is not recommended, but not
         * prohibited in any way. */
        counters = reader.getStaticCounters();
                                               
        /* Operations that want to bail out after having checked their state need
         * to do so in the preInit step */
        RootPack.v().preInit();

        /* Now, disable all operations for which dependencies are not statisfied
         * (in terms of events and/or other operations */
        showMessage("Checking dependencies");
        checkDependencies();
        showMessage("Dependencies checked");

        /* If we have to preparse the file, do it now. Otherwise, our static counters
         * will have to do the job. */
        if (!pipedMode) {
            reader.preparse();
            counters = reader.getRuntimeCounters();
        }
    }

    public void handleDependencyFailure(IEventBoxApplicable eba, String dependencyName) {
        String depString = (dependencyName != null ? (": " + dependencyName + ".") : ".");
        showMessage("Disabling " + eba.getName() + ": Dependency not satisfied" + depString);
        eba.setEnabled(false);
    }

    public void checkDependencies() {
        /* Make all EventBoxApplicables register for the events they need */
        List[] tmpOpList = new List[AdaptJEvent.ADAPTJ_EVENT_COUNT];
        for (int i = 0; i < tmpOpList.length; i++) {
            tmpOpList[i] = new ArrayList();
        }
        List opList = RootPack.v().flattenHierarchy();
        Iterator it = opList.iterator();
        while (it.hasNext()) {
            IEventBoxApplicable eba = (IEventBoxApplicable) it.next();
            EventDependency[] deps = eba.registerEventDependencies();
            boolean disableEBA = (deps.length > 0); // Disable eba if no dependency is satisfied
            
            /* Check dependencies */
            for (int i = 0; i < deps.length; i++) {
                EventDependency dep = deps[i];
                int eventID = dep.getEventID();
                if (dep.checkInfoAgainst(eventInfo[eventID])) {
                    List l = tmpOpList[eventID];
                    l.add(eba);
                    disableEBA = false; // Don't disable this one, it has something to do!
                } else {
                    int altID = dep.getAlternateEventID();
                    if (altID != AdaptJEvent.ADAPTJ_UNKNOWN) {
                        if (dep.checkInfoAgainst(eventInfo[altID])) {
                            List l = tmpOpList[altID];
                            l.add(eba);
                            disableEBA = false; // Don't disable this one, it has something to do!
                            continue; // Next iteration
                        }
                    }

                    /* Both the primary and alternate event selections are unavailable */
                    if (dep.isRequired()) {
                        handleDependencyFailure(eba, getEventNameFromID(dep.getEventID()) + " Event");
                        break;
                    }
                }
            }

            if (disableEBA) {
                handleDependencyFailure(eba, "No satisfied optional dependency");
            }
        }

        /* Convert lists to arrays, for efficiency */
        eventToOps = new IEventBoxApplicable[AdaptJEvent.ADAPTJ_EVENT_COUNT][];
        for (int i = 0; i < tmpOpList.length; i++) {
            List l = tmpOpList[i];
            int lSize = l.size();
            IEventBoxApplicable[] ops;
            if (lSize > 0) {
                ops = new IEventBoxApplicable[lSize];
                int p = 0;
                Iterator lIt = l.iterator();
                while (lIt.hasNext()) {
                    IEventBoxApplicable ebApp = (IEventBoxApplicable) lIt.next();
                    if (ebApp instanceof ITimedCustomizable) {
                        ((ITimedCustomizable) ebApp).setTimed(showTimes);
                    }
                    ops[p++] = ebApp;
                }
            } else {
                ops = null;
            }
            eventToOps[i] = ops;
            l = tmpOpList[i] = null;
        }
        tmpOpList = null;

        /* Check Operation Dependencies using a DAG of SCCs on the dependency graph */
        EventBoxApplicableGraph g = new EventBoxApplicableGraph(RootPack.v());
        g.checkDependencies();
    }

    public boolean performOperations() {
        ProgressFrame progressFrame = null;
        
        if (reader == null) {
            reportError("Event Reader is not initialized");
            return false;
        }
        
        eventCount = reader.getEventCount();
        currentEvent = 0L;
        
        if (eventCount >= 0) {
            showMessage(eventCount + " events available");
        } else {
            showMessage("Event count not available");
        }
        

        if (showProgress) {
            progressFrame = new ProgressFrame(refreshRate);
        }

        try {
            RootPack.v().init();            

            showMessage("Rechecking dependencies");
            checkDependencies();
            showMessage("Dependencies checked");
            
            EventBox box = new SingleEventBox(reader);
            isProcessing = true;
            if (progressFrame != null) {
                progressFrame.update();
            }
            
            showMessage("Performing operations");
            IEventBoxApplicable[] eventOps;
            while (true) {
                try {
                    eventOps = eventToOps[box.getEvent().getTypeID()];
                    if (eventOps != null) {
                        for (int j = 0; j < eventOps.length; j++) {
                            eventOps[j].apply(box);
                        }
                    }
                } catch (EventSkipException e) {
                    /* Just skip to the next event */
                }
                
                if (!box.step()) {
                    break;
                }
                
                currentEvent++;
            }

            if (progressFrame != null) {
                progressFrame.update();
            }

            if (reader.isPiped()) {
                counters = reader.getRuntimeCounters();
            }
            
            RootPack.v().done();
            

            if (showTimes) {
                RootPack.v().showTimes(System.out);
            }
        } finally {
            if (progressFrame != null) {
                progressFrame.stop();
            }
        }

        showMessage("Operations Performed Successfully");

        return true;
    }
        
    public char getFileSeparator() {
        return fileSeparator;
    }

    public char getPathSeparator() {
        return pathSeparator.charAt(0);
    }

    public boolean isOSWindows() {
        return isRunningUnderWindows;
    }

    private String processPath(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        try {
            return f.getCanonicalPath();
        } catch (IOException e) {
            return null;
        }
    }
    
    public List getClassPath() {
        return Collections.unmodifiableList(classpath);
    }

    public Iterator classPathIterator() {
        return classpath.iterator();
    }

    public boolean setClassPath(String path) {
        List tmpClassPath = new LinkedList();
        StringTokenizer tokenizer = new StringTokenizer(path, pathSeparator, false);
        
        while (tokenizer.hasMoreTokens()) {
            String candidate = tokenizer.nextToken();
            String processedPath = processPath(candidate);
            if (!tmpClassPath.contains(processedPath) && processedPath != null) {
                tmpClassPath.add(processedPath);
            }
        }
        
        classpath = tmpClassPath;
        return true;
    }

    public boolean addToClassPathAfter(String path) {
        boolean result = false;
        StringTokenizer tokenizer = new StringTokenizer(path, pathSeparator, false);

        while(tokenizer.hasMoreTokens()) {
            String candidate = tokenizer.nextToken();
            String processedPath = processPath(candidate);
            if (classpath.contains(processedPath)) {
                result = true;
            } else if (processedPath != null) {
                classpath.add(processedPath);
                result = true;
            }
        }

        return result;
    }
    
    public boolean addToClassPathBefore(String path) {
        boolean result = false;
        StringTokenizer tokenizer = new StringTokenizer(path, pathSeparator, false);

        while(tokenizer.hasMoreTokens()) {
            String candidate = tokenizer.nextToken();
            String processedPath = processPath(candidate);
            if (classpath.contains(processedPath)) {
                result = true;
            } else if (processedPath != null) {
                classpath.add(0, processedPath);
                result = true;
            }
        }

        return result;
    }
    
    public boolean removeFromClassPath(String path) {
        boolean result = false;
        StringTokenizer tokenizer = new StringTokenizer(path, pathSeparator, false);

        while(tokenizer.hasMoreTokens()) {
            String candidate = tokenizer.nextToken();
            String processedPath = processPath(candidate);
            if (classpath.contains(processedPath) && processedPath != null) {
                result = classpath.remove(processedPath) || result;
            }
        }

        return result;
    }
}
