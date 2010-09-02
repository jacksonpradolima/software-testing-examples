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

#include "ccfr.h"

int startsWith(const char *str, const char *prefix) {
    int prefixLen, strLen;
    if ((prefix == NULL) || (str == NULL)) {
        return 0;
    }

    prefixLen = strlen(prefix);
    if (prefixLen == 0) {
        return 1; /* Java API compliance */
    }
    strLen = strlen(str);

    if (prefixLen > strLen) {
        return 0;
    }

    return (strncmp(str, prefix, prefixLen) == 0);
}


int endsWith(const char *str, const char *suffix) {
    int suffixLen, strLen;
    
    if ((suffix == NULL) || (str == NULL)) {
        return 0;
    }

    suffixLen = strlen(suffix);
    if (suffixLen == 0) {
        return 1; // Java API compliance
    }
    strLen = strlen(str);

    if (suffixLen > strLen) {
        return 0;
    }

    return (strcmp(str + (strLen - suffixLen), suffix) == 0);
}


int isCharAt(const char *str, unsigned int pos, char c) {
    if (pos >= strlen(str)) {
        return 0;
    }

    return (str[pos] == c);
}

void replace(char *str, char from, char to) {
    for (; *str; str++) {
        if (*str == from) {
            *str = to;
        }
    }
}

u2 calculateUTF8CharLength(u1 *bytes, u2 length) {
    u2 i;
    u2 result = 0; 
    u1 x;

    for (i = 0; i < length; result++) {
        x = bytes[i];

        if (!(x & (u1)(0x80))) {
            /* x is of the form 0xxxxxx */
            i += 1;
        } else if (((x & (u1)(0xe0)) == (u1)(0xc0))) {
            i += 2;
        } else {
            i += 3;
        }
    }

    return result;
}



u2 getNextUTF8Char(u1 *bytes, u2 *position) {
    u1 x, y, z;

    x = bytes[(*position)++];
    if (!(x & (u1)(0x80))) {
        return (u2)(x);
    } else if (((x & (u1)(0xe0)) == (u1)(0xc0))) {
        y = bytes[(*position)++];
        return (u2)(((x & 0x1f) << 6) | (y & 0x3f));
    } else {
        y = bytes[(*position)++];
        z = bytes[(*position)++];
        return (u2)(((x & 0xf) << 12) | ((y & 0x3f) << 6) | (z & 0x3f));
    }
}

int UTF8toCString(u1 *bytes, u2 length, char *str, int strLen) {
    u2 i;
    u2 position = 0;
    u2 char_length;
    
    if (str == NULL) {
        return -1;
    }

    char_length = calculateUTF8CharLength(bytes, length);
    if (strLen < char_length + 1) {
        return -1;
    }
    
    for (i = (u2)(0); i < char_length; i++) {
        /* Check for valid ASCII */
        u2 c;
        c = getNextUTF8Char(bytes, &position);
        if (c > 127) {
            return 0;
        }

        str[i] = (char) c;
    }

    str[i] = '\0';

    return 1;
}
