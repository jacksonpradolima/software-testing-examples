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

int readCPItem(cp_info_t *cp, ccfrFile_t *f) {
    /* fprintf(stderr, "CCFRCP >> in readCPItem\n"); */
    if (!read_u1(&(cp->tag), f)) {
        return 0;
    }

    /* fprintf(stderr, "CCFRCP >> Tag is %d\n", cp->tag); */
    switch (cp->tag) {
        case CONSTANT_Class:
            return readCONSTANT_Class_info(cp, f);
        case CONSTANT_Fieldref:
            return readCONSTANT_Fieldref_info(cp, f);
        case CONSTANT_Methodref:
            return readCONSTANT_Methodref_info(cp, f);
        case CONSTANT_InterfaceMethodref:
            return readCONSTANT_InterfaceMethodref_info(cp, f);
        case CONSTANT_String:
            return readCONSTANT_String_info(cp, f);
        case CONSTANT_Integer:
            return readCONSTANT_Integer_info(cp, f);
        case CONSTANT_Float:
            return readCONSTANT_Float_info(cp, f);
        case CONSTANT_Long:
            return readCONSTANT_Long_info(cp, f);
        case CONSTANT_Double:
            return readCONSTANT_Double_info(cp, f);
        case CONSTANT_NameAndType:
            return readCONSTANT_NameAndType_info(cp, f);
        case CONSTANT_Utf8:
            return readCONSTANT_Utf8_info(cp, f);
        default:
            return 0;
    }
}

int readCONSTANT_Class_info(cp_info_t *cp, ccfrFile_t *f) {
    return read_u2(&(cp->u.CONSTANT_Class_info.name_index), f);
}

int readCONSTANT_Fieldref_info(cp_info_t *cp, ccfrFile_t *f) {
    if (!read_u2(&(cp->u.CONSTANT_Fieldref_info.class_index), f)) {
        return 0;
    }

    return read_u2(&(cp->u.CONSTANT_Fieldref_info.name_and_type_index), f);
}

int readCONSTANT_Methodref_info(cp_info_t *cp, ccfrFile_t *f) {
    if (!read_u2(&(cp->u.CONSTANT_Methodref_info.class_index), f)) {
        return 0;
    }

    return read_u2(&(cp->u.CONSTANT_Methodref_info.name_and_type_index), f);
}

int readCONSTANT_InterfaceMethodref_info(cp_info_t *cp, ccfrFile_t *f) {
    if (!read_u2(&(cp->u.CONSTANT_InterfaceMethodref_info.class_index), f)) {
        return 0;
    }

    return read_u2(&(cp->u.CONSTANT_InterfaceMethodref_info.name_and_type_index), f);
}

int readCONSTANT_String_info(cp_info_t *cp, ccfrFile_t *f) {
    return read_u2(&(cp->u.CONSTANT_String_info.string_index), f);
}

int readCONSTANT_Integer_info(cp_info_t *cp, ccfrFile_t *f) {
    return read_u4(&(cp->u.CONSTANT_Integer_info.bytes), f);
}

int readCONSTANT_Float_info(cp_info_t *cp, ccfrFile_t *f) {
    return read_u4(&(cp->u.CONSTANT_Float_info.bytes), f);
}

int readCONSTANT_Long_info(cp_info_t *cp, ccfrFile_t *f) {
    if (!read_u4(&(cp->u.CONSTANT_Long_info.high_bytes), f)) {
        return 0;
    }

    return read_u4(&(cp->u.CONSTANT_Long_info.low_bytes), f);
}

int readCONSTANT_Double_info(cp_info_t *cp, ccfrFile_t *f) {
    if (!read_u4(&(cp->u.CONSTANT_Double_info.high_bytes), f)) {
        return 0;
    }

    return read_u4(&(cp->u.CONSTANT_Double_info.low_bytes), f);
}

int readCONSTANT_NameAndType_info(cp_info_t *cp, ccfrFile_t *f) {
    if (!read_u2(&(cp->u.CONSTANT_NameAndType_info.name_index), f)) {
        return 0;
    }

    return read_u2(&(cp->u.CONSTANT_NameAndType_info.descriptor_index), f);
}

int readCONSTANT_Utf8_info(cp_info_t *cp, ccfrFile_t *f) {
    u2 length;

    if (!read_u2(&length,  f)) {
        return 0;
    }
    
    /* fprintf(stderr, "UTF8 length: %d\n", length); */
    cp->u.CONSTANT_Utf8_info.length = length;
    return read_u1_table(&(cp->u.CONSTANT_Utf8_info.bytes), (u4) length, f);
}

/*
void print_cp_info(cp_info_t *cp, FILE *f) {
    char s[2000];
    if (cp->tag & CONSTANT_Cont_mask) {
        fprintf(f, "(continued)\n");
        return;
    }
    
    switch (cp->tag) {
        case CONSTANT_Class:
            fprintf(f, "CONSTANT_Class\n");
            break;
        case CONSTANT_Fieldref:
            fprintf(f, "CONSTANT_Fieldref\n");
            break;
        case CONSTANT_Methodref:
            fprintf(f, "CONSTANT_Methodref\n");
            break;
        case CONSTANT_InterfaceMethodref:
            fprintf(f, "CONSTANT_InterfaceMethodref\n");
            break;
        case CONSTANT_String:
            fprintf(f, "CONSTANT_String\n");
            break;
        case CONSTANT_Integer:
            fprintf(f, "CONSTANT_Integer\n");
            break;
        case CONSTANT_Float:
            fprintf(f, "CONSTANT_Float\n");
            break;
        case CONSTANT_Long:
            fprintf(f, "CONSTANT_Long\n");
            break;
        case CONSTANT_Double:
            fprintf(f, "CONSTANT_Double\n");
            break;
        case CONSTANT_NameAndType:
            fprintf(f, "CONSTANT_NameAndType\n");
            break;
        case CONSTANT_Utf8:
            strncpy(s, cp->u.CONSTANT_Utf8_info.bytes, cp->u.CONSTANT_Utf8_info.length);
            s[cp->u.CONSTANT_Utf8_info.length] = '\0';
            fprintf(f, "CONSTANT_Utf8: %s\n", s);
            break;
        default:
            fprintf(f, "<Erroneous CP item>\n");
    }
}
*/
