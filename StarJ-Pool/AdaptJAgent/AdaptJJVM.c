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
                       
extern FILE *outputFile;
extern char fileName[];
extern jshort eventInfo[];
extern int fileBytes;
extern jshort (*JShortMSB)(jshort);
extern jint (*JIntMSB)(jint);
extern jlong (*JLongMSB)(jlong);
#ifdef ADAPTJ_ENABLE_PIPE
extern boolean pipeMode;
#endif
extern long counters[];
extern int fileCounter;

extern char **cp;
extern int cpSize;
extern HashMap_t methodIDtoBytecode;

void AdaptJVMInitDone(JNIEnv *env_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[5];
    byte *b;
    size_t bLength = 0;
    jshort s;

    s = eventInfo[ADAPTJ_JVM_INIT_DONE];
    b = byteBuff;
    
    eventID = ADAPTJ_JVM_INIT_DONE;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJJVMShutDown(JNIEnv *env_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[11];
    byte *b;
    size_t bLength = 0;
    jshort s;
    int i;
    FILE *tmpFile;

 
    s = eventInfo[ADAPTJ_JVM_SHUT_DOWN];
    b = byteBuff;
    
    if (s & ADAPTJ_FIELD_RECORDED) {
        eventID = ADAPTJ_JVM_SHUT_DOWN;
        if (requested) {
            eventID |= ADAPTJ_REQUESTED_EVENT;
        }
        ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
        if (s & ADAPTJ_FIELD_ENV_ID) {
            ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
        }
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
    
    tmpFile = outputFile;
    outputFile = NULL;
#ifdef ADAPTJ_ENABLE_PIPE
    if (!pipeMode) {
#endif
        if (fileCounter > 0) {
            fclose(tmpFile);
            tmpFile = fopen(fileName, "r+b");
        }
        fseek(tmpFile, 6, SEEK_SET);
        for (i = 0; i <= ADAPTJ_MAX_EVENT; i++) {
            jshort info = eventInfo[i];
            if (info & ADAPTJ_FIELD_REQUIRED) {
                info &= (~ADAPTJ_FIELD_REQUIRED);
            }

            b = byteBuff;
            bLength = 0;
            ADAPTJ_WRITE_BYTE(((byte) i), b, bLength);
            ADAPTJ_WRITE_JSHORT(info, b, bLength);
            if (eventInfo[i] & ADAPTJ_FIELD_COUNTED) {
                ADAPTJ_WRITE_JLONG(counters[i], b, bLength);
            }
#ifdef ADAPTJ_WRITE_OUTPUT
            fwrite(byteBuff, 1, bLength, tmpFile);
#endif
#ifdef ADAPTJ_ENABLE_PIPE
        }
#endif
    } 
    
    fclose(tmpFile);
    for (i = 0; i < cpSize; i++) {
        free(cp[i]);
    }
    if (cp != NULL) {
        free(cp);
    }

    releaseHashMap(&methodIDtoBytecode);
    showMessage("Done");
}
