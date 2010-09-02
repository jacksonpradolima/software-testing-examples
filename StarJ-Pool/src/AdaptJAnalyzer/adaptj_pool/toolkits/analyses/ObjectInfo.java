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

public class ObjectInfo {
    private int id;
    private ArenaInfo arena;
    private ClassInfo declaredClass;
    private ClassInfo associatedClass;
    private boolean isArray;
    private int arrayType;
    private int size;
    private boolean requested;
    private int instanciatedCount = 0;
    private boolean isStandardLib;
    
    public ObjectInfo(int id, ArenaInfo arena, ClassInfo declaredClass, ClassInfo associatedClass,
            boolean isArray, int size, boolean requested) {
        this.id = id;
        this.arena = arena;
        this.declaredClass = declaredClass;
        this.associatedClass = associatedClass;
        this.isArray = isArray;
        this.size = size;
        this.requested = requested;
        updateStandardLib();
    }

    private void updateStandardLib() {
        if (declaredClass != null) {
            this.isStandardLib = declaredClass.isStandardLib();
        } else if (associatedClass != null) {
            this.isStandardLib = associatedClass.isStandardLib();
        } else {
            this.isStandardLib = false;
        }
    }
    
    /*
    public ObjectInfo(int id, ArenaInfo arena, ClassInfo declaredClass, ClassInfo associatedClass,
            boolean isArray, int size) {
        this.id = id;
        this.arena = arena;
        this.declaredClass = declaredClass;
        this.associatedClass = associatedClass;
        this.isArray = isArray;
        this.size = size;
        this.requested = false;
    }
    */

    public ObjectInfo(int id, ArenaInfo arena, int arrayType, int size, boolean requested) {
        this.id = id;
        this.arena = arena;
        this.arrayType = arrayType;
        this.isArray = true;
        this.declaredClass = null;
        this.associatedClass = null;
        this.size = size;
        this.requested = requested;
        this.isStandardLib = false;
    }

    /*
    public ObjectInfo(int id, ArenaInfo arena, int arrayType, int size) {
        this.id = id;
        this.arena = arena;
        this.arrayType = arrayType;
        this.isArray = true;
        this.declaredClass = null;
        this.associatedClass = null;
        this.size = size;
    }
    */

    public int getID() {
        return id;
    }

    public ArenaInfo getArena() {
        return arena;
    }

    public ClassInfo getDeclaredClass() {
        return declaredClass;
    }

    public ClassInfo getAssociatedClass() {
        return associatedClass;
    }
    
    public void setDeclaredClass(ClassInfo klass) {
        declaredClass = klass;
        updateStandardLib();
    }

    public void setAssociatedClass(ClassInfo klass) {
        associatedClass = klass;
        updateStandardLib();
    }
    
    public boolean isArray() {
        return isArray;
    }

    public int getArrayType() {
        return arrayType;
    }

    public int getSize() {
        return size;
    }

    public boolean setRequested() {
        return requested;
    }

    public void getRequested(boolean requested) {
        this.requested = requested;
    }

    public int getInstanciatedCount() {
        return instanciatedCount;
    }

    public void setInstanciatedCount(int count) {
        instanciatedCount = count;
    }

    public void stepInstanciatedCount() {
        instanciatedCount += 1;
    }

    public void moveTo(int id, ArenaInfo arena) {
        if (this.arena != null) {
            this.arena.remove(this);
        }
        this.arena = arena;
        this.id = id;
        if (arena != null) {
            arena.add(this);
        }
    }

    public boolean isStandardLib() {
        return isStandardLib;
    }
}
