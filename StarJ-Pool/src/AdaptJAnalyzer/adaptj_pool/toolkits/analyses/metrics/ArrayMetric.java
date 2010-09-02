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
import adaptj_pool.toolkits.EventDependency;
import java.text.*;

import org.apache.bcel.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.*;

public class ArrayMetric extends MetricAnalysis {
    private final static int CACHE_SIZE = 32; // NB: This has to be a power of 2!
    private final static int CACHE_MASK = 0x0000001F; // Keep 5 bits (2^5 = 32)

    private int[] cachedMethodIDs;
    private MethodEntity[] cachedMethodEntities;
    
    private long charArrayAccesses;
    private long numArrayAccesses;
    private long refArrayAccesses;
    private long totalArrayAccesses;
    private long totalInstCount;
    
    private long appCharArrayAccesses;
    private long appNumArrayAccesses;
    private long appRefArrayAccesses;
    private long totalAppArrayAccesses;
    private long totalAppInstCount;
    
    public ArrayMetric(String name) {
        super(name, "Array Metrics", "Measure array accesses in a program");
    }

    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_INSTRUCTION_START
        };

        return events;
    }
    */
 
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START)
        };

        return deps;
    }   
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.INSTRUCTION_RESOLVER};
        return deps;
    }

    public void doInit() {
        charArrayAccesses = 0L;
        numArrayAccesses = 0L;
        refArrayAccesses = 0L;
        totalArrayAccesses = 0L;
        totalInstCount = 0L;

        appCharArrayAccesses = 0L;
        appNumArrayAccesses = 0L;
        appRefArrayAccesses = 0L;
        totalAppArrayAccesses = 0L;
        totalAppInstCount = 0L;

        cachedMethodIDs = new int[CACHE_SIZE];
        cachedMethodEntities = new MethodEntity[CACHE_SIZE];
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        if (event.getTypeID() == AdaptJEvent.ADAPTJ_INSTRUCTION_START) {
            InstructionStartEvent e = (InstructionStartEvent) event;

            int method_id = e.getMethodID();
            int key = (method_id >> CACHE_MASK);
            MethodEntity me;
            if (cachedMethodIDs[key] == method_id) {
                me = cachedMethodEntities[key];
            } else {
                me = IDResolver.v().getMethodEntity(method_id);
                cachedMethodIDs[key] = method_id;
                cachedMethodEntities[key] = me;
            }

            boolean app = (me != null ? !me.isStandardLib() : true);
            
            totalInstCount++;
            if (app) {
                totalAppInstCount++;
            }
            switch (e.getCode()) {
                case Constants.AALOAD:
                case Constants.AASTORE:
                    refArrayAccesses++;
                    if (app) {
                        appRefArrayAccesses++;
                    }
                    break;
                case Constants.CALOAD:
                case Constants.CASTORE:
                    charArrayAccesses++;
                    if (app) {
                        appCharArrayAccesses++;
                    }
                    break;
                case Constants.BALOAD:
                case Constants.BASTORE:
                case Constants.DALOAD:
                case Constants.DASTORE:
                case Constants.FALOAD:
                case Constants.FASTORE:
                case Constants.IALOAD:
                case Constants.IASTORE:
                case Constants.LALOAD:
                case Constants.LASTORE:
                case Constants.SALOAD:
                case Constants.SASTORE:
                    numArrayAccesses++;
                    if (app) {
                        appNumArrayAccesses++;
                    }
                    break;
                case Constants.ARRAYLENGTH:
                case Constants.NEWARRAY:
                case Constants.MULTIANEWARRAY:
                case Constants.ANEWARRAY:
                    break;
                default:
                    /* Not an array instruction */
                    return;
            }
            
            totalArrayAccesses++;
            if (app) {
                totalAppArrayAccesses++;
            }
        }
    }

    public void outputXML(XMLMetricPrinter xmlPrinter) {
        double dblVal;
        
        // data.charArrayDensity.value
        dblVal = (1000.0 * ((double) charArrayAccesses)) / totalInstCount;
        xmlPrinter.addValue("data", "charArrayDensity", dblVal);
        
        // data.numArrayDensity.value
        dblVal = (1000.0 * ((double) numArrayAccesses)) / totalInstCount;
        xmlPrinter.addValue("data", "numArrayDensity", dblVal);

        // data.refArrayDensity.value
        dblVal = (1000.0 * ((double) refArrayAccesses)) / totalInstCount;
        xmlPrinter.addValue("data", "refArrayDensity", dblVal);

        // data.arrayDensity.value
        dblVal = (1000.0 * ((double) totalArrayAccesses)) / totalInstCount;
        xmlPrinter.addValue("data", "arrayDensity", dblVal);
        
        // data.charArrayDensity.value
        dblVal = (1000.0 * ((double) appCharArrayAccesses)) / totalAppInstCount;
        xmlPrinter.addValue("data", "appCharArrayDensity", dblVal);
        
        // data.numArrayDensity.value
        dblVal = (1000.0 * ((double) appNumArrayAccesses)) / totalAppInstCount;
        xmlPrinter.addValue("data", "appNumArrayDensity", dblVal);

        // data.refArrayDensity.value
        dblVal = (1000.0 * ((double) appRefArrayAccesses)) / totalAppInstCount;
        xmlPrinter.addValue("data", "appRefArrayDensity", dblVal);

        // data.appArrayDensity.value
        dblVal = (1000.0 * ((double) totalAppArrayAccesses)) / totalAppInstCount;
        xmlPrinter.addValue("data", "appArrayDensity", dblVal);
    }
}
