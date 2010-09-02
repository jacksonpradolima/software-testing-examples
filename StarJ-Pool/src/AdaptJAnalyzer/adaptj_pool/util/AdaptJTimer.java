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

package adaptj_pool.util;

/** 
 * Utility class providing a timer.
 * Used for profiling various operations.
 *
 * @author Bruno Dufour
 */
public class AdaptJTimer {
    private long duration;       // cumulative duration
    private long startTime;      // last startTime
    private boolean hasStarted;  // whether the timer is currently running
    private String name;         // the name for this timer
		
    /** 
     * Creates a new timer with the given name.
     * 
     * @param name the name of the new timer 
     */
    public AdaptJTimer(String name) {
        this.name = name;
        duration = 0;
    }
    
    /**
     * Creates a new timer with the default name, which is "unnamed".
     */
    public AdaptJTimer() {
        this("unnamed");
    }
    
    /**
     * Starts the given timer.
     */
    public void start() {
        startTime = System.currentTimeMillis();
        if(hasStarted) {
            throw new RuntimeException("timer " + name + " has already been started!");
        } else {
            hasStarted = true;
        }
    }

    /**
     * Returns a string represeantation of this timer, which is simply the name of this timer.
     *
     * @return a <code>String</code> representation of this timer
     */
    public String toString() {
        return name;
    }
    
    /** 
     * Stops the current timer. 
     */
    public void stop() {
        if(!hasStarted) {
            throw new RuntimeException("timer " + name + " has not been started!");
        } else {
            hasStarted = false;
        }
        
        duration += System.currentTimeMillis() - startTime;
    }

    /**
     * Returns the cumulative duration for this timer. This value is the total time
     * this timer has spent in the enabled state. 
     * @return the cumulative duration of this timer, as a <code>long</code>*/
    public long getTime() {
        return duration;
    }
}
