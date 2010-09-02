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

#define ADAPTJ_MESSAGE_PREFIX "AdaptJ Agent> "
#define ADAPTJ_WARNING_PREFIX "AdaptJ Agent Warning> "
#define ADAPTJ_ERROR_PREFIX   "AdaptJ Agent Error> "

extern boolean verboseMode;

void showMessage(const char *msg) {
    if (verboseMode) {
        fprintf(stderr, ADAPTJ_MESSAGE_PREFIX);
        fprintf(stderr, "%s\n", msg);
    }
}

void showMessage2(const char *format, const char *param) {
    if (verboseMode) {
        fprintf(stderr, ADAPTJ_MESSAGE_PREFIX);
        fprintf(stderr, format, param);
    }
}

void showMessageInt(const char *format, const long int param) {
    if (verboseMode) {
        fprintf(stderr, ADAPTJ_MESSAGE_PREFIX);
        fprintf(stderr, format, param);
    }
}

void reportWarning(const char *msg) {
    fprintf(stderr, ADAPTJ_WARNING_PREFIX);
    fprintf(stderr, "%s\n", msg);
}

void reportWarning2(const char *format, const char *param) {
    fprintf(stderr, ADAPTJ_WARNING_PREFIX);
    fprintf(stderr, format, param);
}

void reportWarningInt(const char *format, const long int param) {
    fprintf(stderr, ADAPTJ_WARNING_PREFIX);
    fprintf(stderr, format, param);
}

void reportError(const char *msg) {
    fprintf(stderr, ADAPTJ_ERROR_PREFIX);
    fprintf(stderr, "%s\n", msg);
}

void reportError2(const char *format, const char *param) {
    fprintf(stderr, ADAPTJ_ERROR_PREFIX);
    fprintf(stderr, format, param);
}

/* LOW-LEVEL FUNCTIONS */

int AdaptJReadEvent(AdaptJEvent *event, FILE *f) {
    if (fread(event, 1, 1, f) == 1) {
        return 1;
    }

    return 0;
}

int AdaptJReadShort(jshort *s, FILE *f) {
    int j;
    int jshortSize = sizeof(jshort);
    jshort result = (jshort) 0;

    for (j = jshortSize - 1; j >= 0; j--) {
        unsigned char c;

        if (fread(&c, 1, 1, f) < 1) {
            return 0;
        }

        result |= ((jshort) c) << (j * 8);
    }

    *s = result;
    return 1;
}

int AdaptJReadByte(byte *b, FILE *f) {
    if (fread(b, 1, 1, f) == 1) {
        return 1;
    }

    return 0;
}

int AdaptJReadBytes(void *s, jint count, FILE *f) {
    if (((jint)fread(s, 1, count, f)) == count) {
        return 1;
    }

    return 0;
}
