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

package adaptj_pool.toolkits.printers;

import adaptj_pool.event.*;
import adaptj_pool.JVMPI.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import java.io.*;
import adaptj_pool.toolkits.*;
import adaptj_pool.toolkits.analyses.IDResolver;
import adaptj_pool.util.text.HelpDisplayManager;

public class DefaultEventPrinter extends EventOperation {
    protected String fileName = null;
    protected PrintStream pw;
    
    public DefaultEventPrinter(String name, String description) {
        super(name, description);
        fileName = "AdaptJ.txt";
    }

    public DefaultEventPrinter(String name, String description, String fileName) {
        super(name, description);
        this.fileName = fileName;
    }
 
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
 
    /*
    public int[] registerEvents() {
        int events[] = new int[AdaptJEvent.ADAPTJ_EVENT_COUNT];
        for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) {
            events[i] = i;
        }

        return events;
    }
    */

    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = new EventDependency[AdaptJEvent.ADAPTJ_EVENT_COUNT];
        for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) {
            deps[i] = new EventDependency(i,
                                          false);
        }

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        return null;
    }

    public void doInit() {
        try {
            this.pw = new PrintStream(new FileOutputStream(fileName));
        } catch (IOException e) {
            this.pw = System.out;
        }
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        if (event instanceof ArenaDeleteEvent) {
            ArenaDeleteEvent e = (ArenaDeleteEvent) event;

            pw.println("ArenaDeleteEvent[env_id=" + e.getEnvID() + ", arena_id=" + e.getArenaID() + "]");
        } else if (event instanceof ArenaNewEvent) {
            ArenaNewEvent e = (ArenaNewEvent) event;

            pw.println("ArenaNewEvent[env_id=" + e.getEnvID() + ", arena_id=" + e.getArenaID() + ", arena_name="
                    + e.getArenaName() + "]");
        } else if (event instanceof ClassLoadEvent) {
            ClassLoadEvent e = (ClassLoadEvent) event;

            pw.println("ClassLoadEvent[env_id=" + e.getEnvID() + ", class_name=" + e.getClassName() + ", source_name="
                    + e.getSourceName() + ", num_interfaces=" + e.getNumInterfaces() + ", num_methods=" + e.getNumMethods()
                    + ", class_id=" + e.getClassID());
            for (int i = 0; i< e.getNumMethods(); i++) {
                printMethod(e.getMethod(i));
            }
        } else if (event instanceof ClassUnloadEvent) {
            ClassUnloadEvent e = (ClassUnloadEvent) event;

            pw.println("ClassUnloadEvent[env_id=" + e.getEnvID() + ", class_id=" + e.getClassID() + "]");
        } else if (event instanceof CompiledMethodLoadEvent) {
            CompiledMethodLoadEvent e = (CompiledMethodLoadEvent) event;

            pw.println("CompiledMethodLoadEvent[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID() + ", code_size="
                    + e.getCodeSize() + "code=" + e.getCode() + "lineno_table_size=" + e.getLinenoTableSize() + "]");
            // TODO 
        } else if (event instanceof CompiledMethodUnloadEvent) {
            CompiledMethodUnloadEvent e = (CompiledMethodUnloadEvent) event;

            pw.println("CompiledMethodUnloadEvent[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID() + "]");
        } else if (event instanceof GCFinishEvent) {
            GCFinishEvent e = (GCFinishEvent) event;

            pw.println("GCFinishEvent[env_id=" + e.getEnvID() + ", used_objects=" + e.getUsedObjects()
                    + ", used_object_space=" + e.getUsedObjectSpace() + ", total_objectspace=" 
                    + e.getTotalObjectSpace() + "]");
        } else if (event instanceof GCStartEvent) {
            GCStartEvent e = (GCStartEvent) event;

            pw.println("GCStartEvent[env_id=" + e.getEnvID() + "]");  
        } else if (event instanceof InstructionStartEvent) {
            InstructionStartEvent e = (InstructionStartEvent) event;

            pw.println("InstructionStartEvent[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID() + ", offset="
                    + e.getOffset() + ", is_true=" + e.getIsTrue() + ", key=" + e.getKey() + ", low=" + e.getLow()
                    + ", hi=" + e.getHi() + ", chosen_pair_index=" + e.getChosenPairIndex()
                    + ", pairs_total=" + e.getPairsTotal() + ", code=" + e.getCode() + "]");  
        } else if (event instanceof JVMInitDoneEvent) {
            JVMInitDoneEvent e = (JVMInitDoneEvent) event;

            pw.println("JVMInitDoneEvent[env_id=" + e.getEnvID() + "]");  
        } else if (event instanceof JVMShutDownEvent) {
            JVMShutDownEvent e = (JVMShutDownEvent) event;

            pw.println("JVMShutDownEvent[env_id=" + e.getEnvID() + "]");
        } else if (event instanceof MethodEntry2Event) {
            MethodEntry2Event e = (MethodEntry2Event) event;

            pw.println("MethodEntry2Event[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID() + ", obj_id=" 
                    + e.getObjID() + "]");
        } else if (event instanceof MethodEntryEvent) {
            MethodEntryEvent e = (MethodEntryEvent) event;

            pw.println("MethodEntryEvent[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID() + "]");
        } else if (event instanceof MethodExitEvent) {
            MethodExitEvent e = (MethodExitEvent) event;

            pw.println("MethodExitEvent[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID() + "]");
        } else if (event instanceof MonitorContendedEnteredEvent) {
            MonitorContendedEnteredEvent e = (MonitorContendedEnteredEvent) event;

            pw.println("MonitorContendedEnteredEvent[env_id=" + e.getEnvID() + ", object=" + e.getObject() + "]");
        } else if (event instanceof MonitorContendedEnterEvent) {
            MonitorContendedEnterEvent e = (MonitorContendedEnterEvent) event;

            pw.println("MonitorContendedEnterEvent[env_id=" + e.getEnvID() + ", object=" + e.getObject() + "]");
        } else if (event instanceof MonitorContendedExitEvent) {
            MonitorContendedExitEvent e = (MonitorContendedExitEvent) event;

            pw.println("MonitorContendedExitEvent[env_id=" + e.getEnvID() + ", object=" + e.getObject() + "]");
        } else if (event instanceof MonitorDumpEvent) {
            MonitorDumpEvent e = (MonitorDumpEvent) event;

            pw.println("MonitorDumpEvent[env_id=" + e.getEnvID() + ", data_len=" + e.getDataLen());
            // TODO
        } else if (event instanceof MonitorWaitedEvent) {
            MonitorWaitedEvent e = (MonitorWaitedEvent) event;

            pw.println("MonitorWaitedEvent[env_id=" + e.getEnvID() + ", object=" + e.getObject() + ", timeout=" 
                    + e.getTimeout() + "]");
        } else if (event instanceof MonitorWaitEvent) {
            MonitorWaitEvent e = (MonitorWaitEvent) event;

            pw.println("MonitorWaitEvent[env_id=" + e.getEnvID() + ", object=" + e.getObject() + ", timeout=" 
                    + e.getTimeout() + "]");
        } else if (event instanceof ObjectAllocEvent) {
            ObjectAllocEvent e = (ObjectAllocEvent) event;

            pw.println("ObjectAllocEvent[env_id=" + e.getEnvID() + ", arena_id=" + e.getArenaID() + ", class_id=" 
                    + e.getClassID() + ", is_array=" + e.getIsArray() + ", size=" + e.getSize() +", obj_id="
                    + e.getObjID() + "]");
        } else if (event instanceof ObjectDumpEvent) {
            ObjectDumpEvent e = (ObjectDumpEvent) event;

            pw.println("ObjectDumpEvent[env_id=" + e.getEnvID() + ", data_len=" + e.getDataLen());
            // TODO
        } else if (event instanceof ObjectFreeEvent) {
            ObjectFreeEvent e = (ObjectFreeEvent) event;

            pw.println("ObjectFreeEvent[env_id=" + e.getEnvID() + ", obj_id=" + e.getObjID() + "]");
        } else if (event instanceof ObjectMoveEvent) {
            ObjectMoveEvent e = (ObjectMoveEvent) event;

            pw.println("ObjectMoveEvent[env_id=" + e.getEnvID() + ", arena_id=" + e.getArenaID() + ", obj_id="
                    + e.getObjID() + ", new_arena_id=" + e.getNewArenaID() + ", new_obj_id" + e.getNewObjID() + "]");
        } else if (event instanceof RawMonitorContendedEnteredEvent) {
            RawMonitorContendedEnteredEvent e = (RawMonitorContendedEnteredEvent) event;

            pw.println("RawMonitorContendedEnteredEvent[env_id=" + e.getEnvID() + ", name=" + e.getName() 
                    + ", id=" + e.getID() + "]");
        } else if (event instanceof RawMonitorContendedEnterEvent) {
            RawMonitorContendedEnterEvent e = (RawMonitorContendedEnterEvent) event;

            pw.println("RawMonitorContendedEnterEvent[env_id=" + e.getEnvID() + ", name=" + e.getName() 
                    + ", id=" + e.getID() + "]");
        } else if (event instanceof RawMonitorContendedExitEvent) {
            RawMonitorContendedExitEvent e = (RawMonitorContendedExitEvent) event;

            pw.println("RawMonitorContendedExitEvent[env_id=" + e.getEnvID() + ", name=" + e.getName() 
                    + ", id=" + e.getID() + "]");
        } else if (event instanceof ThreadEndEvent) {
            ThreadEndEvent e = (ThreadEndEvent) event;

            pw.println("ThreadEndEvent[env_id=" + e.getEnvID() + "]");
        } else if (event instanceof ThreadStartEvent) {
            ThreadStartEvent e = (ThreadStartEvent) event;

            pw.println("ThreadStartEvent[env_id=" + e.getEnvID() + ", thread_name=" + e.getThreadName()
            + ", group_name=" + e.getGroupName() + ", parent_name=" + e.getParentName() + ", thread_id="
            + e.getThreadID() + ", thread_env_id=" + e.getThreadEnvID() + "]");
        } else if (event instanceof ThreadStatusChangeEvent) {
            ThreadStatusChangeEvent e = (ThreadStatusChangeEvent) event;

            pw.println("ThreadStatusChangeEvent[env_id=" + e.getEnvID() + ", newStatus = " + e.getNewStatus() + "]");
        } else {
            pw.println(event);
        }
    }
    
    public void printMethod(JVMPIMethod m) {
        this.pw.println("    Method[name=" + m.getMethodName() + ", signature=" + m.getMethodSignature()
                + ", start_lineno=" + m.getStartLineno() + ", end_lineno=" + m.getEndLineno()
                + ", method_id=" + m.getMethodID() +"]");
    }
    
    public void doDone() {
        if (this.pw != System.out) {
            this.pw.close();
        }
    }

    public void setOption(String name, String value) {
        if (name.equals("file")) {
            if (value != null) {
                fileName = value;
            } else {
                throw new InvalidOptionFileNameException(this, name, value);
            }
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) {
        if (name.equals("file")) {
            return fileName;
        }

        return super.getOption(name);
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);

        manager.displayOptionHelp("file:<file>", "Specifies the name of a file to write the output to");
    }
}
