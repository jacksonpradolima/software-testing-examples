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

package adaptj_pool.event;

import adaptj_pool.JVMPI.*;
import java.io.*;

/**
 * An Event corresponding to the <code>JVMPI_MONITOR_DUMP</code> event. This event is triggered when requested through the
 * <code>RequestEvent</code> JVMPI function. The event data contains a snapshot of all the threads and monitors in the VM.
 *
 * @author Bruno Dufour
 * @see ObjectDumpEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class MonitorDumpEvent extends DumpEvent {
    /** FIXME */
    private int num_traces;
    /** FIXME */
    private JVMPICallTrace[] traces;
    /** FIXME */
    private int[] threads_status;
    
    public MonitorDumpEvent() {
        this(null, null, null);
    }

    public MonitorDumpEvent(byte[] data, JVMPICallTrace[] traces, int[] threads_status) {
        super(data);

        setTypeID(ADAPTJ_MONITOR_DUMP);
        this.num_traces = (traces != null ? traces.length : 0);
        int tmp = (threads_status != null ? threads_status.length : 0);

        if (num_traces != tmp) {
            throw new RuntimeException("Both 'threads_status' and 'traces' must have the same length");
        }

        this.traces = traces;
        this.threads_status = threads_status;
    }
    
    /**
     * Get num_traces.
     *
     * @return num_traces as int.
     */
    public int getNumTraces() {
        return num_traces;
    }
    
    /**
     * Get traces.
     *
     * @return traces as JVMPICallTrace[].
     */
    public JVMPICallTrace[] getTraces() {
        return traces;
    }
    
    /**
     * Get traces element at specified index.
     *
     * @param index the index.
     * @return traces at index as JVMPICallTrace.
     */
    public JVMPICallTrace getTraces(int index) {
        return traces[index];
    }
    
    /**
     * Set traces.
     *
     * @param traces the value to set.
     */
    public void setTraces(JVMPICallTrace[] traces) {
        this.traces = traces;
        if (traces == null) {
            num_traces = 0;
        } else {
            num_traces = traces.length;
        }
    }
    
    /**
     * Set traces at the specified index.
     *
     * @param traces the value to set.
     * @param index the index.
     */
    public void setTraces(JVMPICallTrace trace, int index) {
        this.traces[index] = trace;
    }
    
    /**
     * Get threads_status.
     *
     * @return threads_status as int[].
     */
    public int[] getThreadsStatus() {
        return threads_status;
    }
    
    /**
     * Get threads_status element at specified index.
     *
     * @param index the index.
     * @return threads_status at index as int.
     */
    public int getThreadsStatus(int index) {
        return threads_status[index];
    }
    
    /**
     * Set threads_status.
     *
     * @param threads_status the value to set.
     */
    public void setThreadsStatus(int[] threads_status) {
        this.threads_status = threads_status;
    }
    
    /**
     * Set threads_status at the specified index.
     *
     * @param threads_status the value to set.
     * @param index the index.
     */
    public void setThreadsStatus(int threads_status, int index) {
        this.threads_status[index] = threads_status;
    }

    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_NUM_TRACES) != 0
                || (info & ADAPTJ_FIELD_TRACES) != 0) {
            num_traces = in.readInt();
        } else {
            num_traces = 0;
        }
        
        if ((info & ADAPTJ_FIELD_TRACES) != 0
                && num_traces > 0) {
            traces = new JVMPICallTrace[num_traces];
            threads_status = new int[num_traces];
            //JVMPICallTrace ct;
            for (int i = 0; i < num_traces; i++) {
                /*
                ct = new JVMPICallTrace();
                ct.env_id     = in.readInt();
                ct.num_frames = in.readInt();
                if (ct.num_frames > 0) {
                    ct.frames = new JVMPICallFrame[ct.num_frames];
                    JVMPICallFrame f;
                    for (int j = 0; j < ct.num_frames; j++) {
                        f = new JVMPICallFrame();
                        f.lineno    = in.readInt();
                        f.method_id = in.readInt();
                        ct.frames[j] = f;
                    }
                }
                traces[i] = ct;
                */
                traces[i] = new JVMPICallTrace(in);
                threads_status[i] = in.readInt();
            }
        } else {
            traces = null;
            threads_status = null;
        }
    }
}
