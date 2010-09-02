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

int initIDSet(AdaptJIDSet_t *idSet, unsigned int size, double loadFactor) {
    if (idSet != NULL) {
        idSet->data = NEW_ARRAY(idset_node_t *, size);
        if (idSet->data != NULL) {
            idSet->size = size;
            idSet->loadFactor = loadFactor;
            idSet->used = 0;
            return ID_SET_OK;
        } else {
            return ID_SET_MALLOC_ERR;
        }
    }

    return ID_SET_NULL_PTR_ERR;
}

void releaseIDSet(AdaptJIDSet_t *idSet) {
    if (idSet != NULL) {
        unsigned int i;

        for (i = 0; i < idSet->size; i++) {
            idset_node_t *node;
            idset_node_t *tmp;
            for (node = idSet->data[i]; node != NULL; ) {
                tmp = node->next;
                free(node);
                node = tmp;
            }
        }
    }
}

int idSetAdd(AdaptJIDSet_t *idSet, jint value) {
    unsigned int index;
    idset_node_t *node;

    index = value % idSet->size;
    for (node = idSet->data[index]; node != NULL; node = node->next) {
        if (node->value == value) {
            return ID_SET_OK;
        }
    }

    node = NEW(idset_node_t);
    if (node != NULL) {
        node->next = idSet->data[index];
        node->value = value;
        idSet->data[index] = node;
        idSet->used += 1;
        if ((idSet->loadFactor > 0.0)
                && (idSet->used > (idSet->size * idSet->loadFactor))) {
            /* increase the size of idSet */
            return idSetResize(idSet, 2 * idSet->size + 1);
        }
        return ID_SET_OK;
    } else {
        return ID_SET_MALLOC_ERR;
    }
}

int idSetRemove(AdaptJIDSet_t *idSet, jint value) {
    unsigned int index;
    idset_node_t *node, *tmp = NULL;

    index = value % idSet->size;
    for (node = idSet->data[index]; node != NULL; node = node->next) {
        if (node->value == value) {
            /* Found a match */
            if (tmp != NULL) {
                tmp->next = node->next;
            } else {
                idSet->data[index] = node->next;
            }
            free(node);
            idSet->used -= 1;
            return ID_SET_OK;
        }
        tmp = node;
    }
    
    return ID_SET_NOT_FOUND;
}

int idSetContains(AdaptJIDSet_t *idSet, jint value) {
    unsigned int index;
    idset_node_t *node;

    index = value % idSet->size;
    for (node = idSet->data[index]; node != NULL; node = node->next) {
        if (node->value == value) {
            /* Found a match */
            return ID_SET_OK;
        }
    }
    
    return ID_SET_NOT_FOUND;
}

int idSetResize(AdaptJIDSet_t *idSet, unsigned int newSize) {
    idset_node_t **newData;
 
    newData = NEW_ARRAY(idset_node_t *, newSize);
    if (newData != NULL) {
        unsigned int i;
        idset_node_t *node, *tmp;
        unsigned int index;

        for (i = 0; i < idSet->size; i++) {
            for(node = idSet->data[i]; node != NULL; ) {
                tmp = node->next;
                index = node->value % newSize;
                node->next = newData[index];
                newData[index] = node;
                node = tmp;
            }
        }

        free(idSet->data);
        idSet->data = newData;
        idSet->size = newSize;
        return ID_SET_OK;

    }

    return ID_SET_MALLOC_ERR;
}

void idSetApplyAll(AdaptJIDSet_t *idSet, void (*f)(jint value)) {
    unsigned int index;
    idset_node_t *node;

    for (index = 0; index < idSet->size; index++) {
        for (node = idSet->data[index]; node != NULL; node = node->next) {
            f(node->value);
        }
    }
}
