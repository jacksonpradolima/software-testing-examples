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

/* Useful hash function for strings */
unsigned int stringHash(const char *s) {
    unsigned int hash = 0;
    if (s != NULL) {
        while (*s) {
            hash = (hash << 1) + *s++;
        }
    }

    return hash;
}

int stringEq(const char *s1, const char *s2) {
    return !strcmp(s1, s2);
}


int initHashMap(HashMap_t *hashMap, unsigned int size, double loadFactor,
        hash_function_t hash_fn, keyEq_function_t keyEq_fn,
        releaseKey_function_t releaseKey, releaseValue_function_t releaseValue) {
    if (hashMap != NULL) {
        hashMap->data = NEW_ARRAY(hash_node_t *, size);
        if (hashMap->data != NULL) {
            hashMap->size = size;
            hashMap->hash_fn = hash_fn;
            hashMap->keyEq_fn = keyEq_fn;
            hashMap->loadFactor = loadFactor;
            hashMap->used = 0;
            hashMap->releaseKey = releaseKey;
            hashMap->releaseValue = releaseValue;
            return HASH_MAP_OK;
        } else {
            return HASH_MAP_MALLOC_ERR;
        }
    }

    return HASH_MAP_NULL_PTR_ERR;
}

void releaseHashMap(HashMap_t *hashMap) {
    if (hashMap != NULL) {
        unsigned int i;

        for (i = 0; i < hashMap->size; i++) {
            hash_node_t *node;
            hash_node_t *tmp;
            for (node = hashMap->data[i]; node != NULL; ) {
                tmp = node->next;
                if (hashMap->releaseKey != NULL) {
                    hashMap->releaseKey(node->key);
                }
                if (hashMap->releaseValue != NULL) {
                    hashMap->releaseValue(node->value);
                }
                free(node);
                node = tmp;
            }
        }
    }
}

int hashMapPut(HashMap_t *hashMap, hash_key_t key, hash_value_t value) {
    unsigned int index;
    hash_node_t *node;

    index = hashMap->hash_fn(key) % hashMap->size;
    for (node = hashMap->data[index]; node != NULL; node = node->next) {
        if (hashMap->keyEq_fn(node->key, key)) {
            /* Found a match */
            if (hashMap->releaseValue != NULL) {
                hashMap->releaseValue(node->value);
            }
            node->value = value;
            /*
            hashMap->used += 1;
            if ((hashMap->loadFactor > 0.0)
                    && (hashMap->used > (hashMap->size * hashMap->loadFactor))) {
                return hashMapResize(hashMap, 2 * hashMap->size + 1);
            }
            */
            return HASH_MAP_OK;
        }
    }

    node = NEW(hash_node_t);
    if (node != NULL) {
        node->next = hashMap->data[index];
        node->key = key;
        node->value = value;
        hashMap->data[index] = node;
        hashMap->used += 1;
        if ((hashMap->loadFactor > 0.0)
                && (hashMap->used > (hashMap->size * hashMap->loadFactor))) {
            /* increase the size of hashMap */
            return hashMapResize(hashMap, 2 * hashMap->size + 1);
        }
        return HASH_MAP_OK;
    } else {
        return HASH_MAP_MALLOC_ERR;
    }
}

int hashMapGet(HashMap_t *hashMap, hash_key_t key, hash_value_t *value) {
    unsigned int index;
    hash_node_t *node;

    index = hashMap->hash_fn(key) % hashMap->size;
    for (node = hashMap->data[index]; node != NULL; node = node->next) {
        if (hashMap->keyEq_fn(node->key, key)) {
            /* Found a match */
            *value = node->value;
            return HASH_MAP_OK;
        }
    }
    
    return HASH_MAP_NOT_FOUND;
}

int hashMapRemove(HashMap_t *hashMap, hash_key_t key) {
    unsigned int index;
    hash_node_t *node, *tmp = NULL;

    index = hashMap->hash_fn(key) % hashMap->size;
    for (node = hashMap->data[index]; node != NULL; node = node->next) {
        if (hashMap->keyEq_fn(node->key, key)) {
            /* Found a match */
            if (tmp != NULL) {
                tmp->next = node->next;
            } else {
                hashMap->data[index] = node->next;
            }
            if (hashMap->releaseKey != NULL) {
                hashMap->releaseKey(node->key);
            }
            if (hashMap->releaseValue != NULL) {
                hashMap->releaseValue(node->value);
            }
            free(node);
            hashMap->used -= 1;
            return HASH_MAP_OK;
        }
        tmp = node;
    }
    
    return HASH_MAP_NOT_FOUND;
}

int hashMapContainsKey(HashMap_t *hashMap, hash_key_t key) {
    unsigned int index;
    hash_node_t *node;

    index = hashMap->hash_fn(key) % hashMap->size;
    for (node = hashMap->data[index]; node != NULL; node = node->next) {
        if (hashMap->keyEq_fn(node->key, key)) {
            /* Found a match */
            return HASH_MAP_OK;
        }
    }
    
    return HASH_MAP_NOT_FOUND;
}

int hashMapContainsValue(HashMap_t *hashMap, hash_value_t value,
        int (*valueEq_fn)(const hash_value_t, const hash_value_t)) {
    unsigned int index;
    hash_node_t *node;
    
    for (index = 0; index < hashMap->size; index++) {
        for (node = hashMap->data[index]; node != NULL; node = node->next) {
            if (valueEq_fn(node->value, value)) {
                return HASH_MAP_OK;
            }
        }
    }
    
    return HASH_MAP_NOT_FOUND;
}

int hashMapResize(HashMap_t *hashMap, unsigned int newSize) {
    hash_node_t **newData;
    
    newData = NEW_ARRAY(hash_node_t *, newSize);
    if (newData != NULL) {
        unsigned int i;
        hash_node_t *node, *tmp;
        unsigned int index;

        for (i = 0; i < hashMap->size; i++) {
            for(node = hashMap->data[i]; node != NULL; ) {
                tmp = node->next;
                index = hashMap->hash_fn(node->key) % newSize;
                node->next = newData[index];
                newData[index] = node;
                node = tmp;
            }
        }

        free(hashMap->data);
        hashMap->data = newData;
        hashMap->size = newSize;

    }

    return HASH_MAP_MALLOC_ERR;
}
