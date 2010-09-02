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

package adaptj_pool.spec;

import adaptj_pool.*;
import adaptj_pool.event.AdaptJEvent;

import java.io.*;

public class Extractor implements AdaptJSpecConstants {

    private static String getEventNameFromByte(byte eventID) {
        switch (eventID) {
            case AdaptJEvent.ADAPTJ_ARENA_DELETE:
                return "ArenaDelete";
            case AdaptJEvent.ADAPTJ_ARENA_NEW:
                return "ArenaNew";
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                return "ClassLoad";
            case AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK:
                return "ClassLoadHook";
            case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                return "ClassUnload";
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD:
                return "CompiledMethodLoad";
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD:
                return "CompiledMethodUnload";
            case AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST:
                return "DataDumpRequest";
            case AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST:
                return "DataResetRequest";
            case AdaptJEvent.ADAPTJ_GC_FINISH:
                return "GCFinish";
            case AdaptJEvent.ADAPTJ_GC_START:
                return "GCStart";
            case AdaptJEvent.ADAPTJ_HEAP_DUMP:
                return "HeapDump";
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC:
                return "JNIGlobalRefAlloc";
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE:
                return "JNIGlobalRefFree";
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC:
                return "JNIWeakGlobalRefAlloc";
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE:
                return "JNIWeakGlobalRefFree";
            case AdaptJEvent.ADAPTJ_JVM_INIT_DONE:
                return "JVMInitDone";
            case AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN:
                return "JVMShutDown";
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
                return "MethodEntry";
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                return "MethodEntry2";
            case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                return "MethodExit";
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER:
                return "MonitorContendedEnter";
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED:
                return "MonitorContendedEntered";
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT:
                return "MonitorContendedExit";
            case AdaptJEvent.ADAPTJ_MONITOR_DUMP:
                return "MonitorDump";
            case AdaptJEvent.ADAPTJ_MONITOR_WAIT:
                return "MonitorWait";
            case AdaptJEvent.ADAPTJ_MONITOR_WAITED:
                return "MonitorWaited";
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                return "ObjectAlloc";
            case AdaptJEvent.ADAPTJ_OBJECT_DUMP:
                return "ObjectDump";
            case AdaptJEvent.ADAPTJ_OBJECT_FREE:
                return "ObjectFree";
            case AdaptJEvent.ADAPTJ_OBJECT_MOVE:
                return "ObjectMove";
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER:
                return "RawMonitorContendedEnter";
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED:
                return "RawMonitorContendedEntered";
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT:
                return "RawMonitorContendedExit";
            case AdaptJEvent.ADAPTJ_THREAD_END:
                return "ThreadEnd";
            case AdaptJEvent.ADAPTJ_THREAD_START:
                return "ThreadStart";
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                return "InstructionStart";
            case AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE:
                return "ThreadStatusChange";
            default:
                return null;
        }
    }

    private static void translateBitMask(short bits, byte eventID, PrintStream out) {
        if ((bits & ADAPTJ_FIELD_RECORDED) != 0) {
            out.println("    recorded: yes");
        }
        if ((bits & ADAPTJ_FIELD_COUNTED) != 0) {
            out.println("    counted: yes");
        }
        if ((bits & ADAPTJ_FIELD_ENV_ID) != 0) {
            out.println("    env_id: yes");
        }
        out.println();

        switch (eventID) {
            case AdaptJEvent.ADAPTJ_ARENA_DELETE:
                if ((bits & ADAPTJ_FIELD_ARENA_ID) != 0) {
                    out.println("    arena_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_ARENA_NEW:
                if ((bits & ADAPTJ_FIELD_ARENA_ID) != 0) {
                    out.println("    arena_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_ARENA_ID) != 0) {
                    out.println("    arena_name: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                if ((bits & ADAPTJ_FIELD_CLASS_NAME) != 0) {
                    out.println("    class_name: yes");
                }
                if ((bits & ADAPTJ_FIELD_SOURCE_NAME) != 0) {
                    out.println("    source_name: yes");
                }
                if ((bits & ADAPTJ_FIELD_NUM_INTERFACES) != 0) {
                    out.println("    num_interfaces: yes");
                }
                if ((bits & ADAPTJ_FIELD_NUM_METHODS) != 0) {
                    out.println("    num_methods: yes");
                }
                if ((bits & ADAPTJ_FIELD_METHODS) != 0) {
                    out.println("    methods: yes");
                }
                if ((bits & ADAPTJ_FIELD_NUM_STATIC_FIELDS) != 0) {
                    out.println("    num_static_fields: yes");
                }
                if ((bits & ADAPTJ_FIELD_STATICS) != 0) {
                    out.println("    statics: yes");
                }
                if ((bits & ADAPTJ_FIELD_NUM_INSTANCE_FIELDS) != 0) {
                    out.println("    num_instance_fields: yes");
                }
                if ((bits & ADAPTJ_FIELD_INSTANCES) != 0) {
                    out.println("    instances: yes");
                }
                if ((bits & ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID) != 0) {
                    out.println("    class_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK:
                // TODO
                break;
            case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                if ((bits & ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID) != 0) {
                    out.println("    class_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD:
                if ((bits & ADAPTJ_FIELD_METHOD_ID) != 0) {
                    out.println("    method_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_CODE_SIZE) != 0) {
                    out.println("    code_size: yes");
                }
                if ((bits & ADAPTJ_FIELD_CODE) != 0) {
                    out.println("    code: yes");
                }
                if ((bits & ADAPTJ_FIELD_LINENO_TABLE_SIZE) != 0) {
                    out.println("    lineno_table_size");
                }
                if ((bits & ADAPTJ_FIELD_LINENO_TABLE) != 0) {
                    out.println("    lineno_table");
                }
                break;
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD:
                if ((bits & ADAPTJ_FIELD_METHOD_ID) != 0) {
                    out.println("    method_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST:
            case AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST:
                /* No event specific information */
                break;
            case AdaptJEvent.ADAPTJ_GC_FINISH:
                if ((bits & ADAPTJ_FIELD_USED_OBJECTS) != 0) {
                    out.println("    used_objects: yes");
                }
                if ((bits & ADAPTJ_FIELD_USED_OBJECT_SPACE) != 0) {
                    out.println("    used_object_space: yes");
                }
                if ((bits & ADAPTJ_FIELD_TOTAL_OBJECT_SPACE) != 0) {
                    out.println("    total_object_space: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_GC_START:
                /* No event specific information */
                break;
            case AdaptJEvent.ADAPTJ_HEAP_DUMP:
                // TODO
                break;
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC:
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC:
                if ((bits & ADAPTJ_FIELD_REF_ID) != 0) {
                    out.println("    ref_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_OBJ_ID) != 0) {
                    out.println("    obj_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE:
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE:
                if ((bits & ADAPTJ_FIELD_REF_ID) != 0) {
                    out.println("    ref_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_JVM_INIT_DONE:
            case AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN:
                /* No event specific information */
                break;
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
            case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                if ((bits & ADAPTJ_FIELD_METHOD_ID) != 0) {
                    out.println("    method_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                if ((bits & ADAPTJ_FIELD_METHOD_ID) != 0) {
                    out.println("    method_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_OBJ_ID) != 0) {
                    out.println("    obj_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER:
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED:
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT:
                if ((bits & ADAPTJ_FIELD_OBJECT) != 0) {
                    out.println("    object: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_MONITOR_DUMP:
                if ((bits & ADAPTJ_FIELD_DATA_LEN) != 0) {
                    out.println("    data_len: yes");
                }
                if ((bits & ADAPTJ_FIELD_DATA) != 0) {
                    out.println("    data: yes");
                }
                if ((bits & ADAPTJ_FIELD_NUM_TRACES) != 0) {
                    out.println("    num_traces: yes");
                }
                if ((bits & ADAPTJ_FIELD_TRACES) != 0) {
                    out.println("    traces: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_MONITOR_WAIT:
            case AdaptJEvent.ADAPTJ_MONITOR_WAITED:
                if ((bits & ADAPTJ_FIELD_OBJECT) != 0) {
                    out.println("    object: yes");
                }
                if ((bits & ADAPTJ_FIELD_TIMEOUT) != 0) {
                    out.println("    object: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                if ((bits & ADAPTJ_FIELD_ARENA_ID) != 0) {
                    out.println("    arena_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_OBJ_ID) != 0) {
                    out.println("    obj_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_IS_ARRAY) != 0) {
                    out.println("    is_array: yes");
                }
                if ((bits & ADAPTJ_FIELD_SIZE) != 0) {
                    out.println("    size: yes");
                }
                if ((bits & ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID) != 0) {
                    out.println("    class_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_DUMP:
                if ((bits & ADAPTJ_FIELD_DATA_LEN) != 0) {
                    out.println("    data_len: yes");
                }
                if ((bits & ADAPTJ_FIELD_DATA) != 0) {
                    out.println("    data: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_FREE:
                if ((bits & ADAPTJ_FIELD_OBJ_ID) != 0) {
                    out.println("    obj_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_MOVE:
                if ((bits & ADAPTJ_FIELD_ARENA_ID) != 0) {
                    out.println("    arena_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_OBJ_ID) != 0) {
                    out.println("    obj_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_NEW_ARENA_ID) != 0) {
                    out.println("    new_arena_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_NEW_OBJ_ID) != 0) {
                    out.println("    new_obj_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER:
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED:
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT:
                if ((bits & ADAPTJ_FIELD_NAME) != 0) {
                    out.println("    name: yes");
                }
                if ((bits & ADAPTJ_FIELD_ID) != 0) {
                    out.println("    id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_THREAD_END:
                /* No event specific information */
                break;
            case AdaptJEvent.ADAPTJ_THREAD_START:
                if ((bits & ADAPTJ_FIELD_THREAD_NAME) != 0) {
                    out.println("    thread_name: yes");
                }
                if ((bits & ADAPTJ_FIELD_GROUP_NAME) != 0) {
                    out.println("    group_name: yes");
                }
                if ((bits & ADAPTJ_FIELD_PARENT_NAME) != 0) {
                    out.println("    parent_name: yes");
                }
                if ((bits & ADAPTJ_FIELD_THREAD_ID) != 0) {
                    out.println("    thread_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_THREAD_ENV_ID) != 0) {
                    out.println("    thread_env_id: yes");
                }
                break;
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                if ((bits & ADAPTJ_FIELD_METHOD_ID) != 0) {
                    out.println("    method_id: yes");
                }
                if ((bits & ADAPTJ_FIELD_OFFSET) != 0) {
                    out.println("    offset: yes");
                }
                if ((bits & ADAPTJ_FIELD_IS_TRUE) != 0) {
                    out.println("    is_true: yes");
                }
                if ((bits & ADAPTJ_FIELD_KEY) != 0) {
                    out.println("    key: yes");
                }
                if ((bits & ADAPTJ_FIELD_LOW) != 0) {
                    out.println("    low: yes");
                }
                if ((bits & ADAPTJ_FIELD_HI) != 0) {
                    out.println("    hi: yes");
                }
                if ((bits & ADAPTJ_FIELD_CHOSEN_PAIR_INDEX) != 0) {
                    out.println("    chosen_pair_index: yes");
                }
                if ((bits & ADAPTJ_FIELD_PAIRS_TOTAL) != 0) {
                    out.println("    pairs_total");
                }
                break;
            case AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE:
                if ((bits & ADAPTJ_FIELD_NEW_STATUS) != 0) {
                    out.println("    new_status: yes");
                }
                break;
            default:
                Scene.v().reportError("Unknown event ID: " + eventID);
                break;
        }
    }

    private static boolean readSpecArray(DataInput in, short[] info, long counts[]) {
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

    private static boolean readSpecFile(DataInput inStream, short[] info) throws IOException {
        int magic = inStream.readInt();
        int version;
        
        try {
            version = Scene.v().checkVersion(magic, ADAPTJ_MAGIC);
            short agentOptions = inStream.readShort();
        } catch (AEFFormatException e) {
            try {
                version = Scene.v().checkVersion(magic, ADAPTJ_SPEC_MAGIC);
            } catch (AEFFormatException e2) {
                Scene.v().reportError("Input file is not a valid AdaptJ data file or is corrupted");
                return false;
            }
        }

        if (!Scene.v().supports(version)) {
            Scene.v().reportError("Input file has an unsupported version: " + version);
            return false;
        }
        
        /*
        if (magic == ADAPTJ_MAGIC) {
            short agentOptions = inStream.readShort();
        } else if (magic != ADAPTJ_SPEC_MAGIC) {
            return false;
        }
        */

        return readSpecArray(inStream, info, null);
    }

    public static void main(String[] args) {
        String inFile;
        String outFile;

        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java adaptj_pool.spec.Extractor <input file> [output file]");
            System.exit(1);
        }

        inFile = args[0];
        
        if (args.length > 1) {
            outFile = args[1];
        } else {
            outFile = null;
        }
        
        short[] spec = new short[AdaptJEvent.ADAPTJ_EVENT_COUNT];
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(inFile));
            if (!readSpecFile(in, spec)) {
                Scene.v().reportError("Failed to read input file");
                System.exit(2);
            }
        } catch (FileNotFoundException e) {
            Scene.v().reportFileNotFoundError(args[0]);
            System.exit(1);
        } catch (IOException e) {
            Scene.v().reportError("Failed to read from file \"" + inFile + "\"");
            System.exit(2);
        }

        try {
            PrintStream out;
            if (outFile == null) {
                out = System.out;
            } else {
                out = new PrintStream(new FileOutputStream(outFile));
            }
            out.println("default: no");
            for (byte i = 0; i < spec.length; i++) {
                if ((spec[i] & ADAPTJ_FIELD_RECORDED) == 0) {
                    continue;
                }
                out.println();
                out.println("event " + getEventNameFromByte(i) + " {");
                translateBitMask(spec[i], i, out);
                out.println("}");
            }
        } catch (IOException e) {
            Scene.v().reportError("Failed to write to file \"" + (outFile == null ? "stdout" : outFile) + "\"");
            System.exit(3);
        }
    }
}
