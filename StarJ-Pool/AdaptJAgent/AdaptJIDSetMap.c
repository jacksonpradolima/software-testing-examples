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


int initIDSetMap(AdaptJIDSetMap_t *idSetMap, unsigned int size, double loadFactor) {
    if (idSetMap != NULL) {
        idSetMap->data = NEW_ARRAY(idSet_node_t *, size);
        if (idSetMap->data != NULL) {
            idSetMap->size = size;
            idSetMap->loadFactor = loadFactor;
            idSetMap->used = 0;
            return ID_SET_MAP_OK;
        } else {
            return ID_SET_MAP_MALLOC_ERR;
        }
    }

    return ID_SET_MAP_NULL_PTR_ERR;
}

void releaseIDSetMap(AdaptJIDSetMap_t *idSetMap) {
    if (idSetMap != NULL) {
        unsigned int i;

        for (i = 0; i < idSetMap->size; i++) {
            idSet_node_t *node;
            idSet_node_t *tmp;
            for (node = idSetMap->data[i]; node != NULL; ) {
                tmp = node->next;
                if (node->value != NULL) {
                    free(node->value);
                    node->value = NULL;
                }
                free(node);
                node = tmp;
            }
        }
    }
}

int idSetMapPut(AdaptJIDSetMap_t *idSetMap, jint idSet, idSet_value_t value) {
    unsigned int index;
    idSet_node_t *node;

    index = idSet % idSetMap->size;
    for (node = idSetMap->data[index]; node != NULL; node = node->next) {
        if (node->idSet == idSet) {
            /* Found a match */
            if (node->value != NULL) {
                free(node->value);
            }
            node->value = value;
            /*
            idSetMap->used += 1;
            if ((idSetMap->loadFactor > 0.0)
                    && (idSetMap->used > (idSetMap->size * idSetMap->loadFactor))) {
                return idSetMapResize(idSetMap, 2 * idSetMap->size + 1);
            }
            */
            return ID_SET_MAP_OK;
        }
    }

    node = NEW(idSet_node_t);
    if (node != NULL) {
        node->next = idSetMap->data[index];
        node->idSet = idSet;
        node->value = value;
        idSetMap->data[index] = node;
        idSetMap->used += 1;
        if ((idSetMap->loadFactor > 0.0)
                && (idSetMap->used > (idSetMap->size * idSetMap->loadFactor))) {
            /* increase the size of idSetMap */
            return idSetMapResize(idSetMap, 2 * idSetMap->size + 1);
        }
        return ID_SET_MAP_OK;
    } else {
        return ID_SET_MAP_MALLOC_ERR;
    }
}

int idSetMapGet(AdaptJIDSetMap_t *idSetMap, jint idSet, idSet_value_t *value) {
    unsigned int index;
    idSet_node_t *node;

    index = idSet % idSetMap->size;
    for (node = idSetMap->data[index]; node != NULL; node = node->next) {
        if (node->idSet == idSet) {
            /* Found a match */
            *value = node->value;
            return ID_SET_MAP_OK;
        }
    }
    
    return ID_SET_MAP_NOT_FOUND;
}

int idSetMapRemove(AdaptJIDSetMap_t *idSetMap, jint idSet) {
    unsigned int index;
    idSet_node_t *node, *tmp = NULL;

    index = idSet % idSetMap->size;
    for (node = idSetMap->data[index]; node != NULL; node = node->next) {
        if (node->idSet == idSet) {
            /* Found a match */
            if (tmp != NULL) {
                tmp->next = node->next;
            } else {
                idSetMap->data[index] = node->next;
            }
            free(node);
            idSetMap->used -= 1;
            return ID_SET_MAP_OK;
        }
        tmp = node;
    }
    
    return ID_SET_MAP_NOT_FOUND;
}

int idSetMapContainsKey(AdaptJIDSetMap_t *idSetMap, jint idSet) {
    unsigned int index;
    idSet_node_t *node;

    index = idSet % idSetMap->size;
    for (node = idSetMap->data[index]; node != NULL; node = node->next) {
        if (node->idSet == idSet) {
            /* Found a match */
            return ID_SET_MAP_OK;
        }
    }
    
    return ID_SET_MAP_NOT_FOUND;
}

int idSetMapContainsValue(AdaptJIDSetMap_t *idSetMap, const idSet_value_t value,
        int (*valueEq_fn)(const idSet_value_t, const idSet_value_t)) {
    unsigned int index;
    idSet_node_t *node;
    
    for (index = 0; index < idSetMap->size; index++) {
        for (node = idSetMap->data[index]; node != NULL; node = node->next) {
            if (valueEq_fn(node->value, value)) {
                return ID_SET_MAP_OK;
            }
        }
    }
    
    return ID_SET_MAP_NOT_FOUND;
}

int idSetMapMove(AdaptJIDSetMap_t *idSetMap, jint key, jint new_key) {
    unsigned int index;
    unsigned int newIndex;
    idSet_node_t *node, *tmp = NULL;

    index = key % idSetMap->size;
    newIndex = new_key % idSetMap->size;
    for (node = idSetMap->data[index]; node != NULL; node = node->next) {
        if (node->idSet == key) {
            /* Found a match */
            if (tmp != NULL) {
                tmp->next = node->next;
            } else {
                idSetMap->data[index] = node->next;
            }
            node->idSet = new_key;
            node->next = idSetMap->data[newIndex];
            idSetMap->data[newIndex] = node;
            return ID_SET_MAP_OK;
        }
        tmp = node;
    }
    
    return ID_SET_MAP_NOT_FOUND;
}

int idSetMapResize(AdaptJIDSetMap_t *idSetMap, unsigned int newSize) {
    idSet_node_t **newData;
 
    newData = NEW_ARRAY(idSet_node_t *, newSize);
    if (newData != NULL) {
        unsigned int i;
        idSet_node_t *node, *tmp;
        unsigned int index;

        for (i = 0; i < idSetMap->size; i++) {
            for(node = idSetMap->data[i]; node != NULL; ) {
                tmp = node->next;
                index = node->idSet % newSize;
                node->next = newData[index];
                newData[index] = node;
                node = tmp;
            }
        }

        free(idSetMap->data);
        idSetMap->data = newData;
        idSetMap->size = newSize;
        return ID_SET_MAP_OK;

    }

    return ID_SET_MAP_MALLOC_ERR;
}
