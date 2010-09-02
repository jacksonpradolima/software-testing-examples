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

package adaptj_pool.event;

import java.io.*;
import org.apache.bcel.generic.InstructionHandle;

/**
 * An Event corresponding to the <code>JVMPI_INSTRUCTION_START</code> event. This event is triggered when an instruction is executed
 * by the Java VM.
 *
 * @author Bruno Dufour
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class InstructionStartEvent extends AdaptJEvent implements MethodEvent {
    private int method_id;
    
    /**
     * The offset of this instruction in the code of the method. The <code>offset</code> field of <code>InstructionStartEvent</code>
     * corresponds to the <code>offset</code> field of the <code>JVMPI_INSTRUCTION_START</code> event.
     */
    private int offset;
    /**
     * The offset of this instruction in the code of the method. The <code>offset</code> field of <code>InstructionStartEvent</code>
     * corresponds to the <code>offset</code> field of the <code>JVMPI_INSTRUCTION_START</code> event.
     */
    private boolean is_true;
    /**
     * The offset of this instruction in the code of the method. The <code>offset</code> field of <code>InstructionStartEvent</code>
     * corresponds to the <code>offset</code> field of the <code>JVMPI_INSTRUCTION_START</code> event.
     */
    private int key;
    /**
     * The offset of this instruction in the code of the method. The <code>offset</code> field of <code>InstructionStartEvent</code>
     * corresponds to the <code>offset</code> field of the <code>JVMPI_INSTRUCTION_START</code> event.
     */
    private int low;
    /**
     * The offset of this instruction in the code of the method. The <code>offset</code> field of <code>InstructionStartEvent</code>
     * corresponds to the <code>offset</code> field of the <code>JVMPI_INSTRUCTION_START</code> event.
     */
    private int hi;
    /**
     * The offset of this instruction in the code of the method. The <code>offset</code> field of <code>InstructionStartEvent</code>
     * corresponds to the <code>offset</code> field of the <code>JVMPI_INSTRUCTION_START</code> event.
     */
    private int chosen_pair_index;
    /**
     * The offset of this instruction in the code of the method. The <code>offset</code> field of <code>InstructionStartEvent</code>
     * corresponds to the <code>offset</code> field of the <code>JVMPI_INSTRUCTION_START</code> event.
     */
    private int pairs_total;
    
    /**
     * The bytecode corresponding to this instruction. This information is typically filled in by the
     * <code>InstructionResolver</code> transformer.
     *
     * @see adaptj_pool.toolkits.transformers.InstructionResolver
     */
    private int code;

    private InstructionHandle ih;
    
    public InstructionStartEvent() {
        this(0, -1, false, -1, -1, -1, -1, -1);
    }
    
    public InstructionStartEvent(int method_id, int offset, boolean is_true, int key, int low, int hi,
            int chosen_pair_index, int pairs_total) {
        setTypeID(ADAPTJ_INSTRUCTION_START);
        this.method_id = method_id;
        this.offset = offset;
        this.is_true = is_true;
        this.key = key;
        this.low = low;
        this.hi = hi;
        this.chosen_pair_index = chosen_pair_index;
        this.pairs_total = pairs_total;
    }
    
    /**
     * Get method_id.
     *
     * @return method_id as int.
     */
    public int getMethodID() {
        return method_id;
    }
    
    /**
     * Set method_id.
     *
     * @param method_id the value to set.
     */
    public void setMethodID(int method_id) {
        this.method_id = method_id;
    }

    /**
     * Get offset.
     *
     * @return offset as int.
     */
    public int getOffset() {
        return offset;
    }
    
    /**
     * Set offset.
     *
     * @param offset the value to set.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    /**
     * Get is_true.
     *
     * @return is_true as boolean.
     */
    public boolean getIsTrue() {
        return is_true;
    }
    
    /**
     * Set is_true.
     *
     * @param is_true the value to set.
     */
    public void setIsTrue(boolean is_true) {
        this.is_true = is_true;
    }
    
    /**
     * Get key.
     *
     * @return key as int.
     */
    public int getKey() {
        return key;
    }
    
    /**
     * Set key.
     *
     * @param key the value to set.
     */
    public void setKey(int key) {
        this.key = key;
    }
    
    /**
     * Get low.
     *
     * @return low as int.
     */
    public int getLow() {
        return low;
    }
    
    /**
     * Set low.
     *
     * @param low the value to set.
     */
    public void setLow(int low) {
        this.low = low;
    }
    
    /**
     * Get hi.
     *
     * @return hi as int.
     */
    public int getHi() {
        return hi;
    }
    
    /**
     * Set hi.
     *
     * @param hi the value to set.
     */
    public void setHi(int hi) {
        this.hi = hi;
    }
    
    /**
     * Get chosen_pair_index.
     *
     * @return chosen_pair_index as int.
     */
    public int getChosenPairIndex() {
        return chosen_pair_index;
    }
    
    /**
     * Set chosen_pair_index.
     *
     * @param chosen_pair_index the value to set.
     */
    public void setChosenPairIndex(int chosen_pair_index) {
        this.chosen_pair_index = chosen_pair_index;
    }
    
    /**
     * Get pairs_total.
     *
     * @return pairs_total as int.
     */
    public int getPairsTotal() {
        return pairs_total;
    }
    
    /**
     * Set pairs_total.
     *
     * @param pairs_total the value to set.
     */
    public void setPairsTotal(int pairs_total) {
        this.pairs_total = pairs_total;
    }
    
    /**
     * Get code.
     *
     * @return code as int.
     */
    public int getCode() {
        return code;
    }
    
    /**
     * Set code.
     *
     * @param code the value to set.
     */
    public void setCode(int code) {
        this.code = code;
    }

    public void setInstructionHandle(InstructionHandle handle) {
        this.ih = handle;
    }

    public InstructionHandle getInstructionHandle() {
        return this.ih;
    }
    
    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_METHOD_ID) != 0) {
            method_id = in.readInt();
        } else {
            method_id = 0;
        }
        
        if ((info & ADAPTJ_FIELD_OFFSET) != 0) {
            offset = in.readInt();
        } else {
            offset = -1;
        }
        
        if ((info & ADAPTJ_FIELD_IS_TRUE) != 0) {
            is_true = in.readBoolean();
        } else {
            is_true = false;
        }
        
        if ((info & ADAPTJ_FIELD_KEY) != 0) {
            key = in.readInt();
        } else {
            key = -1;
        }
        
        if ((info & ADAPTJ_FIELD_LOW) != 0) {
            low = in.readInt();
        } else {
            low = -1;
        }
        
        if ((info & ADAPTJ_FIELD_HI) != 0) {
            hi = in.readInt();
        } else {
            hi = -1;
        }
        
        if ((info & ADAPTJ_FIELD_CHOSEN_PAIR_INDEX) != 0) {
            chosen_pair_index = in.readInt();
        } else {
            chosen_pair_index = -1;
        }
        
        if ((info & ADAPTJ_FIELD_PAIRS_TOTAL) != 0) {
            pairs_total = in.readInt();
        } else {
            pairs_total = -1;
        }

        code = -1;
        ih = null;
    }
}
