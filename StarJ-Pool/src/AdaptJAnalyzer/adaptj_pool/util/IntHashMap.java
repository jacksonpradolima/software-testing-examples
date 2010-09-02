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

public class IntHashMap {
    public static final int DEFAULT_SIZE = 256;
    public static final double DEFAULT_LOAD_FACTOR = 0.75;
    private IntHashMapEntry data[];
    private int capacity;
    private int cutoff;
    private double loadFactor;
    private int size;
    private String name;

    /*
    private IntHashMapEntry entryPool;
    */
    
    public IntHashMap(String name) {
        this(DEFAULT_SIZE, DEFAULT_LOAD_FACTOR, name);
    }

    public IntHashMap(int size, String name) {
        this(size, DEFAULT_LOAD_FACTOR, name);
    }

    public IntHashMap(double loadFactor, String name) {
        this(DEFAULT_SIZE, loadFactor, name);
    }

    public IntHashMap(int size, double loadFactor, String name) {
        if (size < 1) {
            throw new RuntimeException("IntHashMap size has to be at least 1");
        }

        if (loadFactor <= 0.0) {
            throw new RuntimeException("IntHashMap load factor must be > 0.0");
        }

        this.size = 0;
        this.capacity = size;
        this.data = new IntHashMapEntry[size];
        this.loadFactor = loadFactor;
        this.cutoff = (int) (capacity * loadFactor);
        //entryPool = null;
        this.name = name;
    }

    public Object clone() {
        return new IntHashMap(capacity, loadFactor, name);
    }

    public boolean containsKey(int key) {
        int hash = key % capacity;
        IntHashMapEntry e, tmp;

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

    public boolean containsValue(Object value) {
        IntHashMapEntry entry;

        if (value == null) {
            for (int i = 0; i < capacity; i++) {
                for (entry = data[i]; entry != null; entry = entry.next) {
                    if (entry.object == null) {
                        return true;
                    }
                }
            }
        } else {
            for (int i = 0; i < capacity; i++) {
                for (entry = data[i]; entry != null; entry = entry.next) {
                    if (value.equals(entry.object)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    
    public Object get(int key) {
        int hash = key % capacity;
        IntHashMapEntry e;

        for (e = data[hash]; e != null; e = e.next) {
            if (e.key == key) {
                return e.object;
            }
        }

        return null;
    }

    public Object put(int key, Object object) {
        int hash = key % capacity;
        IntHashMapEntry e, head;

        head = data[hash];
        for (e = head; e != null; e = e.next) {
            if (e.key == key) {
                Object o = e.object;
                e.object = object;
                return o;
            }
        }
        /*
        if (entryPool == null) {
            e = new IntHashMapEntry(key, object, head);
        } else {
            e = entryPool;
            entryPool = entryPool.next;
            e.key = key;
            e.object = object;
            e.next = head;
        }
        */
        data[hash] = new IntHashMapEntry(key, object, head);
        if (++size >= cutoff) {
            rehash(2 * capacity + 1);
        }
        return null;
    }

    public Object remove(int key) {
        int hash = key % capacity;
        IntHashMapEntry e, tmp;

        for (tmp = null, e = data[hash]; e != null; tmp = e, e = e.next) {
            if (e.key == key) {
                if (tmp == null) {
                    data[hash] = e.next;
                } else {
                    tmp.next = e.next;
                }
                Object o = e.object;
                /*
                e.next = entryPool;
                entryPool = e;
                */
                e = null;
                size--;
                return o;
            }
        }

        return null;
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
            IntHashMapEntry newData[];
            IntHashMapEntry entry, tmp;
            int newHash;

            newData = new IntHashMapEntry[newCapacity];
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
        IntHashMapEntry entry;
        for(int i = 0; i < capacity; i++) {
            for (entry = data[i]; entry != null; entry = entry.next) {
                result[j++] = entry.key;
            }
        }

        return result;
    }
    
    private Object[] valueSet() {
        Object[] result;
        int j = 0;

        result = new Object[size];
        IntHashMapEntry entry;
        for(int i = 0; i < capacity; i++) {
            for (entry = data[i]; entry != null; entry = entry.next) {
                result[j++] = entry.object;
            }
        }

        return result;
    }
    
    class IntHashMapEntry {
        int key;
        Object object;
        IntHashMapEntry next;

        public IntHashMapEntry(int key, Object object) {
            this.key = key;
            this.object = object;
            this.next = null;
        }
        
        public IntHashMapEntry(int key, Object object, IntHashMapEntry next) {
            this.key = key;
            this.object = object;
            this.next = next;
        }
    }
}


