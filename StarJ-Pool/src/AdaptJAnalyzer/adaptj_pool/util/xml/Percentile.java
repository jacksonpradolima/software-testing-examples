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

import java.text.*;

public class Percentile {
    private String value;
    private double threshold;
    private static DecimalFormat format = new DecimalFormat("0.000");

    public Percentile() {
        this(null);
    }
    
    public Percentile(String value) {
        this(value, -1.0);
    }
    
    public Percentile(String value, double threshold) {
        this.value = value;
        this.threshold = threshold;
    }

    public String getValue() {
        return value;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public void toXML(XMLPrintStream xmlStream) {
        String[] argNames = null;
        String[] argValues = null;
        if (threshold >= 0.0) {
            argNames = new String[1];
            argValues = new String[1];

            argNames[0] = "threshold";
            argValues[0] = format.format(threshold);
        }
        xmlStream.printTaggedValueLn("percentile", argNames, argValues, value);
    }
}

