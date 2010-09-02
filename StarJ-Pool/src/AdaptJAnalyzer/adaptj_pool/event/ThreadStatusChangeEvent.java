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

public class ThreadStatusChangeEvent extends AdaptJEvent {
    private int newStatus;
    
    public ThreadStatusChangeEvent() {
        this(-1);
    }
    
    public ThreadStatusChangeEvent(int newStatus) {
        setTypeID(ADAPTJ_THREAD_STATUS_CHANGE);
        this.newStatus = newStatus;
    }
    
    /**
     * Get newStatus.
     *
     * @return newStatus as int.
     */
    public int getNewStatus() {
        return newStatus;
    }
    
    /**
     * Set newStatus.
     *
     * @param newStatus the value to set.
     */
    public void setNewStatus(int newStatus) {
        this.newStatus = newStatus;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_NEW_STATUS) != 0) {
            newStatus = in.readInt();
        } else {
            newStatus = -1;
        }
    }
}
