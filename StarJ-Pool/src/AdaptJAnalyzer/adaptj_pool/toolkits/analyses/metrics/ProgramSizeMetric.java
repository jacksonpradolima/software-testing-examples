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
import adaptj_pool.JVMPI.*;
import adaptj_pool.util.*;
import adaptj_pool.util.xml.*;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.toolkits.transformers.EventDistiller;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.util.text.HelpDisplayManager;

import java.text.*;
import java.util.*;

import java.lang.Math;

import it.unimi.dsi.fastUtil.*;
import org.apache.bcel.generic.InstructionHandle;

public class ProgramSizeMetric extends MetricAnalysis {
    private static final double DEFAULT_THRESHOLD = 0.90;
    //private final static int CACHE_SIZE = 32; // NB: This has to be a power of 2!
    //private final static int CACHE_MASK = 0x0000001F; // Keep 5 bits (2^5 = 32)

    private double threshold;

    //private int[] cachedMethodIDs;
    //private Bytecode cachedCode;
    //private MethodEntity[] cachedMethodEntities;
    
    private int totalLoadedClasses = 0;
    private int totalLoadedMethods = 0;
    private int totalLoadedAppClasses = 0;
    private int totalLoadedAppMethods = 0;
    
    private long totalSize;
    private long totalAppSize;
    private long touchedSize;
    private long touchedAppSize;
    private long totalTouchedInsts;
    private long totalAppTouchedInsts;
    private long hotInsts;
    private long hotAppInsts;
    private long hotMethods;
    private long hotAppMethods;
    private long hotClasses;
    private long hotAppClasses;
    private int touchedMethods;
    private int touchedAppMethods;
    private int touchedClasses;
    private int touchedAppClasses;
    //private Object2LongOpenHashMap instructionToCount;
    //private Object2ObjectOpenHashMap instructionKeys;
    //private InstructionEntity instEntity;

    private HashSet bytecodeSet;
    
    public ProgramSizeMetric(String name) {
        this(name, DEFAULT_THRESHOLD);
    }
    
    public ProgramSizeMetric(String name, double threshold) {
        super(name, "Program Size Metrics", "Measure the dynamic size of a program, in bytecodes");
        checkThreshold(threshold);
        this.threshold = threshold;
    }

    private void checkThreshold(double threshold) {
        if (threshold >= 0 && threshold <= 1.0) {
            return;
        }

        throw new RuntimeException("Invalid Threshold: " + threshold + ". (Must be in [0.0, 1.0])");
    }
    
    public void setOption(String name, String value) {
        if (name.equals("threshold")) {
            threshold = Double.parseDouble(value);
            checkThreshold(threshold);
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) { 
        if (name.equals("threshold")) {
            return "" + threshold;
        }

        return super.getOption(name);
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);

        manager.displayOptionHelp("threshold:<float>", "Specifies the proportion of the execution that 'hot' instructions represent. "
                                                        + "Must be in the range [0.0, 1.0]");
    }
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_LOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_NAME
                                | AdaptJSpecConstants.ADAPTJ_FIELD_METHODS),

            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OFFSET)

            /*
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJEvent.ADAPTJ_METHOD_ENTRY,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID)
            */
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER, Scene.INSTRUCTION_RESOLVER};
        return deps;
    }

    public void doInit() {
        totalSize = 0L;
        totalAppSize = 0L;
        touchedSize = 0L;
        touchedAppSize = 0L;
        totalTouchedInsts = 0L;
        totalAppTouchedInsts = 0L;
        //instEntity = new InstructionEntity();
        //instructionToCount = new Object2LongOpenHashMap();
        //instructionKeys = new Object2ObjectOpenHashMap();
        //instructionToCount.setDefRetValue(0L);

        //cachedMethodIDs = new int[CACHE_SIZE];
        //cachedMethodEntities = new MethodEntity[CACHE_SIZE];
        //cachedCode = null;
        
        bytecodeSet = new HashSet();
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                {
                    ClassLoadEvent e = (ClassLoadEvent) event;
                    ClassInfo info = IDResolver.v().getClassInfo(e.getClassID());
                    boolean app = (info == null || !info.isStandardLib());

                    totalLoadedClasses++;
                    if (app) {
                        totalLoadedAppClasses++;
                    }

                    JVMPIMethod m;
                    Bytecode code;
                    for (int i = 0; i < e.getNumMethods(); i++) {
                        m = e.getMethod(i);
                        int method_id = m.getMethodID();
                        code = BytecodeResolver.v().getBytecode(method_id);
                        
                        if (code != null) {
                            totalSize += code.size();
                            if (app) {
                                totalAppSize += code.size();
                            }
                        }
                    }
                }
                break;
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2: {
                    MethodEvent e = (MethodEvent) event;

                    /*
                    int method_id = e.getMethodID();
                    int key = (method_id >> 4) & CACHE_MASK;
                    cachedMethodIDs[key] = method_id;
                    cachedMethodEntities[key] = IDResolver.v().getMethodEntity(method_id);
                    //cachedCode = BytecodeResolver.v().getBytecode(cachedMethodID);
                    */
                }
                break;
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START: {
                    InstructionStartEvent e = (InstructionStartEvent) event;
                    
                    int method_id = e.getMethodID();
                    /*
                    int mkey = (method_id >> 4) & CACHE_MASK;
                    MethodEntity m = null;
                    //Bytecode code = null;

                    if (method_id == cachedMethodIDs[mkey] && method_id != 0) {
                        // Cache hit
                        m = cachedMethodEntities[mkey];
                        //code = cachedCode;
                    } else {
                        // Cache Miss
                        m = IDResolver.v().getMethodEntity(method_id);
                        //code = BytecodeResolver.v().getBytecode(method_id);
                        cachedMethodIDs[mkey] = method_id;
                        cachedMethodEntities[mkey] = m;
                        //cachedCode = code;
                    }
                    */

                    InstructionHandle ih = BytecodeResolver.v().getInstructionHandle(method_id, e.getOffset());
                    if (ih != null) {
                        Bytecode b = BytecodeResolver.v().getBytecode(method_id);
                        bytecodeSet.add(b);
                        ih.touch();
                        /*
                        totalTouchedInsts++;
                        MethodEntity me = (b != null ? b.getMethodEntity() : null);
                        boolean isStdLib = (me != null ? me.isStandardLib() : false);
                        if (!isStdLib) {
                            totalAppTouchedInsts++;
                        }
                        */
                    }
                }
                break;
            default:
                break;
        }
    }

    public void computeResults() {
        touchedSize = 0L;
        touchedAppSize = 0L;
        totalTouchedInsts = 0L;
        totalAppTouchedInsts = 0L;
        touchedMethods = 0;
        touchedAppMethods = 0;
        touchedClasses = 0;
        touchedAppClasses = 0;
        
        // Instruction freq maps
        Long2LongOpenHashMap touchedCountToInstCount = new Long2LongOpenHashMap();
        touchedCountToInstCount.setDefRetValue(0L);
        Long2LongOpenHashMap appTouchedCountToInstCount = new Long2LongOpenHashMap();
        appTouchedCountToInstCount.setDefRetValue(0L);

        // Method freq maps
        Long2LongOpenHashMap touchedCountToMethodCount = new Long2LongOpenHashMap();
        touchedCountToMethodCount.setDefRetValue(0L);
        Long2LongOpenHashMap appTouchedCountToMethodCount = new Long2LongOpenHashMap();
        appTouchedCountToMethodCount.setDefRetValue(0L);

        // Class freq maps
        Object2LongOpenHashMap classToCount = new Object2LongOpenHashMap();
        classToCount.setDefRetValue(0L);
        Object2LongOpenHashMap appClassToCount = new Object2LongOpenHashMap();
        appClassToCount.setDefRetValue(0L);

        Iterator it = bytecodeSet.iterator();
        while (it.hasNext()) {
            Bytecode code = (Bytecode) it.next();
            MethodEntity me = code.getMethodEntity();
            String className = (me != null ? me.getClassName() : null);
            boolean app = (me == null || !me.isStandardLib());
            
            int num_handles = code.getInstructionHandleCount();
            InstructionHandle[] iHandles = code.getInstructionHandles();
            long methodTouchedCount = 0L;
            for (int i = 0; i < num_handles; i++) {
                long touchedCount = iHandles[i].getTouchedCount();
                if (touchedCount > 0L) {
                    methodTouchedCount += touchedCount;
                    totalTouchedInsts += touchedCount;
                    touchedSize += 1L;
                    touchedCountToInstCount.put(touchedCount,
                            touchedCountToInstCount.get(touchedCount) + 1);
                    if (app) {
                        totalAppTouchedInsts += touchedCount;
                        touchedAppSize += 1L;
                        appTouchedCountToInstCount.put(touchedCount,
                                appTouchedCountToInstCount.get(touchedCount) + 1);
                    }
                }
            }

            if (methodTouchedCount > 0L) {
                touchedCountToMethodCount.put(methodTouchedCount,
                        touchedCountToMethodCount.get(methodTouchedCount) + 1);
                classToCount.put(className, classToCount.getLong(className) + methodTouchedCount);
                touchedMethods++;
                if (app) {
                    appTouchedCountToMethodCount.put(methodTouchedCount,
                            appTouchedCountToMethodCount.get(methodTouchedCount) + 1);
                    appClassToCount.put(className, appClassToCount.getLong(className) + methodTouchedCount);
                    touchedAppMethods++;
                }
            }
        }

        /*
        {
            // Compute the number of executed instructions that represents
            // (threshold x 100)% of the execution
            long cutoff = (long) Math.ceil(threshold * totalTouchedInsts);
            hotInsts = 0L;

            // Approach this number as close as possible
            List l = new ArrayList(touchedCountToInstCount.keySet());
            Collections.sort(l, new DecLongComparator());
            Iterator listIt = l.iterator();
            while (listIt.hasNext()) {
                long tmpTouched = ((Long) listIt.next()).longValue();
                long freq = touchedCountToInstCount.get(tmpTouched);

                long numAvail = cutoff / tmpTouched;
                if (cutoff % tmpTouched != 0) {
                    numAvail += 1L;
                }

                if (numAvail <= freq) {
                    // We just take the necessary amount and stop
                    hotInsts += numAvail;
                    break;
                } else {
                    // We have not enough of these instruction to reach the
                    // threshold. --> Take all of the instructions that we
                    // have and continue
                    hotInsts += freq;
                    cutoff -= (freq * tmpTouched);
                }
            }
        }
        */
        
        hotInsts = getHotSize(touchedCountToInstCount, totalTouchedInsts, threshold);
        hotAppInsts = getHotSize(appTouchedCountToInstCount, totalAppTouchedInsts, threshold);
        hotMethods = getHotSize(touchedCountToMethodCount, totalTouchedInsts, threshold);
        hotAppMethods = getHotSize(appTouchedCountToMethodCount, totalAppTouchedInsts, threshold);
        hotClasses = getHotSize(classToCount, totalTouchedInsts, threshold);
        hotAppClasses = getHotSize(appClassToCount, totalAppTouchedInsts, threshold);

        touchedClasses = classToCount.size();
        touchedAppClasses = appClassToCount.size();
        
        /*
        List l = new ArrayList(instructionToCount.keySet());
        Collections.sort(l, new InstructionCountComparator());
        touchedSize = l.size();
        long cutoff = (long) Math.ceil(threshold * totalTouchedInsts);
        long currentTotal = 0L;
        hotInsts = 0L;

        Iterator it = l.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            long val = instructionToCount.getLong(o);
            
            if (currentTotal < cutoff) {
                currentTotal += val;
                hotInsts++;
            } else {
                break;
            }
        }
        */
    }

    public long getHotSize(Long2LongOpenHashMap map, long totalSize, double threshold) {
        // Compute the number of executed instructions that represents
        // (threshold x 100)% of the execution
        long cutoff = (long) Math.ceil(threshold * totalSize);
        long result = 0L;

        // Approach this number as close as possible
        List l = new ArrayList(map.keySet());
        Collections.sort(l, new DecLongComparator());
        Iterator listIt = l.iterator();
        while (listIt.hasNext()) {
            long tmpTouched = ((Long) listIt.next()).longValue();
            long freq = map.get(tmpTouched);

            long numAvail = cutoff / tmpTouched;
            if (cutoff % tmpTouched != 0) {
                numAvail += 1L;
            }

            if (numAvail <= freq) {
                // We just take the necessary amount and stop
                result += numAvail;
                break;
            } else {
                // We have not enough of these instruction to reach the
                // threshold. --> Take all of the instructions that we
                // have and continue
                result += freq;
                cutoff -= (freq * tmpTouched);
            }
        }

        return result;
    }

    public long getHotSize(Object2LongOpenHashMap map, long totalSize, double threshold) {
        List l = new ArrayList(map.keySet());
        Collections.sort(l, new InstructionCountComparator(map));
        long cutoff = (long) Math.ceil(threshold * totalSize);
        long currentTotal = 0L;
        long result = 0L;

        Iterator it = l.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            long val = map.getLong(o);
            
            if (currentTotal < cutoff) {
                currentTotal += val;
                result++;
            } else {
                break;
            }
        }

        return result;
    }

    public long getLoadSize() {
        return this.totalSize;
    }

    public long getAppLoadSize() {
        return this.totalAppSize;
    }

    public long getRunSize() {
        return this.touchedSize;
    }

    public long getAppRunSize() {
        return this.touchedAppSize;
    }

    public void outputXML(XMLMetricPrinter xmlPrinter) {
        double dblVal;
        
        // size.loadedClasses.value
        xmlPrinter.addValue("size", "loadedClasses", totalLoadedClasses);
        // size.appLoadedClasses.value
        xmlPrinter.addValue("size", "appLoadedClasses", totalLoadedAppClasses);
        
        // size.load.value
        xmlPrinter.addValue("size", "load", totalSize);
        // size.appLoad.value
        xmlPrinter.addValue("size", "appLoad", totalAppSize);
                               
        // size.run.value
        xmlPrinter.addValue("size", "run", touchedSize);
        // size.appRun.value
        xmlPrinter.addValue("size", "appRun", touchedAppSize);
          
        // size.hot.value
        xmlPrinter.addValue("size", "hot", hotInsts);
        // size.appHot.value
        xmlPrinter.addValue("size", "appHot", hotAppInsts);
        
        // size.hotMethods.value
        xmlPrinter.addValue("size", "hotMethods", hotMethods);
        // size.appHotMethods.value
        xmlPrinter.addValue("size", "appHotMethods", hotAppMethods);

        // size.hotClasses.value
        xmlPrinter.addValue("size", "hotClasses", hotClasses);
        // size.appHotClasses.value
        xmlPrinter.addValue("size", "appHotClasses", hotAppClasses);

        // size.hot.percentile
        dblVal = ((double) hotInsts) / touchedSize;
        xmlPrinter.addPercentile("size", "hot", dblVal, threshold);
        // size.appHot.percentile
        dblVal = ((double) hotAppInsts) / touchedAppSize;
        xmlPrinter.addPercentile("size", "appHot", dblVal, threshold);
        
        // size.hotMethods.percentile
        dblVal = ((double) hotMethods) / touchedMethods;
        xmlPrinter.addPercentile("size", "hotMethods", dblVal, threshold);
        // size.appHotMethods.percentile
        dblVal = ((double) hotAppMethods) / touchedAppMethods;
        xmlPrinter.addPercentile("size", "appHotMethods", dblVal, threshold);
        
        // size.hotClasses.percentile
        dblVal = ((double) hotClasses) / touchedClasses;
        xmlPrinter.addPercentile("size", "hotClasses", dblVal, threshold);
        // size.appHotClasses.percentile
        dblVal = ((double) hotAppClasses) / touchedAppClasses;
        xmlPrinter.addPercentile("size", "appHotClasses", dblVal, threshold);
    }

    class InstructionCountComparator implements Comparator {
        private Object2LongOpenHashMap map;
        
        public InstructionCountComparator(Object2LongOpenHashMap map) {
            this.map = map;
        }
        
        public int compare(Object o1, Object o2) {
            long l1 = map.getLong(o1);
            long l2 = map.getLong(o2);

            if (l2 > l1) {
                return 1;
            }

            if (l2 == l1) {
                return 0;
            }

            return -1;
        }

        public boolean equals(Object obj) {
            return (obj instanceof InstructionCountComparator)
                && ((InstructionCountComparator) obj).map == map;
        }
    }

    class DecLongComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Long l1 = (Long) o1;
            Long l2 = (Long) o2;

            long l = l1.longValue() - l2.longValue();
            if (l > 0L) {
                // o1 > o2
                return -1;
            } else if (l == 0L) {
                return 0;
            }

            return 1;
        }

        public boolean equals(Object obj) {
            return (obj instanceof DecLongComparator);
        }
    }
}
