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
 * An Event corresponding to the <code>JVMPI_GC_FINISH</code> event. This event is triggered after the Garbage Collector (GC)
 * of the Java VM has run.
 *
 * @author Bruno Dufour
 * @see GCStartEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class GCFinishEvent extends AdaptJEvent implements GCEvent {
    /**
     * The number of used objects in the heap. The <code>used_objects</code> field in <code>GCFinishEvent</code>
     * corresponds to the <code>used_objects</code> field in the <code>JVMPI_GC_FINISH</code> event.
     */   
    private long used_objects;
    /**
     * The total amount of space (in bytes) used by the objects in the heap. The <code>used_object_space</code>
     * field in <code>GCFinishEvent</code> corresponds to the <code>used_object_space</code> field in the
     * <code>JVMPI_GC_FINISH</code> event.
     */   
    private long used_object_space;
    /**
     * The total amount of object space (in bytes) available. The <code>total_object_space</code>
     * field in <code>GCFinishEvent</code> corresponds to the <code>total_object_space</code> field in the
     * <code>JVMPI_GC_FINISH</code> event.
     */   
    private long total_object_space;
    
    public GCFinishEvent() {
        this(-1, -1, -1);
    }

    public GCFinishEvent(int used_objects, int used_object_space, int total_object_space) {
        setTypeID(ADAPTJ_GC_FINISH);
        this.used_objects = used_objects;
        this.used_object_space = used_object_space;
        this.total_object_space = total_object_space;
    }
    
    /**
     * Get used_objects.
     *
     * @return used_objects as long.
     */
    public long getUsedObjects() {
        return used_objects;
    }
    
    /**
     * Set used_objects.
     *
     * @param used_objects the value to set.
     */
    public void setUsedObjects(long used_objects) {
        this.used_objects = used_objects;
    }
    
    /**
     * Get used_object_space.
     *
     * @return used_object_space as long.
     */
    public long getUsedObjectSpace() {
        return used_object_space;
    }
    
    /**
     * Set used_object_space.
     *
     * @param used_object_space the value to set.
     */
    public void setUsedObjectSpace(long used_object_space) {
        this.used_object_space = used_object_space;
    }
    
    /**
     * Get total_object_space.
     *
     * @return total_object_space as long.
     */
    public long getTotalObjectSpace() {
        return total_object_space;
    }
    
    /**
     * Set total_object_space.
     *
     * @param total_object_space the value to set.
     */
    public void setTotalObjectSpace(long total_object_space) {
        this.total_object_space = total_object_space;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_USED_OBJECTS) != 0) {
            used_objects = in.readLong();
        } else {
            used_objects = -1;
        }
        if ((info & ADAPTJ_FIELD_USED_OBJECT_SPACE) != 0) {
            used_object_space = in.readLong();
        } else {
            used_object_space = -1;
        }
        if ((info & ADAPTJ_FIELD_TOTAL_OBJECT_SPACE) != 0) {
            total_object_space = in.readLong();
        } else {
            total_object_space = -1;
        }
    }
}
