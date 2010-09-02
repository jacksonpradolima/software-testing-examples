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

import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.util.xml.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.toolkits.EventDependency;
import java.text.*;
import java.util.Iterator;

import it.unimi.dsi.fastUtil.*;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;

public class ConcurrencyMetric extends MetricAnalysis {
    private int numStartedThreads;
    private int numOverlappingThreads;
    private int maxOverlappingThreads;
    private int maxActiveThreads;
    private Int2IntOpenHashMap threadToStatus;
    
    private static final int THREAD_STATUS_UNKNOWN = 1;
    private static final int THREAD_STATUS_RUNNABLE = 1;
    private static final int THREAD_STATUS_MONITOR_WAIT = 2;
    private static final int THREAD_STATUS_CONDVAR_WAIT = 3;

    private static final int THREAD_STATUS_SUSPENDED = 0x8000;
    private static final int THREAD_STATUS_INTERRUPTED = 0x4000;


    public ConcurrencyMetric(String name) {
        super(name,
              "Concurrency Metrics",
              "Measure the level of concurrency in a program");
    }

    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE,
            AdaptJEvent.ADAPTJ_THREAD_START,
            AdaptJEvent.ADAPTJ_THREAD_END
        };

        return events;
    }
    */
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_THREAD_START,
                                AdaptJSpecConstants.ADAPTJ_FIELD_THREAD_ENV_ID),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_THREAD_END,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_NEW_STATUS)
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        return null;
    }
    
    public void doInit() {
        numStartedThreads = 0;
        numOverlappingThreads = 0;
        maxActiveThreads = 0;
        threadToStatus = new Int2IntOpenHashMap(32);
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        
        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_THREAD_START:
                threadToStatus.put(((ThreadStartEvent) event).getThreadEnvID(), ConcurrencyMetric.THREAD_STATUS_UNKNOWN);
                numStartedThreads++;
                numOverlappingThreads++;
                if (numOverlappingThreads > maxOverlappingThreads) {
                    maxOverlappingThreads = numOverlappingThreads;
                }
                break;
            case AdaptJEvent.ADAPTJ_THREAD_END:
                threadToStatus.remove(((ThreadEndEvent) event).getEnvID());
                numOverlappingThreads--;
                break;
            case AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE:
                {
                    ThreadStatusChangeEvent e = (ThreadStatusChangeEvent) event;
                    threadToStatus.put(e.getEnvID(), e.getNewStatus());
                    int numRunnable = 0;

                    Iterator it = threadToStatus.values().iterator();
                    while (it.hasNext()) {
                        Integer i = (Integer) it.next();
                        if (i.intValue() == ConcurrencyMetric.THREAD_STATUS_RUNNABLE) {
                            numRunnable++;
                        }
                    }

                    if (numRunnable > maxActiveThreads) {
                        maxActiveThreads = numRunnable;
                    }
                }
                break;
            default:
                // Should not happen
                break;
        }
    }

    public void computeResults() {        
        //totalContendedMonitorEnterSites = contendedMonitorEnterSites.size();
        //totalMonitorEnterSites  = monitorEnterSites.size();

        /*
        Iterator it = hadContention.keySet().iterator();
        while (it.hasNext()) {
            Integer key = (Integer) it.next();

            if (hadContention.get(key.intValue())) {
                totalContendedMonitorEnterSites += 1;
            }

            totalMonitorEnterSites += 1;
        }
        */
    }
    
    /*
    // KEPT FOR REFERENCE
    public void outputXMLResults(XMLPrintStream out) {
        DecimalFormat format = new DecimalFormat("0.000");
        String argNames[] = new String[2];
        String argValues[] = new String[2];

        argNames[0] = "label";
        argNames[1] = "category";

        out.openTagLn("results");
        
        argValues[0] = "Number of started threads";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(numStartedThreads));

        argValues[0] = "Maximum number of overlapping threads";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(maxOverlappingThreads));

        argValues[0] = "Maximum number of concurrent active threads";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(maxActiveThreads));

        out.closeTagLn("results");
    }
    */
}
