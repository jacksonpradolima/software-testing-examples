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

public class ColumnTextFormatter {
    public static final int DEFAULT_WIDTH = 40;
    public static final int SEPARATOR_WIDTH = 3;
    
    private int leftWidth;
    private int rightWidth;
    private boolean drawSeparator;
    
    public ColumnTextFormatter() {
        this(DEFAULT_WIDTH, DEFAULT_WIDTH, false);
    }
    
    public ColumnTextFormatter(boolean drawSeparator) {
        this(DEFAULT_WIDTH, DEFAULT_WIDTH, drawSeparator);
    }

    public ColumnTextFormatter(int columnWidth) {
        this(columnWidth, columnWidth, false);
    }
    
    public ColumnTextFormatter(int columnWidth, boolean drawSeparator) {
        this(columnWidth, columnWidth, drawSeparator);
    }

    public ColumnTextFormatter(int leftWidth, int rightWidth) {
        this(leftWidth, rightWidth, false);
    }
    
    public ColumnTextFormatter(int leftWidth, int rightWidth, boolean drawSeparator) {
        this.leftWidth = leftWidth;
        this.rightWidth = rightWidth;
        this.drawSeparator = drawSeparator;
    }

    public Strings format(String left, String right) {
        Strings result = new Strings();
        TextWidthFormatter leftFormat = new TextWidthFormatter(leftWidth);
        TextWidthFormatter rightFormat = new TextWidthFormatter(rightWidth);

        String sep = (drawSeparator ? " | " : "   ");

        Strings sl = leftFormat.format(left);
        Strings sr = rightFormat.format(right);

        while (sl.hasNext() || sr.hasNext()) {
            String l = patchStringToWidth(sl.getNext(), leftWidth);
            String r = sr.getNext();

            if (l.length() > leftWidth) {
                /* We have overflow */
                result.add(l);
                sr.pushBack();
                continue;
            }

            if (r != null) {
                result.add(l + sep + r);
            } else {
                result.add(l + sep);
            }
        }

        return result;
    }

    public static String getBlankString(int width) {
        if (width < 0) {
            return null;
        }
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < width; i++) {
            sb.append(' ');
        }

        return sb.toString();
    }

    public static String patchStringToWidth(String s, int width) {
        if (s == null) {
            return getBlankString(width);
        }

        StringBuffer sb = new StringBuffer(s);
        for (int i = sb.length(); i < width; i++) {
            sb.append(' ');
        }

        return sb.toString();
    }
}

