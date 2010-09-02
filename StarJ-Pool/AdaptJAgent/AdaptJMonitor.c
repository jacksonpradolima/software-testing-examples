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

#include "AdaptJAgent.h"
                                                            
extern JVMPI_Interface *jvmpi_interface;

extern FILE *outputFile;
extern jshort eventInfo[];
extern jshort (*JShortMSB)(jshort);
extern jint (*JIntMSB)(jint);
extern jlong (*JLongMSB)(jlong);
extern int fileBytes;

extern AdaptJIDSet_t knownObjectIDs;

void AdaptJMonitorContendedEnter(JNIEnv *env_id, jobjectID object, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[9];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_MONITOR_CONTENDED_ENTER];
    
    if ((object != NULL) && (idSetContains(&knownObjectIDs, (jint) object) != ID_SET_OK)) {
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, object) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC event failed (MonitorContendedEnter)");
        }
    }

    b = byteBuff;

    eventID = ADAPTJ_MONITOR_CONTENDED_ENTER;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_OBJECT) {
        ADAPTJ_WRITE_JINT(((jint)object), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}


void AdaptJMonitorContendedEntered(JNIEnv *env_id, jobjectID object, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[9];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_MONITOR_CONTENDED_ENTERED];
    
    if ((object != NULL) && (idSetContains(&knownObjectIDs, (jint) object) != ID_SET_OK)) {
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, object) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC event failed (MonitorContendedEntered)");
        }
    }

    b = byteBuff;

    eventID = ADAPTJ_MONITOR_CONTENDED_ENTERED;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_OBJECT) {
        ADAPTJ_WRITE_JINT(((jint)object), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJMonitorContendedExit(JNIEnv *env_id, jobjectID object, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[9];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_MONITOR_CONTENDED_EXIT];
    
    if ((object != NULL) && (idSetContains(&knownObjectIDs, (jint) object) != ID_SET_OK)) {
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, object) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC event failed (MonitorContendedExit)");
        }
    }

    b = byteBuff;

    eventID = ADAPTJ_MONITOR_CONTENDED_EXIT;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_OBJECT) {
        ADAPTJ_WRITE_JINT(((jint)object), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJMonitorDump(JNIEnv *env_id, const char *begin, const char *end, jint num_traces,
                      JVMPI_CallTrace *traces, jint *threads_status, jint requested) {
    /*
    jshort s = eventInfo[ADAPTJ_MONITOR_DUMP];
    
    AdaptJWriteOption(ADAPTJ_MONITOR_DUMP, f);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        AdaptJWriteJNIEnv(env_id, f);
    }
    jint byteCount = (jint)(end - begin + 1);
    if ((s & ADAPTJ_FIELD_DATA_LEN) || (s & ADAPTJ_FIELD_DATA)) {
        AdaptJWriteJInt(byteCount, f);
    }
    if (s & ADAPTJ_FIELD_DATA) {
        AdaptJWriteBytes((void *)begin, byteCount, f);
    }
    if ((s & ADAPTJ_FIELD_NUM_TRACES) || (s & ADAPTJ_FIELD_TRACES)) {
        AdaptJWriteJInt(num_traces, f);
    }
    if (s & ADAPTJ_FIELD_TRACES) {
        for (jint i = 0; i < num_traces; i++) {
            AdaptJWriteJVMPICallTrace((traces + i), f);
            AdaptJWriteJInt(threads_status[i], f);
        }
    }
    */
}

void AdaptJMonitorWait(JNIEnv *env_id, jobjectID object, jlong timeout, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[17];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_MONITOR_WAIT];
    
    if ((object != NULL) && (idSetContains(&knownObjectIDs, (jint) object) != ID_SET_OK)) {
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, object) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC event failed (MonitorWait)");
        }
    }

    b = byteBuff;

    eventID = ADAPTJ_MONITOR_WAIT;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_OBJECT) {
        ADAPTJ_WRITE_JINT(((jint)object), b, bLength);
    }
    if (s & ADAPTJ_FIELD_TIMEOUT) {
        ADAPTJ_WRITE_JLONG(timeout, b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJMonitorWaited(JNIEnv *env_id, jobjectID object, jlong timeout, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[17];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_MONITOR_WAITED];
    
    if ((object != NULL) && (idSetContains(&knownObjectIDs, (jint) object) != ID_SET_OK)) {
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, object) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC event failed (MonitorWaited)");
        }
    }

    b = byteBuff;

    eventID = ADAPTJ_MONITOR_WAITED;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_OBJECT) {
        ADAPTJ_WRITE_JINT(((jint)object), b, bLength);
    }
    if (s & ADAPTJ_FIELD_TIMEOUT) {
        ADAPTJ_WRITE_JLONG(timeout, b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJRawMonitorContendedEnter(JNIEnv *env_id, const char *name, JVMPI_RawMonitor id, jint requested) {
    AdaptJEvent eventID;
    byte *byteBuff;
    byte *b;
    size_t buffSize;
    size_t bLength = 0;
    size_t tmpLen = 0;
    jshort s = eventInfo[ADAPTJ_RAW_MONITOR_CONTENDED_ENTER];

    /* Compute dynamic buffer size */
    buffSize = 1;
    if (s & ADAPTJ_FIELD_ENV_ID) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_NAME) {
        tmpLen = (name != NULL ? strlen(name) : 0);
        buffSize += tmpLen + 2;
    }
    if (s & ADAPTJ_FIELD_ID) {
        buffSize += 4;
    } 
    
    /* Write data to buffer */
    byteBuff = (byte *) malloc(buffSize);
    b = byteBuff;

    eventID = ADAPTJ_RAW_MONITOR_CONTENDED_ENTER;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_NAME) {
        ADAPTJ_WRITE_UTF8(name, tmpLen, b, bLength);
    }
    if (s & ADAPTJ_FIELD_ID) {
        ADAPTJ_WRITE_JINT(((jint)id), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
    free(byteBuff);
}

void AdaptJRawMonitorContendedEntered(JNIEnv *env_id, const char *name, JVMPI_RawMonitor id, jint requested) {
    AdaptJEvent eventID;
    byte *byteBuff;
    byte *b;
    size_t buffSize;
    size_t bLength = 0;
    size_t tmpLen = 0;
    jshort s = eventInfo[ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED];

    /* Compute dynamic buffer size */
    buffSize = 1;
    if (s & ADAPTJ_FIELD_ENV_ID) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_NAME) {
        tmpLen = (name != NULL ? strlen(name) : 0);
        buffSize += tmpLen + 2;
    }
    if (s & ADAPTJ_FIELD_ID) {
        buffSize += 4;
    } 
    
    /* Write data to buffer */
    byteBuff = (byte *) malloc(buffSize);
    b = byteBuff;

    eventID = ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_NAME) {
        ADAPTJ_WRITE_UTF8(name, tmpLen, b, bLength);
    }
    if (s & ADAPTJ_FIELD_ID) {
        ADAPTJ_WRITE_JINT(((jint)id), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
    free(byteBuff);
}

void AdaptJRawMonitorContendedExit(JNIEnv *env_id, const char *name, JVMPI_RawMonitor id, jint requested) {
    AdaptJEvent eventID;
    byte *byteBuff;
    byte *b;
    size_t buffSize;
    size_t bLength = 0;
    size_t tmpLen = 0;
    jshort s = eventInfo[ADAPTJ_RAW_MONITOR_CONTENDED_EXIT];

    /* Compute dynamic buffer size */
    buffSize = 1;
    if (s & ADAPTJ_FIELD_ENV_ID) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_NAME) {
        tmpLen = (name != NULL ? strlen(name) : 0);
        buffSize += tmpLen + 2;
    }
    if (s & ADAPTJ_FIELD_ID) {
        buffSize += 4;
    } 
    
    /* Write data to buffer */
    byteBuff = (byte *) malloc(buffSize);
    b = byteBuff;

    eventID = ADAPTJ_RAW_MONITOR_CONTENDED_EXIT;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_NAME) {
        ADAPTJ_WRITE_UTF8(name, tmpLen, b, bLength);
    }
    if (s & ADAPTJ_FIELD_ID) {
        ADAPTJ_WRITE_JINT(((jint)id), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
    free(byteBuff);
}
