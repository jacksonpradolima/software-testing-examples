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
 * An Event corresponding to the <code>JVMPI_COMPILED_METHOD_LOAD</code> event. This event is triggered when a method is compiled
 * an loaded into memory by the Java VM.
 *
 * @author Bruno Dufour
 * @see CompiledMethodUnloadEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class CompiledMethodLoadEvent extends AdaptJEvent implements MethodEvent{
    private int method_id;
    /**
     * The size of the code of the method being loaded. The <code>code_size</code> field in <code>CompiledMethodLoadEvent</code>
     * corresponds to the <code>code_size</code> field in the <code>JVMPI_COMPILED_METHOD_LOAD</code> event. If <code>code</code> is
     * also recorded, then the length of the <code>code</code> array is precisely <code>code_size</code>.
     */
    private int code_size;
    /**
     * The code of the method being loaded. The <code>code</code> field in <code>CompiledMethodLoadEvent</code>
     * corresponds to the <code>code</code> field in the <code>JVMPI_COMPILED_METHOD_LOAD</code> event. If <code>code_length</code> is
     * also recorded, then is is equal to <code>code.length</code>.
     */
    private byte[] code;
    /**
     * The size of the line number table of the method being loaded. The <code>lineno_table_size</code> field in 
     * <code>CompiledMethodLoadEvent</code> corresponds to the <code>code_size</code> field in the
     * <code>JVMPI_COMPILED_METHOD_LOAD</code> event. If <code>lineno_table</code> is also recorded, then the length of
     * the <code>lineno_table</code> array is precisely <code>lineno_table_size</code>.
     */
    private int lineno_table_size;
    /**
     * The line number table of the method being loaded. The <code>lineno_table</code> field in <code>CompiledMethodLoadEvent</code>
     * corresponds to the <code>lineno_table</code> field in the <code>JVMPI_COMPILED_METHOD_LOAD</code> event. If
     * <code>lineno_table_size</code> is also recorded, then is is equal to <code>lineno_table.length</code>.
     */
    private JVMPILineno[] lineno_table;
    
    public CompiledMethodLoadEvent() {
        this(0, null, null);
    }
    
    public CompiledMethodLoadEvent(int method_id, byte[] code, JVMPILineno[] lineno_table) {
        setTypeID(ADAPTJ_COMPILED_METHOD_LOAD);
        this.method_id = method_id;
        this.code_size = (code != null ? code.length : 0);
        this.code = code;
        this.lineno_table_size = (lineno_table != null ? lineno_table.length : 0);
        this.lineno_table = lineno_table;
    }
    
    /**
     * Get method_id.
     *
     * @return method_id as int.
     */
    public int getMethodID() {
        return method_id;
    }
    
    /**
     * Set method_id.
     *
     * @param method_id the value to set.
     */
    public void setMethodID(int method_id) {
        this.method_id = method_id;
    }
    
    /**
     * Get code_size.
     *
     * @return code_size as int.
     */
    public int getCodeSize() {
        return code_size;
    }
    
    /**
     * Get lineno_table_size.
     *
     * @return lineno_table_size as int.
     */
    public int getLinenoTableSize() {
        return lineno_table_size;
    }
    
    /**
     * Get code.
     *
     * @return code as byte[].
     */
    public byte[] getCode() {
        return code;
    }
    
    /**
     * Get code element at specified index.
     *
     * @param index the index.
     * @return code at index as byte.
     */
    public byte getCode(int index) {
        return code[index];
    }
    
    /**
     * Set code.
     *
     * @param code the value to set.
     */
    public void setCode(byte[] code) {
        this.code = code;
        if (code == null) {
            code_size = 0;
        } else {
            code_size = code.length;
        }
    }
    
    /**
     * Set code at the specified index.
     *
     * @param code the value to set.
     * @param index the index.
     */
    public void setCode(byte code, int index) {
        this.code[index] = code;
    }
    
    /**
     * Get lineno_table.
     *
     * @return lineno_table as JVMPILineno[].
     */
    public JVMPILineno[] getLinenoTable() {
        return lineno_table;
    }
    
    /**
     * Get lineno_table element at specified index.
     *
     * @param index the index.
     * @return lineno_table at index as JVMPILineno.
     */
    public JVMPILineno getLinenoTable(int index) {
        return lineno_table[index];
    }
    
    /**
     * Set lineno_table.
     *
     * @param lineno_table the value to set.
     */
    public void setLinenoTable(JVMPILineno[] lineno_table) {
        this.lineno_table = lineno_table;
        if (lineno_table == null) {
            lineno_table_size = 0;
        } else {
            lineno_table_size = lineno_table.length;
        }
    }
    
    /**
     * Set lineno_table at the specified index.
     *
     * @param lineno_table the value to set.
     * @param index the index.
     */
    public void setLinenoTable(JVMPILineno lineno_table, int index) {
        this.lineno_table[index] = lineno_table;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_METHOD_ID) != 0) {
            method_id = in.readInt();
        } else {
            method_id = 0;
        }

        if ((info & ADAPTJ_FIELD_CODE_SIZE) != 0
                || (info & ADAPTJ_FIELD_CODE) != 0) {
            code_size = in.readInt();
        } else {
            code_size = 0;
        }
        
        if ((info & ADAPTJ_FIELD_CODE) != 0
                && code_size > 0) {
            code = new byte[code_size];
            in.readFully(code);
        } else {
            code = null;
        }
        
        if ((info & ADAPTJ_FIELD_LINENO_TABLE_SIZE) != 0
                || (info & ADAPTJ_FIELD_LINENO_TABLE) !=  0) {
            lineno_table_size = in.readInt();
        } else {
            lineno_table_size = 0;
        }
        
        if ((info & ADAPTJ_FIELD_LINENO_TABLE) != 0
                && lineno_table_size > 0) {
            lineno_table = new JVMPILineno[lineno_table_size];
            //JVMPILineno l;
            for (int i = 0; i < lineno_table_size; i++) {
                lineno_table[i] = new JVMPILineno(in);
                /*
                l = new JVMPILineno();
                l.offset = in.readInt();
                l.lineno = in.readInt();
                lineno_table[i] = l;
                */
            }
        } else {
            lineno_table = null;
        }
    }    
}
