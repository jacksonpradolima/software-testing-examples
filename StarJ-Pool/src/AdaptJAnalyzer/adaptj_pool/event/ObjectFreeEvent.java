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
 * An Event corresponding to the <code>JVMPI_OBJECT_FREE</code> event. This event is triggered when an object is freed by the
 * Java VM.
 *
 * @author Bruno Dufour
 * @see ObjectAllocEvent
 * @see ObjectMoveEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class ObjectFreeEvent extends AdaptJEvent implements ObjectEvent {
    /**
     * The ID of the freed object. The <code>obj_id</code> field in <code>ObjectFreeEvent</code>
     * corresponds to the <code>obj_id</code> field in the <code>JVMPI_OBJECT_FREE</code> event.
     */
    private int obj_id;
    
    public ObjectFreeEvent() {
        this(0);
    }

    public ObjectFreeEvent(int obj_id) {
        setTypeID(ADAPTJ_OBJECT_FREE);
        this.obj_id = obj_id;
    }
    
    /**
     * Get obj_id.
     *
     * @return obj_id as int.
     */
    public int getObjID() {
        return obj_id;
    }
    
    /**
     * Set obj_id.
     *
     * @param obj_id the value to set.
     */
    public void setObjID(int obj_id) {
        this.obj_id = obj_id;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_OBJ_ID) != 0) {
            obj_id = in.readInt();
        } else {
            obj_id = 0;
        }
    }
}
