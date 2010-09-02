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

public abstract class RawMonitorEvent extends AdaptJEvent {
    /**
     * The name of the raw monitor. 
     */
    private String name;
    
    /**
     * The ID of the raw monitor. 
     */
    private int id;

    public RawMonitorEvent() {
        this(null, 0);
    }
    
    public RawMonitorEvent(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Get name.
     *
     * @return name as String.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set name.
     *
     * @param name the value to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get id.
     *
     * @return id as int.
     */
    public int getID() {
        return id;
    }
    
    /**
     * Set id.
     *
     * @param id the value to set.
     */
    public void setID(int id) {
        this.id = id;
    }


    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_NAME) != 0) {
            name = in.readUTF();
        } else {
            name = null;
        }
        
        if ((info & ADAPTJ_FIELD_ID) != 0) {
            id   = in.readInt();
        } else {
            id = 0;
        }
    }
}
