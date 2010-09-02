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

import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.util.xml.*;
import adaptj_pool.toolkits.EventDependency;

public class BaseMetric extends MetricAnalysis {
    private long totalBytes;
    private long totalAllocs;
    private long totalInsts;
    private long totalMethods;
    private long totalClasses;

    public BaseMetric(String name) {
        super(name, "Base Metrics", "Report various basic dynamic measures");
    }
    
    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_CLASS_LOAD,
            AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
            AdaptJEvent.ADAPTJ_INSTRUCTION_START,
            AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
        };

        return events;
    }
    */

    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_LOAD),
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_ALLOC),
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2),
            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START)
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        return null;
    }
    
    public void doInit() {
        totalBytes = 0L;
        totalAllocs = 0L;
        totalInsts = 0L;
        totalMethods = 0L;
        totalClasses = 0L;
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                {
                    ObjectAllocEvent e = (ObjectAllocEvent) event;
                    // Absolute
                    totalBytes += e.getSize();
                    totalAllocs += 1;
                }
                break;
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                totalClasses += 1;
                break;
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                totalInsts += 1;
                break;
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                totalMethods += 1;
                break;
            default:
                // Should not happen
                break;
        }
    }
    
    public void outputXML(XMLMetricPrinter xmlPrinter) {
        // base.allocatedBytes.value
        xmlPrinter.addValue("base", "allocatedBytes", totalBytes);
        
        // base.allocations.value
        xmlPrinter.addValue("base", "allocations", totalAllocs);
        
        // base.executedInstructions.value
        xmlPrinter.addValue("base", "executedInstructions", totalInsts);

        // base.methods.value
        xmlPrinter.addValue("base", "methods", totalMethods);

        // base.loadedClasses.value
        xmlPrinter.addValue("base", "loadedClasses", totalClasses);
    }

    /*
    public void outputXMLResults(XMLPrintStream out) {
        String argNames[] = new String[1];
        String argValues[] = new String[1];
        
        argNames[0] = "name";

        // base.allocatedBytes.value
        argValues[0] = "base.allocatedBytes.value";
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               String.valueOf(totalBytes));
                               
        // base.allocations.value
        argValues[0] = "base.allocations.value";
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               String.valueOf(totalAllocs));
                               
        // base.executedInstructions.value
        argValues[0] = "base.executedInstructions.value";
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               String.valueOf(totalInsts));

        // base.methods.value
        argValues[0] = "base.methods.value";
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               String.valueOf(totalMethods));

        // base.loadedClasses.value
        argValues[0] = "base.loadedClasses.value";
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               String.valueOf(totalClasses));

    }
    */
}
