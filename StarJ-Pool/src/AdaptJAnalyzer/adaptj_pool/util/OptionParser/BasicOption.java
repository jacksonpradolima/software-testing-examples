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

package adaptj_pool.util.OptionParser;

import java.util.*;

public class BasicOption implements Option {
    private String description;
    private List switches;
    private List arguments;
    private List argumentDescs;

    /*
    public BasicOption(char c, String description) {
        this.switches = new ArrayList();
        switches.add("-" + c);
        this.arguments = null;
        this.description = description;
    }

    public ShortOption(String s, String description) {
        if (s == null) {
            this.switches = null;
        } else {
            this.switches = new ArrayList();
            this.switches.add(s);
        }
        this.arguments = null;
    }
    */

    public BasicOption(String description) {
        this.description = description;
        this.switches = null;
        this.arguments = null;
    }

    public void addShortSwitch(String s) {
        if (switches == null) {
            switches = new ArrayList();
        }
        switches.add("-" + s);
    }

    public void addLongSwitch(String s) {
        if (switches == null) {
            switches = new ArrayList();
        }
        switches.add("--" + s);
    }

    public void addSwitch(String s) {
        if (switches == null) {
            switches = new ArrayList();
        }
        switches.add(s);
    }

    public String getDescription() {
        return description;
    }

    public String[] getSwitches() {
        if (switches == null) {
            return null;
        }

        String[] result = new String[switches.size()];
        int i = 0;
        Iterator it = switches.iterator();
        while (it.hasNext()) {
            result[i++] = (String) it.next();
        }

        return result;
    }

    public Argument[] getArguments() {
        if (arguments == null) {
            return null;
        }

        int i = 0;
        Argument[] result = new Argument[arguments.size()];
        Iterator it = arguments.iterator();
        while (it.hasNext()) {
            result[i++] = (Argument) it.next();
        }

        return result;
    }

    public String[] getArgumentDescriptions() {
        if (arguments == null) {
            return null;
        }

        int i = 0;
        String[] result = new String[argumentDescs.size()];
        Iterator it = argumentDescs.iterator();
        while (it.hasNext()) {
            result[i++] = (String) it.next();
        }

        return result;
    }

    public void addArgument(Argument argument, String description) {
        if (arguments == null) {
            arguments = new ArrayList();
        }

        if (argumentDescs == null) {
            argumentDescs = new ArrayList();
        }

        arguments.add(argument);
        argumentDescs.add(description);
    }
}
