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

package adaptj_pool.toolkits.transformers;

import adaptj_pool.Scene;
import adaptj_pool.toolkits.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.JVMPI.*;
import adaptj_pool.util.text.OptionStringParser;
import adaptj_pool.util.text.HelpDisplayManager;

import java.io.*;
import java.util.*;
import it.unimi.dsi.fastUtil.*;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;

public class InstructionResolver extends EventOperation {
    private static InstructionResolver instance = new InstructionResolver();
    //private Int2ObjectHashMap methodIDtoInstructions;
    private String summaryFile = null;
    private boolean printSummary = false;
    private int[] counters = new int[Constants.MAX_BYTE + 2];
    
    private InstructionResolver() {
        super("InstructionResolver", "Adds the bytecode number to the event corresponding to an Instruction Start event");
    }

    public static InstructionResolver v() {
        return instance;
    }

    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_CLASS_LOAD,
            AdaptJEvent.ADAPTJ_CLASS_UNLOAD,
            AdaptJEvent.ADAPTJ_INSTRUCTION_START
        };

        return events;
    }
    */
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_LOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_NAME
                                | AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_METHODS),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_UNLOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID),

            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OFFSET)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER};
        return deps;
    }

    public void doInit() {
        //methodIDtoInstructions = new Int2ObjectHashMap();
    }

    public void doDone() {
        //methodIDtoInstructions = null;
        if (printSummary) {
            if (summaryFile != null) {
                try {
                    PrintStream stream = new PrintStream(new FileOutputStream(summaryFile));
                    writeSummary(stream);
                } catch (IOException e) {
                    Scene.v().reportWriteError(summaryFile);
                }
            } else {
                writeSummary(System.out);
            }
        }
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                {
                    ClassLoadEvent e = (ClassLoadEvent) event;
                    BytecodeResolver.v().loadClass(e);
                }
                break;
            case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                {
                    ClassUnloadEvent e = (ClassUnloadEvent) event;
                    BytecodeResolver.v().unloadClass(e);
                }
                break;
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                {
                    InstructionStartEvent e = (InstructionStartEvent) event;

                    InstructionHandle ih = BytecodeResolver.v().getInstructionHandle(e.getMethodID(), e.getOffset());
                    e.setInstructionHandle(ih);
                    if (ih != null) {
                        Instruction inst = ih.getInstruction();
                        ////e.code = ((int)instr.getCode()) & 0x000000FF;
                        int code = inst.getOpcode();
                        e.setCode(code);
                        counters[code] += 1;
                        return ;
                    }
                    
                    counters[Constants.MAX_BYTE + 1] += 1;
                    e.setCode(-1);
                }
                break;
            default:
                break;
        }
    }
    
    public void setOption(String name, String value) {
        if (name.equals("summary")) {
            printSummary = OptionStringParser.parseBoolean(value);
        } else if (name.equals("summaryFile")) {
            summaryFile = value;
            if (value == null) {
                throw new InvalidOptionFileNameException(this, name, value);
            }
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) {
        if (name.equals("summary")) {
            return "" + printSummary;
        } else if (name.equals("summaryFile")) {
            return summaryFile;
        } else {
            return super.getOption(name);
        }
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);
    
        manager.displayOptionHelp("summary[:boolean]", 
                                  "Specifies whether a summary of the executed instructions "
                                        + "(by category) is to be outputput");
        manager.displayOptionHelp("summaryFile:<file>", "Specifies the name of the file to output "
                                        + "summary information to");
    }
    
    public void writeSummary(PrintStream stream) {
        String codeName;
        stream.println("ByteCode distribution summary:");
        stream.println("------------------------------");
        for (int i = 0; i <= Constants.MAX_BYTE; i++) {
            codeName = Constants.OPCODE_NAMES[i];
            if (codeName != Constants.ILLEGAL_OPCODE) {
                stream.println(codeName + ": " + counters[i]);
            }
        }

        stream.println("Unknown: " + counters[Constants.MAX_BYTE + 1]);
        stream.println();
    }
}
