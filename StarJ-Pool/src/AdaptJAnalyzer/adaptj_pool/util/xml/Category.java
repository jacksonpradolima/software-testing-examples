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

public class Category {
    private String name;
    private String description;
    private HashMap nameToMetric;
    private List metricList;
    
    public Category(String name) {
        this(name, null);
    }
    
    public Category(String name, String description) {
        this.name = name;
        this.description = description;

        this.nameToMetric = new HashMap();
        this.metricList = new LinkedList();
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Metric registerMetric(String metric) {
        if (nameToMetric.containsKey(metric)) {
            return (Metric) nameToMetric.get(metric);
        }

        Metric result = new Metric(metric);
        nameToMetric.put(metric, result);
        metricList.add(result);
        return result;
    }

    public Metric getMetric(String metric) {
        if (nameToMetric.containsKey(metric)) {
            return (Metric) nameToMetric.get(metric);
        }

        return null;
    }

    public void toXML(XMLPrintStream xmlStream) {
        xmlStream.openTagLn("category name=\"" + name + "\"");
        if (description != null) {
            xmlStream.printTaggedValueLn("description", description);
        }
        Iterator it = metricList.iterator();
        while (it.hasNext()) {
            ((Metric) it.next()).toXML(xmlStream);
        }
        xmlStream.closeTagLn("category");
    }
}

