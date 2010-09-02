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
                                  
extern FILE *outputFile;
extern jshort eventInfo[];
extern jshort (*JShortMSB)(jshort);
extern jint (*JIntMSB)(jint);
extern int fileBytes;

extern AdaptJIDSetMap_t arenaIDtoObjects;
extern AdaptJIDSet_t knownObjectIDs;

void AdaptJArenaNew(JNIEnv *env_id, jint arena_id, const char *arena_name, jint requested) {
    AdaptJEvent eventID;
    jshort s;
    byte *byteBuff;
    byte *b;
    jshort nameLength;
    size_t bLength = 0;
    AdaptJIDSet_t *set;
 
    idSetMapRemove(&arenaIDtoObjects, arena_id);
    set = NEW(AdaptJIDSet_t);
    if (set != NULL) {
        initIDSet(set, 64, 0.7);
        idSetMapPut(&arenaIDtoObjects, arena_id, set);
    }
    
    nameLength = (jshort) (arena_name == NULL ? 0 : strlen(arena_name));
    byteBuff = (byte *) malloc(1 + 4 + 4 + nameLength);
    
    s = eventInfo[ADAPTJ_ARENA_NEW];
    b = byteBuff;

    eventID = ADAPTJ_ARENA_NEW;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    
    if (s & ADAPTJ_FIELD_ARENA_ID) {
        ADAPTJ_WRITE_JINT(arena_id, b, bLength);
    }

    if (s & ADAPTJ_FIELD_ARENA_NAME) {
        ADAPTJ_WRITE_UTF8(arena_name, nameLength, b, bLength);
    }
    
    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
    free(byteBuff);
}

void AdaptJArenaDelete(JNIEnv *env_id, jint arena_id, jint requested) {
    AdaptJEvent eventID;
    jshort s;
    byte byteBuff[9];
    byte *b;
    size_t bLength = 0;
    AdaptJIDSet_t *set;
    
    if (idSetMapGet(&arenaIDtoObjects, arena_id, &set) == ID_SET_MAP_OK) {
        idSetApplyAll(set, removeFromKnownObjects);
        idSetMapRemove(&arenaIDtoObjects, arena_id);
    }
    
    s = eventInfo[ADAPTJ_ARENA_DELETE];
    b = byteBuff;

    eventID = ADAPTJ_ARENA_DELETE;
    if (requested) {
        eventID |= ADAPTJ_REQUESTED_EVENT;
    }
    ADAPTJ_WRITE_BYTE(((byte)eventID), b, bLength);
    if (s & ADAPTJ_FIELD_ENV_ID) {
        ADAPTJ_WRITE_JINT(((jint)env_id), b, bLength);
    }
    
    if (s & ADAPTJ_FIELD_ARENA_ID) {
        ADAPTJ_WRITE_JINT(arena_id, b, bLength);
    }

    fileBytes += bLength;
#ifdef ADAPTJ_WRITE_OUTPUT
    fwrite(byteBuff, 1, bLength, outputFile);
#endif
}

void removeFromKnownObjects(jint object_id) {
    idSetRemove(&knownObjectIDs, object_id);
}
