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
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.util.text.HelpDisplayManager;
import adaptj_pool.toolkits.analyses.*;
import java.text.*;

public class MemoryMetric extends MetricAnalysis {
    private final static int CACHE_SIZE = 32; // NB: This has to be a power of 2!
    private final static int CACHE_MASK = 0x0000001F; // Keep 5 bits (2^5 = 32)
    
    private static final int DEFAULT_HEADER_SIZE = 8; // default object header size, in bytes
    private static final int DEFAULT_SLICE_SIZE = 10000;
    private static final int OBJECT_DIST_BIN_SIZE = 9;
    private static final int WORD_SIZE = 8; // word size, in bytes

    private long totalBytes;
    private long totalObjects;
    private long totalAppBytes;
    private long totalAppObjects;
    //private long totalSliceBytes;
    //private long totalSliceObjects;
    private long objectSizeBin[];
    private long appObjectSizeBin[];
    private long instCount;
    private long appInstCount;
    
    private int headerSize;
    private int sliceSize;

    private SliceInfo sliceInfos;
    public double avgSliceBytes;
    public double avgSliceObjects;
    public double varSliceBytes;
    public double varSliceObjects;

    private int[] cachedMethodIDs;
    private MethodEntity[] cachedMethodEntities;

    public MemoryMetric(String name) {
        this(name, DEFAULT_SLICE_SIZE);
    }

    public MemoryMetric(String name, int sliceSize) {
        this(name, sliceSize, DEFAULT_HEADER_SIZE);
    }
    
    public MemoryMetric(String name, int sliceSize, int headerSize) {
        super(name, "Memory Metrics", "Measure heap memory usage throughout the program execution");
        this.sliceSize = sliceSize;
        this.headerSize = headerSize;
    }

    public void setSliceSize(int size) {
        sliceSize = size;
    }

    public int getSliceSize() {
        return sliceSize;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(int size) {
        headerSize = size;
    }
    
    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
            AdaptJEvent.ADAPTJ_INSTRUCTION_START
        };

        return events;
    }
    */ 

    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
                                AdaptJSpecConstants.ADAPTJ_FIELD_SIZE),

            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START)
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        String[] deps = { Scene.ID_RESOLVER };
        return deps;
    }

    private void addToBin(int size) {
        if (size <= headerSize) {
            objectSizeBin[0] += 1;
        } else if (size <= headerSize + 1 * WORD_SIZE) {
            objectSizeBin[1] += 1;
        } else if (size <= headerSize + 2 * WORD_SIZE) {
            objectSizeBin[2] += 1;
        } else if (size <= headerSize + 3 * WORD_SIZE) {
            objectSizeBin[3] += 1;
        } else if (size <= headerSize + 4 * WORD_SIZE) {
            objectSizeBin[4] += 1;
        } else if (size <= headerSize + 8 * WORD_SIZE) {
            objectSizeBin[5] += 1;
        } else if (size <= headerSize + 16 * WORD_SIZE) {
            objectSizeBin[6] += 1;
        } else if (size <= headerSize + 48 * WORD_SIZE) {
            objectSizeBin[7] += 1;
        } else {
            objectSizeBin[8] += 1;
        }
    }

    private void addToAppBin(int size) {
        if (size <= headerSize) {
            appObjectSizeBin[0] += 1;
        } else if (size <= headerSize + 1 * WORD_SIZE) {
            appObjectSizeBin[1] += 1;
        } else if (size <= headerSize + 2 * WORD_SIZE) {
            appObjectSizeBin[2] += 1;
        } else if (size <= headerSize + 3 * WORD_SIZE) {
            appObjectSizeBin[3] += 1;
        } else if (size <= headerSize + 4 * WORD_SIZE) {
            appObjectSizeBin[4] += 1;
        } else if (size <= headerSize + 8 * WORD_SIZE) {
            appObjectSizeBin[5] += 1;
        } else if (size <= headerSize + 16 * WORD_SIZE) {
            appObjectSizeBin[6] += 1;
        } else if (size <= headerSize + 48 * WORD_SIZE) {
            appObjectSizeBin[7] += 1;
        } else {
            appObjectSizeBin[8] += 1;
        }
    }

    private void getBinRange(int index, int[] tuple) {
        if (tuple.length < 2) {
            throw new RuntimeException("Incorrect tuple size");
        }
        switch (index) {
            case 0:
                tuple[0] = tuple[1] = headerSize;
                break;
            case 1:
                tuple[0] = tuple[1] = headerSize + WORD_SIZE;
                break;
            case 2:
                tuple[0] = tuple[1] = headerSize + 2 * WORD_SIZE;
                break;
            case 3:
                tuple[0] = tuple[1] = headerSize + 3 * WORD_SIZE;
                break;
            case 4:
                tuple[0] = tuple[1] = headerSize + 4 * WORD_SIZE;
                break;
            case 5:
                tuple[0] = headerSize + 5 * WORD_SIZE;
                tuple[1] = headerSize + 8 * WORD_SIZE;
                break;
            case 6:
                tuple[0] = headerSize + 9 * WORD_SIZE;
                tuple[1] = headerSize + 16 * WORD_SIZE;
                break;
            case 7:
                tuple[0] = headerSize + 17 * WORD_SIZE;
                tuple[1] = headerSize + 48 * WORD_SIZE;
                break;
            case 8:
                tuple[0] = headerSize + 49 * WORD_SIZE;
                tuple[1] = Integer.MAX_VALUE;
                break;
            default:
                throw new RuntimeException("Bin index out of range");
        }
    }

    public void doInit() {
        totalBytes = 0L;
        totalObjects = 0L;
        totalAppBytes = 0L;
        totalAppObjects = 0L;
        //totalSliceBytes = 0L;
        //totalSliceObjects = 0L;
        instCount = 0L;
        appInstCount = 0L;
        objectSizeBin = new long[OBJECT_DIST_BIN_SIZE];
        appObjectSizeBin = new long[OBJECT_DIST_BIN_SIZE];

        for (int i = 0; i < OBJECT_DIST_BIN_SIZE; i++) {
            objectSizeBin[i] = 0L;
            appObjectSizeBin[i] = 0L;
        }

        sliceInfos = null;

        cachedMethodIDs = new int[CACHE_SIZE];
        cachedMethodEntities = new MethodEntity[CACHE_SIZE];
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                {
                    ObjectAllocEvent e = (ObjectAllocEvent) event;
            
                    int size = e.getSize();
                    boolean app = false;
                    int is_array = e.getIsArray();
                    if (is_array == ObjectAllocEvent.NORMAL_OBJECT || is_array == ObjectAllocEvent.OBJECT_ARRAY) {
                        ClassInfo cinfo = IDResolver.v().getClassInfo(e.getClassID());
                        if (cinfo == null || !cinfo.isStandardLib()) {
                            totalAppBytes += size;
                            totalAppObjects += 1;
                            addToAppBin(size);
                        }
                    }
            
                    // Absolute
                    totalBytes += size;
                    totalObjects += 1;

                    /*
                    // Relative
                    totalSliceBytes += e.size;
                    totalSliceObjects += 1;
                    */

                    // Dynamic
                    addToBin(size);

                    /*
                    if (totalSliceBytes >= sliceSize) {
                        sliceInfos = new SliceInfo(totalSliceBytes, totalSliceObjects, sliceInfos);

                        //instCount = 0;
                        totalSliceBytes = 0;
                        totalSliceObjects = 0;
                    }
                    */
                }
                break;
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                instCount += 1;

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

                if (me == null || !me.isStandardLib()) {
                    appInstCount += 1;
                }
                /*
                if (instCount == sliceSize) {
                    sliceInfos = new SliceInfo(totalSliceBytes, totalSliceObjects, sliceInfos);

                    //instCount = 0;
                    totalSliceBytes = 0;
                    totalSliceObjects = 0;
                }
                */
                break;
            default:
                break;
        } 
    }

    public void computeResults() {
        /*
        int sliceCount = 0;
        SliceInfo s;
        int i;

        sliceInfos = new SliceInfo(totalSliceBytes, totalSliceObjects, sliceInfos);

        for (s = sliceInfos; s != null; s = s.getNext()) {
            sliceCount++;
        }

        double sliceBytes[] = new double[sliceCount];
        double sliceObjects[] = new double[sliceCount];
        
        for (i = 0, s = sliceInfos; s != null; s = s.getNext(), i++) {
            sliceBytes[i] = s.getBytes();
            sliceObjects[i] = s.getObjects();
        }

        DoubleDataSetStatistics byteStats = new DoubleDataSetStatistics(sliceBytes);
        DoubleDataSetStatistics objectStats = new DoubleDataSetStatistics(sliceObjects);

        avgSliceBytes = byteStats.getAverage();
        avgSliceObjects = objectStats.getAverage();
        varSliceBytes = byteStats.getStandardDeviation();
        varSliceObjects = objectStats.getStandardDeviation();
        */
    }
    
    public void outputXML(XMLMetricPrinter xmlPrinter) {
        double dblVal;

        // memory.byteAllocationDensity.value
        dblVal = (1000.0 * ((double) totalBytes)) / instCount;
        xmlPrinter.addValue("memory", "byteAllocationDensity", dblVal);

        // memory.objectAllocationDensity.value
        dblVal = (1000.0 * ((double) totalObjects)) / instCount;
        xmlPrinter.addValue("memory", "objectAllocationDensity", dblVal);

        // memory.averageObjectSize.value
        dblVal = ((double) totalBytes) / totalObjects;
        xmlPrinter.addValue("memory", "averageObjectSize", dblVal);
        
        // memory.byteAppAllocationDensity.value
        if (appInstCount == 0L) {
            dblVal = Double.NaN;
        } else {
            dblVal = 1000.0 * ((double) totalAppBytes) / appInstCount;
        }
        xmlPrinter.addValue("memory", "byteAppAllocationDensity", dblVal);

        // memory.objectAppAllocationDensity.value
        if (appInstCount == 0L) {
            dblVal = Double.NaN;
        } else {
            dblVal = 1000.0 * ((double) totalAppObjects) / appInstCount;
        }
        xmlPrinter.addValue("memory", "objectAppAllocationDensity", dblVal);

        // memory.averageAppObjectSize.value
        dblVal = ((double) totalAppBytes) / totalAppObjects;
        xmlPrinter.addValue("memory", "averageAppObjectSize", dblVal);
        
        int[] binBounds = new int[2];
        // memory.averageObjectSize.bin
        xmlPrinter.addBin("memory", "averageObjectSize");
        for (int i = 0; i < OBJECT_DIST_BIN_SIZE; i++) {
            dblVal = ((double) objectSizeBin[i]) / totalObjects;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("memory", "averageObjectSize", binBounds[0], binBounds[1], dblVal);
        }

        // memory.averageAppObjectSize.bin
        xmlPrinter.addBin("memory", "appobjectSize");
        for (int i = 0; i < OBJECT_DIST_BIN_SIZE; i++) {
            dblVal = ((double) appObjectSizeBin[i]) / totalAppObjects;
            getBinRange(i, binBounds);
            xmlPrinter.addBinRange("memory", "appobjectSize", binBounds[0], binBounds[1], dblVal);
        }
    }

    // public void outputXMLResults(XMLPrintStream out) {
    /*
        DecimalFormat format = new DecimalFormat("0.000");
        String argNames[] = new String[1];
        String argValues[] = new String[1];
        String[] binArgNames = new String[2];
        String[] binArgValues = new String[2];
        double dblVal;

        argNames[0] = "name";
        binArgNames[0] = "from";
        binArgNames[1] = "to";

        // memory.byteAllocationDensity.value
        argValues[0] = "memory.byteAllocationDensity.value";
        dblVal = (1000.0 * ((double) totalBytes)) / instCount;
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));

        // memory.objectAllocationDensity.value
        argValues[0] = "memory.objectAllocationDensity.value";
        dblVal = (1000.0 * ((double) totalObjects)) / instCount;
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));

        // memory.averageObjectSize.value
        argValues[0] = "memory.averageObjectSize.value";
        dblVal = ((double) totalBytes) / totalObjects;
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));

        // memory.averageObjectSize.value
        argValues[0] = "memory.averageObjectSize.bin";
        out.openTagLn("metric", argNames, argValues);
        for (int i = 0; i < OBJECT_DIST_BIN_SIZE; i++) {
            getBinRange(i, binArgValues);
            dblVal = ((double) objectSizeBin[i]) / totalObjects;
            out.printTaggedValueLn("bin",
                                   binArgNames,
                                   binArgValues,
                                  (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));
        }
        out.closeTagLn("metric");
        */

        /*
        // Absolute -- Total Bytes
        argValues[0] = "Total Bytes Allocated";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(totalBytes));

        // Absolute -- Total Objects
        argValues[0] = "Total Objects Allocated";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(totalObjects));
        * /
        
        /* Relative -- Average # bytes / slice */
        /* ---
        argValues[0] = "Average bytes allocated per interval";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (avgSliceBytes != Double.NaN ? format.format(avgSliceBytes) : "N/A"));
        --- */
                               
        /* Relative -- Average # bytes / slice */
        /* ---
        argValues[0] = "Average number of objects allocated per interval";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (avgSliceObjects != Double.NaN ? format.format(avgSliceObjects) : "N/A"));
        --- */

        /* Relative -- Average # bytes / slice */
        /* ---
        argValues[0] = "Standard Deviation for bytes allocated per interval";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (varSliceBytes != Double.NaN ? format.format(varSliceBytes) : "N/A"));
        --- */
                               
        /* Relative -- Average # bytes / slice */
        /* ---
        argValues[0] = "Standard Deviation for the number of objects allocated per interval";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (varSliceObjects != Double.NaN ? format.format(varSliceObjects) : "N/A"));
        --- */

        /* Dynamic -- Distribution of object sizes */
        /*
        argValues[0] = "Distribution of object sizes";
        argValues[1] = "Dynamic";
        out.openTagLn("bin itemType=\"percent\"", argNames, argValues);
        for (int i = 0; i < OBJECT_DIST_BIN_SIZE; i++) {
            out.openTagLn("item");
            out.printTaggedValueLn("range", getBinRange(i));
            out.printTaggedValueLn("value", format.format(((double) objectSizeBin[i]) / totalObjects));
            out.closeTagLn("item");
        }
        out.closeTagLn("bin");
        */
    //}

    public void setOption(String name, String value) {
        if (name.equals("header")) {
            headerSize = Integer.parseInt(value);
        /*} else if (name.equals("slice")) {
            sliceSize = Integer.parseInt(value); */
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) { 
        if (name.equals("header")) {
            return "" + headerSize;
        }/* else if (name.equals("slice")) {
            return "" + sliceSize;
        }*/

        return super.getOption(name);
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);

        manager.displayOptionHelp("header:<int>", "Specifies the size of the object header to use in "
                                                  + "the analysis, in bytes. Defaults to " + DEFAULT_HEADER_SIZE);
    }
    
    class SliceInfo {
        private long bytes;
        private long objects;
        public SliceInfo next;

        public SliceInfo(long bytes, long objects) {
            this(bytes, objects, null);
        }
        
        public SliceInfo(long bytes, long objects, SliceInfo next) {
            this.bytes = bytes;
            this.objects = objects;
            this.next = next;
        }

        public void setNext(SliceInfo next) {
            this.next = next;
        }

        public SliceInfo getNext() {
            return next;
        }

        public long getBytes() {
            return bytes;
        }

        public long getObjects() {
            return objects;
        }
    }
}
