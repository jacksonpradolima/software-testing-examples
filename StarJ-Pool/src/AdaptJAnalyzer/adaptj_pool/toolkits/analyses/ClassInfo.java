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

import adaptj_pool.toolkits.transformers.EventDistiller;

public class ClassInfo {
    private String name;
    private String sourceName;
    private MethodInfo methods[];
    private boolean requested;
    private int loadedCount;
    private boolean isStandardLib;

    public ClassInfo(String name, String sourceName, MethodInfo methods[]) {
        this.name = name;
        this.sourceName = sourceName;
        this.methods = methods;
        this.requested = false;
        this.loadedCount = 0;
        this.isStandardLib = EventDistiller.isStandardLib(name);
    }

    public ClassInfo(String name, String sourceName, MethodInfo methods[], boolean requested) {
        this.name = name;
        this.sourceName = sourceName;
        this.methods = methods;
        this.requested = requested;
        this.loadedCount = 0;
        this.isStandardLib = EventDistiller.isStandardLib(name);
    }

    public String getName() {
        return name;
    }

    public String getSourceName() {
        return sourceName;
    }

    public MethodInfo[] getMethods() {
        return methods;
    }

    public boolean setRequested() {
        return requested;
    }

    public void getRequested(boolean requested) {
        this.requested = requested;
    }

    public int getLoadedCount() {
        return loadedCount;
    }

    public void setLoadedCount(int count) {
        loadedCount = count;
    }

    public void stepLoadedCount() {
        loadedCount += 1;
    }

    public boolean isStandardLib() {
        return isStandardLib;
    }
    
}
