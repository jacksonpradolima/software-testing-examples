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

import java.io.*;

public abstract class DumpEvent extends AdaptJEvent {
    protected int data_len;
    protected byte[] data;

    public DumpEvent() {
        this(null);
    }

    public DumpEvent(byte[] data) {
        this.data_len = (data != null ? data.length : 0);
        this.data = data;
    }

    /**
     * Get data_len.
     *
     * @return data_len as int.
     */
    public int getDataLen() {
        return data_len;
    }
    
    /**
     * Get data.
     *
     * @return data as byte[].
     */
    public byte[] getData() {
        return data;
    }
    
    /**
     * Get data element at specified index.
     *
     * @param index the index.
     * @return data at index as byte.
     */
    public byte getData(int index) {
        return data[index];
    }
    
    /**
     * Set data.
     *
     * @param data the value to set.
     */
    public void setData(byte[] data) {
        this.data = data;
        if (data == null) {
            data_len = 0;
        } else {
            data_len = data.length;
        }
    }
    
    /**
     * Set data at the specified index.
     *
     * @param data the value to set.
     * @param index the index.
     */
    public void setData(byte data, int index) {
        this.data[index] = data;
    }

    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_DATA_LEN) != 0
                || (info & ADAPTJ_FIELD_DATA) != 0) {
            data_len = in.readInt();
        } else {
            data_len = 0;
        }
        
        if ((info & ADAPTJ_FIELD_DATA) != 0
                && data_len > 0) {
            data = new byte[data_len];
            in.readFully(data);
        } else {
            data = null;
        }
    }
}
