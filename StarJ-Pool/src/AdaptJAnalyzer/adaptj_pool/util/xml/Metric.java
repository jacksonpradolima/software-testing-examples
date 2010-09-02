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

public class Metric {
    private String name;
    private String description;
    
    private Value value;
    private Percentile percentile;
    private Bin bin;

    public Metric(String name) {
        this(name, null);
    }

    public Metric(String name, String description) {
        this.name = name;
        this.description = description;

        this.value = null;
        this.percentile = null;
        this.bin = null;
    }

    public String getName() {
        return name;
    }

    public String getDesctiption() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bin addBin() {
        this.bin = new Bin();
        return bin;
    }

    public Value addValue(String value) {
        this.value = new Value(value);
        return this.value;
    }

    public Percentile addPercentile(String value, double threshold) {
        this.percentile = new Percentile(value, threshold);
        return this.percentile;
    }

    public Bin getBin() {
        return this.bin;
    }

    public Percentile getPercentile() {
        return this.percentile;
    }

    public Value getValue() {
        return this.value;
    }

    public void toXML(XMLPrintStream xmlStream) {
        xmlStream.openTagLn("metric name=\"" + name + "\"");
        if (description != null) {
            xmlStream.printTaggedValueLn("description", description);
        }
        if (value != null) {
            value.toXML(xmlStream);
        }
        if (percentile != null) {
            percentile.toXML(xmlStream);
        }
        if (bin != null) {
            bin.toXML(xmlStream);
        }
        xmlStream.closeTagLn("metric");
    }

}

