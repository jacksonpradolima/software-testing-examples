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

package adaptj_pool.toolkits;

import adaptj_pool.event.EventBox;
import adaptj_pool.Scene; // for verbose output
import adaptj_pool.toolkits.EventDependency;

public abstract class TimedEventBoxApplicable extends TimedCustomizable implements IEventBoxApplicable {
    public TimedEventBoxApplicable(String name, String description) {
        super(name, description, false);
    }

    public TimedEventBoxApplicable(String name, String description, boolean timed) {
        super(name, description, timed);
    }

    public final void preInit() {
        boolean timerRunning = isTimed();
        
        if (!isEnabled()) {
            return ;
        }

        if (isVerbose()) {
            doVerboseInit();
        }
        
        if (timerRunning) {
            startTimer();
        }
        
        try {
            doPreInit();
        } finally {
            if (timerRunning) {
                stopTimer();
            }
        }
    }

    public final void init() {
        boolean timerRunning = isTimed();
        
        if (!isEnabled()) {
            return ;
        }

        if (isVerbose()) {
            doVerboseInit();
        }

        if (timerRunning) {
            startTimer();
        }
        
        try {
            doInit();
        } finally {
            if (timerRunning) {
                stopTimer();
            }
        }
    }

    public final void apply(EventBox box) {
        boolean timerRunning = isTimed();
        
        if (!isEnabled()) {
            return ;
        }

        /*
        if (isVerbose()) {
            doVerboseApply(box);
        }
        */

        if (timerRunning) {
            startTimer();
        }
        
        try {
            doApply(box);
        } finally {
            if (timerRunning) {
                stopTimer();
            }
        }
    }

    public final void done() {
        boolean timerRunning = isTimed();
        
        if (!isEnabled()) {
            return ;
        }

        if (isVerbose()) {
            //Scene.v().showMessage(getName() + " finalizing");
            doVerboseDone();
        }
        
        if (timerRunning) {
            startTimer();
        }
        
        try {
            doDone();
        } finally {
            if (timerRunning) {
                stopTimer();
            }
        }
    }

    public abstract void doPreInit();
    public abstract void doInit();
    public abstract void doApply(EventBox box);
    public abstract void doDone();
    //public abstract int[] registerEvents();
    public abstract EventDependency[] registerEventDependencies();
    public abstract String[] registerOperationDependencies();

    
    public void doVerbosePreInit() {
    }
    
    public void doVerboseInit() {
    }
    
    /*
    public void doVerboseApply(EventBox box) {
    }
    */
    
    public void doVerboseDone() {

    }
}
