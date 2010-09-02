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

package adaptj_pool;

import adaptj_pool.Scene;
import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.toolkits.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

public class RootPack extends Pack {
    private static RootPack instance = new RootPack();
    private final static String SEC_FORMAT = "0.00";
    private final static String PERC_FORMAT = "0.00";
    private DecimalFormat secFormat;
    private DecimalFormat percFormat;
    private long totalTime = 0L;
    private AdaptJTimer timer;
    
    private RootPack() {
        super("RootPack", "Internally represents the root of the pack/operation hierarchy");
        secFormat = new DecimalFormat(SEC_FORMAT);
        percFormat = new DecimalFormat(PERC_FORMAT);
        /* no instances */
    }

    public void reset() {
        instance = new RootPack();
    }

    public static RootPack v() {
        return instance;
    }

    public void doInit() {
        timer = new AdaptJTimer("Total Time");
        timer.start();
    }

    public void doDone() {
        timer.stop();
    }
    
    public void doVerboseInit() {
        Scene.v().showMessage("Initializing packages and operations");
    }

    public void doVerboseApply(EventBox box) {
        Scene.v().showMessage("Applying operations");
    }

    public void doVerboseDone() {
        Scene.v().showMessage("Operations applied");
        Scene.v().showMessage("Finalizing packages and operations");
    }

    public void doVerboseAddOperation(EventOperation operation) {
        Scene.v().showMessage("EventOperation " + operation.getName() + " registered");
    }

    public void doVerboseAddPack(Pack pack) {
        Scene.v().showMessage("Pack " + pack.getName() + " registered");
    }

    public void showTimes(PrintStream stream) {
        stream.println("Timing information:");
        stream.println("-------------------------------------------------");
        totalTime = timer.getTime();
        stream.println("Total execution time: " + secFormat.format(totalTime / 1000.0) + "s");
        Iterator it = iterator();
        while (it.hasNext()) {
            recursiveShowTimes((ICustomizable) it.next(), "", stream);
        }
        stream.println("-------------------------------------------------\n");
    }

    private void recursiveShowTimes(ICustomizable customizable, String prefix, PrintStream stream) {
        String fullName;
        if (prefix.equals("")) {
            fullName = customizable.getName();
        } else {
            fullName = prefix + "." + customizable.getName();
        }
        if (customizable instanceof ITimedCustomizable) {
            if (((ITimedCustomizable) customizable).isTimed()) {
                long time = ((ITimedCustomizable) customizable).getTime();
                String timeString = secFormat.format(time / 1000.0);
                String percString;
                try {
                    percString = percFormat.format(time * 100.0 / totalTime);
                } catch (ArithmeticException e) {
                    percString = "N/A";
                }

                stream.println("    " + fullName + ": " + timeString + "s" + " (" + percString + "%)");
            }
        }

        if (customizable instanceof AdaptJContainer) {
            Iterator it = ((AdaptJContainer) customizable).iterator();
            while (it.hasNext()) {
                recursiveShowTimes((ICustomizable) it.next(), fullName, stream);
            }
        }
    }

    public void disableAllOps() {
        Iterator it = iterator();
        while (it.hasNext()) {
            ((ICustomizable) it.next()).setEnabled(false);
        }
    }

    public List flattenHierarchy() {
        List result = new ArrayList();
        addToList(this, result);
        return result;
    }

    private void addToList(ICustomizable customizable, List l) {
        if (customizable.isEnabled()) {
            if (customizable instanceof AdaptJContainer) {
                /* Recursively add all IEventBoxApplicables to the list*/
                Iterator it = ((AdaptJContainer) customizable).iterator();
                while (it.hasNext()) {
                    ICustomizable tmpCustomizable = (ICustomizable) it.next();
                    addToList(tmpCustomizable, l);
                }
            } else if (customizable instanceof IEventBoxApplicable) {
                l.add(customizable);
            }
        }
    }

    private String getIndentString(int level) {
        StringBuffer sb = new StringBuffer();
        sb.ensureCapacity(level);
        for (int i = 0; i < level; i++) {
            sb.append(' ');
        }

        return sb.toString();
    }

    public void displayHierarchy(PrintStream out) {
        displayHierarchy(out, this, 0);
        /*
        Iterator it = iterator();
        
        while (it.hasNext()) {
            ICustomizable c = (ICustomizable) it.next();

            displayHierarchy(out, c, 0);
        }
        */
    }

    public void displayHierarchy(PrintStream out, ICustomizable c, int level) {
        String s;
        if (level == 0) {
            s = "";
        } else {
            s = getIndentString(level - 3) + " \\_ ";
        }
        out.println(s + c.getName());
        if (c instanceof AdaptJContainer) {
            int newLevel = level + 3;
            Iterator it = ((AdaptJContainer) c).iterator();
            while (it.hasNext()) {
                displayHierarchy(out, (ICustomizable) it.next(), newLevel);
            }
        }
    }
}
