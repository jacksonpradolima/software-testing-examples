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

public class PackOptionArgument implements Argument {
    private boolean required;
    private static final int STATE_EXPECTING_NAME  = 0;
    private static final int STATE_EXPECTING_COLON = 1;
    private static final int STATE_EXPECTING_VALUE = 2;
    private static final int STATE_EXPECTING_COMMA = 3;
    
    public PackOptionArgument(boolean required) {
        this.required = required;
    }

    public Object[] parse(ArgumentQueue queue) throws OptionProcessingException {
        String val;
        if (queue == null || (val = queue.pop()) == null) {
            if (required) {
                throw new OptionProcessingException("Missing required argument");
            }
            return null;
        }

        StringTokenizer st = new StringTokenizer(val, ":,", true);

        Vector v = new Vector();
        int state = STATE_EXPECTING_NAME;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (s.equals(":")) {
                if (state != STATE_EXPECTING_COLON) {
                    throw new OptionProcessingException("Option format error (1)");
                }
                state = STATE_EXPECTING_VALUE;
            } else if (s.equals(",")) {
                switch (state) {
                    case STATE_EXPECTING_COLON:
                        v.add("true");
                        state = STATE_EXPECTING_NAME;
                        break;
                    case STATE_EXPECTING_COMMA:
                        state = STATE_EXPECTING_NAME;
                        break;
                    default:
                        throw new OptionProcessingException("Option format error (2)");
                }
            } else {
                switch (state) {
                    case STATE_EXPECTING_NAME:
                        v.add(s);
                        state = STATE_EXPECTING_COLON;
                        break;
                    case STATE_EXPECTING_COLON:
                        throw new OptionProcessingException("Expected ':'");
                    case STATE_EXPECTING_VALUE:
                        v.add(s);
                        state = STATE_EXPECTING_COMMA;
                        break;
                    case STATE_EXPECTING_COMMA:
                        throw new OptionProcessingException("Expected ','");
                    default:
                        throw new RuntimeException("Illegal Parser State");
                }
            }
        }

        if (state == STATE_EXPECTING_COLON) {
            v.add("true");
            state = STATE_EXPECTING_COMMA;
        }

        if (v.size() < 2 || state != STATE_EXPECTING_COMMA) {
            throw new OptionProcessingException("Invalid option format (3)");
        }

        /*
        int sep = val.indexOf(':');
         
        Object[] result = new Object[2];
        if (sep >= 0) {
            result[0] = val.substring(0, sep);
            result[1] = val.substring(sep + 1);
        } else {
            result[0] = val;
            result[1] = "true";
        }
        */

        return v.toArray();
    }

    public boolean isRequired() {
        return required;
    }
}
