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

package adaptj_pool.JVMPI;

import java.io.*;

/**
 * A class corresponding to the <code>JVMPI_CallTrace</code> structure. This structure represents
 * a call trace of a method execution. 
 *
 * @author Bruno Dufour
 * @see adaptj_pool.event.HeapDumpEvent
 * @see adaptj_pool.event.MonitorDumpEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class JVMPICallTrace {
    /** The ID of the thread which executed this trace.*/
    private int env_id;

    /** The number of frames in the trace. This value is normally equal to <code>frames.length</code> */
    private int num_frames;
    
    /** the JVMPICallFrame objects that make up this trace (callee followed by callers) */
    private JVMPICallFrame frames[];

    public JVMPICallTrace() {
        env_id = 0;
        num_frames = 0;
        frames = null;
    }
    
    public JVMPICallTrace(DataInput in) throws IOException {
        env_id     = in.readInt();
        num_frames = in.readInt();
        if (num_frames > 0) {
            frames = new JVMPICallFrame[num_frames];
            for (int j = 0; j < num_frames; j++) {
                frames[j] = new JVMPICallFrame(in);
            }
        }
    }
    
    public int getEnvID() {
        return env_id;       
    }

    public int getNumFrames() {
        return num_frames;
    }

    public JVMPICallFrame[] getFrames() {
        return frames;
    }

    public JVMPICallFrame getFrame(int index) {
        return frames[index];
    }

    public void setEnvID(int env_id) {
        this.env_id = env_id;
    }

    public void setFrames(JVMPICallFrame[] frames) {
        this.frames = frames;
    }

    public void setFrame(JVMPICallFrame frame, int index) {
        this.frames[index] = frame;
    }
}
