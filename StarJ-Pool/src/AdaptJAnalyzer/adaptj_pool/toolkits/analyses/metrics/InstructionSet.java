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

package adaptj_pool.toolkits.analyses.metrics;

import java.util.*;
import adaptj_pool.util.BytecodeNameResolver;
import it.unimi.dsi.fastUtil.*;

public class InstructionSet {
    private String name;
    private IntOpenHashSet data = new IntOpenHashSet();
    private long counter = 0;
    
    public InstructionSet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean add(int instructionCode) {
        if (instructionCode < 0) {
            return false;
        }
        data.add(instructionCode);
        return true;
    }

    public boolean add(String instructionName) {
        int code = BytecodeNameResolver.getOpcodeFromMnemonic(instructionName);
        if (code >= 0) {
            data.add(code);
            return true;
        }

        return false;
    }

    public void setUnion(InstructionSet set) {
        this.data.addAll(set.data);
    }

    public void remove(int instructionCode) {
        data.remove(instructionCode);
    }

    public void remove(String instructionName) {
        int code = BytecodeNameResolver.getOpcodeFromMnemonic(instructionName);
        if (code >= 0) {
            data.remove(code);
        }
    }

    public boolean contains(int instructionCode) {
        return data.contains(instructionCode);
    }

    public int getSize() {
        return data.size();
    }

    public boolean isEmpty() {
        return (data.size() == 0);
    }

    public int[] getContents() {
        if (data.size() <= 0) {
            return null;
        }
        int result[] = new int[data.size()];
        
        result = data.toIntArray(result);
        /*
        Iterator it = data.iterator();
        int offset = 0;
        while (it.hasNext()) {
            Integer i = (Integer) it.next();
            result[offset++] = i.intValue();
        }
        */

        return result;
    }

    public void stepCounter() {
        counter++;
    }

    public long getCounter() {
        return counter;
    }

    public void resetCounter() {
        counter = 0;
    }
}
