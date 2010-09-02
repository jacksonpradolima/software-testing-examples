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
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.toolkits.types.*;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.util.xml.*;
import java.text.*;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.classfile.*;

public class  FloatingPointMetric extends MetricAnalysis {
    private final static int CACHE_SIZE = 32; // NB: This has to be a power of 2!
    private final static int CACHE_MASK = 0x0000001F; // Keep 5 bits (2^5 = 32)

    private long fInstCount;
    private long dInstCount;
    //private long iInstCount;
    private long totalInstCount;

    private int[] cachedMethodIDs;
    private Bytecode[] cachedCodes;
    private MethodEntity[] cachedMethodEntities;
    private ConstantPool[] cachedCPs;

    public FloatingPointMetric(String name) {
        super(name, "Floating Point Metrics", "Measure the use of floating point operations");
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
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OFFSET),

            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJEvent.ADAPTJ_METHOD_ENTRY,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID)
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        String[] deps = {Scene.INSTRUCTION_RESOLVER};
        return deps;
    }
    
    public void doInit() {
        fInstCount = 0L;
        dInstCount = 0L;
        //iInstCount = 0L;
        totalInstCount = 0L;

        cachedMethodIDs = new int[CACHE_SIZE];
        cachedMethodEntities = new MethodEntity[CACHE_SIZE];
        cachedCodes = new Bytecode[CACHE_SIZE];
        cachedCPs = new ConstantPool[CACHE_SIZE];
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2: {
                    MethodEvent e = (MethodEvent) event;

                    int method_id = e.getMethodID();
                    int key = (method_id >> 4) & CACHE_MASK;
                    cachedMethodIDs[key] = method_id;
                    MethodEntity cachedMethodEntity = IDResolver.v().getMethodEntity(method_id);
                    cachedMethodEntities[key] = cachedMethodEntity;
                    cachedCodes[key] = BytecodeResolver.v().getBytecode(method_id);
                    cachedCPs[key] = null;
                    if (cachedMethodEntity != null) {
                        JavaClass clazz = ClassPathExplorer.v().getJavaClass(cachedMethodEntity.getClassName());
                        if (clazz != null) {
                            cachedCPs[key] = clazz.getConstantPool();
                        }
                    }
                }
                break;
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START: {
                    InstructionStartEvent e = (InstructionStartEvent) event;
                    totalInstCount++;
                    switch (e.getCode()) {
                        case Constants.GETFIELD:
                        case Constants.PUTFIELD:
                        case Constants.GETSTATIC:
                        case Constants.PUTSTATIC:
                            int method_id = e.getMethodID();
                            Bytecode b;
                            MethodEntity m;
                            ConstantPool cp = null;

                            int key = (method_id >> 4) & CACHE_MASK;
                            if (method_id == cachedMethodIDs[key] && method_id != 0) {
                                /* Cache hit */
                                m = cachedMethodEntities[key];
                                b = cachedCodes[key];
                                cp = cachedCPs[key];
                            } else {
                                /* Cache Miss */
                                b = BytecodeResolver.v().getBytecode(method_id);
                                if (b != null) {
                                    m = IDResolver.v().getMethodEntity(method_id);
                                    if (m != null) {
                                        JavaClass clazz = ClassPathExplorer.v().getJavaClass(m.getClassName());
                                        if (clazz != null) {
                                            cp = clazz.getConstantPool();
                                        }
                                    }
                                } else {
                                    m = null;
                                }

                                cachedMethodIDs[key] = method_id;
                                cachedMethodEntities[key] = m;
                                cachedCodes[key] = b;
                                cachedCPs[key] = cp;
                            }

                            
                            if (b != null && m != null && cp != null) {
                                //JavaClass clazz = ClassPathExplorer.v().getJavaClass(m.getClassName());
                                //if (clazz != null) {
                                InstructionHandle ih = b.locateInstruction(e.getOffset());
                                if (ih != null) {
                                    CPInstruction inst = (CPInstruction) ih.getInstruction();
                                    //Type t = TypeRepository.v().getType(clazz.getConstantPool(), inst.getIndex());
                                    Type t = TypeRepository.v().getType(cp, inst.getIndex());
                                    int tID = t.getTypeID();
                                    switch (tID) {
                                        case Type.FLOAT_TYPE:
                                            fInstCount++;
                                            break;
                                        case Type.DOUBLE_TYPE:
                                            dInstCount++;
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                //}
                            }
                            break;
                        case Constants.D2F:
                        case Constants.D2I:
                        case Constants.D2L:
                        case Constants.DADD:
                        case Constants.DALOAD:
                        case Constants.DASTORE:
                        case Constants.DCMPG:
                        case Constants.DCMPL:
                        case Constants.DCONST_0:
                        case Constants.DCONST_1:
                        case Constants.DDIV:
                        case Constants.DLOAD:
                        case Constants.DLOAD_0:
                        case Constants.DLOAD_1:
                        case Constants.DLOAD_2:
                        case Constants.DLOAD_3:
                        case Constants.DMUL:
                        case Constants.DNEG:
                        case Constants.DREM:
                        case Constants.DRETURN:
                        case Constants.DSTORE:
                        case Constants.DSTORE_0:
                        case Constants.DSTORE_1:
                        case Constants.DSTORE_2:
                        case Constants.DSTORE_3:
                        case Constants.DSUB:
                            /* Double */
                            dInstCount++;
                            break;
                        case Constants.F2D:
                        case Constants.F2I:
                        case Constants.F2L:
                        case Constants.FADD:
                        case Constants.FALOAD:
                        case Constants.FASTORE:
                        case Constants.FCMPG:
                        case Constants.FCMPL:
                        case Constants.FCONST_0:
                        case Constants.FCONST_1:
                        case Constants.FCONST_2:
                        case Constants.FDIV:
                        case Constants.FLOAD:
                        case Constants.FLOAD_0:
                        case Constants.FLOAD_1:
                        case Constants.FLOAD_2:
                        case Constants.FLOAD_3:
                        case Constants.FMUL:
                        case Constants.FNEG:
                        case Constants.FREM:
                        case Constants.FRETURN:
                        case Constants.FSTORE:
                        case Constants.FSTORE_0:
                        case Constants.FSTORE_1:
                        case Constants.FSTORE_2:
                        case Constants.FSTORE_3:
                        case Constants.FSUB:
                            /* Float */
                            fInstCount++;
                            break;
                        /*
                        case Constants.I2B:
                        case Constants.I2C:
                        case Constants.I2D:
                        case Constants.I2F:
                        case Constants.I2L:
                        case Constants.I2S:
                        case Constants.IADD:
                        case Constants.IALOAD:
                        case Constants.IAND:
                        case Constants.IASTORE:
                        case Constants.ICONST_0:
                        case Constants.ICONST_1:
                        case Constants.ICONST_2:
                        case Constants.ICONST_3:
                        case Constants.ICONST_4:
                        case Constants.ICONST_5:
                        case Constants.IDIV:
                        case Constants.IINC:
                        case Constants.ILOAD:
                        case Constants.ILOAD_0:
                        case Constants.ILOAD_1:
                        case Constants.ILOAD_2:
                        case Constants.ILOAD_3:
                        case Constants.IMUL:
                        case Constants.INEG:
                        case Constants.IOR:
                        case Constants.IREM:
                        case Constants.IRETURN:
                        case Constants.ISHL:
                        case Constants.ISHR:
                        case Constants.ISTORE:
                        case Constants.ISTORE_0:
                        case Constants.ISTORE_1:
                        case Constants.ISTORE_2:
                        case Constants.ISTORE_3:
                        case Constants.ISUB:
                        case Constants.IUSHR:
                        case Constants.IXOR:
                        */
                            /* Int */
                            //iInstCount++;
                            //break;    
                        default:
                            /* Other -- Ignore */
                            
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }
    
    public void outputXML(XMLMetricPrinter xmlPrinter) {
        double dblVal;

        // data.floatDensity.value
        dblVal = (1000.0 * ((double) (fInstCount + dInstCount))) / totalInstCount;
        xmlPrinter.addValue("data", "floatDensity", dblVal);
    }

    /*
    public void outputXMLResults(XMLPrintStream out) {
        DecimalFormat format = new DecimalFormat("0.000");
        String argNames[] = new String[1];
        String argValues[] = new String[1];
        double dblVal;

        argNames[0] = "name";

        // data.floatDensity.value
        argValues[0] = "data.floatDensity.value";
        dblVal = (1000.0 * ((double) (fInstCount + dInstCount))) / totalInstCount;
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));

        /*
        // Absolute -- FP ops
        argValues[0] = "Number of floating-point operations";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(fInstCount + dInstCount));

        // Absolute -- Int ops
        argValues[0] = "Number of integer operations";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(iInstCount));
        
        // Relative -- Percentage of FP ops
        double ratio;
        ratio = ((double) (fInstCount + dInstCount)) / totalInstCount;
        argValues[0] = "Percentage of floating-point operations";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (ratio != Double.NaN ? format.format(ratio) : "N/A"));

        ratio = ((double) (fInstCount + dInstCount)) / (fInstCount + dInstCount + iInstCount);
        // Absolute -- Percentage of FP ops relative to Arithmetic Ops
        argValues[0] = "Percentage of floating-point operations relative to the number "
                      + "of integer and floating-point instructions";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (ratio != Double.NaN ? format.format(ratio) : "N/A"));
        * /
    }
    */
}
