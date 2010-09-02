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

package adaptj_pool.toolkits.analyses.metrics;

import adaptj_pool.Scene;
import adaptj_pool.event.*;
import adaptj_pool.toolkits.*;
import adaptj_pool.util.*;
import adaptj_pool.util.xml.*;
import adaptj_pool.util.text.HelpDisplayManager;
import java.io.*;

public abstract class MetricAnalysis extends EventOperation implements XMLOutputable {
    private String xmlFileName = null;
    private String longName;

    public MetricAnalysis(String name, String longName, String description) {
        super(name, description);
        this.longName = longName;
    }

    public String getLongName() {
        return longName;
    }
    
    public void setOption(String name, String value) {
        if (name.equals("xmlfile")) {
            xmlFileName = value;
            if (value == null) {
                throw new InvalidOptionFileNameException(this, name, value);
            }
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) { 
        if (name.equals("xmlfile")) {
            return xmlFileName;
        }

        return super.getOption(name);
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);

        manager.displayOptionHelp("xmlfile:<file>", "Specifies the name of the file to output XML results to");
    }

    public void outputXML(XMLMetricPrinter xmlPrinter) {

    }

    public void computeResults() {

    }

    public void analysisDone() {

    }

    public final void doDone() {
        analysisDone();
        computeResults();
        if (xmlFileName != null && isEnabled()) {
            try {
                XMLMetricPrinter xmlPrinter = new XMLMetricPrinter(xmlFileName);
                /* The following line won't work with most browsers, so it has been replaced with
                 * the one right after it, which does seem to be supported */
                outputXML(xmlPrinter);
                xmlPrinter.close();
            } catch (IOException e) {
                Scene.v().reportWriteError(xmlFileName);
            }
        }
    }
}
