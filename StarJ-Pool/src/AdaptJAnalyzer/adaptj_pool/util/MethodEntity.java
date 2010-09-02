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
import adaptj_pool.util.IntToIntHashMap;

public class MethodEntity implements Comparable {
    private String className;
    private String methodName;
    private String methodSignature;
    private int hashCode; // avoid repeating an expensive computation
    private boolean isStandardLib;
    private String methodFullName; // full method name (name + signature)
    private String fullName; // fully qualified method name
    private IntToIntHashMap entry_counts;

    public MethodEntity(String className, String methodName,
            String methodSignature, boolean isStandardLib) {
        this.className = className;
        this.methodName = methodName;
        this.methodSignature = methodSignature;
        this.methodFullName = null;
        this.hashCode = className.hashCode() + methodName.hashCode() + methodSignature.hashCode();
        this.isStandardLib = isStandardLib;
        this.entry_counts = new IntToIntHashMap(5, 0);
    }

    public boolean isStandardLib() {
        return isStandardLib;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String getMethodFullName() {
        if (methodFullName == null) {
            methodFullName = methodName + methodSignature;
        }

        return methodFullName;
    }

    public String getFullName() {
        if (fullName == null) {
            fullName = className + "." + methodName + methodSignature;
        }

        return fullName;
    }

    public int hashCode() {
        return hashCode;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MethodEntity) {
            if (obj == this) {
                return true;
            }

            return this.getFullName().equals(((MethodEntity) obj).getFullName());
        }

        return false;
    }

////    protected boolean equals_internal(MethodEntity me) {
////        if (me == null) {
////            return false;
////        }
////
////        return this.getFullName().equals(me.getFullName());
////        /*
////        // className
////        if (className == null && me.className != null) {
////            return false;
////        }
////
////        if (!(className.equals(me.className))) {
////            return false;
////        }
////
////        // methodName
////        if (methodName == null && me.methodName != null) {
////            return false;
////        }
////
////        if (!(methodName.equals(me.methodName))) {
////            return false;
////        }
////        
////        // methodSignature
////        if (methodSignature == null && me.methodSignature != null) {
////            return false;
////        }
////
////        if (!(methodSignature.equals(me.methodSignature))) {
////            return false;
////        }
////
////        return true;
////        */
////    }
////
////    protected boolean equals_internal(String className, String methodName, String methodSignature) {
////        /* className */
////        if (this.className == null && className != null) {
////            return false;
////        }
////
////        if (!(this.className.equals(className))) {
////            return false;
////        }
////
////        /* methodName */
////        if (this.methodName == null && methodName != null) {
////            return false;
////        }
////
////        if (!(this.methodName.equals(methodName))) {
////            return false;
////        }
////        
////        /* methodSignature */
////        if (this.methodSignature == null && methodSignature != null) {
////            return false;
////        }
////
////        if (!(this.methodSignature.equals(methodSignature))) {
////            return false;
////        }
////
////        return true;
////    }

    public String toString() {
        return this.getFullName();
        //return "[" + className + "]" + this.getMethodFullName();
    }

    public int compareTo(Object obj) {
        MethodEntity me = (MethodEntity) obj;
        int result;
        
        result = methodName.compareTo(me.methodName);
        if (result == 0) {
            result = methodSignature.compareTo(me.methodSignature);
            if (result == 0) {
                result = className.compareTo(me.className);
            }
        }
        return result;
    }

    public boolean matchesInvocation(MethodEntity invoke_decl) {
        // Sanity check
        if (invoke_decl == null) {
            return false;
        }
        
        String method_name = invoke_decl.methodName;
        String method_sig = invoke_decl.methodSignature;

        // Sanity check -- Make sure that the method signatures match
        if (!this.methodName.equals(method_name)
                || !this.methodSignature.equals(method_sig)) {
            return false;
        }

        if (this.className.equals(invoke_decl.className)) {
            // Method is the same, so we are certain of equality here
            return true;
        }

        JavaClass sc = ClassPathExplorer.v().getJavaClass(invoke_decl.className);
        JavaClass dc = ClassPathExplorer.v().getJavaClass(this.className);

        if (sc.isInterface()) {
            // Read comments in ClassFileUtils.matchesInterface for details
            return ClassFileUtils.matchesInterface(dc, sc, method_name, method_sig);
        }

        if (!ClassFileUtils.hasMethod(dc, method_name, method_sig)) {
            return false;
        }

        // We have an actual class, not interface
        if (ClassFileUtils.definesMethod(sc, method_name, method_sig)) {
            // sc does define the method, so dyn must be a matching
            // method in a subclass of sc
            return ClassFileUtils.isSubclass(dc, sc);
        } else {
            // sc does not define the method, dyn can either be a matching
            // method in a subclass of sc (#1), or a superclass of sc (#2)
            // 
            // Examples for calling E.foo()
            // 
            // Case #1:
            //   Class C:
            //      void foo()
            //   Class D:
            //      // foo not overridden
            //   Class E:
            //      void foo()
            //  ==> sc = D, dc = E, E is a subclass of D
            //
            // Case #2:
            //   Class C:
            //      void foo()
            //   Class D:
            //      // foo not overridden
            //   Class E:
            //      // foo not overriden 
            //  ==> sc = D, dc = C, C is a superclass of D
            return ClassFileUtils.isSubclass(sc, dc)
                    || ClassFileUtils.isSubclass(dc, sc);
        }

        // We can never reach this point
    }

    /*
    public boolean matchesInvocation(String className, String methodName, String methodSignature) {
        // Sanity check
        if (className == null || methodName == null || methodSignature == null) {
            return false;
        }

        if (!this.methodName.equals(methodName)
                || !this.methodSignature.equals(methodSignature)) {
            return false;
        }

        if (this.className.equals(className)) {
            // Method is the same, so we are certain of equality here
            return true;
        }

        JavaClass sc = ClassPathExplorer.v().getJavaClass(className);
        JavaClass dc = ClassPathExplorer.v().getJavaClass(this.className);

        if (sc.isInterface()) {
            // Read comments in ClassFileUtils.matchesInterface for details
            return ClassFileUtils.matchesInterface(dc, sc, methodName, methodSignature);
            
        }

        // We have an actual class, not interface
        if (ClassFileUtils.definesMethod(sc, methodName, methodSignature)) {
            // sc does define the method, so dyn must be a matching
            // method in a subclass of c
            return ClassFileUtils.isSubclass(dc, sc)
                    && ClassFileUtils.definesMethod(dc, methodName, methodSignature);
        } else {
            // sc does not define the method, dyn must be a matching
            // method in a superclass of c
            return ClassFileUtils.isSubclass(sc, dc)
                    && ClassFileUtils.definesMethod(dc, methodName, methodSignature);
        }

        // We can never reach this point        
    }
    */

    public void enter(int env_id) {
        this.entry_counts.put(env_id, this.entry_counts.get(env_id) + 1);
    }

    public boolean exit(int env_id) {
        int curr_count = this.entry_counts.get(env_id);
        if (curr_count > 0) {
            this.entry_counts.put(env_id, curr_count - 1);
            return true;
        }

        return false;
    }

    public int getEntryCount(int env_id) {
        return this.entry_counts.get(env_id);
    }
    
}
