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
 * A class corresponding to the <code>JVMPI_Method</code> structure. This structure represents
 * a method defined in a class. This class is used by the {@link adaptj_pool.event.ClassLoadEvent ClassLoadEvent}
 * class.
 *
 * @author Bruno Dufour
 * @see adaptj_pool.event.ClassLoadEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class JVMPIMethod {
    /** The name of the method */
    private String method_name;
    
    /** The signature of the method */
    private String method_signature;
    
    /** The starting line number in the source file */
    private int start_lineno;
    
    /** The ending line number in the source file */
    private int end_lineno;

    /** The ID given to this method */
    private int method_id;

    public JVMPIMethod() {
        method_name = null;
        method_signature = null;
        start_lineno = -1;
        end_lineno = -1;
        method_id = 0;
    }

    public JVMPIMethod(DataInput in) throws IOException {
        method_name = in.readUTF();
        method_signature = in .readUTF();
        start_lineno = in.readInt();
        end_lineno = in.readInt();
        method_id = in.readInt();
    }

    public String getMethodName() {
        return method_name;
    }

    public String getMethodSignature() {
        return method_signature;
    }

    public int getStartLineno() {
        return start_lineno;
    }

    public int getEndLineno() {
        return end_lineno;
    }

    public int getMethodID() {
        return method_id;
    }

    public void setMethodName(String method_name) {
        this.method_name = method_name;
    }

    public void setMethodSignature(String method_signature) {
        this.method_signature = method_signature;
    }

    public void setStartLineno(int start_lineno) {
        this.start_lineno = start_lineno;
    }

    public void setEndLineno(int end_lineno) {
        this.end_lineno = end_lineno;
    }

    public void setMethodID(int method_id) {
        this.method_id = method_id;
    }
}
