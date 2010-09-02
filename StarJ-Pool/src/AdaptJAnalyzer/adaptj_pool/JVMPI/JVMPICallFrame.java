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
 * A class corresponding to the <code>JVMPI_CallFrame</code> structure. This structure represents
 * a method being executed. This class is used by the {@link adaptj_pool.JVMPI.JVMPICallTrace CallTrace}
 * class.
 *
 * @author Bruno Dufour
 * @see JVMPICallTrace
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class JVMPICallFrame {
    /** The line number in the source file */
    private int lineno;
    
    /** The method executed in this frame */
    private int method_id;

    public JVMPICallFrame() {
        lineno = -1;
        method_id = 0;
    }

    public JVMPICallFrame(DataInput in) throws IOException {
        lineno = in.readInt();
        method_id = in.readInt();
    }
    
    public int getLineno() {
        return this.lineno;
    }

    public int getMethodID() {
        return this.method_id;
    }
    
    public void setLineno(int lineno) {
        this.lineno = lineno;
    }

    public void setMethodID(int method_id) {
        this.method_id = method_id;
    }
}
