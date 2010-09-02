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

public interface Type extends Comparable {
    public static final int ARRAY_TYPE  =  1;
    public static final int BOOL_TYPE   =  2;
    public static final int BYTE_TYPE   =  3;
    public static final int CHAR_TYPE   =  4;
    public static final int DOUBLE_TYPE =  5;
    public static final int FLOAT_TYPE  =  6;
    public static final int INT_TYPE    =  7;
    public static final int LONG_TYPE   =  8;
    public static final int SHORT_TYPE  =  9;
    public static final int OBJECT_TYPE = 10;

    public String toTypeString();
    public int getTypeID();
}
