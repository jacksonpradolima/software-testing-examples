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
import adaptj_pool.util.*;
import adaptj_pool.util.xml.*;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.util.text.HelpDisplayManager;
import java.text.*;
import java.util.*;

import it.unimi.dsi.fastUtil.*;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;

public class SynchronizationMetric extends MetricAnalysis {
    public static final double DEFAULT_THRESHOLD = 0.90;

    private long totalMonitorEnters;
    private long totalContendedMonitorEnters;
    private long instCount;
    private Int2IntOpenHashMap currentContention;
    private Int2LongOpenHashMap contendedMonitorEnterSites;
    private Object2LongOpenHashMap monitorEnterSites;
    private int maxContention;
    private long totalMonitorEnterSites;
    private long totalContendedMonitorEnterSites;

    private long commonMonitors;
    private long commonContendedMonitors;

    private double monitorThreshold;
    private double contendedMonitorThreshold;

    public SynchronizationMetric(String name) {
        this(name, DEFAULT_THRESHOLD, DEFAULT_THRESHOLD);
    }
    
    public SynchronizationMetric(String name, double threshold) {
        this(name, threshold, threshold);
    }
    
    public SynchronizationMetric(String name, double monitorThreshold, double contendedMonitorThreshold) {
        super(name,
              "Synchronization Metrics",
              "Measure the number of synchronization operations and the level " +
              "of contention in a program");

        checkThreshold(monitorThreshold);
        checkThreshold(contendedMonitorThreshold);
        this.monitorThreshold = monitorThreshold;
        this.contendedMonitorThreshold = contendedMonitorThreshold;
    }

    public void setOption(String name, String value) {
        if (name.equals("threshold")) {
            double threshold = Double.parseDouble(value);
            checkThreshold(threshold);
            monitorThreshold = contendedMonitorThreshold = threshold;
        } else if (name.equals("monitorThreshold")) {
            double threshold = Double.parseDouble(value);
            checkThreshold(threshold);
            monitorThreshold = threshold;
        } else if (name.equals("contendedMonitorThreshold")) {
            double threshold = Double.parseDouble(value);
            checkThreshold(threshold);
            contendedMonitorThreshold = threshold;
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) { 
        if (name.equals("monitorThreshold")) {
            return "" + monitorThreshold;
        } else if (name.equals("contendedMonitorThreshold")) {
            return "" + contendedMonitorThreshold;
        }

        return super.getOption(name);
    }
    
    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);

        manager.displayOptionHelp("monitorThreshold:<float>", "Specifies the proportion of the total monitor activity that 'hot' monitors represent. "
                                                        + "Must be in the range [0.0, 1.0]");
        manager.displayOptionHelp("threshold:<float>", "Specifies the proportion of the monitor contention activity that 'hot' monitors represent. "
                                                        + "Must be in the range [0.0, 1.0]");
        manager.displayOptionHelp("threshold:<float>", "Simultaneously sets the value for both 'monitorThreshold' and 'monitorContentionThreshold' to <float>");
    }
    
    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER,
            AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED,
            AdaptJEvent.ADAPTJ_INSTRUCTION_START
        };

        return events;
    }
    */
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OBJECT),

            new EventDependency(AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OBJECT),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OFFSET)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER, Scene.INSTRUCTION_RESOLVER};
        return deps;
    }

    private void checkThreshold(double threshold) {
        if (threshold >= 0 && threshold <= 1.0) {
            return;
        }

        throw new RuntimeException("Invalid Threshold: " + threshold + ". (Must be in [0.0, 1.0])");
    }

    public void doInit() {
        totalMonitorEnters = 0L;
        totalContendedMonitorEnters = 0L;
        instCount = 0L;
        maxContention = 0;

        currentContention = new Int2IntOpenHashMap();
        contendedMonitorEnterSites = new Int2LongOpenHashMap();
        monitorEnterSites = new Object2LongOpenHashMap();
        contendedMonitorEnterSites.setDefRetValue(0);
        monitorEnterSites.setDefRetValue(0);
        currentContention.setDefRetValue(0);
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER:
                {
                    totalContendedMonitorEnters += 1;
                    int lockIndex = ((MonitorContendedEnterEvent) event).getObject();
                    int contention = currentContention.get(lockIndex) + 1;
                    if (contention > maxContention) {
                        maxContention = contention;
                    }
                    currentContention.put(lockIndex,  contention);
                    contendedMonitorEnterSites.put(lockIndex, contendedMonitorEnterSites.get(lockIndex) + 1);
                }
                break;
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED:
                {
                    int lockIndex = ((MonitorContendedEnteredEvent) event).getObject();
                    currentContention.put(lockIndex, currentContention.get(lockIndex) - 1);
                }
                break;
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                {
                    InstructionStartEvent e = (InstructionStartEvent) event;
                    instCount += 1;

                    if (e.getCode() == Constants.MONITORENTER) {
                        int method_id = e.getMethodID();
                        totalMonitorEnters += 1;
                        InstructionHandle ih = BytecodeResolver.v().getInstructionHandle(method_id, e.getOffset());
                        MethodEntity mi = IDResolver.v().getMethodEntity(method_id);
                        InstructionEntity ii = new InstructionEntity(ih, mi);
                        monitorEnterSites.put(ii, monitorEnterSites.getLong(ii) + 1);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void computeResults() {        
        totalContendedMonitorEnterSites = contendedMonitorEnterSites.size();
        totalMonitorEnterSites  = monitorEnterSites.size();
        
        { /* Monitor Enter Sites */
            List l = new ArrayList(monitorEnterSites.keySet());
            Collections.sort(l, new MonitorEnterSitesComparator());
            long cutoff = (long) Math.ceil(monitorThreshold * totalMonitorEnters);
            long currentTotal = 0L;
            commonMonitors = 0L;

            Iterator it = l.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                long val = monitorEnterSites.getLong(o);
                
                if (currentTotal < cutoff) {
                    currentTotal += val;
                    commonMonitors++;
                } else {
                    break;
                }
            }
        }
        
        { /* Contended Monitor Enter Sites */
            List l = new ArrayList(contendedMonitorEnterSites.keySet());
            Collections.sort(l, new ContendedMonitorEnterSitesComparator());
            long cutoff = (long) Math.ceil(contendedMonitorThreshold * totalContendedMonitorEnters);
            long currentTotal = 0L;
            commonContendedMonitors = 0L;

            Iterator it = l.iterator();
            while (it.hasNext()) {
                Integer o = (Integer) it.next();
                long val = contendedMonitorEnterSites.get(o.intValue());
                
                if (currentTotal < cutoff) {
                    currentTotal += val;
                    commonContendedMonitors++;
                } else {
                    break;
                }
            }
        }
    }

    public void outputXML(XMLMetricPrinter xmlPrinter) {
        double dblVal;
        
        // concurrency.lockDensity.value
        dblVal = (1000.0 * ((double) totalMonitorEnters)) / instCount;
        xmlPrinter.addValue("concurrency", "lockDensity", dblVal);

        // concurrency.lockContendedDensity.value
        dblVal = (1000.0 * ((double) totalContendedMonitorEnters)) / instCount;
        xmlPrinter.addValue("concurrency", "lockContendedDensity", dblVal);

        // concurrency.lock.percentile
        dblVal = ((double) commonMonitors) / totalMonitorEnterSites;
        xmlPrinter.addPercentile("concurrency", "lock", dblVal, monitorThreshold);

        // concurrency.lockContended.percentile
        dblVal = ((double) commonContendedMonitors) / totalContendedMonitorEnterSites;
        xmlPrinter.addPercentile("concurrency", "lockContended", dblVal, contendedMonitorThreshold);
    }
    
    /*
    public void outputXMLResults(XMLPrintStream out) {
        DecimalFormat format = new DecimalFormat("0.000");
        String argNames[] = new String[1];
        String argValues[] = new String[1];
        double dblVal;

        argNames[0] = "name";

        // concurrency.lockDensity.value
        argValues[0] = "concurrency.lockDensity.value";
        dblVal = (1000.0 * ((double) totalMonitorEnters)) / instCount;
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));

        // concurrency.lockContendedDensity.value
        argValues[0] = "concurrency.lockContendedDensity.value";
        dblVal = (1000.0 * ((double) totalContendedMonitorEnters)) / instCount;
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));

        // concurrency.lock.percentile
        argValues[0] = "concurrency.lock.percentile";
        dblVal = ((double) commonMonitors) / totalMonitorEnterSites;
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));

        // concurrency.lockContended.percentile
        argValues[0] = "concurrency.lockContended.percentile";
        dblVal = ((double) commonContendedMonitors) / totalContendedMonitorEnterSites;
        out.printTaggedValueLn("metric",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));

        /*
        // Absolute -- Number of "monitorenter" instructions
        argValues[0] = "Number of \"monitorenter\" instructions";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(totalMonitorEnters));
        
        // Absolute -- Number of contended "monitorenter" instructions
        argValues[0] = "Number of contended \"monitorenter\" instructions";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(totalContendedMonitorEnters));

        // Absolute -- Number of "monitorenter" sites
        argValues[0] = "Number of \"monitorenter\" sites";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(monitorEnterSites.size()));
        
        // Absolute -- Number of contended "monitorenter" sites
        argValues[0] = "Number of contended \"monitorenter\" sites";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(totalContendedMonitorEnterSites));

        double freq;
        // Relative -- Frequeny of "monitorenter" instructions
        freq = ((double) totalMonitorEnters) / instCount;
        argValues[0] = "Frequency of \"monitorenter\" instructions";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (freq != Double.NaN ? format.format(freq) : "N/A"));

        // Relative -- Number of contended "monitorenter" instructions
        freq = ((double) totalContendedMonitorEnters) / instCount;
        argValues[0] = "Frequency of contended \"monitorenter\" instructions";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (freq != Double.NaN ? format.format(freq) : "N/A"));

        // Dynamic -- Contended sites
        freq = ((double) totalContendedMonitorEnterSites) / totalMonitorEnterSites;
        
        argValues[0] = "Percentage of contended \"monitorenter\" sites";
        argValues[1] = "Relative";
        out.printTaggedValueLn("percent",
                               argNames,
                               argValues,
                               (freq != Double.NaN ? format.format(freq) : "N/A"));
        
        // Dynamic -- Max contention
        argValues[0] = "Maximum contention";
        argValues[1] = "Dynamic";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(maxContention));

        out.closeTagLn("results");

        * /
    }
    */

    class MonitorEnterSitesComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            long l1 = monitorEnterSites.getLong(o1);
            long l2 = monitorEnterSites.getLong(o2);

            if (l2 > l1) {
                return 1;
            }

            if (l2 == l1) {
                return 0;
            }

            return -1;
        }

        public boolean equals(Object obj) {
            return (obj instanceof MonitorEnterSitesComparator);
        }
    }

    class ContendedMonitorEnterSitesComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Integer i1 = (Integer) o1;
            Integer i2 = (Integer) o2;
            
            long l1 = contendedMonitorEnterSites.get(i1.intValue());
            long l2 = contendedMonitorEnterSites.get(i2.intValue());

            if (l2 > l1) {
                return 1;
            }

            if (l2 == l1) {
                return 0;
            }

            return -1;
        }

        public boolean equals(Object obj) {
            return (obj instanceof ContendedMonitorEnterSitesComparator);
        }
    }
}
