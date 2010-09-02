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

public class ArrayType implements Type {
    private Type type;

    public ArrayType(Type arrayType) {
        this.type = arrayType;
    }

    public Type getType() {
        return type;
    }

    public int compareTo(Object obj) {
        if (obj instanceof ArrayType) {
            return (type.compareTo(((ArrayType) obj).type));
        } else if (obj instanceof ObjectType) {
            return -1;
        } else if (obj instanceof Type) {
            return 1;
        }

        throw new RuntimeException("Incompatible object");
    }

    public boolean equals(Object obj) {
        return (obj instanceof ArrayType && compareTo(obj) == 0);
    }

    public int hashCode() {
        if (type == null) {
            return 0;
        } else {
            return type.hashCode();
        }
    }

    public String toTypeString() {
        return "[" + (type != null ? type.toTypeString() : null);
    }

    /* Experimental -- May be removed from the AdaptJ API at any time !! */
    public final int getTypeID() {
        return Type.ARRAY_TYPE;
    }
}
