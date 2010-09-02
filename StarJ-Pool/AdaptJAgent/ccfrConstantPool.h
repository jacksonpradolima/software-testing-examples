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

#ifndef _CCFR_CONSTANT_POOL_H
#define _CCFR_CONSTANT_POOL_H

#define CONSTANT_Cont_mask          ((u1) 0x80)

#define CONSTANT_Class              ((u1)  7)
#define CONSTANT_Fieldref           ((u1)  9)
#define CONSTANT_Methodref          ((u1) 10)
#define CONSTANT_InterfaceMethodref ((u1) 11)
#define CONSTANT_String             ((u1)  8)
#define CONSTANT_Integer            ((u1)  3)
#define CONSTANT_Float              ((u1)  4)
#define CONSTANT_Long               ((u1)  5)
#define CONSTANT_Double             ((u1)  6)
#define CONSTANT_NameAndType        ((u1) 12)
#define CONSTANT_Utf8               ((u1)  1)

typedef struct cp_info {
    u1 tag;
    union {
        struct {
            u2 name_index;
        } CONSTANT_Class_info;

        struct {
            u2 class_index;
            u2 name_and_type_index;
        } CONSTANT_Fieldref_info;

        struct {
            u2 class_index;
            u2 name_and_type_index;
        } CONSTANT_Methodref_info;

        struct {
            u2 class_index;
            u2 name_and_type_index;
        } CONSTANT_InterfaceMethodref_info;

        struct {
            u2 string_index;
        } CONSTANT_String_info;

        struct {
            u4 bytes;
        } CONSTANT_Integer_info;

        struct {
            u4 bytes;
        } CONSTANT_Float_info;

        struct {
            u4 high_bytes;
            u4 low_bytes;
        } CONSTANT_Long_info;
        
        struct {
            u4 high_bytes;
            u4 low_bytes;
        } CONSTANT_Double_info;

        struct {
            u2 name_index;
            u2 descriptor_index;
        } CONSTANT_NameAndType_info;

        struct {
            u2 length;
            u1 *bytes;
        } CONSTANT_Utf8_info;
    } u;
} cp_info_t;

int readCPItem(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_Class_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_Fieldref_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_Methodref_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_InterfaceMethodref_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_String_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_Integer_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_Float_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_Long_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_Double_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_NameAndType_info(cp_info_t *cp, ccfrFile_t *f);
int readCONSTANT_Utf8_info(cp_info_t *cp, ccfrFile_t *f);

/*
void print_cp_info(cp_info_t *cp, FILE *f);
*/
    
#endif
