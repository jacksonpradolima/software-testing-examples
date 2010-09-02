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

extern AdaptJEvent charToEvent[];
extern jshort eventInfo[];

char *getNextOption(char *src, char *option, char *value) {
    char *p = src;
    char *q = option;
    
    *option = '\0';
    *value = '\0';
    while (true) {
        switch (*p) {
            case '=':
                p++;
                *q = '\0';
                q = value;
                break;
            case ',':
                *q = '\0';
                p++;
                return p;
            case '\0':
                *q = *p;
                return p;
                break;
            default:
                *q = *p;
                p++;
                q++;
                break;
        }
    }
}

char *stripQuotes(char *s) {
    char *p = s;
    char *q = s;
    while (*p == '"') {
        p++;
    }

    while (*q != '\0') {
        q++;
    }
    q--;

    while (*q == '"') {
        *q = '\0';
        q--;
    }
    
    return p;
}

jint processEventString(char *eventString, jshort infoMask) {
    char *q;
    q = stripQuotes(eventString);
    if (q[0] == '\0') {
        reportError("no specified events");
        return JNI_ERR;
    }

    while (*q != '\0') {
        AdaptJEvent event = charToEvent[(int)*q];
        if (event == ADAPTJ_INVALID_EVENT) {
            q[1] = '\0';
            reportError2("Unrecognized event option: \"%s\"\n", q);
            return JNI_ERR;
        }

        eventInfo[event] |= infoMask;
        q++;
    }

    return JNI_OK;
}

boolean parseBoolean(char *buff, boolean *result) {
    char *q;
    q = stripQuotes(buff);

    if (!strcmp(q, "true") || !strcmp(q, "on") || !strcmp(q, "yes")) {
        *result = true;
        return true;
    } else if (!strcmp(q, "false") || !strcmp(q, "off") || !strcmp(q, "no")) {
        *result = false;
        return true;
    } else {
        reportError2("Invalid boolean value: \"%s\"\n", q);
        return false;
    }
}

/* Byte order */
boolean isMSB() {
    int i = 1;
    char *c;

    c = (char *) &i;
    return (*c == '\0');
}

/* LSB to MSB functions */
jshort JShortLSB2MSB(jshort val) {
    jshort result = ((jshort) 0);
    int i;

    for (i = 0; i < sizeof(jshort); i++) {
        result <<= 8;
        result |= val & ((jshort) 0x00FF);
        val >>= 8;
    }

    return result;
}

jint JIntLSB2MSB(jint val) {
    jint result = ((jint) 0);
    int i;
    
    for (i = 0; i < sizeof(jint); i++) {
        result <<= 8;
        result |= val & ((jint) 0x000000FF);
        val >>= 8;
    }

    return result;
}

jlong JLongLSB2MSB(jlong val) {
    jlong result = ((jlong) 0);
    int i;
    
    for (i = 0; i < sizeof(jlong); i++) {
        result <<= 8;
        result |= val & ((jlong) 0x000000FFL);
        val >>= 8;
    }

    return result;
}

jshort JShortMSB2MSB(jshort val) {
    return val;
}

jint JIntMSB2MSB(jint val) {
    return val;
}

jlong JLongMSB2MSB(jlong val) {
    return val;
}
