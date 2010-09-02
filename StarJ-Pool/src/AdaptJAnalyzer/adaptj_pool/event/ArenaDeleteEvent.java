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
 * An Event corresponding to the <code>JVMPI_ARENA_DELETE</code> event. This event
 * is triggered when a heap arena is deleted. It results in all of the objects in
 * this arena to be deleted.
 *
 * @author Bruno Dufour
 * @see ArenaNewEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class ArenaDeleteEvent extends AdaptJEvent implements ArenaEvent {
    /**
     * The ID of the arena being deleted. The <code>arena_id</code> field in <code>ArenaDeleteEvent</code>
     * corresponds to the <code>arena_id</code> field in the <code>JVMPI_ARENA_DELETE</code> event.
     */
    private int arena_id;
    
    public ArenaDeleteEvent() {
        this(0);
    }
    
    public ArenaDeleteEvent(int arena_id) {
        setTypeID(ADAPTJ_ARENA_DELETE);
        this.arena_id = arena_id;
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

    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_ARENA_ID) != 0) {
            arena_id = in.readInt();
        } else {
            arena_id = 0;
        }
    }
}
