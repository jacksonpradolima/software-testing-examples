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

import adaptj_pool.util.*;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.event.*;

public class ExtendedEventPrinter extends RestrictedEventPrinter {

    public ExtendedEventPrinter(String name, String description) {
        super(name, description, "AdaptJ.extended.txt");
    }

    public ExtendedEventPrinter(String name, String description, String fileName) {
        super(name, description, fileName);
    }
    
    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        
        if (event instanceof MethodEntry2Event) {
            MethodEntry2Event e = (MethodEntry2Event) event;
            MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());
            if (me != null) {
                pw.println("MethodEntry2Event[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID()
                    + "(" + me.getClassName() + "." + me.getMethodName() + me.getMethodSignature() + ")"
                    + ", obj_id=" 
                    + e.getObjID() + "]");
            } else {
                pw.println("MethodEntry2Event[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID() + ", obj_id=" 
                    + e.getObjID() + "]");
            }
        } else if (event instanceof MethodEntryEvent) {
            MethodEntryEvent e = (MethodEntryEvent) event;

            MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());
            if (me != null) {
                pw.println("MethodEntryEvent[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID()
                        + "(" + me.getClassName() + "." + me.getMethodName() + me.getMethodSignature() + ")]");
            } else {
                pw.println("MethodEntryEvent[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID() + "]");
            }
        } else if (event instanceof MethodExitEvent) {
            MethodExitEvent e = (MethodExitEvent) event;
            MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());
            if (me != null) {
                pw.println("MethodExitEvent[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID()
                        + "(" + me.getClassName() + "." + me.getMethodName() + me.getMethodSignature() + ")]");
            } else {
                pw.println("MethodExitEvent[env_id=" + e.getEnvID() + ", method_id=" + e.getMethodID() + "]");
            }
        } else if (event instanceof ObjectAllocEvent) {
            ObjectAllocEvent e = (ObjectAllocEvent) event;
            ClassInfo ci;
            if (e.getIsArray() == ObjectAllocEvent.NORMAL_OBJECT || e.getIsArray() == ObjectAllocEvent.OBJECT_ARRAY) {
                ci = IDResolver.v().getClassInfo(e.getClassID());
            } else {
                ci = null;
            }
            if (ci != null) {
                pw.println("ObjectAllocEvent[env_id=" + e.getEnvID() + ", arena_id=" + e.getArenaID() + ", class_id=" 
                    + e.getClassID() + "(" + ci.getName() + ")"
                    + ", is_array=" + e.getIsArray() + ", size=" + e.getSize() +", obj_id="
                    + e.getObjID() + "]");
            } else {
                pw.println("ObjectAllocEvent[env_id=" + e.getEnvID() + ", arena_id=" + e.getArenaID() + ", class_id=" 
                    + e.getClassID() + ", is_array=" + e.getIsArray() + ", size=" + e.getSize() +", obj_id="
                    + e.getObjID() + "]");
            }
        } else {
            super.doApply(box);
        }
    }
}
