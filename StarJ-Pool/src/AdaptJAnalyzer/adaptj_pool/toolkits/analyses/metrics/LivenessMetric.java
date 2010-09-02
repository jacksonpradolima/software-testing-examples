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
import adaptj_pool.*;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.spec.AdaptJSpecConstants;

import java.text.*;

import it.unimi.dsi.fastUtil.*;

public class LivenessMetric extends MetricAnalysis {
    private static final int LIFETIME_BIN_SIZE = 8;
    private Int2LongOpenHashMap objectToBirth;
    private long currentInterval;
    private int totalObjects;
    int[] lifetimeBin;

    public LivenessMetric(String name) {
        super(name, "Liveness Metrics", "Measure the liveness of allocated objects");
    }

    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
            AdaptJEvent.ADAPTJ_OBJECT_MOVE,
            AdaptJEvent.ADAPTJ_OBJECT_FREE,
            AdaptJEvent.ADAPTJ_GC_START,
            AdaptJEvent.ADAPTJ_GC_FINISH
        };

        return events;
    }
    */
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OBJ_ID),

            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_FREE,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OBJ_ID),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_MOVE,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OBJ_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_NEW_OBJ_ID),

            new EventDependency(AdaptJEvent.ADAPTJ_GC_START),
            
            new EventDependency(AdaptJEvent.ADAPTJ_GC_FINISH)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        return null;
    }

    private void addToBin(long lifetime) {
        if (lifetime == 0L) {
            lifetimeBin[0] += 1;
        } else if (lifetime == 1L) {
            lifetimeBin[1] += 1;
        } else if (lifetime == 2L) {
            lifetimeBin[2] += 1;
        } else if (lifetime == 3L) {
            lifetimeBin[3] += 1;
        } else if (lifetime <= 7L) {
            lifetimeBin[4] += 1;
        } else if (lifetime <= 15L) {
            lifetimeBin[5] += 1;
        } else if (lifetime <= 31L) {
            lifetimeBin[6] += 1;
        } else {
            lifetimeBin[7] += 1;
        }
    }

    private String getBinRange(int index) {
        switch (index) {
            case 0:
                return "1";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
            case 4:
                return "5 - 8";
            case 5:
                return "9 - 16";
            case 6:
                return "17 -32";
            case 7:
                return "33 - Inf";
            default:
                throw new RuntimeException("Bin index out of range");
        }
    }

    public void doInit() {
        objectToBirth = new Int2LongOpenHashMap();
        currentInterval = 0L;
        totalObjects = 0;
        lifetimeBin = new int[LIFETIME_BIN_SIZE];
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                totalObjects++;
                objectToBirth.put(((ObjectAllocEvent) event).getObjID(), currentInterval);
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_FREE:
                {
                    ObjectFreeEvent e = (ObjectFreeEvent) event;
                    int obj_id = e.getObjID();
                    if (objectToBirth.containsKey(obj_id)) {
                        long lifetime = currentInterval - objectToBirth.get(obj_id);
                        addToBin(lifetime);
                    } else {
                        Scene.v().showWarning("Cannot determine object birth time (objectID = " + obj_id + ")");
                    }
                }
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_MOVE:
                {
                    ObjectMoveEvent e = (ObjectMoveEvent) event;

                    long birth = objectToBirth.get(e.getObjID());
                    objectToBirth.put(e.getNewObjID(), birth);
                }
                break;
            case AdaptJEvent.ADAPTJ_GC_START:
                break;
            case AdaptJEvent.ADAPTJ_GC_FINISH:
                currentInterval++;
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

        argNames[0] = "label";
        argNames[1] = "category";

        out.openTagLn("results");
        
        argValues[0] = "Total number of intervals";
        argValues[1] = "Absolute";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               String.valueOf(currentInterval + 1L));
        
        /*
        argValues[0] = "Average bytes allocated per interval";
        argValues[1] = "Relative";
        out.printTaggedValueLn("numeric",
                               argNames,
                               argValues,
                               (avgSliceBytes != Double.NaN ? format.format(avgSliceBytes) : "N/A"));
        * /
        
        argValues[0] = "Distribution of object lifetimes (in GC intervals)";
        argValues[1] = "Dynamic";
        out.openTagLn("bin itemType=\"percent\"", argNames, argValues);
        for (int i = 0; i < LIFETIME_BIN_SIZE; i++) {
            out.openTagLn("item");
            out.printTaggedValueLn("range", getBinRange(i));
            out.printTaggedValueLn("value", format.format(((double) lifetimeBin[i]) / totalObjects));
            out.closeTagLn("item");
        }
        out.closeTagLn("bin");

        out.closeTagLn("results");
    }
    */


    /*
    class ObjectLivenessInfo {
        private long birth;
        private long death;
        
        public ObjectLivenessInfo() {
            this.birth = -1L;
            this.death = -1L;
        }
        
        public ObjectLivenessInfo(long birthTime) {
            this.birth = birthTime;
            this.death = -1L;
        }
        
        public ObjectLivenessInfo(long birthTime, long deathTime) {
            this.birth = birthTime;
            this.death = deathTime;
        }

        public long getBirthTime() {
            return birth;
        }

        public long getDeathTime() {
            return death;
        }

        public void setBirthTime(long birthTime) {
            birth = birthTime;
        }

        public void setDeathTime(long deathTime) {
            death = deathTime;
        }
    }
    */
}
