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

package adaptj_pool.toolkits.types;

public class ObjectType implements Type {
    private String klass;

    public ObjectType() {
        klass = null;
    }

    public ObjectType(String klass) {
        this.klass = klass;
    }

    public String klass() {
        return klass;
    }

    public void setKlass(String klass) {
        this.klass = klass;
    }
    
    public int compareTo(Object obj) {
        if (obj == this) {
            return 0;
        }
        
        if (obj == null) {
            return -1;
        }
        
        if (obj instanceof ObjectType) {
            ObjectType ot = (ObjectType) obj;

            if (klass == null && ot.klass != null) {
                return 1;
            }

            if (klass != null && ot.klass == null) {
                return -1;
            }

            if (klass == ot.klass) {
                return 0;
            }

            return klass.compareTo(ot.klass);
        } else if (obj instanceof Type) {
            return 1;
        }

        throw new RuntimeException("Incompatible object");
    }

    public boolean equals(Object obj) {
        return (obj instanceof ObjectType && compareTo(obj) == 0);
    }

    public int hashCode() {
        if (klass == null) {
            return 0;
        } else {
            return klass.hashCode();
        }
    }

    public String toTypeString() {
        return "L" + (klass != null ? klass.replace('.', '/') : klass) + ";";
    }

    /* Experimental -- May be removed from the AdaptJ API at any time !! */
    public final int getTypeID() {
        return Type.OBJECT_TYPE;
    }
}
