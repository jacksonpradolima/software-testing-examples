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
import adaptj_pool.event.*;

import java.io.*;
import java.text.DecimalFormat;

public class Compiler implements AdaptJSpecConstants {
    private static StreamTokenizer tokenizer;
    private static String filename = "AdaptJ.spec";
    public static final byte VERSION = (byte) 0;

    public final static byte ADAPTJ_ERR = -1;
    /* Options */
    public final static byte OPTION_DEFAULT = 0;

    private static boolean[] parsedStates = new boolean[AdaptJEvent.ADAPTJ_EVENT_COUNT];
    private static short[] states = new short[AdaptJEvent.ADAPTJ_EVENT_COUNT];

    private final static byte STATE_WAIT_EVENT_OPTION =  1; // initial state: wait for "event" or a option ID
    private final static byte STATE_GOT_EVENT         =  2; // got "event"
    private final static byte STATE_GOT_EVENT_ID      =  3; // got event ID
    private final static byte STATE_WAIT_FIELD        =  4; // next is a field ID or '}'
    private final static byte STATE_GOT_ID            =  5; // got event member ID
    private final static byte STATE_GOT_COLON         =  6; // got ':'
    private final static byte STATE_GOT_VALUE         =  7; // got event member value
    private final static byte STATE_GOT_RBRACKET      =  8; // got '}'
    private final static byte STATE_GOT_OPTION        =  9; // got option ID
    private final static byte STATE_GOT_OPTION_COLON  = 10; // got ':' in option mode
    private final static byte STATE_GOT_OPTION_VALUE  = 11; // got value in option mode

    public static String getEventNameFromID(int eventID) {
        switch (eventID) {
            case AdaptJEvent.ADAPTJ_ARENA_DELETE:
                return "Arena Delete";
            case AdaptJEvent.ADAPTJ_ARENA_NEW:
                return "Arena New";
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                return "Class Load";
            case AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK:
                return "Class Load Hook";
            case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                return "Class Unload";
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD:
                return "Compiled Method Load";
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD:
                return "Compiled Method Unload";
            case AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST:
                return "Data Dump Request";
            case AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST:
                return "Data Reset Request";
            case AdaptJEvent.ADAPTJ_GC_FINISH:
                return "GC Finish";
            case AdaptJEvent.ADAPTJ_GC_START:
                return "GC Start";
            case AdaptJEvent.ADAPTJ_HEAP_DUMP:
                return "Heap Dump";
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC:
                return "JNI Globalref Alloc";
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC:
                return "JNI Weak Globalref Alloc";
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE:
                return "JNI Globalref Free";
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE:
                return "JNI Weak Globalref Free";
            case AdaptJEvent.ADAPTJ_JVM_INIT_DONE:
                return "JVM Init Done";
            case AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN:
                return "JVM Shut Down";
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
                return "Method Entry";
            case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                return "Method Exit";
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                return "Method Entry 2";
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER:
                return "Monitor Contended Enter";
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED:
                return "Monitor Contended Entered";
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT:
                return "Monitor Contended Exit";
            case AdaptJEvent.ADAPTJ_MONITOR_DUMP:
                return "Monitor Dump";
            case AdaptJEvent.ADAPTJ_MONITOR_WAIT:
                return "Monitor Wait";
            case AdaptJEvent.ADAPTJ_MONITOR_WAITED:
                return "Monitor Waited";
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                return "Object Alloc";
            case AdaptJEvent.ADAPTJ_OBJECT_DUMP:
                return "Object Dump";
            case AdaptJEvent.ADAPTJ_OBJECT_FREE:
                return "Object Free";
            case AdaptJEvent.ADAPTJ_OBJECT_MOVE:
                return "Object Move";
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER:
                return "Raw Monitor Contended Enter";
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED:
                return "Raw Monitor Contended Entered";
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT:
                return "Raw Monitor Contended Exit";
            case AdaptJEvent.ADAPTJ_THREAD_END:
                return "Thread End";
            case AdaptJEvent.ADAPTJ_THREAD_START:
                return "Thread Start";
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                return "Instruction Start";
            case AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE:
                return "Thread Status Change";
            default:
                throw new RuntimeException("Unknown event ID: " + eventID);
        }
    }

    private static byte getByteFromEventName(String eventName) {
        if (eventName.equals("ArenaDelete")) {
            return AdaptJEvent.ADAPTJ_ARENA_DELETE;
        } else if (eventName.equals("ArenaNew")) {
            return AdaptJEvent.ADAPTJ_ARENA_NEW;
        } else if (eventName.equals("ClassLoad")) {
            return AdaptJEvent.ADAPTJ_CLASS_LOAD;
        } else if (eventName.equals("ClassLoadHook")) {
            return AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK;
        } else if (eventName.equals("ClassUnload")) {
            return AdaptJEvent.ADAPTJ_CLASS_UNLOAD;
        } else if (eventName.equals("CompiledMethodLoad")) {
            return AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD;
        } else if (eventName.equals("CompiledMethodUnload")) {
            return AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD;
        } else if (eventName.equals("DataDumpRequest")) {
            return AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST;
        } else if (eventName.equals("DataResetRequest")) {
            return AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST;
        } else if (eventName.equals("GCFinish")) {
            return AdaptJEvent.ADAPTJ_GC_FINISH;
        } else if (eventName.equals("GCStart")) {
            return AdaptJEvent.ADAPTJ_GC_START;
        } else if (eventName.equals("HeapDump")) {
            return AdaptJEvent.ADAPTJ_HEAP_DUMP;
        } else if (eventName.equals("JNIGlobalRefAlloc")) {
            return AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC;
        } else if (eventName.equals("JNIGlobalRefFree")) {
            return AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE;
        } else if (eventName.equals("JNIWeakGlobalRefAlloc")) {
            return AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC;
        } else if (eventName.equals("JNIWeakGlobalRefFree")) {
            return AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE;
        } else if (eventName.equals("JVMInitDone")) {
            return AdaptJEvent.ADAPTJ_JVM_INIT_DONE;
        } else if (eventName.equals("JVMShutDown")) {
            return AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN;
        } else if (eventName.equals("MethodEntry")) {
            return AdaptJEvent.ADAPTJ_METHOD_ENTRY;
        } else if (eventName.equals("MethodEntry2")) {
            return AdaptJEvent.ADAPTJ_METHOD_ENTRY2;
        } else if (eventName.equals("MethodExit")) {
            return AdaptJEvent.ADAPTJ_METHOD_EXIT;
        } else if (eventName.equals("MonitorContendedEnter")) {
            return AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER;
        } else if (eventName.equals("MonitorContendedEntered")) {
            return AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED;
        } else if (eventName.equals("MonitorContendedExit")) {
            return AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT;
        } else if (eventName.equals("MonitorDump")) {
             return AdaptJEvent.ADAPTJ_MONITOR_DUMP;           
        } else if (eventName.equals("MonitorWait")) {
             return AdaptJEvent.ADAPTJ_MONITOR_WAIT;       
        } else if (eventName.equals("MonitorWaited")) {
            return AdaptJEvent.ADAPTJ_MONITOR_WAITED;
        } else if (eventName.equals("ObjectAlloc")) {
             return AdaptJEvent.ADAPTJ_OBJECT_ALLOC;       
        } else if (eventName.equals("ObjectDump")) {
            return AdaptJEvent.ADAPTJ_OBJECT_DUMP;
        } else if (eventName.equals("ObjectFree")) {
            return AdaptJEvent.ADAPTJ_OBJECT_FREE;
        } else if (eventName.equals("ObjectMove")) {
            return AdaptJEvent.ADAPTJ_OBJECT_MOVE;
        } else if (eventName.equals("RawMonitorContendedEnter")) {
            return AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER;
        } else if (eventName.equals("RawMonitorContendedEntered")) {
            return AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED;
        } else if (eventName.equals("RawMonitorContendedExit")) {
             return AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT;       
        } else if (eventName.equals("ThreadEnd")) {
            return AdaptJEvent.ADAPTJ_THREAD_END;
        } else if (eventName.equals("ThreadStart")) {
            return AdaptJEvent.ADAPTJ_THREAD_START;
        } else if (eventName.equals("InstructionStart")) {
            return AdaptJEvent.ADAPTJ_INSTRUCTION_START;
        } else if (eventName.equals("ThreadStatusChange")) {
            return AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE;
        }
     
        return AdaptJEvent.ADAPTJ_UNKNOWN;
    }

    private static short getFieldMask(byte event, String fieldName) {
        if (event >= 0 && event < AdaptJEvent.ADAPTJ_EVENT_COUNT) {
            if (fieldName.equals("recorded")) {
                return (short) ADAPTJ_FIELD_RECORDED;
            } else if (fieldName.equals("counted")) {
                return (short) ADAPTJ_FIELD_COUNTED;
            } else if (fieldName.equals("env_id")) {
                return (short) ADAPTJ_FIELD_ENV_ID;
            }

            switch (event) {
                case AdaptJEvent.ADAPTJ_ARENA_DELETE:
                    if (fieldName.equals("arena_id")) {
                        return (short) ADAPTJ_FIELD_ARENA_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_ARENA_NEW:
                    if (fieldName.equals("arena_id")) {
                        return (short) ADAPTJ_FIELD_ARENA_ID;
                    } else if (fieldName.equals("arena_name")) {
                        return (short) ADAPTJ_FIELD_ARENA_NAME;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                    if (fieldName.equals("class_name")) {
                        return (short) ADAPTJ_FIELD_CLASS_NAME;
                    } else if (fieldName.equals("source_name")) {
                        return (short) ADAPTJ_FIELD_SOURCE_NAME;
                    } else if (fieldName.equals("num_interfaces")) {
                        return (short) ADAPTJ_FIELD_NUM_INTERFACES;
                    } else if (fieldName.equals("num_methods")) {
                        return (short) ADAPTJ_FIELD_NUM_METHODS;
                    } else if (fieldName.equals("methods")) {
                        return (short) ADAPTJ_FIELD_METHODS;
                    } else if (fieldName.equals("num_static_fields")) {
                        return (short) ADAPTJ_FIELD_NUM_STATIC_FIELDS;
                    } else if (fieldName.equals("statics")) {
                        return (short) ADAPTJ_FIELD_STATICS;
                    } else if (fieldName.equals("num_instance_fields")) {
                        return (short) ADAPTJ_FIELD_NUM_INSTANCE_FIELDS;
                    } else if (fieldName.equals("instances")) {
                        return (short) ADAPTJ_FIELD_INSTANCES;
                    } else if (fieldName.equals("class_id")) {
                        return (short) ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK:
                    // TODO
                    break;
                case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                    if (fieldName.equals("class_id")) {
                        return (short) ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD:
                    if (fieldName.equals("method_id")) {
                        return (short) ADAPTJ_FIELD_METHOD_ID;
                    } else if (fieldName.equals("code_size")) {
                        return (short) ADAPTJ_FIELD_CODE_SIZE;
                    } else if (fieldName.equals("code")) {
                        return (short) ADAPTJ_FIELD_CODE;
                    } else if (fieldName.equals("lineno_table_size")) {
                        return (short) ADAPTJ_FIELD_LINENO_TABLE_SIZE;
                    } else if (fieldName.equals("lineno_table")) {
                        return (short) ADAPTJ_FIELD_LINENO_TABLE;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD:
                    if (fieldName.equals("method_id")) {
                        return (short) ADAPTJ_FIELD_METHOD_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST:
                case AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST:
                    /* No event specific information */
                    break;
                case AdaptJEvent.ADAPTJ_GC_FINISH:
                    if (fieldName.equals("used_objects")) {
                        return (short) ADAPTJ_FIELD_USED_OBJECTS;
                    } else if (fieldName.equals("used_object_space")) {
                        return (short) ADAPTJ_FIELD_USED_OBJECT_SPACE;
                    } else if (fieldName.equals("total_object_space")) {
                        return (short) ADAPTJ_FIELD_TOTAL_OBJECT_SPACE;
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
                    if (fieldName.equals("ref_id")) {
                        return (short) ADAPTJ_FIELD_REF_ID;
                    } else if (fieldName.equals("obj_id")) {
                        return (short) ADAPTJ_FIELD_OBJ_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE:
                case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE:
                    if (fieldName.equals("ref_id")) {
                        return (short) ADAPTJ_FIELD_REF_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_JVM_INIT_DONE:
                case AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN:
                    /* No event specific information */
                    break;
                case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
                case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                    if (fieldName.equals("method_id")) {
                        return (short) ADAPTJ_FIELD_METHOD_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                    if (fieldName.equals("method_id")) {
                        return (short) ADAPTJ_FIELD_METHOD_ID;
                    } else if (fieldName.equals("obj_id")) {
                        return (short) ADAPTJ_FIELD_OBJ_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER:
                case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED:
                case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT:
                    if (fieldName.equals("object")) {
                        return (short) ADAPTJ_FIELD_OBJECT;
                    }

                    break;
                case AdaptJEvent.ADAPTJ_MONITOR_DUMP:
                    if (fieldName.equals("data_len")) {
                        return (short) ADAPTJ_FIELD_DATA_LEN;
                    } else if (fieldName.equals("data")) {
                        return (short) ADAPTJ_FIELD_DATA;
                    } else if (fieldName.equals("num_traces")) {
                        return (short) ADAPTJ_FIELD_NUM_TRACES;
                    } else if (fieldName.equals("traces")) {
                        return (short) ADAPTJ_FIELD_TRACES;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_MONITOR_WAIT:
                case AdaptJEvent.ADAPTJ_MONITOR_WAITED:
                    if (fieldName.equals("object")) {
                        return (short) ADAPTJ_FIELD_OBJECT;
                    } else if (fieldName.equals("timeout")) {
                        return (short) ADAPTJ_FIELD_TIMEOUT;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                    if (fieldName.equals("arena_id")) {
                        return (short) ADAPTJ_FIELD_ARENA_ID;
                    } else if (fieldName.equals("obj_id")) {
                        return (short) ADAPTJ_FIELD_OBJ_ID;
                    } else if (fieldName.equals("is_array")) {
                        return (short) ADAPTJ_FIELD_IS_ARRAY;
                    } else if (fieldName.equals("size")) {
                        return (short) ADAPTJ_FIELD_SIZE;
                    } else if (fieldName.equals("class_id")) {
                        return (short) ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_OBJECT_DUMP:
                    if (fieldName.equals("data_len")) {
                        return (short) ADAPTJ_FIELD_DATA_LEN;
                    } else if (fieldName.equals("data")) {
                        return (short) ADAPTJ_FIELD_DATA;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_OBJECT_FREE:
                    if (fieldName.equals("obj_id")) {
                        return (short) ADAPTJ_FIELD_OBJ_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_OBJECT_MOVE:
                    if (fieldName.equals("arena_id")) {
                        return (short) ADAPTJ_FIELD_ARENA_ID;
                    } else if (fieldName.equals("obj_id")) {
                        return (short) ADAPTJ_FIELD_OBJ_ID;
                    } else if (fieldName.equals("new_arena_id")) {
                        return (short) ADAPTJ_FIELD_NEW_ARENA_ID;
                    } else if (fieldName.equals("new_obj_id")) {
                        return (short) ADAPTJ_FIELD_NEW_OBJ_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER:
                case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED:
                case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT:
                    if (fieldName.equals("name")) {
                        return (short) ADAPTJ_FIELD_NAME;
                    } else if (fieldName.equals("id")) {
                        return (short) ADAPTJ_FIELD_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_THREAD_END:
                    /* No event specific information */
                    break;
                case AdaptJEvent.ADAPTJ_THREAD_START:
                    if (fieldName.equals("thread_name")) {
                        return (short) ADAPTJ_FIELD_THREAD_NAME;
                    } else if (fieldName.equals("group_name")) {
                        return (short) ADAPTJ_FIELD_GROUP_NAME;
                    } else if (fieldName.equals("parent_name")) {
                        return (short) ADAPTJ_FIELD_PARENT_NAME;
                    } else if (fieldName.equals("thread_id")) {
                        return (short) ADAPTJ_FIELD_THREAD_ID;
                    } else if (fieldName.equals("thread_env_id")) {
                        return (short) ADAPTJ_FIELD_THREAD_ENV_ID;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                    if (fieldName.equals("method_id")) {
                        return (short) ADAPTJ_FIELD_METHOD_ID;
                    } else if (fieldName.equals("offset")) {
                        return (short) ADAPTJ_FIELD_OFFSET;
                    } else if (fieldName.equals("is_true")) {
                        return (short) ADAPTJ_FIELD_IS_TRUE;
                    } else if (fieldName.equals("key")) {
                        return (short) ADAPTJ_FIELD_KEY;
                    } else if (fieldName.equals("low")) {
                        return (short) ADAPTJ_FIELD_LOW;
                    } else if (fieldName.equals("hi")) {
                        return (short) ADAPTJ_FIELD_HI;
                    } else if (fieldName.equals("chosen_pair_index")) {
                        return (short) ADAPTJ_FIELD_CHOSEN_PAIR_INDEX;
                    } else if (fieldName.equals("pairs_total")) {
                        return (short) ADAPTJ_FIELD_PAIRS_TOTAL;
                    }
                    break;
                case AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE:
                    if (fieldName.equals("new_status")) {
                        return (short) ADAPTJ_FIELD_NEW_STATUS;
                    }
                    break;
                default:
                    /* should not happen */
                    break;

            }
        }
        return (short) ADAPTJ_ERR;
    }

    private static byte getByteFromOptionName(String optionName) {
        if (optionName != null) {
            if (optionName.equals("default")) {
                return OPTION_DEFAULT;
            }
        }
        return ADAPTJ_ERR;
    }
    
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            printUsage();
            System.exit(1);
        }

        if (args.length == 2) {
            filename = args[1];
        }

        try {            
            System.out.println("AdaptJ Spec Compiler> Compiling Specification File");
            if (!compileFile(args[0])) {
                Scene.v().reportError("Compilation failed!!");
                System.exit(2);
            }

            System.out.println("AdaptJ Spec Compiler> Compiled Information:");
            for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) {
                String s = Integer.toBinaryString(((int)states[i]) & 0x0000FFFF);
                for (int j = s.length(); j < 16; j++) {
                    s = '0' + s;
                }
                String indexStr;
                DecimalFormat format = new DecimalFormat("00");
                if (parsedStates[i]) {
                    indexStr = " " + format.format(i) + " : ";
                } else {
                    indexStr = "[" + format.format(i) + "]: ";
                }
                
                System.out.println(indexStr + s);
            }
            System.out.println("AdaptJ Spec Compiler> Specification File Compiled Successfully");
        } catch (InvalidSyntaxException e) {
            System.err.println(e);
            System.exit(3);
        }

        System.out.println("AdaptJ Spec Compiler> Generating Compiled File to \"" + filename + "\"");
        try  {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(filename));
            outStream.writeInt(ADAPTJ_SPEC_MAGIC | VERSION);
            for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) {
                outStream.writeByte(i);
                outStream.writeShort(states[i]);
            }
        } catch (IOException e) {
            Scene.v().reportError("Error writing to file: \"" + filename + "\"");
            System.exit(4);
        }
        System.out.println("AdaptJ Spec Compiler> Done");
    }

    public static void printUsage() {
        System.out.println("Usage: java adaptj_pool.spec.Compiler <input_file> [output_file]");
    }

    private static boolean parseBoolean(String value) throws InvalidSyntaxException {
        if (value == null) {
            throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid boolean value: " + value);
        }

        if (value.equals("on") || value.equals("true") || value.equals("yes")) {
            return true;
        }
        
        if (value.equals("off") || value.equals("false") || value.equals("no")) {
            return false;
        }

        throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid boolean value: " + value);

    }

    public static boolean compileFile(String fileName) throws InvalidSyntaxException {
        StreamTokenizer tokenizer;
        try {
            FileInputStream inStream = new FileInputStream(fileName);
            Reader r = new BufferedReader(new InputStreamReader(inStream));
            tokenizer = new StreamTokenizer(r);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: \"" + fileName + "\"");
            return false;
        } catch (IOException e) {
            System.err.println("Error reading file: \"" + fileName + "\"");
            return false;
        }

        tokenizer.resetSyntax();
        tokenizer.wordChars(33, Character.MAX_VALUE);
        tokenizer.whitespaceChars(0, 32);
        tokenizer.commentChar('#');
        tokenizer.slashSlashComments(true);
        tokenizer.slashStarComments(true);
        tokenizer.ordinaryChar('{');
        tokenizer.ordinaryChar('}');
        tokenizer.ordinaryChar(':');
        tokenizer.ordinaryChar(';');


        for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) {
            parsedStates[i] = false;
            states[i] = (short) (0);
        }

        int token;
        byte state = STATE_WAIT_EVENT_OPTION;
        byte option = ADAPTJ_ERR;
        byte event = ADAPTJ_ERR;
        short fieldMask = (short) (0);
        try {
            while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
                switch (token) {
                    case '{':
                        if (state == STATE_GOT_EVENT_ID) {
                            state = STATE_WAIT_FIELD;
                        } else {
                            throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: '{'");
                        }
                        break;
                    case '}':
                        if (state == STATE_WAIT_FIELD) {
                            state = STATE_GOT_RBRACKET;
                        } else {
                            throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: '}'");
                        }
                        break;
                    case ':':
                        if (state == STATE_GOT_ID) {
                            state = STATE_GOT_COLON;
                        } else if (state == STATE_GOT_OPTION) {
                            state = STATE_GOT_OPTION_COLON;
                        } else {
                            throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: ':'");
                        }
                        break;
                    case ';':
                        if (state == STATE_GOT_VALUE) {
                            state = STATE_WAIT_FIELD;
                        } else if (state == STATE_GOT_OPTION_VALUE) {
                            state = STATE_WAIT_EVENT_OPTION;
                        } else {
                            throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: ';'");
                        }
                        break;
                    case StreamTokenizer.TT_WORD:
                        switch (state) {
                            case STATE_WAIT_EVENT_OPTION:
                                if ((option = getByteFromOptionName(tokenizer.sval)) != ADAPTJ_ERR) {
                                    state = STATE_GOT_OPTION;
                                    break;
                                }
                            case STATE_GOT_RBRACKET:
                                if (tokenizer.sval.equals("event")) {
                                    state = STATE_GOT_EVENT;                                    
                                } else {
                                    throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: \"" + tokenizer.sval + "\"");
                                }
                                break;
                            case STATE_GOT_EVENT:
                                event = getByteFromEventName(tokenizer.sval);
                                if (event == AdaptJEvent.ADAPTJ_UNKNOWN) {
                                    throw new InvalidSyntaxException(tokenizer.lineno(), "Unknown event: \"" + tokenizer.sval + "\"");
                                }
                                if (parsedStates[event]) {
                                    throw new InvalidSyntaxException(tokenizer.lineno(), "Redefined event: \"" + tokenizer.sval + "\"");
                                }
                                parsedStates[event] = true;
                                state = STATE_GOT_EVENT_ID;
                                break;
                            case STATE_WAIT_FIELD:
                                fieldMask = getFieldMask(event, tokenizer.sval);
                                if (fieldMask == ADAPTJ_ERR) {
                                    throw new InvalidSyntaxException(tokenizer.lineno(), "Unknown field: \"" + tokenizer.sval + "\"");
                                }
                                state = STATE_GOT_ID;
                                break;
                            case STATE_GOT_COLON:
                                {
                                    boolean b = parseBoolean(tokenizer.sval);
                                    processField(event, fieldMask, b);
                                    state = STATE_GOT_VALUE;
                                }
                                break;
                            case STATE_GOT_OPTION_COLON:
                                {
                                    boolean b = parseBoolean(tokenizer.sval);
                                    processOption(option, b);
                                    state = STATE_GOT_OPTION_VALUE;
                                }
                                break;
                        }
                        break;
                    default:
                        System.out.println("????");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error while reading file: \"" + fileName + "\"");
            return false;
        }

        return true;
    }

    private static void processOption(byte option, boolean value) {
        switch (option) {
            case OPTION_DEFAULT:
                short defaultValue;
                if (value) {
                    defaultValue = (short) (~0);
                } else {
                    defaultValue = (short) (0);
                }
                for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) {
                    states[i] = defaultValue;
                }
                break;
        }
    }

    private static void processField(byte event, short fieldMask, boolean value) {
        short tmpState = states[event];
        if (value) {
            tmpState |= fieldMask;
        } else {
            tmpState &= (~fieldMask);
        }
        states[event] = tmpState;
    }
}
