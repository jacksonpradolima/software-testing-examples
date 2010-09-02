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

package adaptj_pool.spec;

import adaptj_pool.*;
import adaptj_pool.event.*;
import adaptj_pool.toolkits.*;

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

/** <code>adaptj_pool.spec.Generator</code> provides an easy way to design
 * AdaptJ Specification files based on the particular task that has
 * to be accomplished.
 *
 * It takes as argument a list of pack/operation names and outputs a
 * an AdaptJ Binary Specification file.
 */
public class Generator {
    public static final int INCLUDE_NO_OPTIONAL_DEPS = 0;
    public static final int INCLUDE_OPTIONAL_DEPS_L1 = 1;
    public static final int INCLUDE_ALL_OPTIONAL_DEPS = 2;
    
    private static short[] eventInfo;
    private static HashSet opSet;
    private static int includeOptional = INCLUDE_NO_OPTIONAL_DEPS;
    
    public static void printUsage() {
        System.out.println("Usage: java adaptj_pool.spec.Generator [--opt | --opt-all] <operation>+ <output file>");
    }
 
    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        int i = 0;
        if ("--opt".equals(args[0])) {
            includeOptional = INCLUDE_OPTIONAL_DEPS_L1;
            i = 1;
            if (args.length < 3) {
                printUsage();
                System.exit(1);
            }
        } else if ("--opt-all".equals(args[0])) {
            includeOptional = INCLUDE_ALL_OPTIONAL_DEPS;
            i = 1;
            if (args.length < 3) {
                printUsage();
                System.exit(1);
            }
        }

        opSet = new HashSet();
        eventInfo = new short[AdaptJEvent.ADAPTJ_EVENT_COUNT];
        for (int j = 0; j < AdaptJEvent.ADAPTJ_EVENT_COUNT; j++) {
            eventInfo[j] = (short) 0;
        }

        System.out.println("AdaptJ Spec Generator> Processing Specified Operations");
        for (; i < args.length - 1; i++) {
            System.out.println("AdaptJ Spec Generator>   Processing operation: \"" + args[i] + "\"");
            process(args[i], 0);
        }
        System.out.println("AdaptJ Spec Generator> Operations Processed");

        /*
        System.out.println("AdaptJ Spec Generator> Processing Event Dependencies");
        Iterator it = opSet.iterator();
        while (it.hasNext()) {
            IEventBoxApplicable eba = (IEventBoxApplicable) it.next();
            processDeps(eba);
        }
        */

        /* Display the binary information */
        for (int j = 0; j < AdaptJEvent.ADAPTJ_EVENT_COUNT; j++) {
            String s = Integer.toBinaryString(((int)eventInfo[j]) & 0x0000FFFF);
            for (int k = s.length(); k < 16; k++) {
                s = '0' + s;
            }
            String indexStr;
            DecimalFormat format = new DecimalFormat("00");
            if ((eventInfo[j] & AdaptJSpecConstants.ADAPTJ_FIELD_RECORDED) != 0) {
                indexStr = " " + format.format(j) + " : ";
            } else {
                indexStr = "[" + format.format(j) + "]: ";
            }
            
            System.out.println(indexStr + s);
        }
        
        /* Write the output */
        try {
            System.out.println("AdaptJ Spec Generator> Generating Compiled File to \"" + args[i] + "\"");
            DataOutputStream out = new DataOutputStream(new FileOutputStream(args[i]));
            int magic = AdaptJSpecConstants.ADAPTJ_SPEC_MAGIC;
            out.writeInt(magic);

            for (int j = 0; j < AdaptJEvent.ADAPTJ_EVENT_COUNT; j++) {
                out.writeByte((byte) (j & 0x000000FF));
                out.writeShort(eventInfo[j]);
            }
            out.close();
        } catch (IOException e) {
            Scene.v().reportError("AdaptJ Spec Generator> Failed to write to the file: \"" + args[i] + "\"");
            System.exit(1);
        }
        
        System.out.println("AdaptJ Spec Generator> Done");
    }

    private static void process(String op, int level) {
        ICustomizable customizable = Scene.v().getByName(op);
        
        if (customizable != null) {
            if (customizable instanceof IEventBoxApplicable) {
                IEventBoxApplicable eba = (IEventBoxApplicable) customizable;
                if (!opSet.contains(eba)) {
                    opSet.add(eba);
                    processDeps(eba, level);

                    String[] deps = eba.registerOperationDependencies();

                    if (deps != null) {
                        for (int i = 0; i < deps.length; i++) {
                            process(deps[i], level + 1);
                        }
                    }
                }
            } else {
                Scene.v().showWarning("Skipping " + op);
            }
        } else {
            Scene.v().reportError(op + " not found");
            System.exit(1);
        }
    }

    private static void processDeps(IEventBoxApplicable eba, int level) {
        EventDependency[] deps = eba.registerEventDependencies();
        if (deps != null) {
            for (int i = 0; i < deps.length; i++) {
                EventDependency dep = deps[i];
                if (dep.isRequired()
                        || (level == 0 && includeOptional == INCLUDE_OPTIONAL_DEPS_L1)
                        || (includeOptional == INCLUDE_ALL_OPTIONAL_DEPS)) {
                    eventInfo[dep.getEventID()] |= (short) (dep.getEventInfo() & 0x0000FFFF);
                }
            }
        }
    }
}
