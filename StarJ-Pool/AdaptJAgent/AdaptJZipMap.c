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


int initZipMap(AdaptJZipMap_t *zipMap, unsigned int size, double loadFactor,
        zip_hash_function_t hash_fn, zipKeyEq_function_t keyEq_fn) {
    if (zipMap != NULL) {
        zipMap->data = NEW_ARRAY(zip_node_t *, size);
        if (zipMap->data != NULL) {
            zipMap->size = size;
            zipMap->hash_fn = hash_fn;
            zipMap->keyEq_fn = keyEq_fn;
            zipMap->loadFactor = loadFactor;
            zipMap->used = 0;
            return ZIP_MAP_OK;
        } else {
            return ZIP_MAP_MALLOC_ERR;
        }
    }

    return ZIP_MAP_NULL_PTR_ERR;
}

void releaseZipMap(AdaptJZipMap_t *zipMap) {
    if (zipMap != NULL) {
        unsigned int i;

        for (i = 0; i < zipMap->size; i++) {
            zip_node_t *node;
            zip_node_t *tmp;
            for (node = zipMap->data[i]; node != NULL; ) {
                tmp = node->next;
                if (node->key != NULL) {
                    free(node->key);
                }
                if (node->value != NULL) {
                    /* free(node->value); */
                }
                free(node);
                node = tmp;
            }
        }
    }
}

int zipMapPut(AdaptJZipMap_t *zipMap, zip_key_t key, zip_value_t value) {
    unsigned int index;
    zip_node_t *node;

    index = zipMap->hash_fn(key) % zipMap->size;
    for (node = zipMap->data[index]; node != NULL; node = node->next) {
        if (zipMap->keyEq_fn(node->key, key)) {
            /* Found a match */
            if (node->value != NULL) {
                /* free(node->value); */
            }
            node->value = value;
            /*
            zipMap->used += 1;
            if ((zipMap->loadFactor > 0.0)
                    && (zipMap->used > (zipMap->size * zipMap->loadFactor))) {
                return zipMapResize(zipMap, 2 * zipMap->size + 1);
            }
            */
            return ZIP_MAP_OK;
        }
    }

    node = NEW(zip_node_t);
    if (node != NULL) {
        node->next = zipMap->data[index];
        node->key = key;
        node->value = value;
        zipMap->data[index] = node;
        zipMap->used += 1;
        if ((zipMap->loadFactor > 0.0)
                && (zipMap->used > (zipMap->size * zipMap->loadFactor))) {
            /* increase the size of zipMap */
            return zipMapResize(zipMap, 2 * zipMap->size + 1);
        }
        return ZIP_MAP_OK;
    } else {
        return ZIP_MAP_MALLOC_ERR;
    }
}

int zipMapGet(AdaptJZipMap_t *zipMap, const zip_key_t key, zip_value_t *value) {
    unsigned int index;
    zip_node_t *node;

    index = zipMap->hash_fn(key) % zipMap->size;
    for (node = zipMap->data[index]; node != NULL; node = node->next) {
        if (zipMap->keyEq_fn(node->key, key)) {
            /* Found a match */
            *value = node->value;
            return ZIP_MAP_OK;
        }
    }
    
    return ZIP_MAP_NOT_FOUND;
}

int zipMapRemove(AdaptJZipMap_t *zipMap, const zip_key_t key) {
    unsigned int index;
    zip_node_t *node, *tmp = NULL;

    index = zipMap->hash_fn(key) % zipMap->size;
    for (node = zipMap->data[index]; node != NULL; node = node->next) {
        if (zipMap->keyEq_fn(node->key, key)) {
            /* Found a match */
            if (tmp != NULL) {
                tmp->next = node->next;
            } else {
                zipMap->data[index] = node->next;
            }
            if (node->key != NULL) {
                free(node->key);
            }
            if (node->value != NULL) {
                /* free(node->value); */
            }
            free(node);
            zipMap->used -= 1;
            return ZIP_MAP_OK;
        }
        tmp = node;
    }
    
    return ZIP_MAP_NOT_FOUND;
}

int zipMapContainsKey(AdaptJZipMap_t *zipMap, const zip_key_t key) {
    unsigned int index;
    zip_node_t *node;

    index = zipMap->hash_fn(key) % zipMap->size;
    for (node = zipMap->data[index]; node != NULL; node = node->next) {
        if (zipMap->keyEq_fn(node->key, key)) {
            /* Found a match */
            return ZIP_MAP_OK;
        }
    }
    
    return ZIP_MAP_NOT_FOUND;
}

int zipMapContainsValue(AdaptJZipMap_t *zipMap, const zip_value_t value,
        int (*valueEq_fn)(const zip_value_t, const zip_value_t)) {
    unsigned int index;
    zip_node_t *node;
    
    for (index = 0; index < zipMap->size; index++) {
        for (node = zipMap->data[index]; node != NULL; node = node->next) {
            if (valueEq_fn(node->value, value)) {
                return ZIP_MAP_OK;
            }
        }
    }
    
    return ZIP_MAP_NOT_FOUND;
}

int zipMapResize(AdaptJZipMap_t *zipMap, unsigned int newSize) {
    zip_node_t **newData;
 
    newData = NEW_ARRAY(zip_node_t *, newSize);
    if (newData != NULL) {
        unsigned int i;
        zip_node_t *node, *tmp;
        unsigned int index;

        for (i = 0; i < zipMap->size; i++) {
            for(node = zipMap->data[i]; node != NULL; ) {
                tmp = node->next;
                index = zipMap->hash_fn(node->key) % newSize;
                node->next = newData[index];
                newData[index] = node;
                node = tmp;
            }
        }

        free(zipMap->data);
        zipMap->data = newData;
        zipMap->size = newSize;
        return ZIP_MAP_OK;

    }

    return ZIP_MAP_MALLOC_ERR;
}
