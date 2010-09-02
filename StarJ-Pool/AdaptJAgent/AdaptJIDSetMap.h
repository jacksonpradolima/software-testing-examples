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

#ifndef _ADAPTJ_ID_SET_MAP_H
#define _ADAPTJ_ID_SET_MAP_H

#include <stdio.h>
#include <stdlib.h>
#include <jvmpi.h>
#include "AdaptJIDSet.h"
#include "ccfrHashMap.h"

#define ID_SET_MAP_OK            0
#define ID_SET_MAP_MALLOC_ERR   -1
#define ID_SET_MAP_NOT_FOUND    -2
#define ID_SET_MAP_NULL_PTR_ERR -3

/* Key and Value types */
#define idSet_value_t AdaptJIDSet_t *

typedef struct idSet_node {
    jint idSet;
    idSet_value_t value;
    struct idSet_node *next;
} idSet_node_t;

typedef struct AdaptJIDSetMap {
    idSet_node_t **data;
    unsigned int size;
    double loadFactor;
    unsigned int used;
} AdaptJIDSetMap_t;

int initIDSetMap(AdaptJIDSetMap_t *idSetMap, unsigned int size, double loadFactor);
void releaseIDSetMap(AdaptJIDSetMap_t *idSetMap);
int idSetMapPut(AdaptJIDSetMap_t *idSetMap, jint key, idSet_value_t value);
int idSetMapGet(AdaptJIDSetMap_t *idSetMap, jint key, idSet_value_t *value);
int idSetMapRemove(AdaptJIDSetMap_t *idSetMap, jint key);
int idSetMapContainsKey(AdaptJIDSetMap_t *idSetMap, jint key);
int idSetMapContainsValue(AdaptJIDSetMap_t *idSetMap, const idSet_value_t value,
        int (*valueEq_fn)(const idSet_value_t, const idSet_value_t));
int idSetMapMove(AdaptJIDSetMap_t *idSetMap, jint key, jint new_key);
int idSetMapResize(AdaptJIDSetMap_t *idSetMap, unsigned int newSize);

#endif
