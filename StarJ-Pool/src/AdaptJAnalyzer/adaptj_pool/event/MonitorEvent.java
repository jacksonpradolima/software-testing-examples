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
 * The base class of Monitor Events (except MonitorDump event).
 */
public abstract class MonitorEvent extends AdaptJEvent {
    /**
     * The object ID associated with the monitor. 
     */
     private int object;
     
     public MonitorEvent() {
        this(0);
     }

     public MonitorEvent(int object) {
        this.object = 0;
     }
     
     /**
      * Get object.
      *
      * @return object as int.
      */
     public int getObject() {
         return object;
     }
     
     /**
      * Set object.
      *
      * @param object the value to set.
      */
     public void setObject(int object) {
         this.object = object;
     }

    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);
        
        if ((info & ADAPTJ_FIELD_OBJECT) != 0) {
            object = in.readInt();
        } else {
            object = 0;
        }
    }
}
