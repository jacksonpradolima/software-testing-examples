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
import adaptj_pool.toolkits.types.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.util.text.HelpDisplayManager;

import java.text.*;
import java.util.*;

import it.unimi.dsi.fastUtil.*;

public class ObjectOrientationMetric extends MetricAnalysis {
    private static double DEFAULT_THRESHOLD = 0.90;
    
    private Object2IntOpenHashMap typeToCount;
    private Object2IntOpenHashMap methodToCount;

    private int totalTypes;
    private int totalMethods;
    private int totalTypeCount;
    private int totalMethodCount;
    private int totalCommonTypes;
    private int totalCommonMethods;
    private double commonTypeMethodRatio;

    private double threshold;
    
    public ObjectOrientationMetric(String name) {
        this(name, DEFAULT_THRESHOLD);
    }
    
    public ObjectOrientationMetric(String name, double threshold) {
        super(name, "Object-Orientation Metrics", "Measure the level of \"object-orientedness\" of a program");
        checkThreshold(threshold);
        this.threshold = threshold;
    }
    
    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
            AdaptJEvent.ADAPTJ_METHOD_ENTRY,
            AdaptJEvent.ADAPTJ_METHOD_ENTRY2
        };

        return events;
    }
    */

    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
                                AdaptJSpecConstants.ADAPTJ_FIELD_IS_ARRAY
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID),

            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJEvent.ADAPTJ_METHOD_ENTRY,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID)

            /*
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID)
            */
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER};
        return deps;
    }

    private void checkThreshold(double threshold) {
        if (threshold >= 0 && threshold <= 1.0) {
            return;
        }

        throw new RuntimeException("Invalid Threshold: " + threshold + ". (Must be in [0.0, 1.0])");
    }
    
    public void doInit() {
        typeToCount = new Object2IntOpenHashMap();
        methodToCount = new Object2IntOpenHashMap();

        typeToCount.setDefRetValue(0);
        methodToCount.setDefRetValue(0);

        totalTypeCount = 0;
        totalMethodCount = 0;
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                {
                    ObjectAllocEvent e = (ObjectAllocEvent) event;
                    
                    Type t = TypeRepository.v().getType(e.getIsArray(), e.getClassID());
                    if (t != null) {
                        int count = typeToCount.getInt(t);
                        totalTypeCount += 1;
                        typeToCount.put(t, count + 1);
                    }
                }
                break;
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
                {
                    int methodID = ((MethodEvent) event).getMethodID();
                    MethodEntity m = IDResolver.v().getMethodEntity(methodID);
                    if (m != null) {
                        int count = methodToCount.getInt(m);
                        totalMethodCount += 1;
                        methodToCount.put(m, count + 1);
                    }
                }
                break;
            default:
                break;
        } 
    }

    public void computeResults() {
        totalCommonTypes = 0;
        totalCommonMethods = 0;

        /* Types */
        {
            List l = new ArrayList(typeToCount.keySet());
            Collections.sort(l, new TypeCountComparator());
            totalTypes = l.size();
            int cutoff = (int)(threshold * totalTypeCount);
            int currentTotal = 0;

            Iterator it = l.iterator();
            while (it.hasNext()) {
                Object t = it.next();
                int val = typeToCount.getInt(t);
                
                if (currentTotal < cutoff) {
                    currentTotal += val;
                    totalCommonTypes++;
                } else {
                    break;
                }
            }
        }

        /* Methods */
        {
            List l = new ArrayList(methodToCount.keySet());
            Collections.sort(l, new MethodCountComparator());
            totalMethods = l.size();
            int cutoff = (int)(threshold * totalMethodCount);
            int currentTotal = 0;
            
            Iterator it = l.iterator();
            while (it.hasNext()) {
                Object t = it.next();
                int val = methodToCount.getInt(t);
                
                if (currentTotal < cutoff) {
                    currentTotal += val;
                    totalCommonMethods++;
                } else {
                    break;
                }
            }
        }

        super.doDone();
    }

    /*
    public void outputXMLResults(XMLPrintStream out) {
        DecimalFormat format = new DecimalFormat("0.000");
        DecimalFormat percentFormat = new DecimalFormat("00.0");
        double dblVal;

        String argNames[] = new String[2];
        String argValues[] = new String[2];

        argNames[0] = "label";
        argNames[1] = "category";

        // Open "results" tag
        out.openTagLn("results");
        
        // Absolute -- Total # of Types
        argValues[0] = "Total number of object types";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(totalTypes));

        // Absolute -- Total # of Methods
        argValues[0] = "Total number of methods invoked";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(totalMethods));
        
        // Dynamic -- Distribution of object sizes
        dblVal = ((double) totalTypes) / totalMethods;
        argValues[0] = "Type/Method ratio";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));


        // Dynamic -- Distribution of object sizes
        dblVal = ((double) totalCommonTypes) / totalCommonMethods;
        argValues[0] = "Common # of types / common number of methods ratio (threshold = "
            + percentFormat.format(threshold * 100.0) + "%)";
        argValues[1] = "Dynamic";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));
        // Close "results" tag
        out.closeTagLn("results");
    }
    */
    
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
    
    class TypeCountComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            int i1 = typeToCount.getInt(o1);
            int i2 = typeToCount.getInt(o2);

            return i2 - i1;
        }

        public boolean equals(Object obj) {
            return (obj instanceof TypeCountComparator);
        }
    }

    class MethodCountComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            int i1 = methodToCount.getInt(o1);
            int i2 = methodToCount.getInt(o2);

            return i2 - i1;
        }

        public boolean equals(Object obj) {
            return (obj instanceof MethodCountComparator);
        }
    }
}
