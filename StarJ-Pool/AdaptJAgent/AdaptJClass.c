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

extern HashMap_t methodIDtoBytecode;
extern char **cp;
extern int cpSize;

extern AdaptJIDSet_t knownObjectIDs;
extern AdaptJIDSet_t knownMethodIDs;
extern AdaptJIDSetMap_t classIDtoMethods;

void processClass(const char *name, jint num_methods, JVMPI_Method *methods,
                  jobjectID class_id) {
    int classAvail;
    classfile_t ccfrClass;
    AdaptJIDSet_t *methodSet;
    jint i;
    
    methodSet = NEW(AdaptJIDSet_t);
    if (methodSet != NULL) {
        initIDSet(methodSet, num_methods, 1.0);
        idSetMapPut(&classIDtoMethods, (jint) class_id, methodSet);
    }

    if (optMode) {
        initClassfile(&ccfrClass);
        classAvail = (name[0] != '[') && resolveClass(name, (const char **)cp, cpSize, &ccfrClass);
    } else {
        classAvail = 0;
    }


    for (i = 0; i < num_methods; i++) {
        idSetAdd(&knownMethodIDs, (jint) methods[i].method_id);
        if (methodSet != NULL) {
            idSetAdd(methodSet, (jint) methods[i].method_id);
        }
        
        if (classAvail) {
            bytecode_t *bytecode;
            method_info_t *ccfrMethod;
         
            /* Add method to HashMap_t */
            if (getMethodByName(&ccfrClass, methods[i].method_name, methods[i].method_signature, &ccfrMethod)) {
                bytecode = (bytecode_t *) malloc(sizeof(bytecode_t *));
                if (getBytecode(ccfrMethod, bytecode)) {
                    u2 index;
                    u1 *c;
                    
                    /* Make a copy of the code */
                    c = (u1 *) malloc(bytecode->length);
                    for (index = 0; index < bytecode->length; index++) {
                        c[index] = bytecode->code[index];
                    }
                    bytecode->code = c;

                    hashMapPut(&methodIDtoBytecode, (jint)methods[i].method_id, bytecode);
                } else {
                    free(bytecode);
                }
            } 
        }
    }

    if (optMode) {
        cleanupClassfile(&ccfrClass);
    }
}

void AdaptJClassLoad(JNIEnv *env_id, const char *name, const char *src_name,
                    jint num_interfaces, jint num_methods, JVMPI_Method *methods,
                    jint num_static_fields, JVMPI_Field *statics,
                    int num_instance_fields, JVMPI_Field *instances,
                    jobjectID class_id, jint requested) {
    AdaptJEvent eventID;
    byte *byteBuff;
    byte *b;
    size_t bLength = 0;
    size_t buffSize; /* Computed buffer size for malloc() */
    size_t tmpLen;
    jint i;
    jshort s;
    
    processClass(name, num_methods, methods, class_id);
    s = eventInfo[ADAPTJ_CLASS_LOAD];

    /* Calculate buffer size */
    buffSize = 1;
    if (s & ADAPTJ_FIELD_ENV_ID) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_CLASS_NAME) {
        buffSize += strlen(name) + 2;
    }
    if (s & ADAPTJ_FIELD_SOURCE_NAME) {
        if (src_name == NULL) {
            src_name = "(null)";
        }
        buffSize += strlen(src_name) + 2;
    }
    if (s & ADAPTJ_FIELD_NUM_INTERFACES) {
        buffSize += 4;
    }
    if ((s & ADAPTJ_FIELD_NUM_METHODS) || (s & ADAPTJ_FIELD_METHODS)) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_METHODS) {
        for (i = 0; i < num_methods; i++) {
            buffSize += strlen(methods[i].method_name) + 2;
            buffSize += strlen(methods[i].method_signature) + 2;
            buffSize += 12;
        }
    }
    if ((s & ADAPTJ_FIELD_NUM_STATIC_FIELDS) || (s & ADAPTJ_FIELD_STATICS)) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_STATICS) {
        for (i = 0; i < num_static_fields; i++) {
            buffSize += strlen(statics[i].field_name) + 2;
            buffSize += strlen(statics[i].field_signature) + 2;
        }
    }
    if ((s & ADAPTJ_FIELD_NUM_INSTANCE_FIELDS) || (s & ADAPTJ_FIELD_INSTANCES)) {
        buffSize += 4;
    }
    if (s & ADAPTJ_FIELD_INSTANCES) {
        for (i = 0; i < num_instance_fields; i++) {
            buffSize += strlen(instances[i].field_name) + 2;
            buffSize += strlen(instances[i].field_signature) + 2;
        }
    }
    if (s & ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID) {
        buffSize += 4;
    }
    
    /* Allocate buffer */
    byteBuff = (byte *) malloc(buffSize);
    b = byteBuff;

    /* Write Data to Buffer */
    eventID = ADAPTJ_CLASS_LOAD;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_CLASS_NAME) {
        tmpLen = strlen(name);
        ADAPTJ_WRITE_UTF8(name, tmpLen, b, bLength);
    }
    if (s & ADAPTJ_FIELD_SOURCE_NAME) {
        tmpLen = strlen(src_name);
        ADAPTJ_WRITE_UTF8(src_name, tmpLen, b, bLength);
    }
    if (s & ADAPTJ_FIELD_NUM_INTERFACES) {
        ADAPTJ_WRITE_JINT(num_interfaces, b, bLength);
    }
    /* FIXME no interfaces array?? */
    if ((s & ADAPTJ_FIELD_NUM_METHODS) || (s & ADAPTJ_FIELD_METHODS)) {
        ADAPTJ_WRITE_JINT(num_methods, b, bLength);
    }
    
    if (s & ADAPTJ_FIELD_METHODS) {
        for (i = 0; i < num_methods; i++) {
            tmpLen = strlen(methods[i].method_name);
            ADAPTJ_WRITE_UTF8(methods[i].method_name, tmpLen, b, bLength);
            tmpLen = strlen(methods[i].method_signature);
            ADAPTJ_WRITE_UTF8(methods[i].method_signature, tmpLen, b, bLength);
            ADAPTJ_WRITE_JINT(methods[i].start_lineno, b, bLength);
            ADAPTJ_WRITE_JINT(methods[i].end_lineno, b, bLength);
            ADAPTJ_WRITE_JINT(((jint)methods[i].method_id), b, bLength);            
        }
    }

    if ((s & ADAPTJ_FIELD_NUM_STATIC_FIELDS) || (s & ADAPTJ_FIELD_STATICS)) {
        ADAPTJ_WRITE_JINT(num_static_fields, b, bLength);
    }
    if (s & ADAPTJ_FIELD_STATICS) {
        for (i = 0; i < num_static_fields; i++) {
            tmpLen = strlen(statics[i].field_name);
            ADAPTJ_WRITE_UTF8(statics[i].field_name, tmpLen, b, bLength);
            tmpLen = strlen(statics[i].field_signature);
            ADAPTJ_WRITE_UTF8(statics[i].field_signature, tmpLen, b, bLength);
        }
    }
    if ((s & ADAPTJ_FIELD_NUM_INSTANCE_FIELDS) || (s & ADAPTJ_FIELD_INSTANCES)) {
        ADAPTJ_WRITE_JINT(num_instance_fields, b, bLength);
    }
    if (s & ADAPTJ_FIELD_INSTANCES) {
        for (i = 0; i < num_instance_fields; i++) {
            tmpLen = strlen(instances[i].field_name);
            ADAPTJ_WRITE_UTF8(instances[i].field_name, tmpLen, b, bLength);
            tmpLen = strlen(instances[i].field_signature);
            ADAPTJ_WRITE_UTF8(instances[i].field_signature, tmpLen, b, bLength);
        }
    }
    if (s & ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID) {
        ADAPTJ_WRITE_JINT(((jint)class_id), b, bLength);
    }
    
    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile); 
#endif
    free(byteBuff);
}

void AdaptJClassUnload(JNIEnv *env_id, jobjectID class_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[9];
    byte *b;
    jshort s;
    size_t bLength = 0;
    AdaptJIDSet_t *set;
    
    /*
    if ((class_id != NULL) && (idSetContains(&knownObjectIDs, (jint) class_id) != ID_SET_OK)) {
#ifdef ADAPTJ_SHOW_REQUESTS
        fprintf(stderr, "ClassUnload Requesting OBJ_ALLOC event\n");
#endif
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_OBJECT_ALLOC, class_id) != JVMPI_SUCCESS) {
            reportError("Request for OBJECT_ALLOC event failed (ClassUnload)");
        }
    }
    if ((class_id != NULL) && (idSetMapContainsKey(&classIDtoMethods, (jint) class_id) != ID_SET_MAP_OK)) {
#ifdef ADAPTJ_SHOW_REQUESTS
        fprintf(stderr, "ClassUnload Requesting CLASS_LOAD event\n");
#endif
        if (jvmpi_interface->RequestEvent(JVMPI_EVENT_CLASS_LOAD, class_id) != JVMPI_SUCCESS) {
            reportError("Request for CLASS_LOAD event failed (ClassUnload)");
        }
    }
    */
    
    if (idSetMapGet(&classIDtoMethods, (jint) class_id, &set) == ID_SET_MAP_OK) {
        idSetApplyAll(set, removeFromKnownMethods);
    }
    
    b = byteBuff;
    s = eventInfo[ADAPTJ_CLASS_UNLOAD];
    
    eventID = ADAPTJ_CLASS_UNLOAD;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID) {
        ADAPTJ_WRITE_JINT(((jint)class_id), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void removeFromKnownMethods(jint method_id) {
    idSetRemove(&knownMethodIDs, method_id);
}
