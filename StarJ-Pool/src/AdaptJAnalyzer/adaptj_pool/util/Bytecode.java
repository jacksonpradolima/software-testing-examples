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

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

public class Bytecode {
    private MethodEntity method;
    private InstructionList iList;
    private InstructionHandle[] iHandles;
    private int iPos[];
    private Attribute[] attributes;

    public Bytecode(MethodEntity method, InstructionList iList, Attribute[] attributes) {
        this.method= method;
        this.iList = iList;
        this.iHandles = iList.getInstructionHandles();
        this.iPos = iList.getInstructionPositions();
        this.attributes = attributes;
    }

    public Bytecode(MethodEntity method, Code code) {
        this(method, new InstructionList(code.getCode()), (code != null ? code.getAttributes() : null));
    }

    public Bytecode(MethodEntity method, byte[] code, Attribute[] attributes) {
        this(method, new InstructionList(code), attributes);
    }

    public InstructionHandle locateInstruction(int offset) {
        return InstructionList.findHandle(iHandles, iPos, iHandles.length, offset);
    }

    public void setMethodEntity(MethodEntity method) {
        this.method = method;
    }
    
    public MethodEntity getMethodEntity() {
        return method;
    }

    public int size() {
        return iList.size();
    }

    public String toString() {
        return (iList == null ? null : iList.toString(false));
    }

    public InstructionHandle[] getInstructionHandles() {
        return iHandles;
    }

    public InstructionHandle getInstructionHandle(int index) {
        return iHandles[index];
    }

    public int getInstructionHandleCount() {
        return (iHandles != null ? iHandles.length : 0);
    }

    public Attribute[] getAttributes() {
        return attributes;
    }
}
