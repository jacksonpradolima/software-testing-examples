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

import adaptj_pool.util.text.OptionStringParser;
import adaptj_pool.util.text.HelpDisplayManager;

public abstract class Customizable implements ICustomizable {
    private String name;
    private String description;
    private boolean enabled = true;
    private boolean verbose = false;
    
    public Customizable(String name, String description) {
        checkName(name);    
        this.name = name;
        this.description = description;
    }
    
    private void checkName(String name) {
        if (name == null) {
            throw new RuntimeException("Invalid Customizable name: " + name);
        }

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!(Character.isLetterOrDigit(c) || (c == '_') || (c == '-' && i > 0))) {
                throw new RuntimeException("Invalid Customizable name: " + name + " (invalid char " + c + " at index " + i + ")");
            }
        }
    }
        
    public void setOption(String name, String value) {
        if (name.equals("enabled")) {
            setEnabled(OptionStringParser.parseBoolean(value));
        } else if (name.equals("verbose")) {
            setVerbose(OptionStringParser.parseBoolean(value));
        } else {
            throw new NoSuchOptionException(this, name);
        }
    }

    public String getOption(String name) {
        if (name.equals("name")) {
            return this.name;
        } else if (name.equals("enabled")) {
            return (enabled ? "true" : "false");
        } else if (name.equals("verbose")) {
            return (verbose ? "true" : "false");
        } else {
            throw new NoSuchOptionException(this, name);
        }
    }

    public void displayHelp(HelpDisplayManager manager) {
        manager.displayOptionHelp("enabled[:<boolean>]", "Controls whether this pack/operation is active or not");
        manager.displayOptionHelp("verbose[:<boolean>]", "Controls whether this pack/operation displays extended information or not");
    }
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
