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

import adaptj_pool.toolkits.*;
import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.spec.AdaptJSpecConstants;

public class ClassNameResolver extends EventOperation {
    private static ClassNameResolver instance = new ClassNameResolver();

    private ClassNameResolver() {
        super("ClassNameResolver", "Adds the full path of the class file to the event corresponding to a class load");
    }

    public static ClassNameResolver v() {
        return instance;
    }
    
    /*
    public ClassNameResolver(String name) {
        super(name);
    }
    */

    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_CLASS_LOAD
        };

        return events;
    }
    */
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_LOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_NAME)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        return null;
    }

    public void doInit() {

    }

    public void doDone() {

    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        if (event.getTypeID() == AdaptJEvent.ADAPTJ_CLASS_LOAD) {
            ClassLoadEvent e = (ClassLoadEvent) event;

            String fullName = ClassPathExplorer.v().getClassFileName(e.getClassName());
            e.setFullClassName(fullName);
        }
    }
}
