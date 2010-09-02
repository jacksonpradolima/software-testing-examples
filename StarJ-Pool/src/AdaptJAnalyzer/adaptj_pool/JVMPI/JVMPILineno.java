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
 * A class corresponding to the <code>JVMPI_Lineno</code> structure. This structure represents
 * a mapping between source line number and offset from the beginning of a compiled method. This
 * class is used by the {@link adaptj_pool.event.CompiledMethodLoadEvent CompiledMethodLoadEvent} class.
 *
 * @author Bruno Dufour
 * @see adaptj_pool.event.CompiledMethodLoadEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class JVMPILineno {
    /** The offset from the beginning of the method */
    private int offset;
    
    /** The line number from the beginning of the source file */
    private int lineno;

    public JVMPILineno() {
        offset = -1;
        lineno = -1;
    }

    public JVMPILineno(DataInput in) throws IOException {
        offset = in.readInt();
        lineno = in.readInt();
    }

    public int getOffset() {
        return offset;
    }

    public int getLineno() {
        return lineno;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLineno(int lineno) {
        this.lineno = lineno;
    }
}
