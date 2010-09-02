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

package adaptj_pool.toolkits.analyses;

import java.util.Set;
import java.util.Collections;
import it.unimi.dsi.fastUtil.ObjectOpenHashSet;

public class ArenaInfo {
    private int id;
    private String name;
    private Set objects;
    
    public ArenaInfo(int id, String name) {
        this.id = id;
        this.name = name;
        objects = new ObjectOpenHashSet();
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean add(ObjectInfo obj) {
        return objects.add(obj);
    }

    public Set getObjects() {
        return Collections.unmodifiableSet(objects);
    }

    public void clearObjects() {
        objects.clear();
    }

    public boolean remove(ObjectInfo obj) {
        return objects.remove(obj);
    }
}
