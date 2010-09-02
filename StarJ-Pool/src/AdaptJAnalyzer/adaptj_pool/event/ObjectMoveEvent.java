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
 * An Event corresponding to the <code>JVMPI_OBJECT_MOVE</code> event. This event is triggered when an object is moved in the heap
 * by the Java VM.
 *
 * @author Bruno Dufour
 * @see ObjectAllocEvent
 * @see ObjectFreeEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class ObjectMoveEvent extends AdaptJEvent implements ArenaEvent, ObjectEvent {
    /**
     * The ID of the arena in which the object can be found. The <code>arena_id</code> field in <code>ObjectMoveEvent</code>
     * corresponds to the <code>arena_id</code> field in the <code>JVMPI_OBJECT_MOVE</code> event.
     */
    private int arena_id;
    /**
     * The ID of the object being moved. The <code>object_id</code> field in <code>ObjectMoveEvent</code>
     * corresponds to the <code>object_id</code> field in the <code>JVMPI_OBJECT_MOVE</code> event.
     */
    private int obj_id;
    /**
     * The ID of the arena to which the object is being moved. The <code>arena_id</code> field in <code>ObjectMoveEvent</code>
     * corresponds to the <code>arena_id</code> field in the <code>JVMPI_OBJECT_MOVE</code> event.
     */
    private int new_arena_id;
    /**
     * The new ID of the object. The <code>object_id</code> field in <code>ObjectMoveEvent</code>
     * corresponds to the <code>object_id</code> field in the <code>JVMPI_OBJECT_MOVE</code> event.
     */
    private int new_obj_id;
    
    public ObjectMoveEvent() {
        this(0, 0, 0, 0);
    }

    public ObjectMoveEvent(int arena_id, int obj_id, int new_arena_id, int new_obj_id) {
        setTypeID(ADAPTJ_OBJECT_MOVE);
        this.arena_id = arena_id;
        this.obj_id = obj_id;
        this.new_arena_id = new_arena_id;
        this.new_obj_id = new_obj_id;
    }
    
    /**
     * Get arena_id.
     *
     * @return arena_id as int.
     */
    public int getArenaID() {
        return arena_id;
    }
    
    /**
     * Set arena_id.
     *
     * @param arena_id the value to set.
     */
    public void setArenaID(int arena_id) {
        this.arena_id = arena_id;
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
    
    /**
     * Get new_arena_id.
     *
     * @return new_arena_id as int.
     */
    public int getNewArenaID() {
        return new_arena_id;
    }
    
    /**
     * Set new_arena_id.
     *
     * @param new_arena_id the value to set.
     */
    public void setNewArenaID(int new_arena_id) {
        this.new_arena_id = new_arena_id;
    }
    
    /**
     * Get new_obj_id.
     *
     * @return new_obj_id as int.
     */
    public int getNewObjID() {
        return new_obj_id;
    }
    
    /**
     * Set new_obj_id.
     *
     * @param new_obj_id the value to set.
     */
    public void setNewObjID(int new_obj_id) {
        this.new_obj_id = new_obj_id;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_ARENA_ID) != 0) {
            arena_id = in.readInt();
        } else {
            arena_id = 0;
        }
        
        if ((info & ADAPTJ_FIELD_OBJ_ID) != 0) {
            obj_id = in.readInt();
        } else {
            obj_id = 0;
        }
        
        if ((info & ADAPTJ_FIELD_NEW_ARENA_ID) != 0) {
            new_arena_id = in.readInt();
        } else {
            new_arena_id = 0;
        }
        
        if ((info & ADAPTJ_FIELD_NEW_OBJ_ID) != 0) {
            new_obj_id = in.readInt();
        } else {
            new_obj_id = 0;
        }
    }
}
