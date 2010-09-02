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
import adaptj_pool.toolkits.*;
import adaptj_pool.util.*;
import adaptj_pool.util.xml.*;
import adaptj_pool.util.text.HelpDisplayManager;
import java.io.*;
import java.util.Iterator;

public class MetricPack extends Pack implements XMLOutputable {
    private String family;
    private String description;
    private String xmlFileName = null;

    public MetricPack(String name, String family, String description) {
        super(name, description);
        this.family = family;
        this.description = description;
    }

    public String getFamily() {
        return family;
    }

    public String getDescription() {
        return description;
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
    
    public void doDone() {
        if (xmlFileName != null) {
            try {
                XMLMetricPrinter xmlPrinter = new XMLMetricPrinter(xmlFileName);
                outputXML(xmlPrinter);
                xmlPrinter.close();
            } catch (IOException e) {
                Scene.v().reportWriteError(xmlFileName);
            }
        }
    }

    public void outputXML(XMLMetricPrinter xmlPrinter) {
        /* Display info about this pack */
        /**
        out.openTagLn("pack");
        out.printTaggedValueLn("name", family);
        out.printTaggedValueLn("desc", description);
        **/

        /* Recursively output other packs / metrics */
        Iterator it = iterator();
        while (it.hasNext()) {
            ICustomizable customizable = (ICustomizable) it.next();

            if (customizable instanceof XMLOutputable) {
                if (customizable.isEnabled()) {
                    ((XMLOutputable) customizable).outputXML(xmlPrinter);
                }
            }
        }
        
        /* Close the open tags */
        /**
        out.closeTagLn("pack");
        **/
    }
}
