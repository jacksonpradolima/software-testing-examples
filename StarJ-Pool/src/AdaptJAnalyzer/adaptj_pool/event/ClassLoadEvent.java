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

import adaptj_pool.JVMPI.*;
import java.io.*;

/**
 * An Event corresponding to the <code>JVMPI_CLASS_LOAD</code> event. This event is triggered when a class is loaded
 * by the Java VM.
 *
 * @author Bruno Dufour
 * @see ClassUnloadEvent
 * @see <a href="http://java.sun.com/j2se/1.4/docs/guide/jvmpi/jvmpi.html">The Java Virtual Machine Profiler Interface (JVMPI)</a>
 */
public class ClassLoadEvent extends AdaptJEvent implements ClassEvent {
    /**
     * The name of the class being loaded. The <code>class_name</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>class_name</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private String class_name;
    /**
     * The name of the source file that defines the class being loaded. The <code>source_name</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>source_name</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private String source_name;
    /**
     * The number of interfaces implemented by the class being loaded. The <code>num_interfaces</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>num_interfaces</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private int num_interfaces;
    /**
     * The number of methods defined in the class being loaded. The <code>num_methods</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>num_methods</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private int num_methods;
    /**
     * An array of the methods defined in the class being loaded. The <code>methods</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>methods</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private JVMPIMethod[] methods;
    /**
     * The number of static fields defined in the class being loaded. The <code>num_static_fields</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>num_static_fields</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private int num_static_fields;
    /**
     * An array of the static fields defined in the class being loaded. The <code>statics</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>statics</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private JVMPIField[] statics;
    /**
     * The number of instance fields defined in the class being loaded. The <code>num_instance_fields</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>num_instance_fields</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private int num_instance_fields;
    /**
     * An array of the instance fields defined in the class being loaded. The <code>instances</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>instances</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private JVMPIField[] instances;
    /**
     * The ID of the class being loaded. The <code>class_id</code> field in <code>ClassLoadEvent</code>
     * corresponds to the <code>class_id</code> field in the <code>JVMPI_CLASS_LOAD</code> event.
     */
    private int class_id;

    /**
     * The full path of the source file that defines the class being loaded. This information is typically filled in by the <code>ClassNameResolver</code>
     * transformer.
     *
     * @see adaptj_pool.toolkits.transformers.ClassNameResolver
     */
    private String fullClassName;

    public ClassLoadEvent() {
        this(null, null, 0, null, null, null, 0);
    }
    
    public ClassLoadEvent(String class_name, String source_name, int num_interfaces,
            JVMPIMethod[] methods, JVMPIField[] statics, JVMPIField[] instances, int class_id) {
        setTypeID(ADAPTJ_CLASS_LOAD);
        this.class_name = class_name;
        this.source_name = source_name;
        this.num_interfaces = num_interfaces;
        this.num_methods = (methods != null ? methods.length : 0);
        this.methods = methods;
        this.num_static_fields = (statics != null ? statics.length : 0);
        this.statics = statics;
        this.num_instance_fields = (instances != null ? instances.length : 0);
        this.instances = instances;
        this.class_id = class_id;
    }

    /**
     * Get class_name.
     *
     * @return class_name as String.
     */
    public String getClassName() {
        return class_name;
    }
    
    /**
     * Set class_name.
     *
     * @param class_name the value to set.
     */
    public void setClassName(String class_name) {
        this.class_name = class_name;
    }
    
    /**
     * Get source_name.
     *
     * @return source_name as String.
     */
    public String getSourceName() {
        return source_name;
    }
    
    /**
     * Set source_name.
     *
     * @param source_name the value to set.
     */
    public void setSourceName(String source_name) {
        this.source_name = source_name;
    }
    
    /**
     * Get num_interfaces.
     *
     * @return num_interfaces as int.
     */
    public int getNumInterfaces() {
        return num_interfaces;
    }
    
    /**
     * Set num_interfaces.
     *
     * @param num_interfaces the value to set.
     */
    public void setNumInterfaces(int num_interfaces) {
        this.num_interfaces = num_interfaces;
    }
    
    /**
     * Get num_methods.
     *
     * @return num_methods as int.
     */
    public int getNumMethods() {
        return num_methods;
    }
    
    /**
     * Set num_methods.
     *
     * @param num_methods the value to set.
     */
    /*
    public void setNumMethods(int num_methods) {
        this.num_methods = num_methods;
    }
    */
    
    /**
     * Get methods.
     *
     * @return methods as JVMPIMethod[].
     */
    public JVMPIMethod[] getMethods() {
        return methods;
    }
    
    /**
     * Get methods element at specified index.
     *
     * @param index the index.
     * @return methods at index as JVMPIMethod.
     */
    public JVMPIMethod getMethod(int index) {
        return methods[index];
    }
    
    /**
     * Set methods.
     *
     * @param methods the value to set.
     */
    public void setMethods(JVMPIMethod[] methods) {
        this.methods = methods;
        if (methods == null) {
            num_methods = 0;
        } else {
            num_methods = methods.length;
        }
    }
    
    /**
     * Set methods at the specified index.
     *
     * @param methods the value to set.
     * @param index the index.
     */
    public void setMethod(JVMPIMethod method, int index) {
        this.methods[index] = method;
    }
    
    /**
     * Get num_static_fields.
     *
     * @return num_static_fields as int.
     */
    public int getNumStaticFields() {
        return num_static_fields;
    }
    
    /**
     * Set num_static_fields.
     *
     * @param num_static_fields the value to set.
     */
    /*
    public void setNumStaticFields(int num_static_fields) {
        this.num_static_fields = num_static_fields;
    }
    */
    
    /**
     * Get statics.
     *
     * @return statics as JVMPIField[].
     */
    public JVMPIField[] getStatics() {
        return statics;
    }
    
    /**
     * Get statics element at specified index.
     *
     * @param index the index.
     * @return statics at index as JVMPIField.
     */
    public JVMPIField getStatics(int index) {
        return statics[index];
    }
    
    /**
     * Set statics.
     *
     * @param statics the value to set.
     */
    public void setStatics(JVMPIField[] statics) {
        this.statics = statics;
        if (statics == null) {
            num_static_fields = 0;
        } else {
            num_static_fields = statics.length;
        }
    }
    
    /**
     * Set statics at the specified index.
     *
     * @param statics the value to set.
     * @param index the index.
     */
    public void setStatics(JVMPIField statik, int index) {
        this.statics[index] = statik;
    }
    
    /**
     * Get num_instance_fields.
     *
     * @return num_instance_fields as int.
     */
    public int getNumInstanceFields() {
        return num_instance_fields;
    }
    
    /**
     * Set num_instance_fields.
     *
     * @param num_instance_fields the value to set.
     */
    /* 
    public void setNum_instance_fields(int num_instance_fields) {
        this.num_instance_fields = num_instance_fields;
    }
    */
    
    /**
     * Get instances.
     *
     * @return instances as JVMPIField[].
     */
    public JVMPIField[] getInstances() {
        return instances;
    }
    
    /**
     * Get instances element at specified index.
     *
     * @param index the index.
     * @return instances at index as JVMPIField.
     */
    public JVMPIField getInstances(int index) {
        return instances[index];
    }
    
    /**
     * Set instances.
     *
     * @param instances the value to set.
     */
    public void setInstances(JVMPIField[] instances) {
        this.instances = instances;
        if (instances == null) {
            num_instance_fields = 0;
        } else {
            num_instance_fields = instances.length;
        }
    }
    
    /**
     * Set instances at the specified index.
     *
     * @param instances the value to set.
     * @param index the index.
     */
    public void setInstances(JVMPIField instance, int index) {
        this.instances[index] = instance;
    }
    
    /**
     * Get class_id.
     *
     * @return class_id as int.
     */
    public int getClassID() {
        return class_id;
    }
    
    /**
     * Set class_id.
     *
     * @param class_id the value to set.
     */
    public void setClassID(int class_id) {
        this.class_id = class_id;
    }
    
    /**
     * Get fullClassName.
     *
     * @return fullClassName as String.
     */
    public String getFullClassName() {
        return fullClassName;
    }
    
    /**
     * Set fullClassName.
     *
     * @param fullClassName the value to set.
     */
    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }


    public void readFromStream(DataInput in, short info) throws IOException {
        super.readFromStream(in, info);

        if ((info & ADAPTJ_FIELD_CLASS_NAME) != 0) {
            class_name = in.readUTF();
        } else {
            class_name = null;
        }
        
        if ((info & ADAPTJ_FIELD_SOURCE_NAME) != 0) {
            source_name = in.readUTF();
        } else {
            source_name = null;
        }
        
        if ((info & ADAPTJ_FIELD_NUM_INTERFACES) != 0) {
            num_interfaces = in.readInt();
        } else {
            num_interfaces = 0;
        }
        
        if ((info & ADAPTJ_FIELD_NUM_METHODS) != 0
                || (info & ADAPTJ_FIELD_METHODS) != 0) {
            num_methods = in.readInt();
        } else {
            num_methods = 0;
        }
        
        if ((info & ADAPTJ_FIELD_METHODS) != 0 
                && num_methods > 0) {
            methods = new JVMPIMethod[num_methods];
            //JVMPIMethod m;
            for (int i = 0; i < num_methods; i++) {
                methods[i] = new JVMPIMethod(in);
                /*
                m = new JVMPIMethod();
                m.method_name      = in.readUTF();
                m.method_signature = in.readUTF();
                m.start_lineno     = in.readInt();
                m.end_lineno       = in.readInt();
                m.method_id        = in.readInt();
                methods[i] = m;
                */
            }
        } else {
            methods = null;
        }
        
        if ((info & ADAPTJ_FIELD_NUM_STATIC_FIELDS) != 0
                || (info & ADAPTJ_FIELD_STATICS) != 0) {
            num_static_fields = in.readInt();
        } else {
            num_static_fields = 0;
        }
        
        if ((info & ADAPTJ_FIELD_STATICS) != 0 
                && num_static_fields > 0) {
            statics = new JVMPIField[num_static_fields];
            //JVMPIField f;
            for (int i = 0; i < num_static_fields; i++) {
                statics[i] = new JVMPIField(in);
                /*
                f = new JVMPIField();
                f.field_name      = in.readUTF();
                f.field_signature = in.readUTF();
                statics[i] = f;
                */
            }
        } else {
            statics = null;
        }

        if ((info & ADAPTJ_FIELD_NUM_INSTANCE_FIELDS) != 0
                || (info & ADAPTJ_FIELD_INSTANCES) != 0) {
            num_instance_fields = in.readInt();
        } else {
            num_instance_fields = 0;
        }
        
        if ((info & ADAPTJ_FIELD_INSTANCES) != 0 
                && num_instance_fields > 0) {
            instances = new JVMPIField[num_instance_fields];
            //JVMPIField f;
            for (int i = 0; i < num_instance_fields; i++) {
                instances[i] = new JVMPIField(in);
                /*
                f = new JVMPIField();
                f.field_name      = in.readUTF();
                f.field_signature = in.readUTF();
                instances[i] = f;
                */
            }
        } else {
            instances = null;
        }

        if ((info & ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID) != 0) {
            class_id = in.readInt();
        } else {
            class_id = 0;
        }
    }
}
