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

import adaptj_pool.util.Bytecode;
import org.apache.bcel.generic.InstructionHandle;

public class MethodInfo {
    private int id;
    private String name;
    private String signature;
    private ObjectInfo klass;
    private Bytecode bytecode;
    private boolean isStandardLib;
    
    public MethodInfo(int id, ObjectInfo classInfo, String name, String signature) {
        this(id, classInfo, name, signature, null);
    }
    
    public MethodInfo(int id, ObjectInfo classInfo, String name, String signature, Bytecode bytecode) {
        this.id = id;
        this.klass = classInfo;
        this.name = name;
        this.signature = signature;
        this.isStandardLib = false;
        if (classInfo != null) {
            ClassInfo info = classInfo.getAssociatedClass();
            if (info != null) {
                this.isStandardLib = info.isStandardLib();
            }
        }
        this.bytecode = bytecode;
    }

    public int getID() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public ObjectInfo getKlass() {
        return klass;
    }


    /**--
    public InstructionList getInstructionList() {
        return iList;
    }
    --**/

    public Bytecode getBytecode() {
        return bytecode;
    }

    public InstructionHandle getInstruction(int offset) {
        if (bytecode != null) {
            return bytecode.locateInstruction(offset);
        }

        return null;
    }

    public boolean isStandardLib() {
        return isStandardLib;
    }
}
