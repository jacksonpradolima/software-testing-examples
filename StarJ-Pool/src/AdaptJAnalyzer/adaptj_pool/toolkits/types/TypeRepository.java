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

import adaptj_pool.Scene;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.event.ObjectAllocEvent;

import org.apache.bcel.classfile.*;
import org.apache.bcel.Constants;

import it.unimi.dsi.fastUtil.*;
import adaptj_pool.util.*;

public class TypeRepository {
    private static TypeRepository instance = new TypeRepository();
    
    private Object2ObjectOpenHashMap knownTypes;
    //private Int2ObjectOpenHashMap knownObjectTypes;
    private IntHashMap knownObjectTypes;
    
    public static final Type BOOL_TYPE   = new BoolType();
    public static final Type BYTE_TYPE   = new ByteType();
    public static final Type CHAR_TYPE   = new CharType();
    public static final Type DOUBLE_TYPE = new DoubleType();
    public static final Type FLOAT_TYPE  = new FloatType();
    public static final Type INT_TYPE    = new IntType();
    public static final Type LONG_TYPE   = new LongType();
    public static final Type SHORT_TYPE  = new ShortType();
    public static final Type STRING_TYPE = new ObjectType("java.lang.String");
    
    public static final Type BOOL_ARRAY_TYPE   = new ArrayType(BOOL_TYPE);
    public static final Type CHAR_ARRAY_TYPE   = new ArrayType(CHAR_TYPE);
    public static final Type FLOAT_ARRAY_TYPE  = new ArrayType(FLOAT_TYPE);
    public static final Type DOUBLE_ARRAY_TYPE = new ArrayType(DOUBLE_TYPE);
    public static final Type BYTE_ARRAY_TYPE   = new ArrayType(BYTE_TYPE);
    public static final Type INT_ARRAY_TYPE    = new ArrayType(INT_TYPE);
    public static final Type LONG_ARRAY_TYPE   = new ArrayType(LONG_TYPE);
    public static final Type SHORT_ARRAY_TYPE  = new ArrayType(SHORT_TYPE);

    private TypeRepository() { // no instances
        knownTypes = new Object2ObjectOpenHashMap();
        //knownObjectTypes = new Int2ObjectOpenHashMap();
        knownObjectTypes = new IntHashMap(1024 * 1024, "TypeRepository Object Map");
    }
    
    public static TypeRepository v() {
        return instance;
    }

    public Type getType(String typeEncoding) {
        /* Check our growing database */
        if (typeEncoding == null) {
            return null;
        }

        if (knownTypes.containsKey(typeEncoding)) {
            return (Type) knownTypes.get(typeEncoding);
        }

        if (typeEncoding.startsWith("[")) {
            Type arrayType;
            switch (typeEncoding.charAt(1)) {
                case 'Z':
                    return BOOL_ARRAY_TYPE;
                case 'B':
                    return BYTE_ARRAY_TYPE;
                case 'C':
                    return CHAR_ARRAY_TYPE;
                case 'D':
                    return DOUBLE_ARRAY_TYPE;
                case 'F':
                    return FLOAT_ARRAY_TYPE;
                case 'I':
                    return INT_ARRAY_TYPE;
                case 'J':
                    return LONG_ARRAY_TYPE;
                case 'S':
                    return SHORT_ARRAY_TYPE;
                case 'L':
                case '[':
                    Type subType = getType(typeEncoding.substring(1));
                    if (subType == null) {
                        return null;
                    }
                    arrayType = new ArrayType(subType);
                    knownTypes.put(typeEncoding, arrayType);
                    return arrayType;
                default:
                    Scene.v().showWarning("Unknown array type: " + typeEncoding);
                    return null;
            }
        } else {
            switch (typeEncoding.charAt(0)) {
                case 'Z':
                    return BOOL_TYPE;
                case 'B':
                    return BYTE_TYPE;
                case 'C':
                    return CHAR_TYPE;
                case 'D':
                    return DOUBLE_TYPE;
                case 'F':
                    return FLOAT_TYPE;
                case 'I':
                    return INT_TYPE;
                case 'J':
                    return LONG_TYPE;
                case 'L':
                    Type objType = new ObjectType(typeEncoding.substring(1, typeEncoding.length() - 1));
                    knownTypes.put(typeEncoding, objType);
                    return objType;
                case 'S':
                    return SHORT_ARRAY_TYPE;
                default:
                    Scene.v().showWarning("Unknown type: " + typeEncoding);
                    return null;
            }
        }
    }

    public Type getType(int objectID) {
        if (knownObjectTypes.containsKey(objectID)) {
            return (Type) knownObjectTypes.get(objectID);
        }
        
        ObjectInfo info = IDResolver.v().getObjectInfo(objectID);
        if (info != null) {
            ClassInfo classInfo = info.getDeclaredClass();
            String className = (classInfo != null ? classInfo.getName() : null);

            Type t = getType(info.getArrayType(), className);
            if (t != null) {
                knownObjectTypes.put(objectID, t);
            }
            return t;
        } else {
            Scene.v().showWarning("Unknown object ID: " + objectID);
        }

        return null;
    }

    public Type getType(int type, int classID) {
        ClassInfo classInfo = IDResolver.v().getClassInfo(classID);
        String className = (classInfo != null ? classInfo.getName() : null);
        return getType(type, className);
    }

    public Type getType(ConstantPool cp, int index) {
        Constant c = cp.getConstant(index);

        switch (c.getTag()) {
            case Constants.CONSTANT_Class:
                {
                    int nameIndex = ((ConstantClass) c).getNameIndex();
                    String internalClassName = ((ConstantUtf8) cp.getConstant(nameIndex)).getBytes();
                    return getType("L" + internalClassName + ";");
                }
            case Constants.CONSTANT_Fieldref:
                {
                    int nameIndex = ((ConstantFieldref) c).getNameAndTypeIndex();
                    ConstantNameAndType nat = (ConstantNameAndType) cp.getConstant(nameIndex);
                    int sigIndex = nat.getSignatureIndex();
                    String sig = ((ConstantUtf8) cp.getConstant(sigIndex)).getBytes();
                    return getType(sig);
                }
            case Constants.CONSTANT_String:
                return STRING_TYPE;
            case Constants.CONSTANT_Integer:
                return INT_TYPE;
            case Constants.CONSTANT_Float:
                return FLOAT_TYPE;
            case Constants.CONSTANT_Long:
                return LONG_TYPE;
            case Constants.CONSTANT_Double:
                return DOUBLE_TYPE;
            default:
                Scene.v().showWarning("Unknown type for ConstantPool item: " + c);
                break;
        }

        return null;
    }
    
    public Type getType(int type, String className) {
        switch (type) {
            case ObjectAllocEvent.NORMAL_OBJECT:
                if (className == null) {
                    return null;
                }
                return getType("L" + className.replace('.', '/') + ";");
            case ObjectAllocEvent.OBJECT_ARRAY:
                if (className == null) {
                    return null;
                }
                return getType("[L" + className.replace('.', '/') + ";");
            case ObjectAllocEvent.BOOLEAN_ARRAY:
                return BOOL_ARRAY_TYPE;
            case ObjectAllocEvent.CHAR_ARRAY:
                return CHAR_ARRAY_TYPE;
            case ObjectAllocEvent.FLOAT_ARRAY:
                return FLOAT_ARRAY_TYPE;
            case ObjectAllocEvent.DOUBLE_ARRAY:
                return DOUBLE_ARRAY_TYPE;
            case ObjectAllocEvent.BYTE_ARRAY:
                return BYTE_ARRAY_TYPE;
            case ObjectAllocEvent.SHORT_ARRAY:
                return SHORT_ARRAY_TYPE;
            case ObjectAllocEvent.INT_ARRAY:
                return INT_ARRAY_TYPE;
            case ObjectAllocEvent.LONG_ARRAY:
                return LONG_ARRAY_TYPE;
            default:
                throw new RuntimeException("Unknown object type: " + type);
        }
    }

    public void invalidateObjectID(int objectID) {
        knownObjectTypes.remove(objectID);
    }

    public void moveObject(int fromID, int toID) {
        Object obj = knownObjectTypes.remove(fromID);
        if (obj != null) {
            knownObjectTypes.put(toID, obj);
        }
    }
}
