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

#ifndef _ADAPTJ_SPEC_READER_H
#define _ADAPTJ_SPEC_READER_H

#define ADAPTJ_SPEC_MAGIC "AES"
#define ADAPTJ_SPEC_VERSION ((unsigned char) 0)

/* ========================================================================= */
/*                                 Bit Masks                                 */
/* ========================================================================= */

/* Common Fields */
#define ADAPTJ_FIELD_RECORDED               ((jshort) 0x0001)
#define ADAPTJ_FIELD_COUNTED                ((jshort) 0x0002)
#define ADAPTJ_FIELD_ENV_ID                 ((jshort) 0x0004)
#define ADAPTJ_FIELD_REQUIRED               ((jshort) 0x8000)

#define ADAPTJ_RECORD_ALL_FIELDS            ((jshort) 0xFFFF & (~ADAPTJ_FIELD_COUNTED))
#define ADAPTJ_ALL_INFO_FIELD               ((jshort) ADAPTJ_RECORD_ALL_FIELDS & (~ADAPTJ_FIELD_RECORDED))

/* Arena Delete/New  Event */
#define ADAPTJ_FIELD_ARENA_ID               ((jshort) 0x0008)
#define ADAPTJ_FIELD_ARENA_NAME             ((jshort) 0x0010)

/* Class Load Event */
#define ADAPTJ_FIELD_CLASS_NAME             ((jshort) 0x0008)
#define ADAPTJ_FIELD_SOURCE_NAME            ((jshort) 0x0010)
#define ADAPTJ_FIELD_NUM_INTERFACES         ((jshort) 0x0020)
#define ADAPTJ_FIELD_NUM_METHODS            ((jshort) 0x0040)
#define ADAPTJ_FIELD_METHODS                ((jshort) 0x0080)
#define ADAPTJ_FIELD_NUM_STATIC_FIELDS      ((jshort) 0x0100)
#define ADAPTJ_FIELD_STATICS                ((jshort) 0x0200)
#define ADAPTJ_FIELD_NUM_INSTANCE_FIELDS    ((jshort) 0x0400)
#define ADAPTJ_FIELD_INSTANCES              ((jshort) 0x0800)
#define ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID    ((jshort) 0x1000)

/* Class Load Hook Event */

/* TODO */

/* Class Unload Event */
#define ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID  ((jshort) 0x0008)

/* Compiled Method Load/Unload Event */
#define ADAPTJ_FIELD_METHOD_ID              ((jshort) 0x0008)
#define ADAPTJ_FIELD_CODE_SIZE              ((jshort) 0x0010)
#define ADAPTJ_FIELD_CODE                   ((jshort) 0x0020)
#define ADAPTJ_FIELD_LINENO_TABLE_SIZE      ((jshort) 0x0040)
#define ADAPTJ_FIELD_LINENO_TABLE           ((jshort) 0x0080)

/* Date Dump/Reset Request Event */

/* GC Finish Event */
#define ADAPTJ_FIELD_USED_OBJECTS           ((jshort) 0x0008)
#define ADAPTJ_FIELD_USED_OBJECT_SPACE      ((jshort) 0x0010)
#define ADAPTJ_FIELD_TOTAL_OBJECT_SPACE     ((jshort) 0x0020)

/* GC Start */

/* JNI (Weak) Globalref Alloc/Free */
#define ADAPTJ_FIELD_REF_ID                 ((jshort) 0x0008)
#define ADAPTJ_FIELD_OBJ_ID                 ((jshort) 0x0010)

/* JVM Init Done / Shut Down */

/* Method Entry / Entry 2 */
  /* Method ID defined in Compiled Method Load */
  /* Obj ID defined in JNI Globalref Alloc */

/* Monitor ___ */
#define ADAPTJ_FIELD_OBJECT                 ((jshort) 0x0008)
#define ADAPTJ_FIELD_TIMEOUT                ((jshort) 0x0010)

/* Monitor Dump */
#define ADAPTJ_FIELD_DATA_LEN               ((jshort) 0x0008)
#define ADAPTJ_FIELD_DATA                   ((jshort) 0x0010)
#define ADAPTJ_FIELD_NUM_TRACES             ((jshort) 0x0020)
#define ADAPTJ_FIELD_TRACES                 ((jshort) 0x0040)


/* Object Alloc */
  /* Arena ID defined in Arena Delete */
  /* Obj ID defined in Globalref Alloc */
#define ADAPTJ_FIELD_IS_ARRAY               ((jshort) 0x0020)
#define ADAPTJ_FIELD_SIZE                   ((jshort) 0x0040)
#define ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID  ((jshort) 0x0080)

/* Object Dump */
  /* Data Len defined in Montitor Dump */
  /* Data defined in Monitor Dump */

/* Object Free*/
  /* Obj ID defined in JNI Globalref Alloc */

/* Object Move */
  /* Arena ID defined in Arena Delete */
  /* Obj ID defined in JNI Globalref Alloc */
#define ADAPTJ_FIELD_NEW_ARENA_ID           ((jshort) 0x0020)
#define ADAPTJ_FIELD_NEW_OBJ_ID             ((jshort) 0x0040)


/* Raw Monitor ____ */
#define ADAPTJ_FIELD_NAME                   ((jshort) 0x0008)
#define ADAPTJ_FIELD_ID                     ((jshort) 0x0010)

/* Thread End Event */

/* Thread Start Event */
#define ADAPTJ_FIELD_THREAD_NAME            ((jshort) 0x0008)
#define ADAPTJ_FIELD_GROUP_NAME             ((jshort) 0x0010)
#define ADAPTJ_FIELD_PARENT_NAME            ((jshort) 0x0020)
#define ADAPTJ_FIELD_THREAD_ID              ((jshort) 0x0040)
#define ADAPTJ_FIELD_THREAD_ENV_ID          ((jshort) 0x0080)

/* Instruction Start Event */
  /* Method ID defined in Compild Method Load */
#define ADAPTJ_FIELD_OFFSET                 ((jshort) 0x0010)
#define ADAPTJ_FIELD_IS_TRUE                ((jshort) 0x0020)
#define ADAPTJ_FIELD_KEY                    ((jshort) 0x0040)
#define ADAPTJ_FIELD_LOW                    ((jshort) 0x0080)
#define ADAPTJ_FIELD_HI                     ((jshort) 0x0100)
#define ADAPTJ_FIELD_CHOSEN_PAIR_INDEX      ((jshort) 0x0200)
#define ADAPTJ_FIELD_PAIRS_TOTAL            ((jshort) 0x0400)

/* Thread Start Event */
#define ADAPTJ_FIELD_NEW_STATUS             ((jshort) 0x0008)


jint AdaptJProcessSpecFile(const char *filename);

#endif
