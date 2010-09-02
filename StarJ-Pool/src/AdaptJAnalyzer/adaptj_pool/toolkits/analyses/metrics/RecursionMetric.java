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
import java.text.*;

import org.apache.bcel.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.*;

public class RecursionMetric extends MetricAnalysis {
    CallstackItem callStack;
    CallstackItem csitemPool;
    int callStackDepth;
    int maxCallStackDepth;
    int maxRecursiveDepth;
    long recEntries;
    long totalEntries;
    
    public RecursionMetric(String name) {
        super(name, "Recursion Metrics", "Measure the amount of recursion in a program");
    }

    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_METHOD_ENTRY,
            AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
            AdaptJEvent.ADAPTJ_METHOD_EXIT
        };

        return events;
    }
    */
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJEvent.ADAPTJ_METHOD_ENTRY,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID),
            /*
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID),
            */
                                
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_EXIT)
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER};
        return deps;
    }

    public void doInit() {
        callStack = null;
        csitemPool = null;
        callStackDepth = 0;
        maxCallStackDepth = 0;
        maxRecursiveDepth = 0;
        recEntries = 0L;
        totalEntries = 0L;
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        CallstackItem newItem;

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                {
                    MethodEvent e = (MethodEvent) event;
                    MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());

                    totalEntries++;
                    if (csitemPool == null) {
                        newItem = new CallstackItem(me, callStack);
                    } else {
                        newItem = csitemPool;
                        csitemPool = newItem.getNext();
                        newItem.setValues(me, 0, callStack);
                    }

                    /* Is this a recursive call? */
                    if (me != null) {
                        CallstackItem tmp;
                        MethodEntity mtmp;
                        
                        for (tmp = callStack; tmp != null; tmp = tmp.getNext()) {
                            mtmp = tmp.getMethodEntity();
                            if (mtmp != null && mtmp.equals(me)) {
                                /* yes, it is */
                                recEntries++;
                                int newDepth = tmp.getRecursiveDepth() + 1;
                                newItem.setRecursiveDepth(newDepth);
                                if (newDepth > maxRecursiveDepth) {
                                    maxRecursiveDepth = newDepth;
                                }
                            }
                            break;
                        }
                    }
                    callStack = newItem;
                    callStackDepth++;
                    if (callStackDepth > maxCallStackDepth) {
                        maxCallStackDepth = callStackDepth;
                    }
                }
                break;
            case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                if (callStack != null) {
                    CallstackItem tmp = callStack.getNext();
                    callStack.setNext(csitemPool);
                    csitemPool = callStack;
                    callStack = tmp;
                    callStackDepth--;
                }
                break;
            default:
                break;
        }
    }

    /*
    public void outputXMLResults(XMLPrintStream out) {
        DecimalFormat format = new DecimalFormat("0.000");
        String argNames[] = new String[2];
        String argValues[] = new String[2];
        double dblVal;

        argNames[0] = "label";
        argNames[1] = "category";

        // Open "results" tag
        out.openTagLn("results");
        
        // Absolute -- Number of recursive method entries
        argValues[0] = "Number of recursive method entries";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(recEntries));

        // Absolute -- Max recursion depth
        argValues[0] = "Maximum recursion depth";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(maxRecursiveDepth));
                               
        // Absolute -- Max call stack depth
        argValues[0] = "Maximum call stack depth";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(maxCallStackDepth));
                               
        // Relative -- % recursive method entries
        dblVal = ((double) recEntries) / totalEntries;
        argValues[0] = "Percentage of recursive method entries";
        argValues[1] = "Relative";
        out.printTaggedValueLn("percent",
                               argNames,
                               argValues,
                               (Double.isNaN(dblVal) ? "N/A" : format.format(dblVal)));
                               
        // Close "results" tag
        out.closeTagLn("results");
    }
    */

    class CallstackItem {
        private MethodEntity mentity;
        private int recursiveDepth;
        private CallstackItem next;
        
        public CallstackItem(MethodEntity method) {
            this.mentity = method;
            this.recursiveDepth = 0;
            this.next = null;
        }
        
        public CallstackItem(MethodEntity method, int recursiveDepth) {
            this.mentity = method;
            this.recursiveDepth = recursiveDepth;
            this.next = null;
        }
        
        public CallstackItem(MethodEntity method, CallstackItem next) {
            this.mentity = method;
            this.recursiveDepth = 0;
            this.next = next;
        }
        
        public CallstackItem(MethodEntity method, int recursiveDepth, CallstackItem next) {
            this.mentity = method;
            this.recursiveDepth = recursiveDepth;
            this.next = next;
        }

        public void setValues(MethodEntity method, int recursiveDepth, CallstackItem next) {
            this.mentity = method;
            this.recursiveDepth = recursiveDepth;
            this.next = next;
        }

        public void setNext(CallstackItem next) {
            this.next = next;
        }

        public CallstackItem getNext() {
            return this.next;
        }

        public MethodEntity getMethodEntity() {
            return this.mentity;
        }
        
        public void setMethodEntity(MethodEntity method) {
            this.mentity = method;
        }

        public void setRecursiveDepth(int recursiveDepth) {
            this.recursiveDepth = recursiveDepth;
        }

        public int getRecursiveDepth() {
            return this.recursiveDepth;
        }
    }
}
