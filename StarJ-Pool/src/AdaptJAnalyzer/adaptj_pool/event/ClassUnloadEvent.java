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
 * An Event corresponding to the <code>JVMPI_CLASS_UNLOAD</code> event. This event is triggered when a previously
 * loaded class is unloaded by the Java VM.
 *
 * @author Bruno Dufour
 * @see ClassLoadEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class ClassUnloadEvent extends AdaptJEvent implements ClassEvent {
    /**
    * The ID of the class being unloaded. The <code>class_id</code> field in <code>ClassUnloadEvent</code>
    * corresponds to the <code>class_id</code> field in the <code>JVMPI_CLASS_UNLOAD</code> event.
    */
    private int class_id;

    public ClassUnloadEvent() {
        this(0);
    }

    public ClassUnloadEvent(int class_id) {
        setTypeID(ADAPTJ_CLASS_UNLOAD);
        this.class_id = class_id;
    }
    
    /**
     * Get class_id.
     *
     * @return class_id as int.
     */
    public int getClassID() {
        return class_id;
    }
     
    /**
     * Set class_id.
     *
     * @param class_id the value to set.
     */
    public void setClassID(int class_id) {
        this.class_id = class_id;
    }
     
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID) != 0) {
            class_id = in.readInt();
        } else {
            class_id = 0;
        }
    }
}
