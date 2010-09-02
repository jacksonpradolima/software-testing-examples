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

#ifndef _ADAPTJ_OBJECT_H
#define _ADAPTJ_OBJECT_H

void AdaptJObjectAlloc(JNIEnv *env_id, jint arena_id, jobjectID class_id, jint is_array, jint size, jobjectID obj_id,
                      jint requested);
void AdaptJObjectDump(JNIEnv *env_id, jint data_len, const char *data, jint requested);
void AdaptJObjectFree(JNIEnv *env_id, jobjectID obj_id, jint requested);
void AdaptJObjectMove(JNIEnv *env_id, jint arena_id, jobjectID obj_id, jint new_arena_id, jobjectID new_obj_id, jint requested);

#endif
