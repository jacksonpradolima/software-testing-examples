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

extern jshort eventInfo[];

jint readFile(FILE *f) {
    int i;
    char magic[4];
    unsigned char version;

    for (i = 0; i <= ADAPTJ_MAX_EVENT; i++) {
        eventInfo[i] = (jshort) 0;
    }
    
    /* Read Magic Number */
    if (!AdaptJReadBytes(magic, 3, f)) {
        reportError("Error reading from Spec File");
        return JNI_ERR;
    }
    magic[3] = '\0';
    if (strcmp(magic, ADAPTJ_SPEC_MAGIC)) {
        reportError("Invalid Magic Number");
        return JNI_ERR;
    }

    /* Read Version */
    if (!AdaptJReadByte(&version, f)) {
        reportError("Error reading from Spec File");
        return JNI_ERR;
    }
    if (version > ADAPTJ_SPEC_VERSION) {
        char tmp[5];
        sprintf(tmp, "%d", (int)(version));
        reportError2("Unsupported version: %s\n", tmp);
        return JNI_ERR;
    }
    
    for (i = 0; i <= ADAPTJ_MAX_EVENT; i++) {
        AdaptJEvent event;
        jshort s;
        if (!AdaptJReadEvent(&event, f) || !AdaptJReadShort(&s, f)) {
            reportError("Error reading from Spec File");
            return JNI_ERR;
        }
        eventInfo[event] = s;
    }
    return JNI_OK;
}

jint AdaptJProcessSpecFile(const char *filename) {
    FILE *inFile = fopen(filename, "rb");
    jint result;

    if (inFile == NULL) {
        reportError2("Failed to open file: \"%s\"\n", filename);
        return JNI_ERR;
    }

    result = readFile(inFile);
    fclose(inFile);
    return result;
}
