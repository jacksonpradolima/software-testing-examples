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

import adaptj_pool.util.text.*;
import java.io.*;
import java.util.*;
import it.unimi.dsi.fastUtil.*;

public class OptionParser {
    private Object2ObjectOpenHashMap switchesToOptions;
    private Object2IntOpenHashMap optionsToIDs;
    private Int2ObjectOpenHashMap idsToOptions;
    private String programCommand;
    private String header;
    private String footer;
    //private int currID = 0;
    private ParsedObject poList, lastPo;
    
    /* option flags */
    private boolean combineShortSwitches = true; // combine Short Switches (eg -abc = -a -b -c, -xvzf file = -x -v -z -f file)
    private boolean interpretDoubleDash = true;  // interpret -- in UN*X way: when -- is encountered, all subsequent args are non-options
    private boolean returnDoubleDash = false;    // return -- as part of the parsed options
    private boolean equalIsSep = true;           // use = as sep (eg --long-opt=foo, -a=foo)
    private boolean aggregateNonOpts = false;    // Group all non-options at the end
    private boolean shortOptionNoSpace = true;   // Short options can have arg without space (eg -a10)
    private boolean displayAsTable = true;

    /* Planned Options:
     *     + Help format: list | table
     *     + Sort help
     */

    public OptionParser(String programCommand) {
        this(programCommand, null, null);
    }

    public OptionParser(String programCommand, String header) {
        this(programCommand, header, null);
    }
    
    public OptionParser(String programCommand, String header, String footer) {
        switchesToOptions = new Object2ObjectOpenHashMap();
        optionsToIDs = new Object2IntOpenHashMap();
        idsToOptions = new Int2ObjectOpenHashMap();
        this.programCommand = programCommand;
        this.header = header;
        this.footer = footer;
    }

    public void addOption(Option option, int optionID) {
        if (option == null) {
            throw new NullPointerException("Option cannot be null");
        }

        if (idsToOptions.containsKey(optionID)) {
            throw new RuntimeException("Option ID " + optionID + " already used");
        }

        String[] switches = option.getSwitches();
        if (switches != null) {
            for (int i = 0; i < switches.length; i++) {
                String currentSwitch = switches[i];
                if (switchesToOptions.containsKey(currentSwitch)) {
                    throw new RuntimeException("Switch already registered");
                }

                switchesToOptions.put(switches[i].trim(), option);
            }
        }

        optionsToIDs.put(option, optionID);
        idsToOptions.put(optionID, option);

        //return currID++;
    }

    /*
    public void removeOption(int optionID) {
        Integer optID = new Integer(optionID);
        if (idsToOptions.contaisKey(optID)) {
            removeOption((Option) idsToOptions
        }
    }

    public void removeOption(Option option) {
        if (optionsTo
    }
    */

    public void combineShortSwitches(boolean value) {
        combineShortSwitches = value;
    }

    public void interpretDoubleDash(boolean value) {
        interpretDoubleDash = value;
    }

    public void returnDoubleDash(boolean value) {
        returnDoubleDash = value;
    }

    public void equalIsSeparator(boolean value) {
        equalIsSep = value;
    }

    public void aggregateNonOptions(boolean value) {
        aggregateNonOpts = value;
    }

    public void shortOptionNoSpace(boolean value) {
        shortOptionNoSpace = value;
    }

    public void displayAsTable(boolean value) {
        displayAsTable = value;
    }

    public ParsedObject parse(String[] args) throws OptionProcessingException {
        poList = null;
        lastPo = null;
        ArgumentQueue queue = new ArgumentQueue(args);
        boolean forceNonOpt = false;
        
        String arg;
        while ((arg = queue.pop()) != null) {
            if (forceNonOpt) {
                addToList(new ParsedNonOption(arg));
            } else if (interpretDoubleDash && arg.equals("--")) {
                forceNonOpt = true;
                if (returnDoubleDash) {
                    addToList(new ParsedNonOption("--"));
                }
            } else if (arg.startsWith("--") || !arg.startsWith("-") || !combineShortSwitches) {
                try {
                    processOption(arg, queue);
                } catch (UnknownOptionException e) {
                    if (arg.startsWith("-")) {
                        throw e;
                    }

                    addToList(new ParsedNonOption(arg));
                }
            } else {
                /* Short switches are to be combined */

                try {
                    /* First check if this is a valid option */
                    processOption(arg, queue);
                } catch (UnknownOptionException e) {
                    /* No --> Try to break it up */
                    arg = arg.substring(1);
                    if (arg.length() > 1) {
                        char[] c = arg.toCharArray();
                        
                        /*
                        for (int j = 0; j < c.length; j++) {
                            processOption("-" + c[j], ((j == c.length - 1) ? queue : null));
                        }
                        */
                        for (int j = c.length - 1; j >= 0; j--) {
                            queue.push("-" + c[j]);
                        }
                    } else {
                        throw e;
                    }
                }
            }
        }


        if (aggregateNonOpts) {
            reorderList(); /* Put all non-options at the end of the list */
        }
        
        return poList;
    }

    private void processOption(String optSwitch, ArgumentQueue queue) throws OptionProcessingException {
        boolean equalFound = false;

        optSwitch = optSwitch.trim();
        if (optSwitch.startsWith("-") && equalIsSep) {
            int equalPos = optSwitch.indexOf('=');
            if (equalPos > 0) {
                queue.push(optSwitch.substring(equalPos + 1));
                optSwitch = optSwitch.substring(0, equalPos);
                equalFound = true;
            }
        }

        Iterator switchIt = switchesToOptions.keySet().iterator();
        while (switchIt.hasNext()) {
            String s = (String) switchIt.next();
            
            if (optSwitch.equals(s)) {
                processFoundOption(optSwitch, queue, equalFound);
                return ;
            }
        }

        if (shortOptionNoSpace && !equalFound) {
            switchIt = switchesToOptions.keySet().iterator();
            while (switchIt.hasNext()) {
                String s = (String) switchIt.next();
                
                if (optSwitch.startsWith(s) && optSwitch.startsWith("-") && (!optSwitch.startsWith("--"))) {
                    queue.push(optSwitch.substring(s.length()));
                    processFoundOption(s, queue, true);
                    return ;
                }
            }
        }
        
        throw new UnknownOptionException(optSwitch);
    }

    private void processFoundOption(String optSwitch, ArgumentQueue queue, boolean argumentPushed) throws OptionProcessingException {
        /* Found a match */
        ParsedObject po = null;
        Option opt = (Option) switchesToOptions.get(optSwitch);
        int optID = optionsToIDs.getInt(opt);
        Argument[] args = opt.getArguments();

        if (args == null) {
            /* No args */
            if (argumentPushed) {
                if (combineShortSwitches) {
                    throw new UnknownOptionException(optSwitch + queue.pop());
                }
                throw new OptionProcessingException("Option \"" + optSwitch + "\" does not take an argument");
            }
            po = new ParsedOption(optID, null);
        } else {
            /* >= 1 arg */
            Vector v = new Vector();
            for (int i = 0; i < args.length; i++) {
                Argument arg = args[i];
                String next = queue.top();
                if (next != null && next.startsWith("-")) {
                    if (!arg.isRequired()) {
                        continue;
                    } else {
                        throw new OptionProcessingException("Option \"" + optSwitch + "\" is missing an argument");
                    }
                }
                Object[] argObjects = arg.parse(queue);
                if (argObjects != null) {
                    for (int j = 0; j < argObjects.length; j++) {
                        v.add(argObjects[j]);
                    }
                }
            }
            
            if (v.size() == 0) {
                po = new ParsedOption(optID, null);
            } else {
                po = new ParsedOption(optID, v.toArray());
            }
        }

        addToList(po);
    }

    private void addToList(ParsedObject po) {
        if (lastPo == null) {
            poList = lastPo = po;
        } else {
            lastPo.setNext(po);
            lastPo = po;
        }
    }

    private void reorderList() {
        /* Reorder list so that non-options come last */
        ParsedObject nonOptList = null;
        ParsedObject lastNonOpt = null;
        ParsedObject tmp = null;
        ParsedObject p = null;
        ParsedObject lastP = null;

        while (poList != null && poList instanceof ParsedNonOption) {
            tmp = poList.getNext();
            if (nonOptList == null) {
                nonOptList = poList;
            } else {
                lastNonOpt.setNext(poList);
            }
            lastNonOpt = poList;
            poList.setNext(null);
            poList = tmp;
        }

        p = poList;
        while (p != null) {
            tmp = p.getNext();
            if (p instanceof ParsedNonOption) {
                lastP.setNext(tmp);
                p.setNext(null);
                if (nonOptList == null) {
                    nonOptList = lastNonOpt = p;
                } else {
                    lastNonOpt.setNext(p);
                    lastNonOpt = p;
                }
            } else {
                lastP = p;
            }
            p = tmp;
        }

        if (lastP != null) {
            lastP.setNext(nonOptList);
        } else {
            poList = nonOptList;
        }
    }
    
    public void printHelp(PrintStream stream) {
        if (header != null) {
            stream.println(header);
            stream.println();
        }
        stream.println(getHelpStrings());
        if (footer != null) {
            stream.println(footer);
            stream.println();
        }
    }

    public void printHelp(PrintWriter writer) {
        if (header != null) {
            writer.println(header);
            writer.println();
        }
        writer.println(getHelpStrings());
        if (footer != null) {
            writer.println(footer);
            writer.println();
        }
    }
    public void printHelp(PrintStream stream, String nonOptions) {
        if (header != null) {
            stream.println(header);
            stream.println();
        }
        stream.println(getUsageString(nonOptions));
        stream.println();
        stream.println(getHelpStrings());
        if (footer != null) {
            stream.println(footer);
            stream.println();
        }
    }

    public void printHelp(PrintWriter writer, String nonOptions) {
        if (header != null) {
            writer.println(header);
            writer.println();
        }
        writer.println(getUsageString(nonOptions));
        writer.println();
        writer.println(getHelpStrings());
        if (footer != null) {
            writer.println(footer);
            writer.println();
        }
    }

    public Strings getHelpStrings() {
        Strings result = new Strings();
        if (displayAsTable) {
            String s;
            HelpDisplayManager helpManager = new HelpDisplayManager(47, 30);
            
            s = helpManager.getStartTableString();
            if (s != null) {
                result.add(s);
            }
            
            Iterator it = optionsToIDs.keySet().iterator();
            while (it.hasNext()) {
                Option opt = (Option) it.next();
                printOption(opt, result, helpManager);
            }

            s = helpManager.getEndTableString();
            if (s != null) {
                result.add(s);
            }
            result.add("");
        } else {
            result.add("Options:\n");
        
            Iterator it = optionsToIDs.keySet().iterator();
            while (it.hasNext()) {
                Option opt = (Option) it.next();
                printOption(opt, result);
            }
        }
        
        return result;
    }
    
    public void printUsage(PrintStream stream, String nonOptions) {
        if (header != null) {
            stream.println(header);
            stream.println();
        }
        stream.println(getUsageString(nonOptions));
        if (footer != null) {
            stream.println(footer);
            stream.println();
        }
    }

    public void printUsage(PrintWriter writer, String nonOptions) {
        if (header != null) {
            writer.println(header);
            writer.println();
        }
        writer.println(getUsageString(nonOptions));
        if (footer != null) {
            writer.println(footer);
            writer.println();
        }
    }

    public String getUsageString(String nonOptions) {
        StringBuffer result = new StringBuffer("Usage:\n    ");
        result.append(programCommand);
        if (optionsToIDs.keySet().size() > 0) {
            result.append(" [options]");
        }
        /*
        Iterator it = optionsToIDs.keySet().iterator();
        while (it.hasNext()) {
            Option opt = (Option) it.next();
            printOptionSwitchList(opt, result);
        }
        */
        
        result.append(" ");
        result.append(nonOptions);
        return result.toString();
    }

    private void printOptionSwitchList(Option opt, StringBuffer sb) {
        String[] switches = opt.getSwitches();
        Argument[] args = opt.getArguments();
        String[] argDescs = opt.getArgumentDescriptions();
        sb.append(" [");
        if (switches != null) {
            for (int i = 0; i < switches.length; i++) {
                if (i != 0) {
                    sb.append(" | ");
                }
                sb.append(switches[i]);
                if (args != null) {
                    for (int j = 0; j < args.length; j++) {
                        if (args[j].isRequired()) {
                            sb.append(" <" + argDescs[j] + ">");
                        } else {
                            sb.append(" [" + argDescs[j] + "]");
                        }
                    }
                }
            }

            sb.append("]");
        }
    }

    private void printOption(Option opt, Strings result) {
        String[] switches = opt.getSwitches();
        Argument[] args = opt.getArguments();
        String[] argDescs = opt.getArgumentDescriptions();
        if (switches != null) {
            for (int i = 0; i < switches.length; i++) {
                StringBuffer sb = new StringBuffer();
                sb.append("    " + switches[i]);
                if (args != null) {
                    for (int j = 0; j < args.length; j++) {
                        if (args[j].isRequired()) {
                            sb.append(" <" + argDescs[j] + ">");
                        } else {
                            sb.append(" [" + argDescs[j] + "]");
                        }
                    }
                }
                result.add(sb.toString());
            }

            result.add("        " + opt.getDescription());
        }
    }

    private void printOption(Option opt, Strings result, HelpDisplayManager helpManager) {
        String[] switches = opt.getSwitches();
        Argument[] args = opt.getArguments();
        String[] argDescs = opt.getArgumentDescriptions();
        if (switches != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < switches.length; i++) {
                sb.append(switches[i]);
                if (args != null) {
                    for (int j = 0; j < args.length; j++) {
                        if (args[j].isRequired()) {
                            sb.append(" <" + argDescs[j] + ">");
                        } else {
                            sb.append(" [" + argDescs[j] + "]");
                        }
                    }
                }
                if (i != switches.length - 1) {
                    sb.append(", ");
                }
            }
            result.add(helpManager.getOptionHelpStrings(sb.toString(), opt.getDescription()));
        }
    }
}
