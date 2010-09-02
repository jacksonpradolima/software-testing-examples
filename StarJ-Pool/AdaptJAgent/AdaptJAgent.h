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

#ifndef _ADAPTJ_AGENT_H
#define _ADAPTJ_AGENT_H

/* Compilation Options */
#define ADAPTJ_LOCK_NOTIFY_EVENT
#define ADAPTJ_WRITE_OUTPUT
/* #define ADAPTJ_PRINT_OBJECT_DEFS */
/* #define ADAPTJ_PRINT_CLASSPATH */
#define ADAPTJ_ENABLE_PIPE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <jvmpi.h>

typedef unsigned char AdaptJEvent;
typedef unsigned char byte;
typedef enum {false, true} boolean;


#include "ccfr.h"
#include "AdaptJZipMap.h"
#include "AdaptJIDSet.h"
#include "AdaptJIDSetMap.h"

#include "AdaptJIO.h"
#include "AdaptJSpecReader.h"
#include "AdaptJSetup.h"
#include "AdaptJUtil.h"

#include "AdaptJArena.h"
#include "AdaptJClass.h"
#include "AdaptJGC.h"
/*
#include "AdaptJGlobalRef.h"
*/
#include "AdaptJInstruction.h"
#include "AdaptJJVM.h"
#include "AdaptJMethod.h"
#include "AdaptJMonitor.h"
#include "AdaptJObject.h"
#include "AdaptJThread.h"

#define BUFF_SIZE 1024
#define DEFAULT_FILE_NAME "AdaptJ.dat"
#define ADAPTJ_GLOBAL_LOCK_NAME "_adaptj_global_access_lock"

#define ADAPTJ_MAGIC 0x41454600
#define ADAPTJ_VERSION 0

#ifdef ADAPTJ_ENABLE_PIPE
    #define ADAPTJ_ISPIPED ((short) 0x0001)
#endif

#define ADAPTJ_REQUESTED_EVENT               ((AdaptJEvent)0x80)

/* Event Values */
#define ADAPTJ_INVALID_EVENT                 ((AdaptJEvent)0xFF)
#define ADAPTJ_ARENA_DELETE                  ((AdaptJEvent)0x00)
#define ADAPTJ_ARENA_NEW                     ((AdaptJEvent)0x01)
#define ADAPTJ_CLASS_LOAD                    ((AdaptJEvent)0x02)
#define ADAPTJ_CLASS_LOAD_HOOK               ((AdaptJEvent)0x03)
#define ADAPTJ_CLASS_UNLOAD                  ((AdaptJEvent)0x04)
#define ADAPTJ_COMPILED_METHOD_LOAD          ((AdaptJEvent)0x05)
#define ADAPTJ_COMPILED_METHOD_UNLOAD        ((AdaptJEvent)0x06)
#define ADAPTJ_DATA_DUMP_REQUEST             ((AdaptJEvent)0x07)
#define ADAPTJ_DATA_RESET_REQUEST            ((AdaptJEvent)0x08)
#define ADAPTJ_GC_FINISH                     ((AdaptJEvent)0x09)
#define ADAPTJ_GC_START                      ((AdaptJEvent)0x0A)
#define ADAPTJ_HEAP_DUMP                     ((AdaptJEvent)0x0B)
#define ADAPTJ_JNI_GLOBALREF_ALLOC           ((AdaptJEvent)0x0C)
#define ADAPTJ_JNI_GLOBALREF_FREE            ((AdaptJEvent)0x0D)
#define ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC      ((AdaptJEvent)0x0E)
#define ADAPTJ_JNI_WEAK_GLOBALREF_FREE       ((AdaptJEvent)0x0F)
#define ADAPTJ_JVM_INIT_DONE                 ((AdaptJEvent)0x10)
#define ADAPTJ_JVM_SHUT_DOWN                 ((AdaptJEvent)0x11)
#define ADAPTJ_METHOD_ENTRY                  ((AdaptJEvent)0x12)
#define ADAPTJ_METHOD_ENTRY2                 ((AdaptJEvent)0x13)
#define ADAPTJ_METHOD_EXIT                   ((AdaptJEvent)0x14)
#define ADAPTJ_MONITOR_CONTENDED_ENTER       ((AdaptJEvent)0x15)
#define ADAPTJ_MONITOR_CONTENDED_ENTERED     ((AdaptJEvent)0x16)
#define ADAPTJ_MONITOR_CONTENDED_EXIT        ((AdaptJEvent)0x17)
#define ADAPTJ_MONITOR_DUMP                  ((AdaptJEvent)0x18)
#define ADAPTJ_MONITOR_WAIT                  ((AdaptJEvent)0x19)
#define ADAPTJ_MONITOR_WAITED                ((AdaptJEvent)0x1A)
#define ADAPTJ_OBJECT_ALLOC                  ((AdaptJEvent)0x1B)
#define ADAPTJ_OBJECT_DUMP                   ((AdaptJEvent)0x1C)
#define ADAPTJ_OBJECT_FREE                   ((AdaptJEvent)0x1D)
#define ADAPTJ_OBJECT_MOVE                   ((AdaptJEvent)0x1E)
#define ADAPTJ_RAW_MONITOR_CONTENDED_ENTER   ((AdaptJEvent)0x1F)
#define ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED ((AdaptJEvent)0x20)
#define ADAPTJ_RAW_MONITOR_CONTENDED_EXIT    ((AdaptJEvent)0x21)
#define ADAPTJ_THREAD_END                    ((AdaptJEvent)0x22)
#define ADAPTJ_THREAD_START                  ((AdaptJEvent)0x23)
#define ADAPTJ_INSTRUCTION_START             ((AdaptJEvent)0x24)
/* Additional Events */
#define ADAPTJ_THREAD_STATUS_CHANGE          ((AdaptJEvent)0x25)

/* Alternative Event Representations --
 * To be converted by the event reader */
#define ADAPTJ_COMPACT_INSTRUCTION_START     ((AdaptJEvent)0x7E)

/* File Split Event */
#define ADAPTJ_FILESPLIT                     ((AdaptJEvent)0x7F)

#define ADAPTJ_MAX_EVENT 37


/* ================================================ *
 *                       Event Mappings             *
 * ================================================ *
JVMPI_EVENT_ARENA_DELETE                            A
JVMPI_EVENT_ARENA_NEW                               a

JVMPI_EVENT_CLASS_LOAD                              c
JVMPI_EVENT_CLASS_LOAD_HOOK                         k
JVMPI_EVENT_CLASS_UNLOAD                            C

JVMPI_EVENT_COMPILED_METHOD_LOAD                    l
JVMPI_EVENT_COMPILED_METHOD_UNLOAD                  L

JVMPI_EVENT_DATA_DUMP_REQUEST                       q
JVMPI_EVENT_DATA_RESET_REQUEST                      Q

JVMPI_EVENT_GC_FINISH                               G
JVMPI_EVENT_GC_START                                g

JVMPI_EVENT_HEAP_DUMP                               h

JVMPI_EVENT_JNI_GLOBALREF_ALLOC                     j
JVMPI_EVENT_JNI_GLOBALREF_FREE                      J

JVMPI_EVENT_JNI_WEAK_GLOBALREF_ALLOC                w
JVMPI_EVENT_JNI_WEAK_GLOBALREF_FREE                 W

JVMPI_EVENT_JVM_INIT_DONE                           v
JVMPI_EVENT_JVM_SHUT_DOWN                           V

JVMPI_EVENT_METHOD_ENTRY                            b
JVMPI_EVENT_METHOD_ENTRY2                           m
JVMPI_EVENT_METHOD_EXIT                             M

JVMPI_EVENT_MONITOR_CONTENDED_ENTER                 d
JVMPI_EVENT_MONITOR_CONTENDED_ENTERED               e
JVMPI_EVENT_MONITOR_CONTENDED_EXIT                  D

JVMPI_EVENT_MONITOR_DUMP                            x
JVMPI_EVENT_MONITOR_WAIT                            y
JVMPI_EVENT_MONITOR_WAITED                          Y

JVMPI_EVENT_OBJECT_ALLOC                            o
JVMPI_EVENT_OBJECT_DUMP                             p
JVMPI_EVENT_OBJECT_FREE                             O
JVMPI_EVENT_OBJECT_MOVE                             P

JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTER             r
JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTERED           E
JVMPI_EVENT_RAW_MONITOR_CONTENDED_EXIT              R

JVMPI_EVENT_THREAD_END                              T
JVMPI_EVENT_THREAD_START                            t

JVMPI_EVENT_INSTRUCTION_START                       i
================================================== */

#endif
