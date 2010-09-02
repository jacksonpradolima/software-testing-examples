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

import adaptj_pool.util.*;
import adaptj_pool.util.text.OptionStringParser;
import adaptj_pool.util.text.HelpDisplayManager;

public abstract class TimedCustomizable extends Customizable implements ITimedCustomizable {
    private AdaptJTimer timer;
    private boolean timed;

    public TimedCustomizable(String name, String description) {
        this(name, description, false);
    }

    public TimedCustomizable(String name, String description, boolean timed) {
        super(name, description);
        if (timed) {
            timer = new AdaptJTimer(name);
        } else {
            timer = null;
        }
        this.timed = timed;
    }

    public void startTimer() {
        if (timed) {
            timer.start();
        }
    }

    public void stopTimer() {
        if (timed) {
            timer.stop();
        }
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
        if (timed && (timer == null)) {
            timer = new AdaptJTimer(getName());
        }
    }

    public long getTime() {
        if (timer != null) {
            return timer.getTime();
        }

        return -1L;
    }

    public String getOption(String name) {
        if (name.equals("timed")) {
            return (timed ? "true" : "false");
        } else {
            return super.getOption(name);
        }
    }
    
    public void setOption(String name, String value) {
        if (name.equals("timed")) {
            timed = OptionStringParser.parseBoolean(value);
        } else {
            super.setOption(name, value);
        }
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);
        manager.displayOptionHelp("timed[:boolean]", "Controls whether this operation is timed");
    }
}
