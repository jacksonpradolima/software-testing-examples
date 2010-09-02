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

int read_u1(u1 *p, ccfrFile_t *f) {
    if (f->type == REGULAR_FILE) {
        return (fread(p, 1, 1, f->u.rf) == 1);
    } else {
        return (unzReadCurrentFile(f->u.af, p, 1) == 1);
    }
}

int read_u2(u2 *p, ccfrFile_t *f) {
    u1 b[2];
    if (f->type == REGULAR_FILE) {
        if (fread(b, 1, 2, f->u.rf) != 2) {
            return 0;
        }
    } else {
        if (unzReadCurrentFile(f->u.af, b, 2) != 2) {
            return 0;
        }
    }
    
    *p = ((u2)b[0] << 8) | (u2)b[1]; 
    return 1;
}

int read_u4(u4 *p, ccfrFile_t *f) {
    u1 b[4];
    int i;
    
    if (f->type == REGULAR_FILE) {
        if (fread(b, 1, 4, f->u.rf) != 4) {
            return 0;
        }
    } else {
        if (unzReadCurrentFile(f->u.af, b, 4) != 4) {
            return 0;
        }
    }
    

    *p = (u4) 0;
    for (i = 0; i < 4; i++) {
        *p |= ((u4)b[i]) << ((3 - i) * 8);
    }
    return 1;
}

/*
int read_u1_reg(u1 *p, FILE *f) {
    return (fread(p, 1, 1, f) == 1);
}

int read_u2(u2 *p, FILE *f) {
    u1 b[2];
    
    if (fread(b, 1, 2, f) == 2) {
        *p = ((u2)b[0] << 8) | (u2)b[1]; 
        return 1;
    } else {
        return 0;
    }
}

int read_u4(u4 *p, FILE *f) {
    u1 b[4];
    
    if (fread(b, 1, 4, f) == 4) {
        int i;

        *p = (u4) 0;
        for (i = 0; i < 4; i++) {
            *p |= ((u4)b[i]) << ((3 - i) * 8);
        }
        return 1;
    } else {
        return 0;
    }
}
*/

int read_u1_table(u1 **t, u4 length, ccfrFile_t *f) {
    /* fprintf(stderr, "Reading u1 table\n"); */
    if (t == NULL) {
        fprintf(stderr, "!!!! t is NULL !!!!\n");
    }
    if (length > ((u4) 0)) {
        u4 i;

        /*
        fprintf(stderr, "Allocating u1 table mem\n");
        */
        *t = NEW_ARRAY(u1, length);
        /*
        fprintf(stderr, "Allocating u1 table mem done\n");
        */
        if (*t == NULL) {
            /* fprintf(stderr, "Allocating u1 table mem failed\n"); */
            return 0;
        }
        /* fprintf(stderr, "Allocating u1 table mem succeeded\n"); */
        for (i = 0; i < length; i++) {
            if (!read_u1(*t + i, f)) {
                free(*t);
                *t = NULL;
                return 0;
            }
        }
    } else {
        *t = NULL;
    }
    
    /* fprintf(stderr, "Reading u1 table... done\n"); */
    return 1;
}

int read_u2_table(u2 **t, u4 length, ccfrFile_t *f) {
    if (length > ((u4) 0)) {
        u4 i;

        *t = NEW_ARRAY(u2, length);
        if (*t == NULL) {
            return 0;
        }
        for (i = 0; i < length; i++) {
            if (!read_u2(*t + i, f)) {
                free(*t);
                *t = NULL;
                return 0;
            }
        }
    } else {
        *t = NULL;
    }

    return 1;
}

int read_u4_table(u4 **t, u4 length, ccfrFile_t *f) {
    if (length > ((u4) 0)) {
        u4 i;

        *t = NEW_ARRAY(u4, length);
        if (*t == NULL) {
            return 0;
        }
        for (i = 0; i < length; i++) {
            if (!read_u4(*t + i, f)) {
                free(*t);
                *t = NULL;
                return 0;
            }
        }
    } else {
        *t = NULL;
    }

    return 1;
}
