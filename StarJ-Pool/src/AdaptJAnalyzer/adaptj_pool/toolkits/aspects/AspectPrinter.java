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

package adaptj_pool.toolkits.aspects;

import adaptj_pool.util.*;
import adaptj_pool.toolkits.printers.RestrictedEventPrinter;
import adaptj_pool.toolkits.analyses.IDResolver;
import adaptj_pool.event.*;
import org.apache.bcel.generic.*;

public class AspectPrinter extends RestrictedEventPrinter {
    private String indent = "";
    private AspectInstTagResolver itr;

    public AspectPrinter(String name, String description) {
        super(name, description, "AdaptJ.tags.txt");
    }

    public AspectPrinter(String name, String description, String fileName) {
        super(name, description, fileName);
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        
        if (event instanceof MethodEntry2Event || event instanceof MethodEntryEvent) {
            MethodEvent e = (MethodEvent) event;
            MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());
            pw.println(indent + "+: " + me);
            //pw.println(indent + AspectInstTagResolver.instance.envIDtoIntStack.get(event.getEnvID()));
            indent = indent + "  ";
        } else if (event instanceof MethodExitEvent) {
            indent = indent.substring(2);
            MethodExitEvent e = (MethodExitEvent) event;
            MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());
            pw.println(indent + "-: " + me);
            //pw.println(indent + AspectInstTagResolver.instance.envIDtoIntStack.get(e.getEnvID()));
        } else if (event instanceof InstructionStartEvent) {
            InstructionStartEvent e = ((InstructionStartEvent) event);
            
            InstructionHandle ih = BytecodeResolver.v().getInstructionHandle(e.getMethodID(), e.getOffset());
            if (ih != null) {
                //MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());
                //int tag = ih.getTag();
                //pw.println(indent + "<" + ((tag < 10  && tag >= 0) ? " " : "") + tag
                //        + "> " + ih);
                pw.println(indent + "<" + ih.getKindTagStack() + "> " + ih);
                //pw.println(indent + AspectInstTagResolver.instance.envIDtoIntStack.get(e.getEnvID()));
            } else {
                pw.println(indent + "<??> (null)");
            }
        }
    }
}
