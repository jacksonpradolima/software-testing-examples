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

public abstract class TimedMonitorEvent extends MonitorEvent {
    /**
     * The number of milliseconds the thread is willing to wait (0 = no limit).
     */
    private long timeout;

    public TimedMonitorEvent() {
        this(0, -1L);
    }

    public TimedMonitorEvent(int object, long timeout) {
        super(object);

        this.timeout = timeout;
    }
    
    /**
     * Get timeout.
     *
     * @return timeout as long.
     */
    public long getTimeout() {
        return timeout;
    }
    
    /**
     * Set timeout.
     *
     * @param timeout the value to set.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_TIMEOUT) != 0) {
            timeout = in.readLong();
        } else {
            timeout = -1L;
        }
    }
}
