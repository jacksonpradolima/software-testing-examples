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

import adaptj_pool.Scene;
import adaptj_pool.event.*;
import adaptj_pool.JVMPI.*;
import java.io.*;
import adaptj_pool.toolkits.*;
import adaptj_pool.toolkits.analyses.IDResolver;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.util.text.HelpDisplayManager;

public class MethodTargetPrinter extends EventOperation {
    protected String fileName = null;
    protected PrintStream pw;
    
    public MethodTargetPrinter(String name, String description) {
        super(name, description);
        fileName = "AdaptJ.methods.txt";
    }

    public MethodTargetPrinter(String name, String description, String fileName) {
        super(name, description);
        this.fileName = fileName;
    }
 
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /*
    public int[] registerEvents() {
        int events[] = {AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                        AdaptJEvent.ADAPTJ_OBJECT_ALLOC};
        return events;
    }
    */
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OBJ_ID),
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OBJ_ID)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER};
        return deps;
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

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                {
                    MethodEntry2Event e = (MethodEntry2Event) event;
                    String s = Integer.toHexString(e.getObjID()).toUpperCase();
                    int numZeros = 8 - s.length();
                    for (int i = 0; i < numZeros; i++) {
                        s = "0" + s;
                    }
            
                    pw.println(">>>> Method Invoked with ID: 0x" + s);
                }
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                {
                    ObjectAllocEvent e = (ObjectAllocEvent) event;
                    String s = Integer.toHexString(e.getObjID()).toUpperCase();
                    int numZeros = 8 - s.length();
                    for (int i = 0; i < numZeros; i++) {
                        s = "0" + s;
                    }
            
                    pw.println(">>>> Created object with ID: 0x" + s);
                }
                break;
            default:
                break;
        }
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
