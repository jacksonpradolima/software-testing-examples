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

package adaptj_pool.debug;

import adaptj_pool.*;
import adaptj_pool.util.*;
import adaptj_pool.util.OptionParser.*;

import java.io.*;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

public class ClassLocator implements Runnable {
    public static final int PROCESSING_FAILED     = 0;
    public static final int PROCESSING_SUCCESSFUL = 1;

    public static final int CLASS_LOCATOR_OPTION_HELP          = 0;
    public static final int CLASS_LOCATOR_OPTION_SHOWVER       = 1;
    public static final int CLASS_LOCATOR_OPTION_VERSION       = 2;
    public static final int CLASS_LOCATOR_OPTION_CP            = 3;
    public static final int CLASS_LOCATOR_OPTION_SHOW_CONTENTS = 4;
    public static final int CLASS_LOCATOR_OPTION_SHOW_CODE     = 5;

    private String commandLineArgs[];
    private OptionParser optionParser;

    public void printVersion() {
        System.out.println(Main.ADAPTJ_HELP_HEADER);
        System.out.println();
        System.out.println(Main.ADAPTJ_HELP_FOOTER);
    }

    public void printUsage() {
        optionParser.printUsage(System.out, "<class_name>");
    }
    
    public static void main(String args[]) throws Exception {
        ClassLocator c = new ClassLocator(args);
        (new Thread(c)).start();
    }

    public ClassLocator(String args[]) {
        commandLineArgs = new String[args.length];
        System.arraycopy(args, 0, commandLineArgs, 0, args.length);

        optionParser = new OptionParser("java adaptj_pool.debug.ClassLocator", Main.ADAPTJ_HELP_HEADER, Main.ADAPTJ_HELP_FOOTER);
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
        optionParser.addOption(helpOption, CLASS_LOCATOR_OPTION_HELP);

        BasicOption showverOption = new BasicOption("Prints the version and continues");
        showverOption.addLongSwitch("showver");
        optionParser.addOption(showverOption, CLASS_LOCATOR_OPTION_SHOWVER);

        BasicOption versionOption = new BasicOption("Prints the version and exits");
        versionOption.addLongSwitch("version");
        optionParser.addOption(versionOption, CLASS_LOCATOR_OPTION_VERSION);

        BasicOption classpathOption = new BasicOption("Manipulates the classpath used by AdaptJ");
        classpathOption.addShortSwitch("cp");
        classpathOption.addLongSwitch("classpath");
        classpathOption.addArgument(strArg, "operation");
        classpathOption.addArgument(strArg, "paths");
        optionParser.addOption(classpathOption, CLASS_LOCATOR_OPTION_CP);

        BasicOption showContentsOption = new BasicOption("Displays the contents of a given class using BCEL");
        showContentsOption.addLongSwitch("show-contents");
        optionParser.addOption(showContentsOption, CLASS_LOCATOR_OPTION_SHOW_CONTENTS);

        BasicOption showCodeOption = new BasicOption("Displays the code of a given class using BCEL");
        showCodeOption.addShortSwitch("c");
        showCodeOption.addLongSwitch("show-code");
        optionParser.addOption(showCodeOption, CLASS_LOCATOR_OPTION_SHOW_CODE);
    }

    public void run() {
        try {
            parseCommandLineArgs();
        } catch (ProcessingDeathException e) {
            if (e.getStatus() != PROCESSING_SUCCESSFUL) {                
                Scene.v().reportError("Error while parsing the arguments: " + e.getMessage());
                printUsage();
                System.exit(1);   
            }
        }
    }

    public void parseCommandLineArgs() {
        boolean showContents = false;
        boolean showCode = false;
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
                    case CLASS_LOCATOR_OPTION_HELP:
                        optionParser.printHelp(System.out, "<trace file> <output file>");
                        throw new ProcessingDeathException(PROCESSING_SUCCESSFUL);
                    case CLASS_LOCATOR_OPTION_SHOWVER:
                        printVersion();
                        System.out.println();
                        break;
                    case CLASS_LOCATOR_OPTION_VERSION:
                        printVersion();
                        throw new ProcessingDeathException(PROCESSING_SUCCESSFUL);
                    case CLASS_LOCATOR_OPTION_CP:
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
                    case CLASS_LOCATOR_OPTION_SHOW_CONTENTS:
                        showContents = true;
                        break;
                    case CLASS_LOCATOR_OPTION_SHOW_CODE:
                        showCode = true;
                        break;
                    default:
                }

            } else if (po instanceof ParsedNonOption) {
                String lookupName = ((ParsedNonOption) po).getValue();
                String className = ClassPathExplorer.v().getClassFileName(lookupName);
                if (className != null) {
                    System.out.println("Class \"" + lookupName + "\" is declared in \"" + className + "\"");
                    if (showContents) {
                        JavaClass clazz = ClassPathExplorer.v().getJavaClass(lookupName);
                        System.out.println("==============================================\n"
                                    + "Contents:\n"
                                    + clazz
                                    + "\n==============================================");
                    }
                    if (showCode) {
                        JavaClass clazz = ClassPathExplorer.v().getJavaClass(lookupName);
                        Method[] methods = clazz.getMethods();
                        if (methods != null) {
                            System.out.println("==============================================");
                            System.out.println(" Displaying code for " + lookupName);
                            for (int i = 0; i < methods.length; i++) {
                                System.out.println("----------------------------------------------");
                                System.out.println(methods[i] + ":");
                                Code c = methods[i].getCode();
                                if (c != null) {
                                    System.out.println(new InstructionList(c.getCode()).toString(false));
                                }
                            }
                            System.out.println("==============================================");
                        }
                    }
                } else {
                    System.out.println("Class \"" + lookupName + "\" was not found");
                }
            }
            po = po.getNext();
        }
    }
}
