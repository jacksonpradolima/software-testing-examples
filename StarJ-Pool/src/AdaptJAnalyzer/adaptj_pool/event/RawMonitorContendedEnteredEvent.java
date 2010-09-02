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

/**
 * An Event corresponding to the <code>JVMPI_RAW_MONITOR_CONTENDED_ENTERED</code> event. This event is triggered when a thread 
 * successfully enters a Raw Java monitor after having waited for it to be released by another thread.
 *
 * @author Bruno Dufour
 * @see RawMonitorContendedEnterEvent
 * @see RawMonitorContendedExitEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class RawMonitorContendedEnteredEvent extends RawMonitorEvent {
    public RawMonitorContendedEnteredEvent() {
        this(null, 0);
    }
    
    public RawMonitorContendedEnteredEvent(String name, int id) {
        super(name, id);
        setTypeID(ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED);
    }
}
