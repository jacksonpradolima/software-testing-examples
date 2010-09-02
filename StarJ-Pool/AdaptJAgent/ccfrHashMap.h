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

#ifndef _CCFR_HASH_MAP_H
#define _CCFR_HASH_MAP_H

#include <stdio.h>
#include <stdlib.h>

#define HASH_MAP_OK            0
#define HASH_MAP_MALLOC_ERR   -1
#define HASH_MAP_NOT_FOUND    -2
#define HASH_MAP_NULL_PTR_ERR -3

/*
#define NEW(type) (type *) malloc(sizeof(type))
#define NEW_ARRAY(type, size) (type *) calloc(size, sizeof(type))
*/

/* Key and Value types */
#define hash_key_t jint
#define hash_value_t bytecode_t *

typedef unsigned int (*hash_function_t)(const hash_key_t);
typedef int (*keyEq_function_t)(const hash_key_t, const hash_key_t);
typedef void (*releaseKey_function_t)(hash_key_t);
typedef void (*releaseValue_function_t)(hash_value_t);

typedef struct hash_node {
    hash_key_t key;
    hash_value_t value;
    struct hash_node *next;
} hash_node_t;

typedef struct HashMap {
    hash_node_t **data;
    unsigned int size;
    hash_function_t hash_fn;
    keyEq_function_t keyEq_fn;
    releaseKey_function_t releaseKey;
    releaseValue_function_t releaseValue;
    double loadFactor;
    unsigned int used;
} HashMap_t;

unsigned int stringHash(const char *s);
int stringEq(const char *s1, const char *s2);

int initHashMap(HashMap_t *hashMap, unsigned int size, double loadFactor,
        hash_function_t hash_fn, keyEq_function_t keyEq_fn,
        releaseKey_function_t releaseKey, releaseValue_function_t releaseValue);
void releaseHashMap(HashMap_t *hashMap);
int hashMapPut(HashMap_t *hashMap, hash_key_t key, hash_value_t value);
int hashMapGet(HashMap_t *hashMap, hash_key_t key, hash_value_t *value);
int hashMapRemove(HashMap_t *hashMap, hash_key_t key);
int hashMapContainsKey(HashMap_t *hashMap, hash_key_t key);
int hashMapContainsValue(HashMap_t *hashMap, hash_value_t value,
        int (*valueEq_fn)(const hash_value_t, const hash_value_t));
int hashMapResize(HashMap_t *hashMap, unsigned int newSize);

#endif
