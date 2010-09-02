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

#ifndef _ADAPTJ_MONITOR_H
#define _ADAPTJ_MONITOR_H

void AdaptJMonitorContendedEnter(JNIEnv *env_id, jobjectID object, jint requested);
void AdaptJMonitorContendedEntered(JNIEnv *env_id, jobjectID object, jint requested);
void AdaptJMonitorContendedExit(JNIEnv *env_id, jobjectID object, jint requested);
void AdaptJMonitorDump(JNIEnv *env_id, const char *begin, const char *end, jint num_traces,
                      JVMPI_CallTrace *traces, jint *threads_status, jint requested);
void AdaptJMonitorWait(JNIEnv *env_id, jobjectID object, jlong timeout, jint requested);
void AdaptJMonitorWaited(JNIEnv *env_id, jobjectID object, jlong timeout, jint requested);
void AdaptJRawMonitorContendedEnter(JNIEnv *env_id, const char *name, JVMPI_RawMonitor id, jint requested);
void AdaptJRawMonitorContendedEntered(JNIEnv *env_id, const char *name, JVMPI_RawMonitor id, jint requested);
void AdaptJRawMonitorContendedExit(JNIEnv *env_id, const char *name, JVMPI_RawMonitor id, jint requested);

#endif
