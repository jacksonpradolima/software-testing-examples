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
import adaptj_pool.util.*;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.event.*;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.spec.AdaptJSpecConstants;
import java.util.*;
import it.unimi.dsi.fastUtil.*;

public class MethodPrinter extends DefaultEventPrinter {
    private Object2IntOpenHashMap methodMap;

    public MethodPrinter(String name, String description) {
        super(name, description, "AdaptJ.methods");
    }

    public MethodPrinter(String name, String description, String fileName) {
        super(name, description, fileName);
    }
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY,
                                AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID,
                                false)
                                

            /*
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID,
                                false)
            */
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER};

        return deps;
    }

    public void doInit() {
        methodMap = new Object2IntOpenHashMap();
        methodMap.setDefRetValue(0);
        super.doInit();
    }
    
    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        
        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                {
                    MethodEvent e = (MethodEvent) event;

                    MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());
                    if (me != null) {
                        int val = methodMap.getInt(me);
                        methodMap.put(me, val + 1);
                    }
                }
                break;
            default:
                break;
        }        
    }

    public void doDone() {
        List l = new ArrayList(methodMap.keySet());
        Collections.sort(l, new MethodPrinterComparator());
        
        Iterator it = l.iterator();
        while (it.hasNext()) {
            MethodEntity me = (MethodEntity) it.next();
            pw.println("(" + methodMap.getInt(me) + ") " + me);
        }

        super.doDone();
    }

    class MethodPrinterComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            return methodMap.getInt(o2) - methodMap.getInt(o1);
        }

        public boolean equals(Object o) {
            return (o instanceof MethodPrinterComparator);
        }
    }
}
