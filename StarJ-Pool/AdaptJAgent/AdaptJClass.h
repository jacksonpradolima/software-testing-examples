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

#ifndef _ADAPTJ_CLASS_H
#define _ADAPTJ_CLASS_H

void AdaptJClassLoad(JNIEnv *env_id, const char *name, const char *src_name, jint num_interfaces, jint num_methods, JVMPI_Method *methods,
                    jint num_static_fields, JVMPI_Field *statics, int num_instance_fields, JVMPI_Field *instances, jobjectID class_id,
                    jint requested);
void AdaptJClassUnload(JNIEnv *env_id, jobjectID class_id, jint requested);

void removeFromKnownMethods(jint method_id);
#endif
