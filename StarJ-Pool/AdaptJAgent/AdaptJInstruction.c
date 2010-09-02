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
extern int fileBytes;

extern boolean optMode;

extern AdaptJIDSet_t knownObjectIDs;
extern AdaptJIDSet_t knownMethodIDs;
extern AdaptJIDSetMap_t classIDtoMethods;

extern HashMap_t methodIDtoBytecode;
struct {
    jint env_id;
    jint method_id;
    jint start;
    jint lastOffset;
    jint count;
} pendingISE;

boolean isePending = false;

void AdaptJInstructionStart(JNIEnv *env_id, jmethodID method_id, jint offset, jboolean is_true, jint key,
                           jint low, jint hi, jint chosen_pair_index, jint pairs_total, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[34];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_INSTRUCTION_START];

    if (idSetContains(&knownMethodIDs, (jint) method_id) != ID_SET_OK) {
        jobjectID definingClassID;

        jvmpi_interface->DisableGC();
        definingClassID = jvmpi_interface->GetMethodClass(method_id);
        jvmpi_interface->EnableGC();

        if ((definingClassID != NULL) && (idSetContains(&knownObjectIDs, (jint) definingClassID) != ID_SET_OK)) {
#ifdef ADAPTJ_SHOW_REQUESTS
            fprintf(stderr, "InstructionStart Requesting OBJ_ALLOC event\n");
#endif
            if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, definingClassID) != JVMPI_SUCCESS) {
                reportError("Request for OBJECT_ALLOC event failed (InstStart)");
            }
        }
        if ((definingClassID != NULL) && (idSetMapContainsKey(&classIDtoMethods, (jint) definingClassID) != ID_SET_OK)) {
#ifdef ADAPTJ_SHOW_REQUESTS
            fprintf(stderr, "InstructionStart Requesting CLASS_LOAD event\n");
#endif
            if (jvmpi_interface->RequestEvent(JVMPI_EVENT_CLASS_LOAD, definingClassID) != JVMPI_SUCCESS) {
                reportError("Request for CLASS_LOAD event failed (InstStart)");
            }
        }
    }

    if (optMode && (s & (ADAPTJ_FIELD_ENV_ID | ADAPTJ_FIELD_METHOD_ID))
            && !(s & (ADAPTJ_FIELD_IS_TRUE | ADAPTJ_FIELD_KEY
                | ADAPTJ_FIELD_LOW | ADAPTJ_FIELD_HI
                | ADAPTJ_FIELD_CHOSEN_PAIR_INDEX | ADAPTJ_FIELD_PAIRS_TOTAL))) {
        if (isePending) {
            if ((pendingISE.env_id == (jint) env_id)
                    && (pendingISE.method_id == (jint) method_id)) {
                bytecode_t *bytecode;

                if (hashMapGet(&methodIDtoBytecode, (jint) method_id, &bytecode) == HASH_MAP_OK) {
                    if (offset == predictNextOffset(bytecode->code, (u4)pendingISE.lastOffset)) {
                        pendingISE.count += 1;
                        pendingISE.lastOffset = offset;
                        return ;
                    }
                }
            }
            AdaptJClearPendingISE();
        } else {
            pendingISE.env_id = (jint) env_id;
            pendingISE.method_id = (jint) method_id;
            pendingISE.start = offset;
            pendingISE.lastOffset = offset;
            pendingISE.count = 1;

            isePending = true;
            return;
        }
    }
    
    b = byteBuff;

    eventID = ADAPTJ_INSTRUCTION_START;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_METHOD_ID) {
        ADAPTJ_WRITE_JINT(((jint)method_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_OFFSET) {
        ADAPTJ_WRITE_JINT(offset, b, bLength);
    }
    if (s & ADAPTJ_FIELD_IS_TRUE) {
        ADAPTJ_WRITE_BYTE(((byte)is_true), b, bLength);
    }
    if (s & ADAPTJ_FIELD_KEY) {
        ADAPTJ_WRITE_JINT(key, b, bLength);
    }
    if (s & ADAPTJ_FIELD_LOW) {
        ADAPTJ_WRITE_JINT(low, b, bLength);
    }
    if (s & ADAPTJ_FIELD_HI) {
        ADAPTJ_WRITE_JINT(hi, b, bLength);
    }
    if (s & ADAPTJ_FIELD_CHOSEN_PAIR_INDEX) {
        ADAPTJ_WRITE_JINT(chosen_pair_index, b, bLength);
    }
    if (s & ADAPTJ_FIELD_PAIRS_TOTAL) {
        ADAPTJ_WRITE_JINT(pairs_total, b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJClearPendingISE() {
    byte byteBuff[17];
    byte *b;
    size_t bLength = 0;

    if (isePending) {
        b = byteBuff;
        if (pendingISE.count == 1) {
            ADAPTJ_WRITE_BYTE(((byte)ADAPTJ_INSTRUCTION_START), b, bLength);
            ADAPTJ_WRITE_JINT(((jint)pendingISE.env_id), b, bLength);
            ADAPTJ_WRITE_JINT(((jint)pendingISE.method_id), b, bLength);
            ADAPTJ_WRITE_JINT(pendingISE.start, b, bLength);
        } else {
            ADAPTJ_WRITE_BYTE(((byte)ADAPTJ_COMPACT_INSTRUCTION_START), b, bLength);
            ADAPTJ_WRITE_JINT(((jint)pendingISE.env_id), b, bLength);
            ADAPTJ_WRITE_JINT(((jint)pendingISE.method_id), b, bLength);
            ADAPTJ_WRITE_JINT(pendingISE.start, b, bLength);
            ADAPTJ_WRITE_JINT(pendingISE.count, b, bLength);
        }

        fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
        fwrite(byteBuff, 1, bLength, outputFile);
#endif
        isePending = false;
    }
}
