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

import java.util.*;

public class TextWidthFormatter {
    public static final int DEFAULT_WIDTH = 80;
    /* FIXME: support tabs */
    public static final String DEFAULT_DELIMS = " \n\r";
    public static final boolean EXCEPTION_ON_OVERFLOW = false;
    
    private int maxWidth;
    private String delims;
    private String indentString;
    private boolean exceptionOnOverflow;
    
    public TextWidthFormatter() {
        this(DEFAULT_WIDTH, DEFAULT_DELIMS, EXCEPTION_ON_OVERFLOW, null);
    }
    
    public TextWidthFormatter(int maxWidth) {
        this(maxWidth, DEFAULT_DELIMS, EXCEPTION_ON_OVERFLOW, null);
    }
    
    public TextWidthFormatter(int maxWidth, String delims) {
        this(maxWidth, delims, EXCEPTION_ON_OVERFLOW, null);
    }

    public TextWidthFormatter(int maxWidth, String delims, boolean exceptionOnOverflow) {
        this(maxWidth, delims, exceptionOnOverflow, null);
    }
    
    public TextWidthFormatter(int maxWidth, String delims, boolean exceptionOnOverflow, String indentString) {
        this.maxWidth = maxWidth;
        this.delims = delims;
        this.exceptionOnOverflow = exceptionOnOverflow;
        this.indentString = indentString;
    }

    public Strings format(String s) {
        Strings result = new Strings();
        if (s == null) {
            return result;
        }

        StringTokenizer st = new StringTokenizer(s, delims, true);
        /* Handle the special case where the string is empty */
        if (!st.hasMoreTokens()) {
            return result;
        }
        
        StringBuffer currentString = new StringBuffer();
        String previewToken = null;

    mainloop:
        while (true) {
            String token;
            if (previewToken != null) {
                token = previewToken;
                previewToken = null;
            } else {
                if (!st.hasMoreTokens()) {
                    break mainloop;
                }
                token = st.nextToken();
            }

            if (currentString == null) {
                // Skip blanks after a forced line break
                while (token.equals(" ")) {
                    if (!st.hasMoreTokens()) {
                        break mainloop;
                    }
                    token = st.nextToken();
                }
                
                if (indentString == null) {
                    currentString = new StringBuffer();
                } else {
                    currentString = new StringBuffer(indentString);
                }
            }

            
            int len = token.length();
            int sbLen = currentString.length();

            if (len > maxWidth) {
                if (exceptionOnOverflow) {
                    throw new TextWidthOverflowException("Token \"" + token + "\" has more than " + maxWidth + " characters");
                } else {
                    result.add(currentString.toString());
                    result.add(token);
                    currentString = null;
                    continue mainloop;
                }
            }

            if (token.equals("\r")) {
                /* Check for the MS Windows encoding "\r\n", which counts
                 * here as a single new line character */
                if (st.hasMoreTokens()) {
                    previewToken = st.nextToken();
                    if (previewToken.equals("\n")) {
                        previewToken = null;
                    }
                }
                result.add(currentString.toString());
                currentString = new StringBuffer();
            } else if (token.equals("\n")) {
                result.add(currentString.toString());
                currentString = new StringBuffer();
            } else if (len + sbLen <= maxWidth) {
                currentString.append(token);
            } else {
                /* Force a line break */
                result.add(currentString.toString());
                previewToken = token;
                currentString = null;
            }
        }
        
        if (currentString != null) {
            result.add(currentString.toString());
        }
        return result;
    }
}

