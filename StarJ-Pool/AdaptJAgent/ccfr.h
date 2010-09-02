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

#ifndef _CCFR_H
#define _CCFR_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "unzip.h"
#include <jvmpi.h>

#define MAGIC_NUMBER ((u4) 0xCAFEBABE)

#define NEW(type) (type *) malloc(sizeof(type))
#define NEW_ARRAY(type, size) (type *) calloc(size, sizeof(type))

typedef unsigned char u1;
typedef unsigned short int u2;
typedef unsigned long int u4;

#include "ccfrUtil.h"
#include "ccfrErrors.h"
#include "ccfrFile.h"
#include "ccfrIO.h"
#include "ccfrConstantPool.h"
#include "ccfrAttributes.h"    /* IMPORTANT: must be included before
                                    ccfrMethods.h and ccfrFields.h */
#include "ccfrFields.h"
#include "ccfrMethods.h"
#include "ccfrBytecode.h"


typedef struct classfile_t {
    u4 magic;
    u2 minor_version;
    u2 major_version;
    u2 constant_pool_count;
    cp_info_t *constant_pool;
    u2 access_flags;
    u2 this_class;
    u2 super_class;
    u2 interfaces_count;
    u2 *interfaces;
    u2 fields_count;
    field_info_t *fields;
    u2 methods_count;
    method_info_t *methods;
    u2 attributes_count;
    attribute_info_t *attributes;
} classfile_t;

#include "ccfrHashMap.h"
#include "ccfrClassPathExplorer.h"

int parseClass(classfile_t *class, ccfrFile_t *f);
int readMagic(classfile_t *class, ccfrFile_t *f);
int readVersion(classfile_t *class, ccfrFile_t *f);
int readConstantPool(classfile_t *class, ccfrFile_t *f);
int readClassInfo(classfile_t *class, ccfrFile_t *f);
int readInterfaces(classfile_t *class, ccfrFile_t *f);
int readFields(classfile_t *class, ccfrFile_t *f);
int readMethods(classfile_t *class, ccfrFile_t *f);
int readAttributes(classfile_t *class, ccfrFile_t *f);


void initClassfile(classfile_t *class);
void cleanupClassfile(classfile_t *class);

int getClassName(classfile_t *class, char *s, int sLen);
int getMethodByName(classfile_t *class, char *method_name, char *signature, method_info_t **method);

#endif
