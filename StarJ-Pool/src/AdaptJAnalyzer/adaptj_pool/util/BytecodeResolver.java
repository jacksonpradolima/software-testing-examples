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


import adaptj_pool.event.*;
import adaptj_pool.JVMPI.*;
import adaptj_pool.Scene;
import adaptj_pool.toolkits.analyses.*;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;
import it.unimi.dsi.fastUtil.*;

import adaptj_pool.util.*;

public class BytecodeResolver {
    private final static int CACHE_SIZE = 32; // NB: This has to be a power of 2!
    private final static int CACHE_MASK = 0x0000001F; // Keep 5 bits (2^5 = 32)
    
    private static BytecodeResolver instance = new BytecodeResolver();
    //private Int2ObjectHashMap methodIDtoInstructions;
    private IntHashMap methodIDtoInstructions;
    private IntOpenHashSet currentClasses;

    private int[] cachedMethodIDs;
    private InstructionHandle[] cachedInstructions;
    private Bytecode[] cachedCodes;
    
    private BytecodeResolver() { // no instance
        //methodIDtoInstructions = new Int2ObjectHashMap();
        methodIDtoInstructions = new IntHashMap(1024*1024, "BytecodeResolver");
        currentClasses = new IntOpenHashSet();

        cachedMethodIDs = new int[CACHE_SIZE];
        cachedInstructions = new InstructionHandle[CACHE_SIZE];
        cachedCodes = new Bytecode[CACHE_SIZE];
    }

    public void loadClass(ClassLoadEvent e) {
        int class_id = e.getClassID();
        boolean freshClass = !currentClasses.contains(class_id);

        String class_name = e.getClassName();
        if (class_name != null && class_name.startsWith("[")) {
            /* This is an array class, and thus cannot be resolved */
            return;
        }

        JavaClass cf = ClassPathExplorer.v().getJavaClass(class_name);
        if (cf == null) {
            Scene.v().showWarning("Failed to read class \"" + class_name + "\"");
            return;
        }
        
        Object2ObjectOpenHashMap cfMethods = getMethods(cf);

        // Process Methods
        JVMPIMethod m;
        for (int i = 0; i < e.getNumMethods(); i++) {
            m = e.getMethod(i);
            int method_id = m.getMethodID();
            MethodEntity me = IDResolver.v().getMethodEntity(method_id);
            if (freshClass) {
                /* Clear the local cache if the method is reloaded */
                int key = (method_id >> 4) & CACHE_MASK;
                if (method_id == cachedMethodIDs[key]) {
                    cachedMethodIDs[key] = 0;
                    cachedCodes[key] = null;
                    cachedInstructions[key] = null;
                }
                
                String methodFullName = m.getMethodName() + m.getMethodSignature();
                
                Method cfMethod = (Method) cfMethods.get(methodFullName);
                
                if (cfMethod != null) {
                    Code code = cfMethod.getCode();
                    if (code != null) {
                        methodIDtoInstructions.put(method_id,
                                new Bytecode(me, code));
                    } else {
                        if (!(cfMethod.isNative() || cfMethod.isAbstract())) {
                            Scene.v().showWarning("Failed to obtain code for " + 
                                    methodFullName + " [from " + class_name + "]");
                        }
                    }
                } else {
                    Scene.v().reportError("Unexpected error in BytecodeResolver. BCEL and JVMPI have divergent opinions...");
                }
            } else {
                Bytecode code = (Bytecode) methodIDtoInstructions.get(method_id);
                if (code != null && code.getMethodEntity() == null) {
                    code.setMethodEntity(me);
                }
            }
        }
        cfMethods = null;
        currentClasses.add(class_id);
    }

    public void unloadClass(ClassUnloadEvent e) {
        int class_id = e.getClassID();
        if (currentClasses.contains(class_id)) {
            ClassInfo cinfo = IDResolver.v().getClassInfo(class_id);
            if (cinfo != null) {
                MethodInfo methods[] = cinfo.getMethods();
                if (methods != null) {
                    for (int i = 0; i < methods.length; i++) {
                        methodIDtoInstructions.remove(methods[i].getID());
                    }
                }
            }

            currentClasses.remove(class_id);
        }
    }

    public InstructionHandle getInstructionHandle(int method_id, int offset) {
        Bytecode code;
        int key = (method_id >> 4) & CACHE_MASK;
        
        if (method_id == cachedMethodIDs[key] && method_id != 0) {
            InstructionHandle cachedInstruction = cachedInstructions[key];
            if (cachedInstruction != null) {
                InstructionHandle nextHandle = cachedInstruction.getNext();
                if (nextHandle != null && nextHandle.getPosition() == offset) {
                    cachedInstruction = nextHandle;
                    return nextHandle;
                }
            }
            code = cachedCodes[key];
        } else { 
            cachedMethodIDs[key] = method_id;
            code = (Bytecode) methodIDtoInstructions.get(method_id);
            cachedCodes[key] = code;
        }
        
        if (code != null) {
            InstructionHandle iHandle = code.locateInstruction(offset);
            cachedInstructions[key] = iHandle;
            return iHandle;
        } else {
            Scene.v().reportError("BytecodeResolver> Requested code for an unknown method\n");
        }

        cachedInstructions[key] = null;
        return null;
    }

    public Bytecode getBytecode(int method_id) {
        int key = (method_id >> 4) & CACHE_MASK;
        if (method_id == cachedMethodIDs[key] && method_id != 0) {
            /* Cache Hit */
            return cachedCodes[key];
        }
        
        Bytecode code = (Bytecode) methodIDtoInstructions.get(method_id);
        cachedMethodIDs[key] = method_id;
        cachedCodes[key] = code;
        cachedInstructions[key] = null;

        return code;
    }

    private Object2ObjectOpenHashMap getMethods(JavaClass clazz) {
        Method[] methods = clazz.getMethods();
        if (methods == null) {
            return null;
        }

        Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(methods.length);
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            result.put(m.getName() + m.getSignature(), m);
        }

        return result;
    }

    public static BytecodeResolver v() {
        return instance;
    }
}
