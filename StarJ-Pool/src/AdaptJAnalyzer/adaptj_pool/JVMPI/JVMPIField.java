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
 * A class corresponding to the <code>JVMPI_Field</code> structure. This structure represents
 * a field defined in a class. This class is used by the @{link adaptj_pool.event.ClassLoadEvent ClassLoadEvent}
 * class.
 *
 * @author Bruno Dufour
 * @see adaptj_pool.event.ClassLoadEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class JVMPIField {
    /** The name of the field */
    private String field_name;
    /** The signature of the field */
    private String field_signature;
    
    public JVMPIField() {
        field_name = null;
        field_signature = null;
    }

    public JVMPIField(DataInput in) throws IOException {
        field_name = in.readUTF();
        field_signature = in.readUTF();
    }

    public String getFieldName() {
        return field_name;
    }

    public String getFieldSignature() {
        return field_signature;
    }

    public void setFieldName(String field_name) {
        this.field_name = field_name;
    }

    public void setFieldSignature(String field_signature) {
        this.field_signature = field_signature;
    }
}
