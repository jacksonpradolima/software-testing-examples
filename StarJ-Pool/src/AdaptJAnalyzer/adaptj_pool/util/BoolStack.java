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

import java.util.EmptyStackException;

public class BoolStack {
    private boolean[] data;
    private int p;
    private static final int DEFAULT_SIZE = 32;

    public BoolStack() {
        this(DEFAULT_SIZE);
    }

    public BoolStack(int initialSize) {
        if (initialSize <= 0) {
            throw new RuntimeException("Stack size cannot be <= 0");
        }
        data = new boolean[initialSize];
        p = -1;
    }

    public boolean empty() {
        return (p < 0);
    }

    public void clear() {
        p = -1;
    }

    public boolean replace(boolean b) {
        if (p < 0) {
            throw new EmptyStackException();
        }
        boolean tmp = data[p];
        data[p] = b;
        return tmp;
    }

    public void pushOrReplace(boolean b) {
        if (p < 0) {
            data[++p] = b;
        } else {
            data[p] = b;
        }
    }

    public void push(boolean b) {
        if (p == (data.length - 1)) {
            // increase array size
            boolean[] newData = new boolean[data.length * 2 + 1];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
        data[++p] = b;
    }

    public boolean pop() {
        if (p < 0) {
            throw new EmptyStackException();
        }
        return data[p--];
    }

    public boolean top() {
        if (p < 0) {
            throw new EmptyStackException();
        }
        return data[p];
    }

    public boolean peek() {
        /* same as top(), just added for compatibility with Sun's
           implementation */
        if (p < 0) {
            throw new EmptyStackException();
        }
        return data[p];
    }

    public boolean top(boolean default_value) {
        if (p < 0) {
            return default_value;
        }
        return data[p];
    }

    public boolean peek(boolean default_value) {
        if (p < 0) {
            return default_value;
        }
        return data[p];
    }

    public int capacity() {
        return data.length;
    }

    public int search(boolean b) {
        for (int q = p; q >= 0; q--) {
            if (data[q] == b) {
                return (p - q + 1);
            }
        }

        return -1;
    }

    public int size() {
        return (p + 1);
    }

    public String toString() {
        String result = "[";
        for (int i = 0; i < p; i++) {
            result = result + data[i] + ", ";
        }
        
        if (p >= 0) {
            result = result + data[p];
        }
        
        return result + "]";
    }
}
