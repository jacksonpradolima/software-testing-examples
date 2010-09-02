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

/* ========================================================================== *
 *                                AdaptJAgent.c                               *
 *                                                                            *
 *  Author: Bruno Dufour (bruno.dufour@mail.mcgill.ca)                        *
 *  Description: Entry Point for the AdaptJ JVMPI Agent                       *
 *                                                                            *
 *  History of Changes                                                        *
 *  ------------------------------------------------------------------------  *
 *   31/12/2002  BD  File Created                                             *
 * ========================================================================== */

#include "AdaptJAgent.h"

/* ========================================================================== *
 *                              Global Variables                              *
 * ========================================================================== */

JVMPI_Interface *jvmpi_interface; 

#ifdef ADAPTJ_LOCK_NOTIFY_EVENT
JVMPI_RawMonitor global_access_lock;
#endif

FILE *outputFile;
char fileName[BUFF_SIZE];

jlong totalEventCount;
jlong counters[ADAPTJ_MAX_EVENT + 1];
int splitThreshold;
int gcInterval;
int gcCurrentTotal = 0;
int fileCounter = 0;
int fileBytes = 0;
size_t nameLen = 0;

#ifdef ADAPTJ_ENABLE_PIPE
boolean pipeMode;
#endif
boolean optMode;
boolean verboseMode;
boolean threadStatus;
ThreadStatus_t *threadStats = NULL;
JNIEnv *lastThread = NULL;

boolean needMSBConversion;
jshort (*JShortMSB)(jshort);
jint (*JIntMSB)(jint);
jlong (*JLongMSB)(jlong);

jshort eventInfo[ADAPTJ_MAX_EVENT + 1]; 
AdaptJEvent charToEvent[128];
AdaptJEvent JVMPIToAdaptJEvent[JVMPI_MAX_EVENT_TYPE_VAL + 1];

boolean initDone = false;

extern boolean isePending;
HashMap_t methodIDtoBytecode;

/* For checking the know IDs and requesting needed events */
AdaptJIDSet_t knownObjectIDs;
AdaptJIDSet_t knownThreadIDs;
AdaptJIDSet_t knownMethodIDs;
AdaptJIDSetMap_t arenaIDtoObjects;
AdaptJIDSetMap_t classIDtoMethods;

char **cp;
int cpSize;


char eventToChar[ADAPTJ_MAX_EVENT + 1] = {
    'A', 'a', 'c', 'k', 'C', 'l', 'L', 'q', 'Q',
    'G', 'g', 'h', 'j', 'J', 'w', 'W', 'v', 'V', 'b',
    'm', 'M', 'd', 'e', 'D', 'x', 'y', 'Y', 'o', 'p',
    'O', 'P', 'r', 'E', 'R', 'T', 't', 'i', 'z'
};

char eventNames[ADAPTJ_MAX_EVENT + 1][42] = {
    "JVMPI_EVENT_ARENA_DELETE",
    "JVMPI_EVENT_ARENA_NEW",
    "JVMPI_EVENT_CLASS_LOAD",
    "JVMPI_EVENT_CLASS_LOAD_HOOK",
    "JVMPI_EVENT_CLASS_UNLOAD",
    "JVMPI_EVENT_COMPILED_METHOD_LOAD",
    "JVMPI_EVENT_COMPILED_METHOD_UNLOAD",
    "JVMPI_EVENT_DATA_DUMP_REQUEST",
    "JVMPI_EVENT_DATA_RESET_REQUEST",
    "JVMPI_EVENT_GC_FINISH",
    "JVMPI_EVENT_GC_START",
    "JVMPI_EVENT_HEAP_DUMP",
    "JVMPI_EVENT_JNI_GLOBALREF_ALLOC",
    "JVMPI_EVENT_JNI_GLOBALREF_FREE",
    "JVMPI_EVENT_JNI_WEAK_GLOBALREF_ALLOC",
    "JVMPI_EVENT_JNI_WEAK_GLOBALREF_FREE",
    "JVMPI_EVENT_JVM_INIT_DONE",
    "JVMPI_EVENT_JVM_SHUT_DOWN",
    "JVMPI_EVENT_METHOD_ENTRY",
    "JVMPI_EVENT_METHOD_ENTRY2",
    "JVMPI_EVENT_METHOD_EXIT",
    "JVMPI_EVENT_MONITOR_CONTENDED_ENTER",
    "JVMPI_EVENT_MONITOR_CONTENDED_ENTERED",
    "JVMPI_EVENT_MONITOR_CONTENDED_EXIT",
    "JVMPI_EVENT_MONITOR_DUMP",
    "JVMPI_EVENT_MONITOR_WAIT",
    "JVMPI_EVENT_MONITOR_WAITED",
    "JVMPI_EVENT_OBJECT_ALLOC",
    "JVMPI_EVENT_OBJECT_DUMP",
    "JVMPI_EVENT_OBJECT_FREE",
    "JVMPI_EVENT_OBJECT_MOVE",
    "JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTER",
    "JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTERED",
    "JVMPI_EVENT_RAW_MONITOR_CONTENDED_EXIT",
    "JVMPI_EVENT_THREAD_END",
    "JVMPI_EVENT_THREAD_START",
    "JVMPI_EVENT_INSTRUCTION_START",
    "NO EQUIVALENT"
};

jint adaptjEventToJVMPI[ADAPTJ_MAX_EVENT + 1] = {
    JVMPI_EVENT_ARENA_DELETE,
    JVMPI_EVENT_ARENA_NEW,
    JVMPI_EVENT_CLASS_LOAD,
    JVMPI_EVENT_CLASS_LOAD_HOOK,
    JVMPI_EVENT_CLASS_UNLOAD,
    JVMPI_EVENT_COMPILED_METHOD_LOAD,
    JVMPI_EVENT_COMPILED_METHOD_UNLOAD,
    JVMPI_EVENT_DATA_DUMP_REQUEST,
    JVMPI_EVENT_DATA_RESET_REQUEST,
    JVMPI_EVENT_GC_FINISH,
    JVMPI_EVENT_GC_START,
    JVMPI_EVENT_HEAP_DUMP,
    JVMPI_EVENT_JNI_GLOBALREF_ALLOC,
    JVMPI_EVENT_JNI_GLOBALREF_FREE,
    JVMPI_EVENT_JNI_WEAK_GLOBALREF_ALLOC,
    JVMPI_EVENT_JNI_WEAK_GLOBALREF_FREE,
    JVMPI_EVENT_JVM_INIT_DONE,
    JVMPI_EVENT_JVM_SHUT_DOWN,
    JVMPI_EVENT_METHOD_ENTRY,
    JVMPI_EVENT_METHOD_ENTRY2,
    JVMPI_EVENT_METHOD_EXIT,
    JVMPI_EVENT_MONITOR_CONTENDED_ENTER,
    JVMPI_EVENT_MONITOR_CONTENDED_ENTERED,
    JVMPI_EVENT_MONITOR_CONTENDED_EXIT,
    JVMPI_EVENT_MONITOR_DUMP,
    JVMPI_EVENT_MONITOR_WAIT,
    JVMPI_EVENT_MONITOR_WAITED,
    JVMPI_EVENT_OBJECT_ALLOC,
    JVMPI_EVENT_OBJECT_DUMP,
    JVMPI_EVENT_OBJECT_FREE,
    JVMPI_EVENT_OBJECT_MOVE,
    JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTER,
    JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTERED,
    JVMPI_EVENT_RAW_MONITOR_CONTENDED_EXIT,
    JVMPI_EVENT_THREAD_END,
    JVMPI_EVENT_THREAD_START,
    JVMPI_EVENT_INSTRUCTION_START,
    -1
};

/* ========================================================================== *
 *                               Implementation                               *
 * ========================================================================== */

void notifyEvent(JVMPI_Event *event) {
    /* Get Event Type (clear REQUESTED_EVENT bit) */
    jint eventType = event->event_type & ~JVMPI_REQUESTED_EVENT;
    jint wasRequested = (event->event_type & JVMPI_REQUESTED_EVENT);
    AdaptJEvent adaptjEvent = JVMPIToAdaptJEvent[eventType];
    jshort s = eventInfo[adaptjEvent];
    /* long currentFilePos; FIXME */
    
    switch (eventType) {
        case JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTER:
        case JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTERED:
        case JVMPI_EVENT_RAW_MONITOR_CONTENDED_EXIT: {
                const char *rm_name = event->u.raw_monitor.name;
                if ((rm_name != NULL)
                        && !strcmp(rm_name, ADAPTJ_GLOBAL_LOCK_NAME)
                        && (rm_name[0] == '_')) {
                    /* This is a Raw Monitor event that we should not receive */
                    return;
                }
            }
        default:
            break;
    }
    
    //fprintf(stderr, "WAL\n");
#ifdef ADAPTJ_LOCK_NOTIFY_EVENT
    jvmpi_interface->RawMonitorEnter(global_access_lock);
#endif
    //fprintf(stderr, "HAL\n");

    if (outputFile == NULL) {
#ifdef ADAPTJ_LOCK_NOTIFY_EVENT
        //fprintf(stderr, "RL1\n");
        jvmpi_interface->RawMonitorExit(global_access_lock);
        //fprintf(stderr, "RL1b\n");
#endif
        return;
    }

    /*
    if ((eventType != JVMPI_EVENT_THREAD_START) && (event->env_id != NULL)) {
        if (initDone) {
            if (idSetContains(&knownThreadIDs, (jint) event->env_id) != ID_SET_OK) {
                jobjectID threadObjID;
                jvmpi_interface->DisableGC();
                threadObjID = jvmpi_interface->GetThreadObject(event->env_id);
                jvmpi_interface->EnableGC();
                if (threadObjID != NULL) {
                    fprintf(stderr, "Requesting thread ID %d (%d)\n", (int) threadObjID, eventType);
                    if (jvmpi_interface->RequestEvent(JVMPI_EVENT_THREAD_START, threadObjID) != JVMPI_SUCCESS) {
                        reportError("Request for THREAD_START event failed");
                    }
                }
            }
        } else {
            idSetAdd(&knownThreadIDs, (jint) event->env_id);
        }
    } 
    */
    if (threadStatus && (lastThread != event->env_id)) {
        //fprintf(stderr, "TS\n");
        checkThreadStatus();
        //fprintf(stderr, "TSb\n");
        lastThread = event->env_id;
    }
    
    totalEventCount++;
    /*
    if ((totalEventCount % 100000) == 0) {
        fprintf(stderr, ">> %lld <<\n", totalEventCount);
    }
    */
    
    if (s & ADAPTJ_FIELD_COUNTED) {
        counters[adaptjEvent] += 1;
    }
    if (!((s & ADAPTJ_FIELD_RECORDED) || (s & ADAPTJ_FIELD_REQUIRED)) && (adaptjEvent != ADAPTJ_OBJECT_ALLOC)) {
#ifdef ADAPTJ_LOCK_NOTIFY_EVENT
        //fprintf(stderr, "RL2");
        jvmpi_interface->RawMonitorExit(global_access_lock);
        //fprintf(stderr, "RL2b");
#endif
        return;
    }
   
    if (isePending && (eventType != JVMPI_EVENT_INSTRUCTION_START)) {
        AdaptJClearPendingISE();
    }
    
    /*
    if ((totalEventCount > 21700000) && (eventType != JVMPI_EVENT_INSTRUCTION_START)
            && (eventType != JVMPI_EVENT_METHOD_ENTRY2)
            && (eventType != JVMPI_EVENT_METHOD_EXIT)) {
        fprintf(stderr, "Event: %d\n", eventType);
    }
    */
    
    switch (eventType) {
        case JVMPI_EVENT_ARENA_DELETE:
            AdaptJArenaDelete(event->env_id,
                             event->u.delete_arena.arena_id,
                             wasRequested);
            break;
        case JVMPI_EVENT_ARENA_NEW:
            AdaptJArenaNew(event->env_id,
                          event->u.new_arena.arena_id,
                          event->u.new_arena.arena_name,
                          wasRequested);
            break;
        case JVMPI_EVENT_CLASS_LOAD:
#ifdef ADAPTJ_PRINT_OBJECT_DEFS
            fprintf(stderr, "ClassLoad: %s --> %d (%d)\n", event->u.class_load.class_name, event->u.class_load.class_id, wasRequested);
#endif
            AdaptJClassLoad(event->env_id,
                           event->u.class_load.class_name,
                           event->u.class_load.source_name,
                           event->u.class_load.num_interfaces,
                           event->u.class_load.num_methods,
                           event->u.class_load.methods,
                           event->u.class_load.num_static_fields,
                           event->u.class_load.statics,
                           event->u.class_load.num_instance_fields,
                           event->u.class_load.instances,
                           event->u.class_load.class_id,
                           wasRequested);
            break;
        case JVMPI_EVENT_CLASS_LOAD_HOOK:
            break;
        case JVMPI_EVENT_CLASS_UNLOAD:
            AdaptJClassUnload(event->env_id,
                             event->u.class_unload.class_id,
                             wasRequested);
            break;
        case JVMPI_EVENT_COMPILED_METHOD_LOAD:
            AdaptJCompiledMethodLoad(event->env_id,
                                    event->u.compiled_method_load.method_id,
                                    event->u.compiled_method_load.code_addr,
                                    event->u.compiled_method_load.code_size,
                                    event->u.compiled_method_load.lineno_table_size,
                                    event->u.compiled_method_load.lineno_table,
                                    wasRequested);
            break;
        case JVMPI_EVENT_COMPILED_METHOD_UNLOAD:
            AdaptJCompiledMethodUnload(event->env_id,
                                      event->u.compiled_method_unload.method_id,
                                      wasRequested);
            break;
        case JVMPI_EVENT_DATA_DUMP_REQUEST:
            break;
        case JVMPI_EVENT_DATA_RESET_REQUEST:
            break;
        case JVMPI_EVENT_GC_FINISH:
            AdaptJGCFinish(event->env_id,
                          event->u.gc_info.used_objects,
                          event->u.gc_info.used_object_space,
                          event->u.gc_info.total_object_space,
                          wasRequested);
            break;
        case JVMPI_EVENT_GC_START:
            AdaptJGCStart(event->env_id,
                    wasRequested);
            break;
        case JVMPI_EVENT_HEAP_DUMP:
            break;

        case JVMPI_EVENT_JNI_GLOBALREF_ALLOC:
            /*
            AdaptJNIGlobalRefAlloc(event->env_id,
                                   event->u.jni_globalref_alloc.obj_id,
                                   event->u.jni_globalref_alloc.ref_id);
            */
            break;
        case JVMPI_EVENT_JNI_GLOBALREF_FREE:
            /*
            AdaptJNIGlobalRefFree(event->env_id,
                                  event->u.jni_globalref_free.ref_id);
            */
            break;
        case JVMPI_EVENT_JNI_WEAK_GLOBALREF_ALLOC:
            /*
            AdaptJNIWeakGlobalRefAlloc(event->env_id,
                                       event->u.jni_globalref_alloc.obj_id,
                                       event->u.jni_globalref_alloc.ref_id);
            */
            break;
        case JVMPI_EVENT_JNI_WEAK_GLOBALREF_FREE:
            /*
            AdaptJNIWeakGlobalRefFree(event->env_id,
                                      event->u.jni_globalref_free.ref_id);
            */
            break;
        case JVMPI_EVENT_JVM_INIT_DONE:
            initDone = true;
            AdaptJVMInitDone(event->env_id,
                    wasRequested);
            break;
        case JVMPI_EVENT_JVM_SHUT_DOWN:
            AdaptJJVMShutDown(event->env_id,
                    wasRequested);
            break;
        case JVMPI_EVENT_METHOD_ENTRY:
            AdaptJMethodEntry(event->env_id,
                             event->u.method.method_id,
                             wasRequested);
            break;
        case JVMPI_EVENT_METHOD_ENTRY2:
            AdaptJMethodEntry2(event->env_id,
                              event->u.method_entry2.method_id,
                              event->u.method_entry2.obj_id,
                              wasRequested);
            break;
        case JVMPI_EVENT_METHOD_EXIT:
            AdaptJMethodExit(event->env_id,
                            event->u.method.method_id,
                            wasRequested);
            break;
        case JVMPI_EVENT_MONITOR_CONTENDED_ENTER:
            AdaptJMonitorContendedEnter(event->env_id,
                                       event->u.monitor.object,
                                       wasRequested);
            break;
        case JVMPI_EVENT_MONITOR_CONTENDED_ENTERED:
            AdaptJMonitorContendedEntered(event->env_id,
                                         event->u.monitor.object,
                                         wasRequested);
            break;
        case JVMPI_EVENT_MONITOR_CONTENDED_EXIT:
            AdaptJMonitorContendedExit(event->env_id,
                                      event->u.monitor.object,
                                      wasRequested);
            break;
        case JVMPI_EVENT_MONITOR_DUMP:
            /*
            AdaptJMonitorDump(event->env_id,
                             event->u.monitor_dump.begin,
                             event->u.monitor_dump.end,
                             event->u.monitor_dump.num_traces,
                             event->u.monitor_dump.traces,
                             event->u.monitor_dump.threads_status);
            */
            break;
        case JVMPI_EVENT_MONITOR_WAIT:
            AdaptJMonitorWait(event->env_id,
                             event->u.monitor_wait.object,
                             event->u.monitor_wait.timeout,
                             wasRequested);
            break;
        case JVMPI_EVENT_MONITOR_WAITED:
            AdaptJMonitorWaited(event->env_id,
                               event->u.monitor_wait.object,
                               event->u.monitor_wait.timeout,
                               wasRequested);
            break;
        case JVMPI_EVENT_OBJECT_ALLOC:
            
#ifdef ADAPTJ_PRINT_OBJECT_DEFS
            fprintf(stderr, "ObjectAlloc: %d --> %d (%d)\n", event->u.obj_alloc.obj_id, event->u.obj_alloc.class_id, wasRequested);
#endif
            if (gcInterval > 0) {
                gcCurrentTotal += event->u.obj_alloc.size;

                /* Do the check here so that the "normal" path
                 * does not have the extra cost added */
                if (!(eventInfo[ADAPTJ_OBJECT_ALLOC] & ADAPTJ_FIELD_RECORDED)) {
                    break;
                }
            }
            AdaptJObjectAlloc(event->env_id,
                             event->u.obj_alloc.arena_id,
                             event->u.obj_alloc.class_id,
                             event->u.obj_alloc.is_array,
                             event->u.obj_alloc.size,
                             event->u.obj_alloc.obj_id,
                             wasRequested);
            break;
        case JVMPI_EVENT_OBJECT_DUMP:
            /*
            AdaptJObjectDump(event->env_id,
                            event->u.object_dump.data_len,
                            event->u.object_dump.data);
            */
            break;
        case JVMPI_EVENT_OBJECT_FREE:
            AdaptJObjectFree(event->env_id,
                            event->u.obj_free.obj_id,
                            wasRequested);
            break;
        case JVMPI_EVENT_OBJECT_MOVE:
            AdaptJObjectMove(event->env_id,
                            event->u.obj_move.arena_id,
                            event->u.obj_move.obj_id,
                            event->u.obj_move.new_arena_id,
                            event->u.obj_move.new_obj_id,
                            wasRequested);
            break;
        case JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTER:
            AdaptJRawMonitorContendedEnter(event->env_id,
                                          event->u.raw_monitor.name,
                                          event->u.raw_monitor.id,
                                          wasRequested);
            break;
        case JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTERED:
            AdaptJRawMonitorContendedEntered(event->env_id,
                                            event->u.raw_monitor.name,
                                            event->u.raw_monitor.id,
                                            wasRequested);
            break;
        case JVMPI_EVENT_RAW_MONITOR_CONTENDED_EXIT:
            AdaptJRawMonitorContendedExit(event->env_id,
                                         event->u.raw_monitor.name,
                                         event->u.raw_monitor.id,
                                         wasRequested);
            break;
        case JVMPI_EVENT_THREAD_END:
            AdaptJThreadEnd(event->env_id, wasRequested);
            break;
        case JVMPI_EVENT_THREAD_START:
            AdaptJThreadStart(event->env_id,
                             event->u.thread_start.thread_name,
                             event->u.thread_start.group_name,
                             event->u.thread_start.parent_name,
                             event->u.thread_start.thread_id,
                             event->u.thread_start.thread_env_id,
                             wasRequested);
            break;
        case JVMPI_EVENT_INSTRUCTION_START:
            AdaptJInstructionStart(event->env_id,
                                  event->u.instruction.method_id,
                                  event->u.instruction.offset,
                                  event->u.instruction.u.if_info.is_true,
                                  event->u.instruction.u.tableswitch_info.key,
                                  event->u.instruction.u.tableswitch_info.low,
                                  event->u.instruction.u.tableswitch_info.hi,
                                  event->u.instruction.u.lookupswitch_info.chosen_pair_index,
                                  event->u.instruction.u.lookupswitch_info.pairs_total,
                                  wasRequested);
            break;
        default:
            reportWarningInt("Unrecognized JVMPI Event Type: %d\n", (long int)eventType);
    }

    if ((splitThreshold > 0) && (outputFile != NULL)) {
        if ((fileBytes + nameLen + 6) >= splitThreshold) {
            char newFileName[BUFF_SIZE + 6];
            byte byteBuff[BUFF_SIZE + 6 + 1];
            byte *b;
            size_t bLength = 0;
            size_t tmpLen;

            fileBytes = 0;
            
            /* Threshold reached. Split the file */
            b = byteBuff;
            ADAPTJ_WRITE_BYTE(((byte)ADAPTJ_FILESPLIT), b, bLength);
            fileCounter++; /* use another file */
            sprintf(newFileName, "%s_%04d", fileName, fileCounter);
            tmpLen = strlen(newFileName);
            ADAPTJ_WRITE_UTF8(newFileName, tmpLen, b, bLength);
            fwrite(byteBuff, 1, bLength, outputFile);
            fclose(outputFile);
            showMessage2("Threshold reached. Starting new file: \"%s\"\n", newFileName);
            outputFile = fopen(newFileName, "wb");
        }
    }
    
    if ((gcInterval > 0) && (gcInterval <= gcCurrentTotal)) {
        gcCurrentTotal = 0;
        showMessage("Forcing GC");
        jvmpi_interface->EnableGC();
        jvmpi_interface->RunGC();
        jvmpi_interface->DisableGC();
    }
    
#ifdef ADAPTJ_LOCK_NOTIFY_EVENT
    //fprintf(stderr, "RL3\n");
    jvmpi_interface->RawMonitorExit(global_access_lock);
    //fprintf(stderr, "RL3b\n");
#endif
}

JNIEXPORT jint JNICALL JVM_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    jint result;

    /* Obtain the JVMPI interface */
    if (((*jvm)->GetEnv(jvm, (void **)&jvmpi_interface, JVMPI_VERSION_1)) < 0) {
        return JNI_ERR;
    }

    totalEventCount = (jlong) 0;
    
    /* 'notifyEvent' will be called for each event */
    jvmpi_interface->NotifyEvent = notifyEvent;

    /* Create the lock which prevents data corruption in
     * Multithreaded programs */
#ifdef ADAPTJ_LOCK_NOTIFY_EVENT
    global_access_lock = jvmpi_interface->RawMonitorCreate(ADAPTJ_GLOBAL_LOCK_NAME);
#endif

    /* Initialize the agent */
    result = AdaptJInit(options);

    if (result == JNI_OK) {
        nameLen = strlen(fileName);
    }
    

    return result;
}

