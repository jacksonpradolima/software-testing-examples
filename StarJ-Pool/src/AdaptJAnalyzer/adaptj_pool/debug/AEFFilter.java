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

package adaptj_pool.debug;

import adaptj_pool.*;
import adaptj_pool.event.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.JVMPI.*;
import adaptj_pool.util.OptionParser.*;
import adaptj_pool.util.*;

import org.apache.bcel.generic.InstructionHandle;

import java.io.*;
import java.text.DecimalFormat;

public class AEFFilter implements AdaptJSpecConstants, Runnable {
    public static final int PROCESSING_FAILED     = 0;
    public static final int PROCESSING_SUCCESSFUL = 1;

    public static final int AEFFILTER_OPTION_HELP         = 0;
    public static final int AEFFILTER_OPTION_SHOWVER      = 1;
    public static final int AEFFILTER_OPTION_VERSION      = 2;
    public static final int AEFFILTER_OPTION_PIPE         = 3;
    public static final int AEFFILTER_OPTION_NUM_EVENTS   = 4;
    public static final int AEFFILTER_OPTION_OUT_SPECFILE = 5;
    public static final int AEFFILTER_OPTION_CP           = 6;
    public static final int AEFFILTER_OPTION_SKIP         = 7;

    /* Parsed options */
    private String inFileName = null;
    private String outFileName = null;
    private String outSpecFileName = null;
    private long numEvents = -1L;
    private long skipEvents = -1L;
    private boolean pipeMode = false;
    
    private String commandLineArgs[];
    private OptionParser optionParser;

    private long[] counters;

    private short[] mergeFilters(short[] f, short[] g) {
        if (f == null) {
            return g;
        }

        if (g == null) {
            return f;
        }

        if (f.length != g.length) {
            throw new RuntimeException("Incompatible Filters");
        }

        short[] result = new short[f.length];
        for (int i = 0; i < f.length; i++) {
            result[i] = (short)((f[i] & g[i]) & 0x0000FFFF);
        }
        
        return result;
    }

    private boolean readSpecArray(DataInput in, short[] info, long counts[]) {
        int index;
        try {
            for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) {
                index = in.readByte();
                short s = in.readShort();
                info[index] = s;

                if ((s & ADAPTJ_FIELD_COUNTED) != 0) {
                    long l = in.readLong();
                    if (counts != null) {
                        counts[index] = l;
                    }
                }
            }
        } catch (IOException e) {
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }

        return true;
    }

    private boolean readSpecFile(DataInput inStream, short[] info) throws IOException {
        int magic = inStream.readInt();
        int version;

        try {
            version = Scene.v().checkVersion(magic, ADAPTJ_SPEC_MAGIC);
        } catch (AEFFormatException e) {
            return false;
        }

        if (!Scene.v().supports(version)) {
            return false;
        }

        return readSpecArray(inStream, info, null);
    }

    private void filterFile(BufferedFileReader in, DataOutputStream out, short[] specFilter) throws IOException, AEFFormatException {
        int magic = in.readInt();
        int version = -1;
        
        try {
            version = Scene.v().checkVersion(magic, ADAPTJ_MAGIC);
        } catch (AEFFormatException e) {
            Scene.v().reportError("Input file is not a valid AdaptJ data file or is corrupted");
            System.exit(1);
        }
        
        if (!Scene.v().supports(version)) {
            Scene.v().reportError("Input file has an unsupported version: " + version);
            System.exit(1);
        }

        short agentOptions = in.readShort();

        /* Read input Specification */
        short[] inputSpec = new short[AdaptJEvent.ADAPTJ_EVENT_COUNT];
        readSpecArray(in, inputSpec, null);

        /* Merge input spec and filter to create a new filter */
        short[] filter = mergeFilters(inputSpec, specFilter);

        /* Write Header */
        out.writeInt(magic);
        out.writeShort(agentOptions);
        for (int i = 0; i < filter.length; i++) {
            out.writeByte((byte) (i & 0x000000FF));
            out.writeShort(filter[i]);
            if ((filter[i] & ADAPTJ_FIELD_COUNTED) != 0) {
                out.writeLong(-1L);
            }
        }

    
        counters = new long[AdaptJEvent.ADAPTJ_EVENT_COUNT];
        for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) {
            counters[i] = 0L;
        }
    
        processFile(in, out, inputSpec, filter);
        /* NB: processFile has closed both 'in' and 'out' at this point */

        RandomAccessFile raFile = new RandomAccessFile(outFileName, "rw");
        raFile.seek(4);
        for (int i = 0; i < filter.length; i++) {
            out.writeByte((byte) (i & 0x000000FF));
            out.writeShort(filter[i]);
            if ((filter[i] & ADAPTJ_FIELD_COUNTED) != 0) {
                out.writeLong(counters[i]);
            }
        }
        raFile.close();
    }

    private void processFile(BufferedFileReader input, DataOutputStream output, short[] spec, short[] filter)
            throws IOException, AEFFormatException {
        int i;
        short s;
        long l;
        String str;
        byte b;
        boolean bool;

        BufferedFileReader in = input;
        DataOutputStream out = output;
        int fileCount = 0;

        ClassLoadEvent classLoadEvent = new ClassLoadEvent();
        ClassUnloadEvent classUnloadEvent = new ClassUnloadEvent();

        int typeID;
        long eventCount = 0L;

        while ((typeID = in.read()) != -1) {
            if (numEvents > 0 && (eventCount >= numEvents)) {
                break;
            }

            boolean requested = ((typeID & AdaptJEvent.ADAPTJ_REQUESTED_EVENT) != 0);
            typeID = (typeID & ~AdaptJEvent.ADAPTJ_REQUESTED_EVENT) & 0x000000FF; /* Clear 'requested' bit */

            int envID = -1;
            AdaptJEvent newEvent = null;

            /* Check for a split in the ADAPTJ Event File */
            if (typeID == ADAPTJ_FILESPLIT) {
                DecimalFormat format = new DecimalFormat("0000");
                /* Open the next file */
                String newFileName = in.readUTF();
                Scene.v().showDebug("AEF continued on file: \"" + newFileName + "\"");
                in.close();
                in = new BufferedFileReader(newFileName);
                fileCount++;
                String newOutName = outFileName + "_" + format.format(fileCount);
                out.writeByte(ADAPTJ_FILESPLIT);
                out.writeUTF(newOutName);
                out.close();
                out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newOutName)));
                Scene.v().showDebug("Output will continue on file: \"" + newOutName + "\"");
                continue;
            }

            if (typeID == ADAPTJ_COMPACT_INSTRUCTION_START) {
                int env_id = in.readInt();
                int methodID = in.readInt();
                int offset = in.readInt();
                int numInsts = in.readInt();
                short finfo = filter[AdaptJEvent.ADAPTJ_INSTRUCTION_START];

                if (skipEvents > 0L) {
                    skipEvents -= numInsts;
                    continue;
                }

                if ((finfo & ADAPTJ_FIELD_RECORDED) == 0) {
                    continue;
                }
                
                if (spec[AdaptJEvent.ADAPTJ_INSTRUCTION_START] == finfo) {
                    b = (byte)(typeID & 0x000000FF);
                    out.write(b);
                    
                    out.writeInt(env_id);
                    out.writeInt(methodID);
                    out.writeInt(offset);
                    out.writeInt(numInsts);

                    counters[AdaptJEvent.ADAPTJ_INSTRUCTION_START] += numInsts;
                    eventCount += numInsts;
                } else {
                    InstructionHandle instHandle = BytecodeResolver.v().getInstructionHandle(methodID, offset);
                    
                    if ((finfo & ADAPTJ_FIELD_RECORDED) != (short) 0) {
                        for (int j = 0; j < numInsts; j++) {
                            out.write((byte)(AdaptJEvent.ADAPTJ_INSTRUCTION_START & 0x000000FF));
                            if ((finfo & ADAPTJ_FIELD_ENV_ID) != (short) 0) {
                                out.writeInt(env_id);
                            }
                            if ((finfo & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                                out.writeInt(methodID);
                            }
                            if ((finfo & ADAPTJ_FIELD_OFFSET) != (short) 0) {
                                offset = instHandle.getPosition();
                                out.writeInt(offset);
                            }

                            instHandle = instHandle.getNext();
                        }

                        counters[AdaptJEvent.ADAPTJ_INSTRUCTION_START] += numInsts;
                        eventCount += numInsts;
                    }
                }

                continue;
              
            }

            if (typeID < 0 || typeID >= AdaptJEvent.ADAPTJ_EVENT_COUNT) {
                throw new AEFFormatException("Unrecognized AdaptJ ID: " + typeID
                                             + " (file offset=" + in.getFilePointer() + ")");
            }
           
            /* Get the Event Specification for this Event ID */
            short info = spec[typeID];
            short finfo = filter[typeID];
            
            /* Check if this event must be copied */
            if ((finfo & ADAPTJ_FIELD_RECORDED) == 0) {
                finfo = (short) 0;
            }
            
            if (skipEvents > 0L
                    //&& finfo != (short) 0
                    && typeID != AdaptJEvent.ADAPTJ_CLASS_LOAD
                    && typeID != AdaptJEvent.ADAPTJ_CLASS_UNLOAD
                    && typeID != AdaptJEvent.ADAPTJ_OBJECT_ALLOC) {
                skipEvents--;
                finfo = (short) 0;
            }

            counters[typeID] += 1L;
            eventCount++;

            if ((finfo & ADAPTJ_FIELD_RECORDED) != 0) {
                b = (byte)(typeID & 0x000000FF);
                if (requested) {
                    b |= (byte) AdaptJEvent.ADAPTJ_REQUESTED_EVENT;
                }
                out.write(b);
            }

            if ((info & ADAPTJ_FIELD_ENV_ID) != (short) 0) {
                envID = in.readInt();
                if ((finfo & ADAPTJ_FIELD_ENV_ID) != (short) 0) {
                    out.writeInt(envID);
                }
            }

            switch (typeID) {
                case AdaptJEvent.ADAPTJ_ARENA_DELETE:
                    if ((info & ADAPTJ_FIELD_ARENA_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_ARENA_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_ARENA_NEW:
                    if ((info & ADAPTJ_FIELD_ARENA_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_ARENA_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_ARENA_NAME) != (short) 0) {
                        str = in.readUTF();
                        if ((finfo & ADAPTJ_FIELD_ARENA_NAME) != (short) 0) {
                            out.writeUTF(str);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                    if ((info & ADAPTJ_FIELD_CLASS_NAME) != (short) 0) {
                        classLoadEvent.setClassName(in.readUTF());
                        if ((finfo & ADAPTJ_FIELD_CLASS_NAME) != (short) 0) {
                            out.writeUTF(classLoadEvent.getClassName());
                        }
                    }
                    if ((info & ADAPTJ_FIELD_SOURCE_NAME) != (short) 0) {
                        classLoadEvent.setSourceName(in.readUTF());
                        if ((finfo & ADAPTJ_FIELD_SOURCE_NAME) != (short) 0) {
                            out.writeUTF(classLoadEvent.getSourceName());
                        }
                    }
                    if ((info & ADAPTJ_FIELD_NUM_INTERFACES) != (short) 0) {
                        classLoadEvent.setNumInterfaces(in.readInt());
                        if ((finfo & ADAPTJ_FIELD_NUM_INTERFACES) != (short) 0) {
                            out.writeInt(classLoadEvent.getNumInterfaces());
                        }
                    }
                    int num_methods = 0;
                    if ((info & ADAPTJ_FIELD_NUM_METHODS) != (short) 0
                            || (info & ADAPTJ_FIELD_METHODS) != (short) 0) {
                        num_methods = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_NUM_METHODS) != (short) 0
                                || (finfo & ADAPTJ_FIELD_METHODS) != (short) 0) {
                            out.writeInt(num_methods);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_METHODS) != (short) 0 
                            && num_methods > 0) {
                        JVMPIMethod[] methods = new JVMPIMethod[num_methods];
                        JVMPIMethod m;
                        for (int j = 0; j < num_methods; j++) {
                            m = new JVMPIMethod(in);
                            methods[j] = m;
                            /*
                            m = new JVMPIMethod();
                            m.method_name      = in.readUTF();
                            m.method_signature = in.readUTF();
                            m.start_lineno     = in.readInt();
                            m.end_lineno       = in.readInt();
                            m.method_id        = in.readInt();

                            methods[j] = m;
                            */
                            
                            if ((finfo & ADAPTJ_FIELD_METHODS) != (short) 0) {
                                out.writeUTF(m.getMethodName());
                                out.writeUTF(m.getMethodSignature());
                                out.writeInt(m.getStartLineno());
                                out.writeInt(m.getEndLineno());
                                out.writeInt(m.getMethodID());
                            }
                        }

                        classLoadEvent.setMethods(methods);
                    }
                    int num_static_fields = 0;
                    if ((info & ADAPTJ_FIELD_NUM_STATIC_FIELDS) != (short) 0
                            || (info & ADAPTJ_FIELD_STATICS) != (short) 0) {
                        num_static_fields = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_NUM_STATIC_FIELDS) != (short) 0
                                || (finfo & ADAPTJ_FIELD_STATICS) != (short) 0) {
                            out.writeInt(num_static_fields);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_STATICS) != (short) 0 
                            && num_static_fields > 0) {
                        JVMPIField[] statics = new JVMPIField[num_static_fields];
                        JVMPIField f;
                        for (int j = 0; j < num_static_fields; j++) {
                            f = new JVMPIField(in);
                            statics[j] = f;
                            /*
                            f = new JVMPIField();
                            f.field_name      = in.readUTF();
                            f.field_signature = in.readUTF();
                            
                            statics[j] = f;
                            */
                            
                            if ((finfo & ADAPTJ_FIELD_STATICS) != (short) 0) {
                                out.writeUTF(f.getFieldName());
                                out.writeUTF(f.getFieldSignature());
                            }
                        }

                        classLoadEvent.setStatics(statics);
                    }
                    int num_instance_fields = 0;
                    if ((info & ADAPTJ_FIELD_NUM_INSTANCE_FIELDS) != (short) 0
                            || (info & ADAPTJ_FIELD_INSTANCES) != (short) 0) {
                        num_instance_fields = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_NUM_INSTANCE_FIELDS) != (short) 0
                                || (finfo & ADAPTJ_FIELD_INSTANCES) != (short) 0) {
                            out.writeInt(num_instance_fields);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_INSTANCES) != (short) 0 
                            && num_instance_fields > 0) {
                        JVMPIField[] instances = new JVMPIField[num_instance_fields];
                        JVMPIField f;
                        for (int j = 0; j < num_instance_fields; j++) {
                            f = new JVMPIField(in);
                            instances[j] = f;
                            /*
                            f = new JVMPIField();
                            f.field_name      = in.readUTF();
                            f.field_signature = in.readUTF();

                            instances[j] = f;
                            */
                            
                            if ((finfo & ADAPTJ_FIELD_INSTANCES) != (short) 0) {
                                out.writeUTF(f.getFieldName());
                                out.writeUTF(f.getFieldSignature());
                            }
                        }

                        classLoadEvent.setInstances(instances);
                    }

                    if ((info & ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID) != (short) 0) {
                        classLoadEvent.setClassID(in.readInt());
                        if ((finfo & ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID) != (short) 0) {
                            out.writeInt(classLoadEvent.getClassID());
                        }
                    }
                    if ((info & ADAPTJ_FIELD_CLASS_NAME) != (short) 0
                            && (info & ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID) != (short) 0) {
                        classLoadEvent.setEnvID(envID);
                        classLoadEvent.setRequested(requested);
                        BytecodeResolver.v().loadClass(classLoadEvent);
                    }
                    break;
                case AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK:
                    // TODO
                    break;
                case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                    if ((info & ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID) != (short) 0) {
                        classUnloadEvent.setClassID(in.readInt());
                        if ((finfo & ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID) != (short) 0) {
                            out.writeInt(classUnloadEvent.getClassID());
                        }
                    }
                    if ((info & ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID) != (short) 0) {
                        classUnloadEvent.setEnvID(envID);
                        classUnloadEvent.setRequested(requested);
                        BytecodeResolver.v().unloadClass(classUnloadEvent);
                    }
                    break;
                case AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD:
                    int code_size = -1;
                    int lineno_table_size = -1;
                    if ((info & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_CODE_SIZE) != (short) 0
                            || (info & ADAPTJ_FIELD_CODE) != (short) 0) {
                        code_size = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_CODE_SIZE) != (short) 0
                                || (finfo & ADAPTJ_FIELD_CODE) != (short) 0) {
                            out.writeInt(code_size);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_CODE) != (short) 0
                            && code_size > 0) {
                        byte[] code = new byte[code_size];
                        in.read(code);
                        if ((finfo & ADAPTJ_FIELD_CODE) != (short) 0) {
                            out.write(code);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_LINENO_TABLE_SIZE) != (short) 0
                            || (info & ADAPTJ_FIELD_LINENO_TABLE) != (short) 0) {
                        lineno_table_size = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_LINENO_TABLE_SIZE) != (short) 0
                                || (finfo & ADAPTJ_FIELD_LINENO_TABLE) != (short) 0) {
                            out.writeInt(lineno_table_size);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_LINENO_TABLE) != (short) 0
                            && lineno_table_size > 0) {
                        JVMPILineno line; // = new JVMPILineno();
                        for (int j = 0; j < lineno_table_size; j++) {
                            line = new JVMPILineno(in);
                            //line.offset = in.readInt();
                            //line.lineno = in.readInt();
                            if ((finfo & ADAPTJ_FIELD_LINENO_TABLE) != (short) 0) {
                                out.writeInt(line.getOffset());
                                out.writeInt(line.getLineno());
                            }
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD:
                    if ((info & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST:
                case AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST:
                    // TODO
                    break;
                case AdaptJEvent.ADAPTJ_GC_FINISH:
                    if ((info & ADAPTJ_FIELD_USED_OBJECTS) != (short) 0) {
                        l = in.readLong();
                        if ((finfo & ADAPTJ_FIELD_USED_OBJECTS) != (short) 0) {
                            out.writeLong(l);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_USED_OBJECT_SPACE) != (short) 0) {
                        l = in.readLong();
                        if ((finfo & ADAPTJ_FIELD_USED_OBJECT_SPACE) != (short) 0) {
                            out.writeLong(l);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_TOTAL_OBJECT_SPACE) != (short) 0) {
                        l = in.readLong();
                        if ((finfo & ADAPTJ_FIELD_TOTAL_OBJECT_SPACE) != (short) 0) {
                            out.writeLong(l);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_GC_START:
                    /* Nothing to do */
                    break;
                case AdaptJEvent.ADAPTJ_HEAP_DUMP:
                case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC:
                case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE:
                case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC:
                case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE:
                    // TODO
                    break;
                case AdaptJEvent.ADAPTJ_JVM_INIT_DONE:
                    /* Nothing to do */
                    break;
                case AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN:
                    /* Nothing to do */
                    break;
                case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
                    if ((info & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                    if ((info & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_OBJ_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJ_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                    if ((info & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER:
                    if ((info & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED:
                    if ((info & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT:
                    if ((info & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_MONITOR_DUMP:
                    /* FIXME */
                    break;
                case AdaptJEvent.ADAPTJ_MONITOR_WAIT:
                    if ((info & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_TIMEOUT) != (short) 0) {
                        l = in.readLong();
                        if ((finfo & ADAPTJ_FIELD_TIMEOUT) != (short) 0) {
                            out.writeLong(l);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_MONITOR_WAITED:
                    if ((info & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJECT) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_TIMEOUT) != (short) 0) {
                        l = in.readLong();
                        if ((finfo & ADAPTJ_FIELD_TIMEOUT) != (short) 0) {
                            out.writeLong(l);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                    if ((info & ADAPTJ_FIELD_ARENA_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_ARENA_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_IS_ARRAY) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_IS_ARRAY) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_SIZE) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_SIZE) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_OBJ_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJ_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_OBJECT_DUMP:
                    int data_len = -1;
                    if ((info & ADAPTJ_FIELD_DATA_LEN) != (short) 0
                            || (info & ADAPTJ_FIELD_DATA) != (short) 0) {
                        data_len = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_DATA_LEN) != (short) 0
                                || (finfo & ADAPTJ_FIELD_DATA) != (short) 0) {
                            out.writeInt(data_len);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_DATA_LEN) != (short) 0
                            && data_len > 0) {
                        byte[] data = new byte[data_len];
                        in.read(data);
                        if ((finfo & ADAPTJ_FIELD_DATA_LEN) != (short) 0) {
                            out.write(data);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_OBJECT_FREE:
                    if ((info & ADAPTJ_FIELD_OBJ_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJ_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_OBJECT_MOVE:
                    if ((info & ADAPTJ_FIELD_ARENA_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_ARENA_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_OBJ_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OBJ_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_NEW_ARENA_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_NEW_ARENA_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_NEW_OBJ_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_NEW_OBJ_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER:
                case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED:
                case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT:
                    if ((info & ADAPTJ_FIELD_NAME) != (short) 0) {
                        str = in.readUTF();
                        if ((finfo & ADAPTJ_FIELD_NAME) != (short) 0) {
                            out.writeUTF(str);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_THREAD_END:
                    /* Nothing to do */
                    break;
                case AdaptJEvent.ADAPTJ_THREAD_START:
                    if ((info & ADAPTJ_FIELD_THREAD_NAME) != (short) 0) {
                        str = in.readUTF();
                        if ((finfo & ADAPTJ_FIELD_THREAD_NAME) != (short) 0) {
                            out.writeUTF(str);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_GROUP_NAME) != (short) 0) {
                        str = in.readUTF();
                        if ((finfo & ADAPTJ_FIELD_GROUP_NAME) != (short) 0) {
                            out.writeUTF(str);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_PARENT_NAME) != (short) 0) {
                        str = in.readUTF();
                        if ((finfo & ADAPTJ_FIELD_PARENT_NAME) != (short) 0) {
                            out.writeUTF(str);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_THREAD_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_THREAD_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_THREAD_ENV_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_THREAD_ENV_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                    if ((info & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_METHOD_ID) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_OFFSET) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_OFFSET) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_IS_TRUE) != (short) 0) {
                        bool = in.readBoolean();
                        if ((finfo & ADAPTJ_FIELD_IS_TRUE) != (short) 0) {
                            out.writeBoolean(bool);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_KEY) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_KEY) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_LOW) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_LOW) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_HI) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_HI) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_CHOSEN_PAIR_INDEX) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_CHOSEN_PAIR_INDEX) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    if ((info & ADAPTJ_FIELD_PAIRS_TOTAL) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_PAIRS_TOTAL) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                case AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE:
                    if ((info & ADAPTJ_FIELD_NEW_STATUS) != (short) 0) {
                        i = in.readInt();
                        if ((finfo & ADAPTJ_FIELD_NEW_STATUS) != (short) 0) {
                            out.writeInt(i);
                        }
                    }
                    break;
                default:
                    throw new AEFFormatException("Unkown event type: " + typeID 
                                                 + " (file offset=" + in.getFilePointer() + ")");
            }
        }

        in.close();
        out.close();
    }

    public void printVersion() {
        System.out.println(Main.ADAPTJ_HELP_HEADER);
        System.out.println();
        System.out.println(Main.ADAPTJ_HELP_FOOTER);
    }

    public void printUsage() {
        optionParser.printUsage(System.out, "<trace file> <output file>");
    }
    
    public static void main(String args[]) throws Exception {
        AEFFilter m = new AEFFilter(args);
        (new Thread(m)).start();
    }

    public AEFFilter(String args[]) {
        commandLineArgs = new String[args.length];
        System.arraycopy(args, 0, commandLineArgs, 0, args.length);

        optionParser = new OptionParser("java adaptj_pool.debug.AEFFilter", Main.ADAPTJ_HELP_HEADER, Main.ADAPTJ_HELP_FOOTER);
        initOptionParser();
    }

    private void initOptionParser() {
        optionParser.combineShortSwitches(true);
        optionParser.aggregateNonOptions(true);
        optionParser.shortOptionNoSpace(true);

        StringArgument strArg = new StringArgument(true);

        BasicOption helpOption = new BasicOption("Prints help and exits");
        helpOption.addShortSwitch("h");
        helpOption.addLongSwitch("help");
        optionParser.addOption(helpOption, AEFFILTER_OPTION_HELP);

        BasicOption showverOption = new BasicOption("Prints the version and continues");
        showverOption.addLongSwitch("showver");
        optionParser.addOption(showverOption, AEFFILTER_OPTION_SHOWVER);

        BasicOption versionOption = new BasicOption("Prints the version and exits");
        versionOption.addLongSwitch("version");
        optionParser.addOption(versionOption, AEFFILTER_OPTION_VERSION);

        BasicOption pipeOption = new BasicOption("Forces to read input as if from a pipe (no preparsing)");
        pipeOption.addLongSwitch("pipe");
        optionParser.addOption(pipeOption, AEFFILTER_OPTION_PIPE);

        BasicOption numEventsOption = new BasicOption("Specifies the number of events to keep in the output file");
        numEventsOption.addShortSwitch("n");
        numEventsOption.addLongSwitch("numevents");
        numEventsOption.addArgument(new LongArgument(true), "number");
        optionParser.addOption(numEventsOption, AEFFILTER_OPTION_NUM_EVENTS);
        
        BasicOption outSpecFileOption = new BasicOption("Specifies an AdaptJ Specification file to use as filter");
        outSpecFileOption.addShortSwitch("s");
        outSpecFileOption.addLongSwitch("specfile");
        outSpecFileOption.addArgument(strArg, "file name");
        optionParser.addOption(outSpecFileOption, AEFFILTER_OPTION_OUT_SPECFILE);

        BasicOption classpathOption = new BasicOption("Manipulates the classpath used by AdaptJ");
        classpathOption.addShortSwitch("cp");
        classpathOption.addLongSwitch("classpath");
        classpathOption.addArgument(strArg, "operation");
        classpathOption.addArgument(strArg, "paths");
        optionParser.addOption(classpathOption, AEFFILTER_OPTION_CP);
        
        BasicOption skipEventsOption = new BasicOption("Specifies the number of events to skip before writing");
        skipEventsOption.addShortSwitch("k");
        skipEventsOption.addLongSwitch("skip");
        skipEventsOption.addArgument(new LongArgument(true), "number");
        optionParser.addOption(skipEventsOption, AEFFILTER_OPTION_SKIP);
    }

    public void run() {
        try {
            parseCommandLineArgs();
        } catch (ProcessingDeathException e) {
            if (e.getStatus() != PROCESSING_SUCCESSFUL) {                
                Scene.v().reportError("Error while parsing the arguments: " + e.getMessage());
                printUsage();
                System.exit(1);   
            }

            System.exit(0);
        }

        /* Open input file */
        BufferedFileReader inFile = null;
        try {
            inFile = new BufferedFileReader(inFileName);
        } catch (FileNotFoundException e) {
            Scene.v().reportFileNotFoundError(inFileName);
            System.exit(1);
        } catch (IOException e) {
            Scene.v().reportFileOpenError(inFileName);
            System.exit(1);
        }

        /* Open output file */
        DataOutputStream outFile = null;
        try {
            outFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFileName)));
        } catch (IOException e) {
            Scene.v().reportFileOpenError(outFileName);
            System.exit(1);
        }

        /* Open spec file, if needed */
        short[] filter = null;
        if (outSpecFileName != null) {
            DataInputStream specFile;
            try {
                specFile = new DataInputStream(new FileInputStream(outSpecFileName));
                filter = new short[AdaptJEvent.ADAPTJ_EVENT_COUNT];
                if (!readSpecFile(specFile, filter)) {
                    Scene.v().reportError("File \"" + outSpecFileName + "\" is an invalid spec file or is corrupted");
                }
            } catch (FileNotFoundException e) {
                Scene.v().reportFileNotFoundError(outSpecFileName);
                System.exit(1);
            } catch (IOException e) {
                Scene.v().reportFileOpenError(outSpecFileName);
                System.exit(1);
            }
            
        }

        /* Go to work */
        try {
            filterFile(inFile, outFile, filter);
        } catch (IOException e) {
            throw new RuntimeException("IOException occured while processing files");
        } catch (AEFFormatException e) {
            Scene.v().reportError("File \"" + inFileName + "\" is an invalid AdaptJ file or is corrupted");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.exit(0);
    }

    public void parseCommandLineArgs() {
        ParsedObject po = null;
        try {
            po = optionParser.parse(commandLineArgs);
        } catch (OptionProcessingException e) {
            throw new ProcessingDeathException(PROCESSING_FAILED, e.getMessage());
        }

        while (po != null) {
            if (po instanceof ParsedOption) {
                ParsedOption option = (ParsedOption) po;
                Object[] args = null;

                switch (option.getOptionID()) {
                    case AEFFILTER_OPTION_HELP:
                        optionParser.printHelp(System.out, "<trace file> <output file>");
                        throw new ProcessingDeathException(PROCESSING_SUCCESSFUL);
                    case AEFFILTER_OPTION_SHOWVER:
                        printVersion();
                        System.out.println();
                        break;
                    case AEFFILTER_OPTION_VERSION:
                        printVersion();
                        throw new ProcessingDeathException(PROCESSING_SUCCESSFUL);
                    case AEFFILTER_OPTION_PIPE:
                        pipeMode = true;
                        break;
                    case AEFFILTER_OPTION_NUM_EVENTS:
                        {
                            args = option.getArguments();
                            long val = ((Long) args[0]).longValue();
                            if (val <= 0L) {
                                throw new ProcessingDeathException(PROCESSING_FAILED, "The number of events must be positive");
                            }
                            numEvents = val;
                        }
                        break;
                    case AEFFILTER_OPTION_SKIP:
                        {
                            args = option.getArguments();
                            long val = ((Long) args[0]).longValue();
                            if (val <= 0L) {
                                throw new ProcessingDeathException(PROCESSING_FAILED, "The number of events must be positive");
                            }
                            skipEvents = val;
                        }
                        break;
                    case AEFFILTER_OPTION_OUT_SPECFILE:
                        if (outSpecFileName != null) {
                            throw new ProcessingDeathException(PROCESSING_FAILED, "More than one spec file specified");
                        }
                        args = option.getArguments();
                        outSpecFileName = (String) args[0];
                        break;
                    case AEFFILTER_OPTION_CP:
                        args = option.getArguments();
                        String op = (String) args[0];
                        String paths = (String) args[1];
                        boolean result;
                        
                        if (op.equals("add") || op.equals("addAfter")) {
                            result = Scene.v().addToClassPathAfter(paths);
                        } else if (op.equals("addBefore")) {
                            result = Scene.v().addToClassPathBefore(paths);
                        } else if (op.equals("set")) {
                            result = Scene.v().setClassPath(paths);
                        } else if (op.equals("remove")) {
                            result = Scene.v().removeFromClassPath(paths);
                        } else {
                            throw new ProcessingDeathException(PROCESSING_FAILED, "Unknown classpath operation: " + op);
                        }

                        if (!result) {
                            throw new ProcessingDeathException(PROCESSING_FAILED, "Classpath operation \"" + op + "\" failed");
                        }
                        break;
                    default:
                }

            } else if (po instanceof ParsedNonOption) {
                if (inFileName == null) {
                    inFileName = ((ParsedNonOption) po).getValue();
                } else if (outFileName == null) {
                    outFileName = ((ParsedNonOption) po).getValue();
                } else {
                    throw new ProcessingDeathException(PROCESSING_FAILED, "More than two files specified");
                }
            } else {
                throw new RuntimeException("Unknown parsed object type");
            }
            po = po.getNext();
        }

        if (inFileName == null) {
            throw new ProcessingDeathException(PROCESSING_FAILED, "No input file name specified");
        }
        
        if (outFileName == null) {
            throw new ProcessingDeathException(PROCESSING_FAILED, "No input file name specified");
        }
    }
}
