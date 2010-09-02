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

import java.util.*;
import java.io.*;
import adaptj_pool.spec.AdaptJSpecConstants;

/**
 * An abstract event class which is the base class for all AdaptJ events.
 * This class defines the various AdaptJ Event Type IDs notably used as
 * array indices in the AdaptJ framework.
 *
 * In order to minimize the time needed to process events, all fields
 * provided by AdaptJEvent are declared public, thus allowing access to
 * their values without requiring a method call. All subclasses of AdaptJEvent
 * use the same strategy.
 * 
 * @author Bruno Dufour
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public abstract class AdaptJEvent implements AdaptJSpecConstants {
    /**
     * The environment ID in which this event has been fired. This
     * information is typically obtained from the JVMPI Agent.
     */
    private int env_id;
    
    /**
     * The AdaptJ Event Type ID of the current event. This is not
     * set automatically by the subclasses. In the AdaptJ framework, it is
     * set by the AEFReader creating the specific events. It is initialized to
     * <code>ADAPTJ_UNKNOWN</code>.
     */
    private byte typeID = ADAPTJ_UNKNOWN;

    /**
     * Indicates whether the event was requested by the JVMPI Agent (<code>true</code>
     * if it was requested, <code>false</code> otherwise)
     */
    private boolean requested = false;

    /**
     * The total number of events that are known to the AdaptJ framework. The index
     * of the last event must be AdaptJ - 1. Valid Event Type IDs range from 0
     * AdaptJ - 1, which allows their use as array indices.
     */
    public final static int ADAPTJ_EVENT_COUNT = 38;

    /**
     * The mask to be applied to an event ID in order to determine whether it was requested.
     */
    public final static int ADAPTJ_REQUESTED_EVENT = 0x00000080;

    /**
     * The AdaptJ Event Type ID indicating an unknown event or an error.
     */
    public final static int ADAPTJ_UNKNOWN                       = -1;
    
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_ARENA_DELETE</code> event.
     */
    public final static int ADAPTJ_ARENA_DELETE                  =  0;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_ARENA_NEW</code> event.
     */
    public final static int ADAPTJ_ARENA_NEW                     =  1;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_CLASS_LOAD</code> event.
     */
    public final static int ADAPTJ_CLASS_LOAD                    =  2;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_CLASS_LOAD_HOOK</code> event.
     */
    public final static int ADAPTJ_CLASS_LOAD_HOOK               =  3;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_CLASS_UNLOAD</code> event.
     */
    public final static int ADAPTJ_CLASS_UNLOAD                  =  4;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_COMPILED_METHOD_LOAD</code> event.
     */
    public final static int ADAPTJ_COMPILED_METHOD_LOAD          =  5;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_COMPILED_METHOD_UNLOAD</code> event.
     */
    public final static int ADAPTJ_COMPILED_METHOD_UNLOAD        =  6;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_DATA_DUMP_REQUEST</code> event.
     */
    public final static int ADAPTJ_DATA_DUMP_REQUEST             =  7;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_DATA_RESET_REQUEST</code> event.
     */
    public final static int ADAPTJ_DATA_RESET_REQUEST            =  8;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_GC_FINISH</code> event.
     */
    public final static int ADAPTJ_GC_FINISH                     =  9;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_GC_START</code> event.
     */
    public final static int ADAPTJ_GC_START                      = 10;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_HEAP_DUMP</code> event.
     */
    public final static int ADAPTJ_HEAP_DUMP                     = 11;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_JNI_GLOBALREF_ALLOC</code> event.
     */
    public final static int ADAPTJ_JNI_GLOBALREF_ALLOC           = 12;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_JNI_GLOBALREF_FREE</code> event.
     */
    public final static int ADAPTJ_JNI_GLOBALREF_FREE            = 13;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_JNI_WEAK_GLOBALREF_ALLOC</code> event.
     */
    public final static int ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC      = 14;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_JNI_WAEK_GLOBALREF_FREE</code> event.
     */
    public final static int ADAPTJ_JNI_WEAK_GLOBALREF_FREE       = 15;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_JVM_INIT_DONE</code> event.
     */
    public final static int ADAPTJ_JVM_INIT_DONE                 = 16;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_JVM_SHUT_DOWN</code> event.
     */
    public final static int ADAPTJ_JVM_SHUT_DOWN                 = 17;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_METHOD_ENTRY</code> event.
     */
    public final static int ADAPTJ_METHOD_ENTRY                  = 18;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_METHOD_ENTRY2</code> event.
     */
    public final static int ADAPTJ_METHOD_ENTRY2                 = 19;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_METHOD_EXIT</code> event.
     */
    public final static int ADAPTJ_METHOD_EXIT                   = 20;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_MONITOR_CONTENDED_ENTER</code> event.
     */
    public final static int ADAPTJ_MONITOR_CONTENDED_ENTER       = 21;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_MONITOR_CONTENDED_ENTERED</code> event.
     */
    public final static int ADAPTJ_MONITOR_CONTENDED_ENTERED     = 22;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_MONITOR_CONTENDED_EXIT</code> event.
     */
    public final static int ADAPTJ_MONITOR_CONTENDED_EXIT        = 23;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_MONITOR_DUMP</code> event.
     */
    public final static int ADAPTJ_MONITOR_DUMP                  = 24;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_MONITOR_WAIT</code> event.
     */
    public final static int ADAPTJ_MONITOR_WAIT                  = 25;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_MONITOR_WAITED</code> event.
     */
    public final static int ADAPTJ_MONITOR_WAITED                = 26;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_OBJECT_ALLOC</code> event.
     */
    public final static int ADAPTJ_OBJECT_ALLOC                  = 27;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_OBJECT_DUMP</code> event.
     */
    public final static int ADAPTJ_OBJECT_DUMP                   = 28;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_OBJECT_FREE</code> event.
     */
    public final static int ADAPTJ_OBJECT_FREE                   = 29;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_OBJECT_MOVE</code> event.
     */
    public final static int ADAPTJ_OBJECT_MOVE                   = 30;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_RAW_MONITOR_CONTENDED_ENTER</code> event.
     */
    public final static int ADAPTJ_RAW_MONITOR_CONTENDED_ENTER   = 31;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_RAW_MONITOR_CONTENDED_ENTERED</code> event.
     */
    public final static int ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED = 32;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_RAW_MONITOR_CONTENDED_EXIT</code> event.
     */
    public final static int ADAPTJ_RAW_MONITOR_CONTENDED_EXIT    = 33;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_THREAD_END</code> event.
     */
    public final static int ADAPTJ_THREAD_END                    = 34;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_THREAD_START</code> event.
     */
    public final static int ADAPTJ_THREAD_START                  = 35;
    /**
     * The AdaptJ Event Type ID corresponding to a <code>JVMPI_INSTRUCTION_START</code> event.
     */
    public final static int ADAPTJ_INSTRUCTION_START             = 36;
    public final static int ADAPTJ_THREAD_STATUS_CHANGE          = 37;
    
    /**
     * Get env_id.
     *
     * @return env_id as int.
     */
    public int getEnvID() {
        return env_id;
    }
    
    /**
     * Set env_id.
     *
     * @param env_id the value to set.
     */
    public void setEnvID(int env_id) {
        this.env_id = env_id;
    }
    
    /**
     * Get typeID.
     *
     * @return typeID as byte.
     */
    public byte getTypeID() {
        return typeID;
    }
    
    /**
     * Set typeID.
     *
     * @param typeID the value to set.
     */
    public void setTypeID(byte typeID) {
        this.typeID = typeID;
    }

    void setTypeID(int typeID) {
        this.typeID = (byte) (typeID & 0x000000FF);
    }
    
    /**
     * Get requested.
     *
     * @return requested as boolean.
     */
    public boolean isRequested() {
        return requested;
    }
    
    /**
     * Set requested.
     *
     * @param requested the value to set.
     */
    public void setRequested(boolean requested) {
        this.requested = requested;
    }

    public void readFromStream(DataInput in, short info) throws IOException {
        if ((info & ADAPTJ_FIELD_ENV_ID) != 0) {
            env_id = in.readInt();
        } else {
            env_id = 0;
        }
    }
}
