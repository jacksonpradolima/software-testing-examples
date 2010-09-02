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

package adaptj_pool.event;

import java.io.*;

/**
 * An Event corresponding to the <code>JVMPI_METHOD_ENTRY</code> event. This event is triggered when a method is entered (i.e.
 * its code starts being executed). <code>MethodEntryEvent</code> is similar to <code>MethodEntry2Event</code> except that
 * MethodEntry2Event additionally provides the object ID of the target in the case of an <code>invokevirtual</code>.
 *
 * @author Bruno Dufour
 * @see MethodEntry2Event
 * @see MethodExitEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class MethodEntryEvent extends AdaptJEvent implements MethodEvent {
    private int method_id;

    public MethodEntryEvent() {
        this(0);
    }

    public MethodEntryEvent(int method_id) {
        setTypeID(ADAPTJ_METHOD_ENTRY);
        this.method_id = method_id;
    }

    /**
     * Get method_id.
     *
     * @return method_id as int.
     */
    public int getMethodID() {
        return method_id;
    }
    
    /**
     * Set method_id.
     *
     * @param method_id the value to set.
     */
    public void setMethodID(int method_id) {
        this.method_id = method_id;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);
        
        if ((info & ADAPTJ_FIELD_METHOD_ID) != 0) {
            method_id = in.readInt();
        } else {
            method_id = 0;
        }
    }
}
