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
import java.util.*;
import adaptj_pool.util.IntHashMap;

import java.text.*;

public class XMLMetricPrinter {
    private XMLPrintStream xmlStream;
    private HashMap nameToCategory;
    private List categoryList;
    private String benchName;
    private String benchDesc;

    private static DecimalFormat format = new DecimalFormat("0.000");
    public static final double DEFAULT_THRESHOLD = -1.0;

    public XMLMetricPrinter(OutputStream stream) {
        xmlStream = new XMLPrintStream(stream);
        nameToCategory = new HashMap();
        categoryList = new LinkedList();

        benchName = null;
        benchDesc = null;
    }
    
    public XMLMetricPrinter(String filename) throws FileNotFoundException {
        this(new FileOutputStream(filename));
    }
    
    public XMLMetricPrinter(File file) throws FileNotFoundException {
        this(new FileOutputStream(file));
    }

    public String getBenchmarkDescription() {
        return benchDesc;
    }

    public String getBenchmarkName() {
        return benchName;
    }

    public void setBenchmarkDescription(String description) {
        this.benchDesc = description;
    }

    public void setBenchmarkName(String name) {
        this.benchName = name;
    }

    public void addDescription(String category, String description) {
        Category cat = registerCategory(category);
        if (cat != null) {
            cat.setDescription(description);
        }
    }

    public void addDescription(String category, String metric, String description) {
        Category cat = registerCategory(category);
        if (cat != null) {
            Metric m = cat.getMetric(metric);
            if (m != null) {
                m.setDescription(description);
            }
        }
    }

    public void addValue(String category, String metric, String value) {
        Category cat = registerCategory(category);
        Metric m = cat.registerMetric(metric);
        m.addValue(value);
    }

    public void addValue(String category, String metric, byte value) {
        addValue(category, metric, String.valueOf(value));
    }

    public void addValue(String category, String metric, short value) {
        addValue(category, metric, String.valueOf(value));
    }

    public void addValue(String category, String metric, int value) {
        addValue(category, metric, String.valueOf(value));
    }
    
    public void addValue(String category, String metric, long value) {
        addValue(category, metric, String.valueOf(value));
    }

    public void addValue(String category, String metric, double value) {
        addValue(category, metric, (Double.isNaN(value) ? "N/A" : format.format(value)));
    }

    public void addPercentile(String category, String metric, String value, double threshold) {
        Category cat = registerCategory(category);
        Metric m = cat.registerMetric(metric);
        m.addPercentile(value, threshold);
    }

    public void addPercentile(String category, String metric, byte value, double threshold) {
        addPercentile(category, metric, String.valueOf(value), threshold);
    }

    public void addPercentile(String category, String metric, short value, double threshold) {
        addPercentile(category, metric, String.valueOf(value), threshold);
    }

    public void addPercentile(String category, String metric, int value, double threshold) {
        addPercentile(category, metric, String.valueOf(value), threshold);
    }

    public void addPercentile(String category, String metric, long value, double threshold) {
        addPercentile(category, metric, String.valueOf(value), threshold);
    }

    public void addPercentile(String category, String metric, double value, double threshold) {
        addPercentile(category, metric, (Double.isNaN(value) ? "N/A" : format.format(value)), threshold);
    }

    public void addPercentile(String category, String metric, String value) {
        addPercentile(category, metric, value, DEFAULT_THRESHOLD);
    }

    public void addPercentile(String category, String metric, byte value) {
        addPercentile(category, metric, String.valueOf(value), DEFAULT_THRESHOLD);
    }

    public void addPercentile(String category, String metric, short value) {
        addPercentile(category, metric, String.valueOf(value), DEFAULT_THRESHOLD);
    }

    public void addPercentile(String category, String metric, int value) {
        addPercentile(category, metric, String.valueOf(value), DEFAULT_THRESHOLD);
    }

    public void addPercentile(String category, String metric, long value) {
        addPercentile(category, metric, String.valueOf(value), DEFAULT_THRESHOLD);
    }

    public void addPercentile(String category, String metric, double value) {
        addPercentile(category, metric, (Double.isNaN(value) ? "N/A" : format.format(value)), DEFAULT_THRESHOLD);
    }

    public void addBin(String category, String metric) {
        Category cat = registerCategory(category);
        Metric m = cat.registerMetric(metric);
        m.addBin();
    }

    public void addBinRange(String category, String metric, int from, int to, String value) {
        Category cat = registerCategory(category);
        Metric m = cat.registerMetric(metric);
        Bin bin = m.getBin();
        if (bin == null) {
            // Throw an exception or add a bin??
            bin = m.addBin();
        }

        bin.addRange(from, to, value);
    }
    
    public void addBinRange(String category, String metric, int from, int to, byte value) {
        addBinRange(category, metric, from, to, String.valueOf(value));
    }

    public void addBinRange(String category, String metric, int from, int to, short value) {
        addBinRange(category, metric, from, to, String.valueOf(value));
    }

    public void addBinRange(String category, String metric, int from, int to, int value) {
        addBinRange(category, metric, from, to, String.valueOf(value));
    }

    public void addBinRange(String category, String metric, int from, int to, long value) {
        addBinRange(category, metric, from, to, String.valueOf(value));
    }

    public void addBinRange(String category, String metric, int from, int to, double value) {
        addBinRange(category, metric, from, to, (Double.isNaN(value) ? "N/A" : format.format(value)));
    }

    public void addBinRange(String category, String metric, int bound, String value) {
        addBinRange(category, metric, bound, bound, value);
    }

    public void addBinRange(String category, String metric, int bound, byte value) {
        addBinRange(category, metric, bound, bound, String.valueOf(value));
    }

    public void addBinRange(String category, String metric, int bound, short value) {
        addBinRange(category, metric, bound, bound, String.valueOf(value));
    }

    public void addBinRange(String category, String metric, int bound, int value) {
        addBinRange(category, metric, bound, bound, String.valueOf(value));
    }

    public void addBinRange(String category, String metric, int bound, long value) {
        addBinRange(category, metric, bound, bound, String.valueOf(value));
    }

    public void addBinRange(String category, String metric, int bound, double value) {
        addBinRange(category, metric, bound, bound, (Double.isNaN(value) ? "N/A" : format.format(value)));
    }

    public Category registerCategory(String category) {
        if (nameToCategory.containsKey(category)) {
            return (Category) nameToCategory.get(category);
        }

        Category result = new Category(category);
        nameToCategory.put(category, result);
        categoryList.add(result);
        return result;
    }

    public Category getCategory(String category) {
        if (nameToCategory.containsKey(category)) {
            return (Category) nameToCategory.get(category);
        }

        return null;
    }

    private void writeOutput() {
        xmlStream.printXMLDeclTagLn("xml version=\"1.0\" encoding=\"ISO-8859-1\"");
        /* The following line won't work with most browsers, so it has been replaced with
         * the one right after it, which does seem to be supported */
        //xmlStream.printXMLDeclTagLn("xml-stylesheet type=\"text/xsl\" href=\"http://www.sable.mcgill.ca/metrics/xsl/metrics.xsl\"");
        xmlStream.printXMLDeclTagLn("xml-stylesheet type=\"text/xsl\" href=\"metrics.xsl\"");

        xmlStream.openTagLn("benchmark");
        if (benchName != null) {
            xmlStream.printTaggedValueLn("name", benchName);
        }
        if (benchDesc != null) {
            xmlStream.printTaggedValueLn("description", benchDesc);
        }
        xmlStream.openTagLn("metrics");
        Iterator it = categoryList.iterator();
        while (it.hasNext()) {
            ((Category) it.next()).toXML(xmlStream);
        }
        xmlStream.closeTagLn("metrics");
        xmlStream.closeTagLn("benchmark");
    }

    public void close() {
        if (xmlStream != null) {
            writeOutput();
            xmlStream.close();
            xmlStream = null;
        }
        
    }

    protected void finalize() {
        close();
    }
}

