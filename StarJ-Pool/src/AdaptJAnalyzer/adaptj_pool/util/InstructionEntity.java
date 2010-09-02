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

import org.apache.bcel.generic.InstructionHandle;

public class InstructionEntity implements Comparable {
    private MethodEntity mi;
    private InstructionHandle inst;
    private int hashCode;
    private boolean hashCodeAvail;

    public InstructionEntity() {
        this.inst = null;
        this.mi = null;
        computeHashCode();
    }
    
    public InstructionEntity(InstructionHandle inst, MethodEntity mi) {
        this.inst = inst;
        this.mi = mi;
        computeHashCode();
    }
    
    public InstructionEntity(InstructionEntity entity) {
        this.inst = entity.inst;
        this.mi = entity.mi;
        computeHashCode();
    }

    public void setMethodEntity(MethodEntity mi) {
        this.mi = mi;
        hashCodeAvail = false;
    }

    public void setInstructionHandle(InstructionHandle inst) {
        this.inst = inst;
        hashCodeAvail = false;
    }

    public void setInternalValues(InstructionHandle inst, MethodEntity mi) {
        this.inst = inst;
        this.mi = mi;
        hashCodeAvail = false;
    }

    public InstructionHandle getInstructionHandle() {
        return inst;
    }

    public MethodEntity getMethodEntity() {
        return mi;
    }

    private void computeHashCode() {
        hashCode = (mi != null ? mi.hashCode() : 0) + (inst != null ? inst.getPosition() : 0);
        hashCodeAvail = true;
    }

    public int hashCode() {
        if (!hashCodeAvail) {
            computeHashCode();
        }
        return hashCode;
    }

    public boolean equals(Object obj) {
        if (obj instanceof InstructionEntity) {
            if (obj == this) {
                return true;
            }

            InstructionEntity ii = (InstructionEntity) obj;

            /* inst */
            if (inst == null && ii.inst != null) {
                return false;
            }

            if (!(inst.equals(ii.inst))) {
                return false;
            }

            /* mi */
            if (mi == null && ii.mi != null) {
                return false;
            }

            if (!(mi.equals(ii.mi))) {
                return false;
            }
            
            return true;
        }

        return false;
    }

    public String toString() {
        return mi + ": " + inst;
    }

    public int compareTo(Object obj) {
        InstructionEntity ii = (InstructionEntity) obj;
        int result;
        
        result = mi.compareTo(ii.mi);
        if (result == 0) {
            result = ii.inst.getPosition() - inst.getPosition();
        }
        return result;
    }
}
