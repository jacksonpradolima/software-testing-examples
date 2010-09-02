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
import java.text.*;

import org.apache.bcel.*;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.classfile.*;

public class PointerMetric extends MetricAnalysis {
    private final static int CACHE_SIZE = 32; // NB: This has to be a power of 2!
    private final static int CACHE_MASK = 0x0000001F; // Keep 5 bits (2^5 = 32)

    private int[] cachedMethodIDs;
    private MethodEntity[] cachedMethodEntities;

    private long objectAccesses;
    private long nonObjectAccesses;
    private long appObjectAccesses;
    private long appNonObjectAccesses;

    private long totalInstCount;
    private long totalAppInstCount;

    public PointerMetric(String name) {
        super(name, "Pointer Metrics", "Measure the amount of pointer-related operations in a program");
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
            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OFFSET)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.INSTRUCTION_RESOLVER};
        return deps;
    }

    public void doInit() {
        objectAccesses = 0L;
        nonObjectAccesses = 0L;
        appObjectAccesses = 0L;
        appNonObjectAccesses = 0L;

        totalInstCount = 0L;
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
                case Constants.GETFIELD:
                case Constants.PUTFIELD:
                case Constants.GETSTATIC:
                case Constants.PUTSTATIC:
                    Bytecode b = BytecodeResolver.v().getBytecode(method_id);
                    if (b != null) {
                        MethodEntity m = IDResolver.v().getMethodEntity(method_id);
                        if (m != null) {
                            JavaClass clazz = ClassPathExplorer.v().getJavaClass(m.getClassName());
                            if (clazz != null) {
                                InstructionHandle ih = b.locateInstruction(e.getOffset());
                                if (ih != null) {
                                    CPInstruction inst = (CPInstruction) ih.getInstruction();
                                    Type t = TypeRepository.v().getType(clazz.getConstantPool(), inst.getIndex());
                                    if (t instanceof ObjectType) {
                                        objectAccesses++;
                                        if (app) {
                                            appObjectAccesses++;
                                        }
                                    } else {
                                        nonObjectAccesses++;
                                        if (app) {
                                            appNonObjectAccesses++;
                                        }
                                    }
                                    /*
                                    Type t = ((FieldInstruction) ih.getInstruction()).getFieldType(clazz.getConstantPool());
                                    if (t instanceof org.apache.bcel.generic.ObjectType) {
                                        objectAccesses++;
                                    }
                                    */
                                }
                            }
                        }
                    }
                case Constants.AALOAD:
                case Constants.AASTORE:
                    break;
                default:
                    /* Other */
                    break;
            }
        }
    }

    public void outputXML(XMLMetricPrinter xmlPrinter) {
        double dblVal;
        
        // data.refFieldAccessDensity.value
        dblVal = (1000.0 * ((double) objectAccesses)) / totalInstCount;
        xmlPrinter.addValue("pointer", "refFieldAccessDensity", dblVal);

        // data.nonrefFieldAccessDensity.value
        dblVal = (1000.0 * ((double) nonObjectAccesses)) / totalInstCount;
        xmlPrinter.addValue("pointer", "nonrefFieldAccessDensity", dblVal);
        
        // data.appRefFieldAccessDensity.value
        dblVal = (1000.0 * ((double) appObjectAccesses)) / totalAppInstCount;
        xmlPrinter.addValue("pointer", "appRefFieldAccessDensity", dblVal);

        // data.appNonrefFieldAccessDensity.value
        dblVal = (1000.0 * ((double) appNonObjectAccesses)) / totalAppInstCount;
        xmlPrinter.addValue("pointer", "appNonrefFieldAccessDensity", dblVal);
        
        // data.fieldAccessDensity.value
        dblVal = (1000.0 * ((double) objectAccesses + nonObjectAccesses)) / totalInstCount;
        xmlPrinter.addValue("pointer", "fieldAccessDensity", dblVal);

        // data.appFieldAccessDensity.value
        dblVal = (1000.0 * ((double) appObjectAccesses + appNonObjectAccesses)) / totalAppInstCount;
        xmlPrinter.addValue("pointer", "appFieldAccessDensity", dblVal);
    }
}
