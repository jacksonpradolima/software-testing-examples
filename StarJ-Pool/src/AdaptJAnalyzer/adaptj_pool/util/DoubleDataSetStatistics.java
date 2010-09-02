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

package adaptj_pool.util;

import java.util.Arrays;

public class DoubleDataSetStatistics {
    private double data[];
    private double average;
    private double stddev;
    private double median;
    private double mode;
    private double varcoef;

    private int avail = 0;

    private static final int AVG_AVAIL     = 0x00000001;
    private static final int STDDEV_AVAIL  = 0x00000002;
    private static final int MED_AVAIL     = 0x00000004;
    private static final int MODE_AVAIL    = 0x00000008;
    private static final int VARCOEF_AVAIL = 0x00000010;
    
    public DoubleDataSetStatistics(double data[]) {
        if (data == null) {
            throw new NullPointerException("Data cannot be null");
        }
        this.data = (double[]) data.clone();
        Arrays.sort(this.data);
    }

    public double getAverage() {
        if ((avail & AVG_AVAIL) != 0) {
            return average;
        }

        average = 0.0;
        for (int i = 0; i < data.length; i++) {
            average += data[i];
        }

        average = average / data.length;
        avail = (avail | AVG_AVAIL);
        return average;
    }

    public double getStandardDeviation() {
        if ((avail & STDDEV_AVAIL) != 0) {
            return stddev;
        }

        stddev = 0;
        double xbar = getAverage();
        for (int i = 0; i < data.length; i++) {
            double tmp = data[i] - xbar;
            stddev += tmp * tmp;
        }

        stddev = stddev / (data.length - 1);
        stddev = Math.sqrt(stddev);
        avail = (avail | STDDEV_AVAIL);
        return stddev;
    }

    public double getMedian() {
        if ((avail & MED_AVAIL) != 0) {
            return median;
        }
        
        if ((data.length % 2) == 0) {
            int mid = data.length / 2;
            median = (data[mid - 1] + data[mid]) / 2.0;
        } else {
            median = data[data.length / 2];
        }

        avail = (avail | MED_AVAIL);
        return median;
    }

    public double getMode() {
        if ((avail & MODE_AVAIL) != 0) {
            return mode;
        }
        
        int consecutive = 1;
        int tmpConsecutive = 0;
        double candidate = data[0];
        mode = candidate;

        for (int i = 0; i < data.length; i++) {
            if (data[i] == candidate) {
                tmpConsecutive++;
            } else {
                if (tmpConsecutive > consecutive) {
                    mode = candidate;
                    consecutive = tmpConsecutive;
                }
                tmpConsecutive = 1;
                candidate = data[i];
            }
        }

        if (tmpConsecutive > consecutive) {
            mode = candidate;
            consecutive = tmpConsecutive;
        }

        avail = (avail | MODE_AVAIL);
        return mode;
    }

    public double getCoefficientOfVariation() {
        if ((avail & VARCOEF_AVAIL) != 0) {
            return varcoef;
        }

        varcoef = getStandardDeviation() / getAverage();
        avail = (avail | VARCOEF_AVAIL);
        return varcoef;
    }
}
