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

package adaptj_pool.util.text;

import java.io.*;

public class HelpDisplayManager {
    public static final int LEFT_WIDTH = 25;
    public static final int RIGHT_WIDTH = 52;

    private PrintStream out;
    private ColumnTextFormatter ctformat;
    private String headerSep;
    private int leftWidth;
    private int rightWidth;
    
    public HelpDisplayManager() {
        this(LEFT_WIDTH, RIGHT_WIDTH, null);
    }

    public HelpDisplayManager(int leftWidth, int rightWidth) {
        this(leftWidth, rightWidth, null);
    }

    public HelpDisplayManager(PrintStream out) {
        this(LEFT_WIDTH, RIGHT_WIDTH, out);
    }
    
    public HelpDisplayManager(int leftWidth, int rightWidth, PrintStream out) {
        this.out = out;
        this.leftWidth= leftWidth;
        this.rightWidth = rightWidth;
        this.ctformat = new ColumnTextFormatter(leftWidth, rightWidth);
    }

    public void displayHeader(String name, String description) {
        out.println("Pack/Operation help for \"" + name + "\"");
        out.println();
        out.println((new TextWidthFormatter(leftWidth + rightWidth + 3)).format("Description: "
                + (description != null ? description : "N/A")));
        out.println();
    }

    public void startTable() {
        /*
        String leftHeader = getStringOf('-', LEFT_WIDTH);
        String rightHeader = getStringOf('-', RIGHT_WIDTH);
        
        out.println(ctformat.format("Option", "Description"));
        out.println(ctformat.format(leftHeader, rightHeader));
        */
        String s = getStartTableString();
        if (s != null) {
            out.println(s);
        }
    }

    public String getStartTableString() {
        String leftHeader = getStringOf('-', leftWidth);
        String rightHeader = getStringOf('-', rightWidth);
        
        return ctformat.format("Option", "Description")
                + "\n"
                + ctformat.format(leftHeader, rightHeader);
    }

    public void displayOptionHelp(String option, String description) {
        Strings s = getOptionHelpStrings(option, description);
        if (s != null) {
            out.println(s);
        }
    }

    public Strings getOptionHelpStrings(String option, String description) {
        return ctformat.format(option, description);
    }

    public void endTable() {
        String s = getEndTableString();
        if (s != null) {
            out.println(s);
        }
    }

    public String getEndTableString() {
        return null;
    }

    public static String getStringOf(char c, int width) {
        if (width < 0) {
            return null;
        }
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < width; i++) {
            sb.append(c);
        }

        return sb.toString();
    }
}
