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

package adaptj_pool.util.xml;

import java.io.*;

public class XMLPrintStream extends PrintStream {
    public static final int DEFAULT_INDENT_STEP = 4;
    private int indentStep;
    private String indentString = "";
    private int level = 0;
    private boolean needIndent = true;

    public XMLPrintStream(OutputStream out) {
        super(out);
        indentStep = DEFAULT_INDENT_STEP;
    }

    public XMLPrintStream(OutputStream out, int indentationStep) {
        super(out);
        indentStep = indentationStep;
    }


    public XMLPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
        indentStep = DEFAULT_INDENT_STEP;
    }
    

    public XMLPrintStream(OutputStream out, boolean autoFlush, int indentationStep) {
        super(out, autoFlush);
        indentStep = indentationStep;
    }

    public static String encodeOffendingChars(String s) {
        StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            switch (c) {
                case '<':
                    sb.replace(i, i + 1, "&lt;");
                    break;
                case '>':
                    sb.replace(i, i + 1, "&gt;");
                    break;
                case '\"':
                    sb.replace(i, i + 1, "&quot;");
                    break;
                case '\'':
                    sb.replace(i, i + 1, "&apos;");
                    break;
                case '&':
                    sb.replace(i, i + 1, "&amp;");
                    break;
                default:
                    break;
            }
        }

        return sb.toString();
    }
    
    private String getIndentString() {
        if (level <= 0) {
            return "";
        } else {
            int blanks = level * indentStep;
            char c[] = new char[blanks];
            for (int i = 0; i < blanks; i++) {
                c[i] = ' ';
            }

            return new String(c);
        }
    }
    
    private void indent() {
        if (needIndent) {
            super.print(indentString);
            needIndent = false;
        }
    }

    public void nextLevel() {
        level++;
        indentString = getIndentString();
    }

    public void prevLevel() {
        level--;
        indentString = getIndentString();
    }
    
    public void openTag(String tag) {
        print("<" + tag + ">");
    }

    public void openTag(String tag, String[] argNames, String[] argValues) {
        if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        }

        print("<" + tag);
        for (int i = 0; i < argNames.length; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">");
    }

    public void closeTag(String tag) {
        print("</" + tag + ">");
    }
    
    public void openTagLn(String tag) {
        println("<" + tag + ">");
        nextLevel();
    }

    public void openTagLn(String tag, String[] argNames, String[] argValues) {
        if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        }

        print("<" + tag);
        for (int i = 0; i < argNames.length; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">");
        nextLevel();
    }

    public void closeTagLn(String tag) {
        prevLevel();
        println("</" + tag + ">");
    }

    public void printXMLDeclTag(String tag) {
        print("<?" + tag + "?>");
    }

    public void printXMLDeclTagLn(String tag) {
        println("<?" + tag + "?>");
    }
    
    public void printXMLDeclTag(String tag, String[] argNames, String[] argValues) {
        if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        }
        print("<?" + tag);
        for (int i = 0; i < argNames.length; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");
        }
        print("?>");
    }
    
    public void printXMLDeclTagLn(String tag, String[] argNames, String[] argValues) {
        if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        }
        print("<?" + tag);
        for (int i = 0; i < argNames.length; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");
        }
        println("?>");
    }
    
    
    public void printTag(String tag) {
        print("<" + tag + "/>");
    }

    public void printTagLn(String tag) {
        println("<" + tag + "/>");
    }
    
    public void printTaggedValue(String tag, boolean x) {
        print("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, char x) {
        print("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, char[] x) {
        print("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, double x) {
        print("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, float x) {
        print("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, int x) {
        print("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, long x) {
        print("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, Object x) {
        print("<" + tag + ">" + encodeOffendingChars(x.toString()) + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, String x) {
        print("<" + tag + ">" + encodeOffendingChars(x) + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, boolean x) {
        println("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, char x) {
        println("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, char[] x) {
        println("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, double x) {
        println("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, float x) {
        println("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, int x) {
        println("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, long x) {
        println("<" + tag + ">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, Object x) {
        println("<" + tag + ">" + encodeOffendingChars(x.toString()) + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String x) {
        println("<" + tag + ">" + encodeOffendingChars(x) + "</" + tag + ">");
    }

    public void printTaggedValue(String tag, String[] argNames, String[] argValues, boolean x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, String[] argNames, String[] argValues, char x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, String[] argNames, String[] argValues, char[] x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, String[] argNames, String[] argValues, double x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, String[] argNames, String[] argValues, float x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, String[] argNames, String[] argValues, int x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, String[] argNames, String[] argValues, long x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, String[] argNames, String[] argValues, Object x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">" + encodeOffendingChars(x.toString()) + "</" + tag + ">");
    }
    
    public void printTaggedValue(String tag, String[] argNames, String[] argValues, String x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        print(">" + encodeOffendingChars(x) + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String[] argNames, String[] argValues, boolean x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String[] argNames, String[] argValues, char x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String[] argNames, String[] argValues, char[] x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String[] argNames, String[] argValues, double x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String[] argNames, String[] argValues, float x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String[] argNames, String[] argValues, int x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String[] argNames, String[] argValues, long x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">" + x + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String[] argNames, String[] argValues, Object x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">" + encodeOffendingChars(x.toString()) + "</" + tag + ">");
    }
    
    public void printTaggedValueLn(String tag, String[] argNames, String[] argValues, String x) {
        int numArgs;
        if (argNames == null && argValues == null) {
            numArgs = 0;
        } else if (argNames.length != argValues.length) {
            throw new RuntimeException("Names and Values do not have the same length");
        } else {
            numArgs = argNames.length;
        }
        print("<" + tag);
        for (int i = 0; i < numArgs; i++) {
            print(" " + argNames[i] + "=\"" + encodeOffendingChars(argValues[i]) + "\"");    
        }
        println(">" + encodeOffendingChars(x) + "</" + tag + ">");
    }
    
    /* Standard PrintStream methods (with added indentation support) */

    public void print(boolean b) {
        indent();
        super.print(b);
    }

    public void print(char c) {
        indent();
        super.print(c);
    }

    public void print(char[] s) {
        indent();
        super.print(s);
    }
  
    public void print(double d) {
        indent();
        super.print(d);
    }

    public void print(float f) {
        indent();
        super.print(f);
    }

    public void print(int i) {
        indent();
        super.print(i);
    }

    public void print(long l) {
        indent();
        super.print(l);
    }

    public void print(Object obj) {
        print(String.valueOf(obj));
        
    }
   
    public void print(String s) {
        indent();
        super.print(s);
        if (s.endsWith("\n")) {
            needIndent = true;
        }
    }


    public void println() {
        super.println();
        needIndent = true;
    }


    public void println(boolean x) {
        indent();
        super.println(x);
        needIndent = true;
    }

    public void println(char x) {
        indent();
        super.println(x);
        needIndent = true;
    }

    public void println(char[] x) {
        indent();
        super.println(x);
        needIndent = true;
    }

    public void println(double x) {
        indent();
        super.println(x);
        needIndent = true;
    }

    public void println(float x) {
        indent();
        super.println(x);
        needIndent = true;
    }
    
    public void println(int x) {
        indent();
        super.println(x);
        needIndent = true;
    }

    public void println(long x) {
        indent();
        super.println(x);
        needIndent = true;
    }

    public void println(Object x) {
        indent();
        super.println(x);
        needIndent = true;
    }

    public void println(String x) {
        indent();
        super.println(x);
        needIndent = true;
    }
}
