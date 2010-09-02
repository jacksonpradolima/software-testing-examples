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
extern jshort eventInfo[];
extern jint (*JIntMSB)(jint);
extern jlong (*JLongMSB)(jlong);
extern int fileBytes;

void AdaptJGCFinish(JNIEnv *env_id, jlong used_objects, jlong used_object_space, jlong total_object_space, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[29];
    byte *b;
    size_t bLength = 0;
    jshort s;
    
    b = byteBuff;
    s = eventInfo[ADAPTJ_GC_FINISH];

    eventID = ADAPTJ_GC_FINISH;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    if (s & ADAPTJ_FIELD_USED_OBJECTS) {
        ADAPTJ_WRITE_JLONG(((jlong)used_objects), b, bLength);
    }
    if (s & ADAPTJ_FIELD_USED_OBJECT_SPACE) {
        ADAPTJ_WRITE_JLONG(((jlong)used_object_space), b, bLength);
    }
    if (s & ADAPTJ_FIELD_TOTAL_OBJECT_SPACE) {
        ADAPTJ_WRITE_JLONG(((jlong)total_object_space), b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void AdaptJGCStart(JNIEnv *env_id, jint requested) {
    AdaptJEvent eventID;
    byte byteBuff[5];
    byte *b;
    size_t bLength = 0;
    jshort s;
    
    b = byteBuff;
    s = eventInfo[ADAPTJ_GC_START];

    eventID = ADAPTJ_GC_START;
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
