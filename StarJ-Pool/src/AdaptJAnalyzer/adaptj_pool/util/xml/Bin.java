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

import java.util.*;

public class Bin {
    private List bins;
    
    public Bin() {
        bins = new LinkedList();
    }

    public void clear() {
        bins = new LinkedList();
    }

    public void addRange(int bound, String value) {
        addRange(bound, bound, value);
    }

    public void addRange(int from, int to, String value) {
        BinRange r = new BinRange(from, to, value);

        Iterator it = bins.iterator();
        while (it.hasNext()) {
            BinRange range = (BinRange) it.next();

            if (range.equals(r)) {
                range.setValue(value);
                return;
            }
        }

        bins.add(r);
    }

    public void toXML(XMLPrintStream xmlStream) {
        xmlStream.openTagLn("bin");
        Iterator it = bins.iterator();
        while (it.hasNext()) {
            ((BinRange) it.next()).toXML(xmlStream);
        }
        xmlStream.closeTagLn("bin");
    }

    class BinRange {
        private int start;
        private int end;
        private String value;
        
        BinRange(int bound, String value) {
            this(bound, bound, value);
        }

        BinRange(int start, int end, String value) {
            this.start = start;
            this.end = end;
            this.value = value;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public String getValue() {
            return value;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean equals(Object obj) {
            if (obj instanceof BinRange) {
                BinRange range = (BinRange) obj;

                return (range.start == this.start)
                        && (range.end == this.end)
                        && (range.value == this.value
                            || (this.value != null
                            && this.value.equals(range.value)));
            }

            return false;
        }

        public boolean overlaps(BinRange r) {
            return (r.start >= this.start && r.start <= this.end)
                    || (this.start >= r.start && this.start <= r.end);
        }

        public void toXML(XMLPrintStream xmlStream) {
            String[] argNames = new String[2];
            String[] argValues = new String[2];
            argNames[0] = "from";
            argNames[1] = "to";
            argValues[0] = getBinBound(start);
            argValues[1] = getBinBound(end);
            xmlStream.printTaggedValueLn("range", argNames, argValues, value);
        }

        public String getBinBound(int bound) {
            if (bound == Integer.MAX_VALUE) {
                return "+Inf";
            } else if (bound == Integer.MIN_VALUE) {
                return "-Inf";
            }

            return String.valueOf(bound);
        }
    }
}

