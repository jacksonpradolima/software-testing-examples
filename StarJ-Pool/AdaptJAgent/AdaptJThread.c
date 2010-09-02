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
extern AdaptJIDSet_t knownThreadIDs;

extern boolean threadStatus;
extern ThreadStatus_t *threadStats;

extern FILE *outputFile;
extern jshort eventInfo[];
extern jshort (*JShortMSB)(jshort);
extern jint (*JIntMSB)(jint);
extern int fileBytes;

extern AdaptJIDSet_t knownObjectIDs;

void checkThreadStatus() {
    ThreadStatus_t *tstat;
    jint currentStatus;

    for (tstat = threadStats; tstat != NULL; tstat = tstat->next) {
        currentStatus = jvmpi_interface->GetThreadStatus(tstat->env);
        if (currentStatus != tstat->status) {
            tstat->status = currentStatus;
            AdaptJThreadStatusChange(tstat->env, currentStatus);
        }
    }
}

void startThread(JNIEnv *envID) {
    ThreadStatus_t *tstat;

    tstat = NEW(ThreadStatus_t);
    if (tstat != NULL) {
        tstat->env = envID;
        tstat->status = (jint) -1;
        tstat->next = threadStats;
        threadStats = tstat;
    }

    checkThreadStatus();
}

void stopThread(JNIEnv *envID) {
    ThreadStatus_t *tstat, *tmp;

    tmp = NULL;
    for (tstat = threadStats; tstat != NULL; tstat = tstat->next) {
        if (tstat->env == envID) {
            if (tmp == NULL) {
                threadStats = tstat->next;
            } else {
                tmp->next = tstat->next;
            }
            free(tstat);
            break;
        } 
    }

    checkThreadStatus();
}


void AdaptJThreadStatusChange(JNIEnv *env_id, jint newStatus) {
    byte byteBuff[9];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_THREAD_STATUS_CHANGE];
    
    
    b = byteBuff;
    ADAPTJ_WRITE_BYTE(((byte)ADAPTJ_THREAD_STATUS_CHANGE), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_NEW_STATUS) {
        ADAPTJ_WRITE_JINT(newStatus, b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJThreadEnd(JNIEnv *thread_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[5];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_THREAD_END];
    
    idSetRemove(&knownThreadIDs, (jint) thread_id);
    
    if (threadStatus) {
        stopThread(thread_id);
    }   
    
    b = byteBuff;

    eventID = ADAPTJ_THREAD_END;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)thread_id), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJThreadStart(JNIEnv *env_id, const char *thread_name, const char *group_name, const char *parent_name,
                      jobjectID thread_id, JNIEnv *thread_env_id, jint requested) {
    AdaptJEvent eventID;
    byte *byteBuff;
    byte *b;
    size_t buffSize;
    size_t bLength = 0;
    size_t tmpLen = 0;
    jshort s = eventInfo[ADAPTJ_THREAD_START];


    idSetAdd(&knownThreadIDs, (jint) thread_env_id);
    
    if ((thread_id != NULL) && (idSetContains(&knownObjectIDs, (jint) thread_id) != ID_SET_OK)) {
#ifdef ADAPTJ_SHOW_REQUESTS
        fprintf(stderr, "ThreadStart Requesting Thread Object ID\n");
#endif
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, thread_id) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC event failed (ThreadStart)");
        }
    } 
    /* Compute buffer size */
    buffSize = 1;
    if (s & ADAPTJ_FIELD_ENV_ID) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_THREAD_NAME) {
        buffSize += strlen(thread_name) + 2;
    }
    if (s & ADAPTJ_FIELD_GROUP_NAME) {
        buffSize += strlen(thread_name) + 2;
    }
    if (s & ADAPTJ_FIELD_PARENT_NAME) {
        buffSize += strlen(thread_name) + 2;
    }
    if (s & ADAPTJ_FIELD_THREAD_ID) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_THREAD_ENV_ID) {
        buffSize += 4;
    } 
    
    byteBuff = (byte *) malloc(buffSize);
    b = byteBuff;
    
    eventID = ADAPTJ_THREAD_START;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_THREAD_NAME) {
        tmpLen += strlen(thread_name);
        ADAPTJ_WRITE_UTF8(thread_name, tmpLen, b, bLength);
    }
    if (s & ADAPTJ_FIELD_GROUP_NAME) {
        tmpLen += strlen(group_name);
        ADAPTJ_WRITE_UTF8(group_name, tmpLen, b, bLength);
    }
    if (s & ADAPTJ_FIELD_PARENT_NAME) {
        tmpLen += strlen(parent_name);
        ADAPTJ_WRITE_UTF8(parent_name, tmpLen, b, bLength);
    }
    if (s & ADAPTJ_FIELD_THREAD_ID) {
        ADAPTJ_WRITE_JINT(((jint)thread_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_THREAD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)thread_env_id), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif

    if (threadStatus) {
        startThread(env_id);
    }
}
