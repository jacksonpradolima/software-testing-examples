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

#ifndef _ADAPTJ_ZIP_MAP_H
#define _ADAPTJ_ZIP_MAP_H

#include <stdio.h>
#include <stdlib.h>
#include "ccfrHashMap.h"

#define ZIP_MAP_OK            0
#define ZIP_MAP_MALLOC_ERR   -1
#define ZIP_MAP_NOT_FOUND    -2
#define ZIP_MAP_NULL_PTR_ERR -3

/* Key and Value types */
#define zip_key_t char *
#define zip_value_t JarEntry_t *

typedef unsigned int (*zip_hash_function_t)(const zip_key_t);
typedef int (*zipKeyEq_function_t)(const zip_key_t, const zip_key_t);

typedef struct zip_node {
    zip_key_t key;
    zip_value_t value;
    struct zip_node *next;
} zip_node_t;

typedef struct AdaptJZipMap {
    zip_node_t **data;
    unsigned int size;
    zip_hash_function_t hash_fn;
    zipKeyEq_function_t keyEq_fn;
    double loadFactor;
    unsigned int used;
} AdaptJZipMap_t;

int initZipMap(AdaptJZipMap_t *zipMap, unsigned int size, double loadFactor,
        zip_hash_function_t hash_fn, zipKeyEq_function_t keyEq_fn);
void releaseZipMap(AdaptJZipMap_t *zipMap);
int zipMapPut(AdaptJZipMap_t *zipMap, zip_key_t key, zip_value_t value);
int zipMapGet(AdaptJZipMap_t *zipMap, const zip_key_t key, zip_value_t *value);
int zipMapRemove(AdaptJZipMap_t *zipMap, const zip_key_t key);
int zipMapContainsKey(AdaptJZipMap_t *zipMap, const zip_key_t key);
int zipMapContainsValue(AdaptJZipMap_t *zipMap, const zip_value_t value,
        int (*valueEq_fn)(const zip_value_t, const zip_value_t));
int zipMapResize(AdaptJZipMap_t *zipMap, unsigned int newSize);

#endif
