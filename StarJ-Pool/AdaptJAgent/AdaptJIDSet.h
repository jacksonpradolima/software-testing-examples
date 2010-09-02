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

#ifndef _ADAPTJ_ID_SET_H
#define _ADAPTJ_ID_SET_H

#include <stdio.h>
#include <stdlib.h>
#include "ccfrHashMap.h"

#define ID_SET_OK            0
#define ID_SET_MALLOC_ERR   -1
#define ID_SET_NOT_FOUND    -2
#define ID_SET_NULL_PTR_ERR -3

typedef struct idset_node {
    jint value;
    struct idset_node *next;
} idset_node_t;

typedef struct AdaptJIDSet {
    idset_node_t **data;
    unsigned int size;
    double loadFactor;
    unsigned int used;
} AdaptJIDSet_t;

int initIDSet(AdaptJIDSet_t *idSet, unsigned int size, double loadFactor);
void releaseIDSet(AdaptJIDSet_t *idSet);
int idSetAdd(AdaptJIDSet_t *idSet, jint value);
int idSetContains(AdaptJIDSet_t *idSet, jint value);
int idSetRemove(AdaptJIDSet_t *idSet, jint value);
int idSetResize(AdaptJIDSet_t *idSet, unsigned int newSize);
void idSetApplyAll(AdaptJIDSet_t *idSet, void (*f)(jint value));

#endif
