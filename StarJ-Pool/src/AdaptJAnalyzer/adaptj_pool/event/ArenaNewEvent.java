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
 * An Event corresponding to the <code>JVMPI_ARENA_NEW</code> event. This event is triggered
 * when a heap arena is created by the Java VM.
 *
 * @author Bruno Dufour
 * @see ArenaDeleteEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class ArenaNewEvent extends AdaptJEvent implements ArenaEvent {
    /**
     * The ID of the arena being created. The <code>arena_id</code> field in <code>ArenaNewEvent</code>
     * corresponds to the <code>arena_id</code> field in the <code>JVMPI_ARENA_NEW</code> event.
     */ 
    private int arena_id;
    
    /**
     * The name of the arena being created. The <code>arena_name</code> field in <code>ArenaNewEvent</code>
     * corresponds to the <code>arena_name</code> field in the <code>JVMPI_ARENA_NEW</code> event.
     */
    private String arena_name;

    public ArenaNewEvent() {
        this(0, null);
    }
    
    public ArenaNewEvent(int arena_id, String arena_name) {
        setTypeID(ADAPTJ_ARENA_NEW);
        this.arena_id = arena_id;
        this.arena_name = arena_name;
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
     * Get arena_name.
     *
     * @return arena_name as String.
     */
    public String getArenaName() {
        return arena_name;
    }
    
    /**
     * Set arena_name.
     *
     * @param arena_name the value to set.
     */
    public void setArenaName(String arena_name) {
        this.arena_name = arena_name;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_ARENA_ID) != 0) {
            arena_id = in.readInt();
        } else {
            arena_id = 0;
        }
        if ((info & ADAPTJ_FIELD_ARENA_NAME) != 0) {
            arena_name = in.readUTF();
        } else {
            arena_name = null;
        }
    }
}
