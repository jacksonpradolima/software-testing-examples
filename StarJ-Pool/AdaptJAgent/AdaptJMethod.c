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
extern AdaptJIDSet_t knownMethodIDs;
extern AdaptJIDSetMap_t classIDtoMethods;

void AdaptJCompiledMethodLoad(JNIEnv *env_id, jmethodID method_id, void *code_addr, jint code_size,
                             jint lineno_table_size, JVMPI_Lineno *lineno_table, jint requested) {
    AdaptJEvent eventID;
    byte *byteBuff;
    byte *b;
    size_t buffSize;
    size_t bLength = 0;
    jint i;
    jshort s = eventInfo[ADAPTJ_COMPILED_METHOD_LOAD];
    
    if (idSetContains(&knownMethodIDs, (jint) method_id) != ID_SET_OK) {
        jobjectID definingClassID;

        jvmpi_interface->DisableGC();
        definingClassID = jvmpi_interface->GetMethodClass(method_id);
        jvmpi_interface->EnableGC();

        if (definingClassID != NULL) {
            if (idSetContains(&knownObjectIDs, (jint) definingClassID) != ID_SET_OK) {
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for OBJECT_ALLOC event failed (CompiledMethodLoad)");
                }
            }
            if (idSetMapContainsKey(&classIDtoMethods, (jint) definingClassID) != ID_SET_OK) {
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_CLASS_LOAD, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for CLASS_LOAD event failed (CompiledMethodLoad)");
                }
            }
        }
    }
    
    /* Compute buffer size */
    buffSize = 1;
    if (s & ADAPTJ_FIELD_ENV_ID) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_METHOD_ID) {
        buffSize += 4;
    }
    if ((s & ADAPTJ_FIELD_CODE_SIZE) || (s & ADAPTJ_FIELD_CODE)) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_CODE) {
        buffSize += code_size;
    }
    if ((s & ADAPTJ_FIELD_LINENO_TABLE_SIZE) || (s & ADAPTJ_FIELD_LINENO_TABLE)) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_LINENO_TABLE) {
        buffSize += 8 * lineno_table_size;
    }
    
    /* Write data to buffer */
    byteBuff = (byte *) malloc(buffSize);
    b = byteBuff;

    eventID = ADAPTJ_COMPILED_METHOD_LOAD;
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
    if ((s & ADAPTJ_FIELD_CODE_SIZE) || (s & ADAPTJ_FIELD_CODE)) {
        ADAPTJ_WRITE_JINT(((jint)code_size), b, bLength);
    }
    if (s & ADAPTJ_FIELD_CODE) {
        strncpy(b, code_addr, code_size);
        b += code_size;
        bLength += code_size;
    }
    if ((s & ADAPTJ_FIELD_LINENO_TABLE_SIZE) || (s & ADAPTJ_FIELD_LINENO_TABLE)) {
        ADAPTJ_WRITE_JINT(((jint)lineno_table_size), b, bLength);
    }
    if (s & ADAPTJ_FIELD_LINENO_TABLE) {
        for (i = 0; i < lineno_table_size; i++) {
            ADAPTJ_WRITE_JINT(lineno_table[i].offset, b, bLength);
            ADAPTJ_WRITE_JINT(lineno_table[i].lineno, b, bLength);
        }
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
    free(byteBuff);
}

void AdaptJCompiledMethodUnload(JNIEnv *env_id, jmethodID method_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[9];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_COMPILED_METHOD_UNLOAD];
    
    if (idSetContains(&knownMethodIDs, (jint) method_id) != ID_SET_OK) {
        jobjectID definingClassID;

        jvmpi_interface->DisableGC();
        definingClassID = jvmpi_interface->GetMethodClass(method_id);
        jvmpi_interface->EnableGC();

        if (definingClassID != NULL) {
            if (idSetContains(&knownObjectIDs, (jint) definingClassID) != ID_SET_OK) {
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for OBJECT_ALLOC event failed (CompiledMethodUnload)");
                }
            }
            if (idSetMapContainsKey(&classIDtoMethods, (jint) definingClassID) != ID_SET_OK) {
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_CLASS_LOAD, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for CLASS_LOAD event failed (CompiledMethodUnload)");
                }
            }
        }
    }

    b = byteBuff;

    eventID = ADAPTJ_COMPILED_METHOD_UNLOAD;
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

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJMethodEntry(JNIEnv *env_id, jmethodID method_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[9];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_METHOD_ENTRY];

    if (idSetContains(&knownMethodIDs, (jint) method_id) != ID_SET_OK) {
        jobjectID definingClassID;

        jvmpi_interface->DisableGC();
        definingClassID = jvmpi_interface->GetMethodClass(method_id);
        jvmpi_interface->EnableGC();

        if (definingClassID != NULL) {
            if (idSetContains(&knownObjectIDs, (jint) definingClassID) != ID_SET_OK) {
#ifdef ADAPTJ_SHOW_REQUESTS
                fprintf(stderr, "MethodEntry requesting OBJ_ALLOC event\n");
#endif
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for OBJECT_ALLOC event failed (MethodEntry)");
                }
            }
            if (idSetMapContainsKey(&classIDtoMethods, (jint) definingClassID) != ID_SET_OK) {
#ifdef ADAPTJ_SHOW_REQUESTS
                fprintf(stderr, "MethodEntry requesting CLASS_LOAD event\n");
#endif                
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_CLASS_LOAD, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for CLASS_LOAD event failed (MethodEntry)");
                }
            }
        }
    }

    b = byteBuff;

    eventID = ADAPTJ_METHOD_ENTRY;
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

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJMethodEntry2(JNIEnv *env_id, jmethodID method_id, jobjectID obj_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[13];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_METHOD_ENTRY2];


    /* fprintf(stderr, "* Processing method entry 2\n"); */
    if ((method_id != NULL) && (idSetContains(&knownMethodIDs, (jint) method_id) != ID_SET_OK)) {
        jobjectID definingClassID;

        /* fprintf(stderr, "** Disabling GC\n"); */
        jvmpi_interface->DisableGC();
        /* fprintf(stderr, "** Getting method class\n"); */
        definingClassID = jvmpi_interface->GetMethodClass(method_id);
        /* fprintf(stderr, "** Enabling GC\n"); */
        jvmpi_interface->EnableGC();

        if (definingClassID != NULL) {
            if (idSetContains(&knownObjectIDs, (jint) definingClassID) != ID_SET_OK) {
#ifdef ADAPTJ_SHOW_REQUESTS
                fprintf(stderr, "MethodEntry2 requesting OBJ_ALLOC event\n");
#endif
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for OBJECT_ALLOC event failed (MethodEntry2)");
                }
            }
            if (idSetMapContainsKey(&classIDtoMethods, (jint) definingClassID) != ID_SET_OK) {
#ifdef ADAPTJ_SHOW_REQUESTS
                fprintf(stderr, "MethodEntry2 requesting CLASS_LOAD event\n");
#endif
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_CLASS_LOAD, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for CLASS_LOAD event failed (MethodEntry2)");
                }
            }
        }
    }

    if ((obj_id != NULL) && (idSetContains(&knownObjectIDs, (jint) obj_id) != ID_SET_OK)) {
#ifdef ADAPTJ_SHOW_REQUESTS
        fprintf(stderr, "MethodEntry2 requesting OBJ_ALLOC event (2)\n");
#endif
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, obj_id) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC (2) event failed (MethodEntry2)");
        }
    }
    
    b = byteBuff;

    /* fprintf(stderr, ">>>> Method Invoked with ID: 0x%08X\n", (jint) obj_id); */

    eventID = ADAPTJ_METHOD_ENTRY2;
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
    if (s & ADAPTJ_FIELD_OBJ_ID) {
        ADAPTJ_WRITE_JINT(((jint)obj_id), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
    /* fprintf(stderr, "* Done processing method entry 2\n"); */
}

void AdaptJMethodExit(JNIEnv *env_id, jmethodID method_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[9];
    byte *b;
    size_t bLength = 0;
    jshort s = eventInfo[ADAPTJ_METHOD_EXIT];

    if (idSetContains(&knownMethodIDs, (jint) method_id) != ID_SET_OK) {
        jobjectID definingClassID;

        jvmpi_interface->DisableGC();
        definingClassID = jvmpi_interface->GetMethodClass(method_id);
        jvmpi_interface->EnableGC();

        if (definingClassID != NULL) {
            if (idSetContains(&knownObjectIDs, (jint) definingClassID) != ID_SET_OK) {
#ifdef ADAPTJ_SHOW_REQUESTS
                fprintf(stderr, "MethodExit requesting OBJ_ALLOC event\n");
#endif
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for OBJECT_ALLOC event failed (MethodExit)");
                }
            }
            if (idSetMapContainsKey(&classIDtoMethods, (jint) definingClassID) != ID_SET_OK) {
#ifdef ADAPTJ_SHOW_REQUESTS
                fprintf(stderr, "MethodExit requesting CLASS_LOAD event\n");
#endif
                if (jvmpi_interface->RequestEvent(JVMPI_EVENT_CLASS_LOAD, definingClassID) != JVMPI_SUCCESS) {
                    reportError("Request for CLASS_LOAD event failed (MethodExit)");
                }
            }
        }
    }

    b = byteBuff;

    eventID = ADAPTJ_METHOD_EXIT;
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

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}
