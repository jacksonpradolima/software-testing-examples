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
extern jint (*JIntMSB)(jint);
extern int fileBytes;

extern AdaptJIDSet_t knownObjectIDs;
extern AdaptJIDSetMap_t classIDtoMethods;
extern AdaptJIDSetMap_t arenaIDtoObjects;

void AdaptJObjectAlloc(JNIEnv *env_id, jint arena_id, jobjectID class_id, jint is_array, jint size,
                      jobjectID obj_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[25];
    byte * b;
    size_t bLength = 0;
    AdaptJIDSet_t *set;
    jshort s = eventInfo[ADAPTJ_OBJECT_ALLOC];
    
    if (class_id != NULL) {
        if (idSetContains(&knownObjectIDs, (jint) class_id) != ID_SET_OK) {
#ifdef ADAPTJ_SHOW_REQUESTS
            fprintf(stderr, "ObjectAlloc requesting OBJ_ALLOC event\n");
#endif
            if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, class_id) != JVMPI_SUCCESS) {
                reportError("Request for OBJECT_ALLOC event failed (ObjectAlloc)");
            }
        }
        if (idSetMapContainsKey(&classIDtoMethods, (jint) class_id) != ID_SET_MAP_OK) {
#ifdef ADAPTJ_SHOW_REQUESTS
            fprintf(stderr, "ObjectAlloc requesting CLASS_LOAD event\n");
#endif
            if (jvmpi_interface->RequestEvent(JVMPI_EVENT_CLASS_LOAD, class_id) != JVMPI_SUCCESS) {
                reportError("Request for CLASS_LOAD event failed (ObjectAlloc)");
            }
        }
    }
    
    idSetAdd(&knownObjectIDs, (jint) obj_id);
    if (idSetMapGet(&arenaIDtoObjects, (jint) arena_id, &set) == ID_SET_MAP_OK) {
        idSetAdd(set, (jint) obj_id);
    }

    b = byteBuff;

    eventID = ADAPTJ_OBJECT_ALLOC;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_ARENA_ID) {
        ADAPTJ_WRITE_JINT(arena_id, b, bLength);
    }
    if (s & ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID) {
        ADAPTJ_WRITE_JINT(((jint)class_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_IS_ARRAY) {
        ADAPTJ_WRITE_JINT(is_array, b, bLength);
    }
    if (s & ADAPTJ_FIELD_SIZE) {
        ADAPTJ_WRITE_JINT(size, b, bLength);
    }
    if (s & ADAPTJ_FIELD_OBJ_ID) {
        ADAPTJ_WRITE_JINT(((jint)obj_id), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}


void AdaptJObjectDump(JNIEnv *env_id, jint data_len, const char *data, jint requested) {
    /*
    jshort s = eventInfo[ADAPTJ_OBJECT_DUMP];
    
    AdaptJWriteOption(ADAPTJ_OBJECT_DUMP, f);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        AdaptJWriteJNIEnv(env_id, f);
    }
    if ((s & ADAPTJ_FIELD_DATA_LEN) || (s & ADAPTJ_FIELD_DATA)) {
        AdaptJWriteJInt(data_len, f);
    }
    if (s & ADAPTJ_FIELD_DATA) {
        AdaptJWriteString(data, data_len, f);
    }
    */
}

void AdaptJObjectFree(JNIEnv *env_id, jobjectID obj_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[9];
    byte * b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_OBJECT_FREE];
    
    if ((obj_id != NULL) && (idSetContains(&knownObjectIDs, (jint) obj_id) != ID_SET_OK)) {
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, obj_id) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC event failed (ObjectFree)");
        }
    }
    idSetRemove(&knownObjectIDs, (jint) obj_id);
    
    b = byteBuff;

    eventID = ADAPTJ_OBJECT_FREE;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_OBJ_ID) {
        ADAPTJ_WRITE_JINT(((jint)obj_id), b, bLength);
    }
    
    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJObjectMove(JNIEnv *env_id, jint arena_id, jobjectID obj_id, jint new_arena_id,
        jobjectID new_obj_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[21];
    byte * b;
    size_t bLength = 0;
    AdaptJIDSet_t *set;
    jshort s = eventInfo[ADAPTJ_OBJECT_MOVE];
    
    if ((obj_id != NULL) && (idSetContains(&knownObjectIDs, (jint) obj_id) != ID_SET_OK)) {
        /* fprintf(stderr, "OBJECT_MOVE event received for an unknown object: 0x%08X --> 0x%08X\n", (jint) obj_id, (jint) new_obj_id); */
        /* Do *NOT* request the event here!! This would make the VM crash */
        /*
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, obj_id) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC event failed (ObjectMove)");
        }
        */
    }

    if (idSetContains(&knownObjectIDs, (jint) obj_id) == ID_SET_OK) {
        idSetRemove(&knownObjectIDs, (jint) obj_id);
        idSetAdd(&knownObjectIDs, (jint) new_obj_id);
        idSetMapMove(&classIDtoMethods, (jint) obj_id, (jint) new_obj_id);
        if (idSetMapGet(&arenaIDtoObjects, (jint) arena_id, &set) == ID_SET_MAP_OK) {
            idSetRemove(set, (jint) obj_id);
        }
        if (idSetMapGet(&arenaIDtoObjects, (jint) new_arena_id, &set) == ID_SET_MAP_OK) {
            idSetAdd(set, (jint) obj_id);
        }
    }
    
    b = byteBuff;
    
    /* fprintf(stderr, ">>>> Moved object to ID: 0x%08X\n", (jint) new_obj_id); */

    eventID = ADAPTJ_OBJECT_MOVE;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_ARENA_ID) {
        ADAPTJ_WRITE_JINT(arena_id, b, bLength);
    }
    if (s & ADAPTJ_FIELD_OBJ_ID) {
        ADAPTJ_WRITE_JINT(((jint)obj_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_NEW_ARENA_ID) {
        ADAPTJ_WRITE_JINT(new_arena_id, b, bLength);
    }
    if (s & ADAPTJ_FIELD_NEW_OBJ_ID) {
        ADAPTJ_WRITE_JINT(((jint)new_obj_id), b, bLength);
    }
    
    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}
