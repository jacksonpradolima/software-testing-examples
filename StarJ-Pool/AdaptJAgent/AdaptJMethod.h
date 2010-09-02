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

#ifndef _ADAPTJ_METHOD_H
#define _ADAPTJ_METHOD_H

void AdaptJCompiledMethodLoad(JNIEnv *env_id, jmethodID method_id, void *code_addr, jint code_size,
                             jint lineno_table_size, JVMPI_Lineno *lineno_table, jint requested);
void AdaptJCompiledMethodUnload(JNIEnv *env_id, jmethodID method_id, jint requested);
void AdaptJMethodEntry(JNIEnv *env_id, jmethodID method_id, jint requested);
void AdaptJMethodEntry2(JNIEnv *env_id, jmethodID method_id, jobjectID obj_id, jint requested);
void AdaptJMethodExit(JNIEnv *env_id, jmethodID method_id, jint requested);

#endif
