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
 * An Event corresponding to the <code>JVMPI_OBJECT_ALLOC</code> event. This event is triggered when an object is allocated by the
 * Java VM.
 *
 * @author Bruno Dufour
 * @see ObjectFreeEvent
 * @see ObjectMoveEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class ObjectAllocEvent extends AdaptJEvent implements ArenaEvent, ClassEvent, ObjectEvent {
    /* Possible values for is_array */
    /** Indicates that the object is not an array */
    public static final int NORMAL_OBJECT =  0;
    /** Indicates that the object is an array of classes */
    public static final int OBJECT_ARRAY  =  2;
    /** Indicates that the object is an array of <code>boolean</code>s */
    public static final int BOOLEAN_ARRAY =  4;
    /** Indicates that the object is an array of <code>char</code>s */
    public static final int CHAR_ARRAY    =  5;
    /** Indicates that the object is an array of <code>float</code>s */
    public static final int FLOAT_ARRAY   =  6;
    /** Indicates that the object is an array of <code>double</code>s */
    public static final int DOUBLE_ARRAY  =  7;
    /** Indicates that the object is an array of <code>byte</code>s */
    public static final int BYTE_ARRAY    =  8;
    /** Indicates that the object is an array of <code>short</code>s */
    public static final int SHORT_ARRAY   =  9;
    /** Indicates that the object is an array of <code>int</code>s */
    public static final int INT_ARRAY     = 10;
    /** Indicates that the object is an array of <code>long</code>s */
    public static final int LONG_ARRAY    = 11;
    
    /**
     * The ID of the arena where the object is allocated. The <code>arena_id</code> field in <code>ObjectAllocEvent</code>
     * corresponds to the <code>arena_id</code> field in the <code>JVMPI_OBJECT_ALLOC</code> event.
     */
    private int arena_id;
    /**
     * The ID of the class to which the allocated object belongs, or the array element class is <code>is_array</code>
     * is equal to <code>OBJECT_ARRAY</code>. The <code>class_id</code> field in <code>ObjectAllocEvent</code>
     * corresponds to the <code>class_id</code> field in the <code>JVMPI_OBJECT_ALLOC</code> event.
     */
    private int class_id;
    /**
     * The type of object being allocated. The <code>is_array</code> field in <code>ObjectAllocEvent</code>
     * corresponds to the <code>is_aray</code> field in the <code>JVMPI_OBJECT_ALLOC</code> event. 
     *
     * Possible values are: <code>NORMAL_OBJECT</code>, <code>OBJECT_ARRAY</code>, <code>BOOLEAN_ARRAY</code>,
     * <code>CHAR_ARRAY</code>, <code>FLOAT_ARRAY</code>, <code>DOUBLE_ARRAY</code>, <code>BYTE_ARRAY</code>,
     * <code>SHORT_ARRAY</code>, <code>INT_ARRAY</code>, <code>LONG_ARRAY</code>
     */
    private int is_array = -1;
    /**
     * The size (in bytes) of the allocated object. The <code>size</code> field in <code>ObjectAllocEvent</code>
     * corresponds to the <code>size</code> field in the <code>JVMPI_OBJECT_ALLOC</code> event.
     */
    private int size;
    /**
     * The ID of the newly allocated object. The <code>obj_id</code> field in <code>ObjectAllocEvent</code>
     * corresponds to the <code>obj_id</code> field in the <code>JVMPI_OBJECT_ALLOC</code> event.
     */
    private int obj_id;
    
    public ObjectAllocEvent() {
        this(0, 0, -1, -1, 0);
    }

    public ObjectAllocEvent(int arena_id, int class_id, int is_array, int size, int obj_id) {
        setTypeID(ADAPTJ_OBJECT_ALLOC);
        this.arena_id = arena_id;
        this.class_id = class_id;
        this.is_array = is_array;
        this.size = size;
        this.obj_id = obj_id;
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
    
    /**
     * Get is_array.
     *
     * @return is_array as int.
     */
    public int getIsArray() {
        return is_array;
    }
    
    /**
     * Set is_array.
     *
     * @param is_array the value to set.
     */
    public void setIsArray(int is_array) {
        this.is_array = is_array;
    }
    
    /**
     * Get size.
     *
     * @return size as int.
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Set size.
     *
     * @param size the value to set.
     */
    public void setSize(int size) {
        this.size = size;
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

        if ((info & ADAPTJ_FIELD_ARENA_ID) != 0) {
            arena_id = in.readInt();
        } else {
            arena_id = 0;
        }
        
        if ((info & ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID) != 0) {
            class_id = in.readInt();
        } else {
            class_id  = 0;
        }
        
        if ((info & ADAPTJ_FIELD_IS_ARRAY) != 0) {
            is_array = in.readInt();
        } else {
            is_array = -1;
        }
        
        if ((info & ADAPTJ_FIELD_SIZE) != 0) {
            size = in.readInt();
        } else {
            size = -1;
        }
        
        if ((info & ADAPTJ_FIELD_OBJ_ID) != 0) {
            obj_id = in.readInt();
        } else {
            obj_id = 0;
        }
    }

    public String toString() {
        return "ObjectAllocEvent[env_id=" + getEnvID()
                    + ", arena_id=" + arena_id
                    + ", class_id=" + class_id
                    + ", is_array=" + is_array
                    + ", size=" + size
                    + ", obj_id=" + obj_id
                    + ", requested=" + isRequested()
                    + "]";
    }
}
