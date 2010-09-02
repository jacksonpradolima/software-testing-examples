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

public class IntToIntHashMap {
    public static final int DEFAULT_SIZE = 256;
    public static final double DEFAULT_LOAD_FACTOR = 0.75;
    private IntToIntHashMapEntry data[];
    private int capacity;
    private int cutoff;
    private double loadFactor;
    private int size;
    private int defaultValue;
    
    public IntToIntHashMap() {
        this(DEFAULT_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public IntToIntHashMap(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    public IntToIntHashMap(double loadFactor) {
        this(DEFAULT_SIZE, loadFactor);
    }
    
    public IntToIntHashMap(int size, int defaultValue) {
        this(size, DEFAULT_LOAD_FACTOR, defaultValue);
    }

    public IntToIntHashMap(int size, double loadFactor) {
        this(size, loadFactor, 0);
    }

    public IntToIntHashMap(int size, double loadFactor, int defaultValue) {
        if (size < 1) {
            throw new RuntimeException("IntToIntHashMap size has to be at least 1");
        }

        if (loadFactor <= 0.0) {
            throw new RuntimeException("IntToIntHashMap load factor must be > 0.0");
        }

        this.size = 0;
        this.capacity = size;
        this.data = new IntToIntHashMapEntry[size];
        this.loadFactor = loadFactor;
        this.cutoff = (int) (capacity * loadFactor);
        this.defaultValue = defaultValue;
    }

    public Object clone() {
        return new IntToIntHashMap(capacity, loadFactor);
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int value) {
        this.defaultValue = value;
    }

    public boolean containsKey(int key) {
        int hash = key % capacity;
        IntToIntHashMapEntry e, tmp;

        for (tmp = null, e = data[hash]; e != null; tmp = e, e = e.next) {
            if (e.key == key) {
                /* Assume that it is likely that
                 * an eventual call to get() for the same
                 * key is near */
                if (tmp != null) {
                    tmp.next = e.next;
                    e.next = data[hash];
                    data[hash] = e;
                }
                return true;
            }
        }

        return false;
    }

    public boolean containsValue(int value) {
        IntToIntHashMapEntry entry;

        
        for (int i = 0; i < capacity; i++) {
            for (entry = data[i]; entry != null; entry = entry.next) {
                if (value == entry.value) {
                    return true;
                }
            }
        }

        return false;
    }

    
    public int get(int key) {
        int hash = key % capacity;
        IntToIntHashMapEntry e;

        for (e = data[hash]; e != null; e = e.next) {
            if (e.key == key) {
                return e.value;
            }
        }

        return defaultValue;
    }

    public int put(int key, int value) {
        int hash = key % capacity;
        IntToIntHashMapEntry e, head;

        head = data[hash];
        for (e = head; e != null; e = e.next) {
            if (e.key == key) {
                int i = e.value;
                e.value = value;
                return i;
            }
        }
        
        data[hash] = new IntToIntHashMapEntry(key, value, head);
        if (++size >= cutoff) {
            rehash(2 * capacity + 1);
        }
        return defaultValue;
    }

    public int remove(int key) {
        int hash = key % capacity;
        IntToIntHashMapEntry e, tmp;

        for (tmp = null, e = data[hash]; e != null; tmp = e, e = e.next) {
            if (e.key == key) {
                if (tmp == null) {
                    data[hash] = e.next;
                } else {
                    tmp.next = e.next;
                }
                int i = e.value;
                /*
                e.next = entryPool;
                entryPool = e;
                */
                e = null;
                size--;
                return i;
            }
        }

        return defaultValue;
    }

    public boolean isEmpty() {
        return (size == 0);
    }
    
    public int size() {
        return size;
    }

    public void clear() {
        Arrays.fill(data, null);
        size = 0;
    }

    private void rehash(int newCapacity) {
        if (newCapacity > capacity) {
            IntToIntHashMapEntry newData[];
            IntToIntHashMapEntry entry, tmp;
            int newHash;

            newData = new IntToIntHashMapEntry[newCapacity];
            for (int i = 0; i < capacity; i++) {
                for (entry = data[i]; entry != null; ) {
                    tmp = entry.next;
                    newHash = entry.key % newCapacity;
                    entry.next = newData[newHash];
                    newData[newHash] = entry;
                    entry = tmp;
                }
            }

            data = newData;
            capacity = newCapacity;
            cutoff = (int)(newCapacity * loadFactor);
        }
    }

    private int[] keySet() {
        int[] result;
        int j = 0;

        result = new int[size];
        IntToIntHashMapEntry entry;
        for(int i = 0; i < capacity; i++) {
            for (entry = data[i]; entry != null; entry = entry.next) {
                result[j++] = entry.key;
            }
        }

        return result;
    }
    
    private int[] valueSet() {
        int[] result;
        int j = 0;

        result = new int[size];
        IntToIntHashMapEntry entry;
        for(int i = 0; i < capacity; i++) {
            for (entry = data[i]; entry != null; entry = entry.next) {
                result[j++] = entry.value;
            }
        }

        return result;
    }
    
    class IntToIntHashMapEntry {
        int key;
        int value;
        IntToIntHashMapEntry next;

        public IntToIntHashMapEntry(int key, int value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
        
        public IntToIntHashMapEntry(int key, int value, IntToIntHashMapEntry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
