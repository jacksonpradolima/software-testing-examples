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

package adaptj_pool;

import java.io.*;
import adaptj_pool.util.*;
import adaptj_pool.util.text.HelpDisplayManager;
import adaptj_pool.util.OptionParser.*;
import adaptj_pool.toolkits.*;
import adaptj_pool.toolkits.printers.*;
import adaptj_pool.toolkits.transformers.*;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.toolkits.analyses.metrics.*;

public class Main implements Runnable {
    private String fileName = null;
    
    private String commandLineArgs[];
    private OptionParser optionParser;
    public static final String ADAPTJ_VERSION = "0.1 Beta 7 (RC3) with Aspect Support";
    public static final int PROCESSING_FAILED     = 0;
    public static final int PROCESSING_SUCCESSFUL = 1;
    
    public static final String ADAPTJ_HELP_HEADER = "AdaptJ version " + ADAPTJ_VERSION + "\n\n"
                                                   + "Written by Bruno Dufour\n"
                                                   + "Contact: bruno.dufour@mail.mcgill.ca\n"
                                                   + "AdaptJ homepage: http://www.sable.mcgill.ca/~bdufou1/AdaptJ/";
    public static final String ADAPTJ_HELP_FOOTER = "Copyright (C) 2002 Sable Research Group\n"
                                                   + "(http://www.sable.mcgill.ca/)\n\n"
                                                   + "AdaptJ comes with ABSOLUTELY NO WARRANTY.  AdaptJ is free software,\n"
                                                   + "and you are welcome to redistribute it under certain conditions. \n"
                                                   + "See the accompanying file 'license.html' for details.";

    public static final int ADAPTJ_OPTION_HELP          =  0;
    public static final int ADAPTJ_OPTION_CP            =  1;
    public static final int ADAPTJ_OPTION_PACK_OPT      =  2;
    public static final int ADAPTJ_OPTION_OPTFILE       =  3;
    public static final int ADAPTJ_OPTION_QUIET         =  4;
    public static final int ADAPTJ_OPTION_SHOWVER       =  5;
    public static final int ADAPTJ_OPTION_VERSION       =  6;
    public static final int ADAPTJ_OPTION_TIME          =  7;
    public static final int ADAPTJ_OPTION_VERBOSE       =  8;
    public static final int ADAPTJ_OPTION_SHOW_PROGRESS =  9;
    public static final int ADAPTJ_OPTION_PIPE          = 10;
    public static final int ADAPTJ_OPTION_ALL_OFF       = 11;
    public static final int ADAPTJ_OPTION_HIERARCHY     = 12;
    public static final int ADAPTJ_OPTION_REFRESH       = 13;
    
    public Main(String args[]) {
        commandLineArgs = new String[args.length];
        System.arraycopy(args, 0, commandLineArgs, 0, args.length);

        optionParser = new OptionParser("java adaptj_pool.Main", ADAPTJ_HELP_HEADER, ADAPTJ_HELP_FOOTER);
        initOptionParser();
    }

    private void initOptionParser() {
        optionParser.combineShortSwitches(true);
        optionParser.aggregateNonOptions(true);
        optionParser.shortOptionNoSpace(true);

        StringArgument strArg = new StringArgument(true);
    
        BasicOption helpOption = new BasicOption("Prints help and exits");
        helpOption.addShortSwitch("h");
        helpOption.addLongSwitch("help");
        helpOption.addArgument(new StringArgument(false), "package/option");
        optionParser.addOption(helpOption, ADAPTJ_OPTION_HELP);

        BasicOption classpathOption = new BasicOption("Manipulates the classpath used by AdaptJ");
        classpathOption.addShortSwitch("cp");
        classpathOption.addLongSwitch("classpath");
        classpathOption.addArgument(strArg, "operation");
        classpathOption.addArgument(strArg, "paths");
        optionParser.addOption(classpathOption, ADAPTJ_OPTION_CP);

        BasicOption packoptOption = new BasicOption("Sets option for a pack/operation");
        packoptOption.addShortSwitch("p");
        packoptOption.addArgument(strArg, "pack/operation");
        packoptOption.addArgument(new PackOptionArgument(true), "option[:value]");
        optionParser.addOption(packoptOption, ADAPTJ_OPTION_PACK_OPT);

        BasicOption optfileOption = new BasicOption("Reads arguments from a file (Other arguments can be given)");
        optfileOption.addShortSwitch("f");
        optfileOption.addLongSwitch("file");
        optfileOption.addArgument(new OptionFileArgument(true), "file");
        optionParser.addOption(optfileOption, ADAPTJ_OPTION_OPTFILE);

        BasicOption quietOption = new BasicOption("Sets the quiet mode:\n" 
                                + "  Any combination of:\n"
                                + "    m or M: Hide messages\n"
                                + "    w or W: Hide warnings\n"
                                + "    e or E: Hide errors\n"
                                + "    0: Show everything\n"
                                + "    1: same as M\n"
                                + "    2: same as MW\n"
                                + "    3: same as MWE)");
        quietOption.addShortSwitch("q");
        quietOption.addLongSwitch("quiet");
        quietOption.addArgument(strArg, "quiet mode");
        optionParser.addOption(quietOption, ADAPTJ_OPTION_QUIET);

        BasicOption showverOption = new BasicOption("Prints the version and continues");
        showverOption.addLongSwitch("showver");
        optionParser.addOption(showverOption, ADAPTJ_OPTION_SHOWVER);

        BasicOption versionOption = new BasicOption("Prints the version and exits");
        versionOption.addLongSwitch("version");
        optionParser.addOption(versionOption, ADAPTJ_OPTION_VERSION);

        BasicOption timeOption = new BasicOption("Prints time statistics");
        timeOption.addShortSwitch("t");
        timeOption.addLongSwitch("time");
        optionParser.addOption(timeOption, ADAPTJ_OPTION_TIME);

        BasicOption verboseOption = new BasicOption("Enables verbose mode");
        verboseOption.addShortSwitch("v");
        verboseOption.addLongSwitch("verbose");
        optionParser.addOption(verboseOption, ADAPTJ_OPTION_VERBOSE);

        BasicOption showProgressOption = new BasicOption("Enables the graphical progress indicator");
        showProgressOption.addLongSwitch("show-progress");
        optionParser.addOption(showProgressOption, ADAPTJ_OPTION_SHOW_PROGRESS);
        
        BasicOption pipeOption = new BasicOption("Forces to read input as if from a pipe (no preparsing)");
        pipeOption.addLongSwitch("pipe");
        optionParser.addOption(pipeOption, ADAPTJ_OPTION_PIPE);

        BasicOption allOffOption = new BasicOption("Turns off all operations");
        allOffOption.addShortSwitch("x");
        allOffOption.addLongSwitch("all-off");
        optionParser.addOption(allOffOption, ADAPTJ_OPTION_ALL_OFF);

        BasicOption hierarchyOption = new BasicOption("Displays the package/operation hierarchy");
        hierarchyOption.addLongSwitch("show-hierarchy");
        optionParser.addOption(hierarchyOption, ADAPTJ_OPTION_HIERARCHY);
        
        BasicOption refreshOption = new BasicOption("Sets the amount of time between GUI updates (in msec)");
        refreshOption.addShortSwitch("r");
        refreshOption.addLongSwitch("refresh-rate");
        refreshOption.addArgument(new IntArgument(true), "rate");
        optionParser.addOption(refreshOption, ADAPTJ_OPTION_REFRESH);
    }
    
    public static void main(String args[]) throws Exception {
        Main m = new Main(args);
        (new Thread(m)).start();
    }

    public void run() {
        //addOperations();
        try {
            parseCommandLineArgs();
        } catch (ProcessingDeathException e) {
            if (e.getStatus() != PROCESSING_SUCCESSFUL) {                
                Scene.v().reportError("Error while parsing the arguments: " + e.getMessage());
                printUsage();
                System.exit(1);   
            }

            System.exit(0);
        }
        

        ClassPathExplorer.v().setClassPath(Scene.v().getClassPath());
        try {
            Scene.v().loadFile(fileName);
        } catch (FileNotFoundException e) {
            Scene.v().reportFileNotFoundError(fileName);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            Scene.v().reportFileOpenError(fileName);
            System.exit(1);
        } catch (AEFFormatException e) {
            Scene.v().reportError("File \"" + fileName + "\" is not a valid AdaptJ file or is corrupted: " + e.getMessage());
            System.exit(1);
        }
        
        if (!(Scene.v().performOperations())) {
            Scene.v().reportError("Could not perform requested operations");
            System.exit(2);
        }

        System.exit(0);
    }


    /* This code has been moved to adaptj_pool.Scene */
    /*
    public void addOperations() {
        DefaultEventPrinter debugPrn = new DefaultEventPrinter("DEBUG_PRN");
        debugPrn.setEnabled(false);
        Scene.v().add(debugPrn);
        
        Scene.v().add(IDResolver.v());
        Scene.v().add(ClassNameResolver.v());
        Scene.v().add(InstructionResolver.v());
        Scene.v().add(new EventDistiller("ED"));
        
        MetricPack metricPack = new MetricPack("metrics", "Metrics", "Collection of dynamic software metrics");
        
        metricPack.add(new BaseMetric("BM"));

        // -- Laurie's Metrics --
        metricPack.add(new MemoryMetric("MM"));
        //metricPack.add(new PointerMetric("PTRM"));
        
        // -- Karel's Metrics --
        metricPack.add(new PolymorphismMetric("PM"));
        //metricPack.add(new ObjectOrientationMetric("OOM"));
        metricPack.add(new ProgramSizeMetric("PSM"));
          
        // -- Clark's Metrics --
        metricPack.add(new SynchronizationMetric("SM"));
        metricPack.add(new FloatingPointMetric("FPM"));
        //metricPack.add(new ConcurrencyMetric("CM"));
        
        // -- Bruno's Metrics --
        //metricPack.add(new ArrayMetric("AM"));
        //metricPack.add(new RecursionMetric("RM"));
        
        Scene.v().add(metricPack);
        DefaultEventPrinter prn = new DefaultEventPrinter("PRN");
        prn.setEnabled(false);
        Scene.v().add(prn);
        ExtendedEventPrinter eprn = new ExtendedEventPrinter("EPRN");
        eprn.setEnabled(false);
        Scene.v().add(eprn);
        MethodPrinter mprn = new MethodPrinter("MPRN");
        mprn.setEnabled(false);
        Scene.v().add(mprn);
    }
    */

    public void printUsage() {
        optionParser.printHelp(System.out, "<trace file>");

        /*
        System.out.println("Usage: java adaptj_pool.Main [options] <file>");
        System.out.println("");
        System.out.println("Options                                      Effect     ");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("  -h, --help                                 Print help and exit");
        System.out.println("  -cp add <paths>                            Append <paths> to the classpath");
        System.out.println("  -cp addAfter <paths>                       Append <paths> to the classpath");
        System.out.println("  -cp addBefore <paths>                      Add <paths> at the start of the");
        System.out.println("                                               classpath");
        System.out.println("  -cp remove <paths>                         Removes <paths> from the");
        System.out.println("                                               classpath");
        System.out.println("  -cp set <paths>                            Set the classpath to <paths>");
        System.out.println("  --classpath <operation> <paths>            Same as -cp <operation> <paths>");
        System.out.println("  -f <file>, --args-file <file>              Read arguments from <file>");
        System.out.println("                                               (Other arguments can be given)");
        System.out.println("  -p <pack_or_operation> <option>[:<value>]  Set option for pack/operation");
        System.out.println("  -q <mode>, --quiet <mode>                  Set quiet mode:");
        System.out.println("                                                Any combination of:");
        System.out.println("                                                  m or M: Hide messages");
        System.out.println("                                                  w or W: Hide warnings");
        System.out.println("                                                  e or E: Hide errors");
        System.out.println("                                                  0: Show everything");
        System.out.println("                                                  1: same as M");
        System.out.println("                                                  2: same as MW");
        System.out.println("                                                  3: same as MWE");
        System.out.println("  --showversion                              Print version and continue");
        System.out.println("  -t, --time                                 Print time statistics");
        System.out.println("  --version                                  Print version and exit");
        System.out.println("  -v, --verbose                              Verbose mode");
        System.out.println("  --no-gui                                   Disable progress bar");
        System.out.println("  --pipe                                     Pipe Mode (no preparsing)");
        System.out.println("  -x, --all-off                              Disables all operations");
        System.out.println("  --show-hierarchy                           Displays the pack/operation hierarchy");
        System.out.println("  -r, --refresh-rate                         Sets the amount of time between GUI");
        System.out.println("                                               updates (in msec)");
        */
    }

    public void printVersion() {
        System.out.println(ADAPTJ_HELP_HEADER);
        System.out.println();
        System.out.println(ADAPTJ_HELP_FOOTER);
    }

    
    public void parseCommandLineArgs() {
        ParsedObject po = null;
        try {
            po = optionParser.parse(commandLineArgs);
        } catch (OptionProcessingException e) {
            throw new ProcessingDeathException(PROCESSING_FAILED, e.getMessage());
        }

        while (po != null) {
            if (po instanceof ParsedOption) {
                ParsedOption option = (ParsedOption) po;
                Object[] args = null;

                switch (option.getOptionID()) {
                    case ADAPTJ_OPTION_HELP:
                        args = option.getArguments();
                        if (args != null && args.length > 0) {
                            String pack = (String) args[0];
                            ICustomizable customizable = Scene.v().getByName(pack);
                            if (customizable != null) {
                                HelpDisplayManager manager = new HelpDisplayManager(System.out);
                                manager.displayHeader(pack, customizable.getDescription());
                                manager.startTable();
                                customizable.displayHelp(manager);
                                manager.endTable();
                            } else {
                                throw new ProcessingDeathException(PROCESSING_FAILED, pack + " not found");
                            }
                        } else {
                            optionParser.printHelp(System.out, "<trace file>");
                        }
                        throw new ProcessingDeathException(PROCESSING_SUCCESSFUL);
                    case ADAPTJ_OPTION_CP:
                        args = option.getArguments();
                        String op = (String) args[0];
                        String paths = (String) args[1];
                        boolean result;
                        
                        if (op.equals("add") || op.equals("addAfter")) {
                            result = Scene.v().addToClassPathAfter(paths);
                        } else if (op.equals("addBefore")) {
                            result = Scene.v().addToClassPathBefore(paths);
                        } else if (op.equals("set")) {
                            result = Scene.v().setClassPath(paths);
                        } else if (op.equals("remove")) {
                            result = Scene.v().removeFromClassPath(paths);
                        } else {
                            throw new ProcessingDeathException(PROCESSING_FAILED, "Unknown classpath operation: " + op);
                        }

                        if (!result) {
                            throw new ProcessingDeathException(PROCESSING_FAILED, "Classpath operation \"" + op + "\" failed");
                        }
                        break;
                    case ADAPTJ_OPTION_PACK_OPT:
                        args = option.getArguments();
                        String pack = (String) args[0];
                        ICustomizable customizable = Scene.v().getByName(pack);
                        if (customizable == null) {
                            throw new ProcessingDeathException(PROCESSING_FAILED, pack + " not found");
                        }
                        for (int i = 1; i < args.length; i += 2) {
                            String optName = (String) args[i];
                            String value = (String) args[i + 1];

                            customizable.setOption(optName, value);
                        }
                        break;
                    case ADAPTJ_OPTION_OPTFILE:
                        // Can safely ignore
                        break;
                    case ADAPTJ_OPTION_QUIET:
                        args = option.getArguments();
                        String modifiers = (String) args[0];
                        int mode = 0;
                    
                        for (int j = 0; j < modifiers.length(); j++) {
                            char c = modifiers.charAt(j);
                            switch (c) {
                                case 'm':
                                case 'M':
                                    mode |= Scene.HIDE_MESSAGES;
                                    break;
                                case 'w':
                                case 'W':
                                    mode |= Scene.HIDE_WARNINGS;
                                    break;
                                case 'e':
                                case 'E':
                                    mode |= Scene.HIDE_ERRORS;
                                    break;
                                case '0':
                                    mode = 0;
                                    break;
                                case '1':
                                    mode = Scene.HIDE_MESSAGES;
                                    break;
                                case '2':
                                    mode = Scene.HIDE_MESSAGES + Scene.HIDE_WARNINGS;
                                    break;
                                case '3':
                                    mode = Scene.HIDE_MESSAGES + Scene.HIDE_WARNINGS + Scene.HIDE_ERRORS;
                                    break;
                                default:
                                    throw new ProcessingDeathException(PROCESSING_FAILED, "Unknown quiet mode/modifier: " + c);
                                
                            }
                        }

                        Scene.v().setQuietMode(mode);
                        break;
                    case ADAPTJ_OPTION_SHOWVER:
                        printVersion();
                        System.out.println();
                        break;
                    case ADAPTJ_OPTION_VERSION:
                        printVersion();
                        throw new ProcessingDeathException(PROCESSING_SUCCESSFUL);
                    case ADAPTJ_OPTION_TIME:
                        Scene.v().setShowTimes(true);
                        break;
                    case ADAPTJ_OPTION_VERBOSE:
                        Scene.v().setOption("verbose", "true");
                        break;
                    case ADAPTJ_OPTION_SHOW_PROGRESS:
                        Scene.v().setShowProgress(true);
                        break;
                    case ADAPTJ_OPTION_PIPE:
                        Scene.v().setPipedMode(true);
                        break;
                    case ADAPTJ_OPTION_ALL_OFF:
                        Scene.v().disableAllOps();
                        break;
                    case ADAPTJ_OPTION_HIERARCHY:
                        Scene.v().displayHierarchy(System.out);
                        throw new ProcessingDeathException(PROCESSING_SUCCESSFUL);
                    case ADAPTJ_OPTION_REFRESH:
                        args = option.getArguments();
                        Integer iObj = (Integer) args[0];
                        Scene.v().setRefreshRate(iObj.intValue());
                        break;
                    default:
                }

            } else if (po instanceof ParsedNonOption) {
                if (fileName == null) {
                    fileName = ((ParsedNonOption) po).getValue();
                } else {
                    throw new ProcessingDeathException(PROCESSING_FAILED, "More than one file specified");
                }
            } else {
                throw new RuntimeException("Unknown parsed object type");
            }
            po = po.getNext();
        }

        if (fileName == null) {
            fileName = "AdaptJ.dat";
            //throw new ProcessingDeathException(PROCESSING_FAILED, "No file name specified");
        }
    }
}
