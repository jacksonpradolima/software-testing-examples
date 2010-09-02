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

package adaptj_pool.spec;

public interface AdaptJSpecConstants {
    /** Magic number for AdaptJ Trace Files */
    public final static int ADAPTJ_MAGIC = 0x41454600;
    /** Magic number for AdaptJ Spec Files */
    public final static int ADAPTJ_SPEC_MAGIC = 0x41455300;
    
    /** Bit mask that must be applied to the first int in the file in order to obtain the file version */
    public final static int VERSION_MASK = 0x000000FF;

    public final static short ADAPTJ_ISPIPED = (short) 0x0001;

    /* =============================================================================================== */
    /*                                    ADAPTJ Spec bit masks                                         */
    /* =============================================================================================== */
    public final static short ADAPTJ_FIELD_RECORDED               = ((short) 0x0001);
    public final static short ADAPTJ_FIELD_COUNTED                = ((short) 0x0002);
    public final static short ADAPTJ_FIELD_ENV_ID                 = ((short) 0x0004);

    /* Arena Delete/New  Event */
    public final static short ADAPTJ_FIELD_ARENA_ID               = ((short) 0x0008);
    public final static short ADAPTJ_FIELD_ARENA_NAME             = ((short) 0x0010);

    /* Class Load Event */
    public final static short ADAPTJ_FIELD_CLASS_NAME             = ((short) 0x0008);
    public final static short ADAPTJ_FIELD_SOURCE_NAME            = ((short) 0x0010);
    public final static short ADAPTJ_FIELD_NUM_INTERFACES         = ((short) 0x0020);
    public final static short ADAPTJ_FIELD_NUM_METHODS            = ((short) 0x0040);
    public final static short ADAPTJ_FIELD_METHODS                = ((short) 0x0080);
    public final static short ADAPTJ_FIELD_NUM_STATIC_FIELDS      = ((short) 0x0100);
    public final static short ADAPTJ_FIELD_STATICS                = ((short) 0x0200);
    public final static short ADAPTJ_FIELD_NUM_INSTANCE_FIELDS    = ((short) 0x0400);
    public final static short ADAPTJ_FIELD_INSTANCES              = ((short) 0x0800);
    public final static short ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID    = ((short) 0x1000);

    /* Class Load Hook Event */

    // TODO

    /* Class Unload Event */
    public final static short ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID  = ((short) 0x0008);

    /* Compiled Method Load/Unload Event */
    public final static short ADAPTJ_FIELD_METHOD_ID              = ((short) 0x0008);
    public final static short ADAPTJ_FIELD_CODE_SIZE              = ((short) 0x0010);
    public final static short ADAPTJ_FIELD_CODE                   = ((short) 0x0020);
    public final static short ADAPTJ_FIELD_LINENO_TABLE_SIZE      = ((short) 0x0040);
    public final static short ADAPTJ_FIELD_LINENO_TABLE           = ((short) 0x0080);

    /* Date Dump/Reset Request Event */

    /* GC Finish Event */
    public final static short ADAPTJ_FIELD_USED_OBJECTS           = ((short) 0x0008);
    public final static short ADAPTJ_FIELD_USED_OBJECT_SPACE      = ((short) 0x0010);
    public final static short ADAPTJ_FIELD_TOTAL_OBJECT_SPACE     = ((short) 0x0020);

    /* GC Start */

    /* JNI (Weak) Globalref Alloc/Free */
    public final static short ADAPTJ_FIELD_REF_ID                 = ((short) 0x0008);
    public final static short ADAPTJ_FIELD_OBJ_ID                 = ((short) 0x0010);

    /* JVM Init Done / Shut Down */

    /* Method Entry / Entry 2 */
      /* Method ID defined in Compiled Method Load */
      /* Obj ID defined in JNI Globalref Alloc */

    /* Monitor ___ */
    public final static short ADAPTJ_FIELD_OBJECT                 = ((short) 0x0008);
    public final static short ADAPTJ_FIELD_TIMEOUT                = ((short) 0x0010);
    
    /* Monitor Dump */
    public final static short ADAPTJ_FIELD_DATA_LEN               = ((short) 0x0008);
    public final static short ADAPTJ_FIELD_DATA                   = ((short) 0x0010);
    public final static short ADAPTJ_FIELD_NUM_TRACES             = ((short) 0x0020);
    public final static short ADAPTJ_FIELD_TRACES                 = ((short) 0x0040);

    /* Object Alloc */
      /* Arena ID defined in Arena Delete */
      /* Obj ID defined in Globalref Alloc */
    public final static short ADAPTJ_FIELD_IS_ARRAY               = ((short) 0x0020);
    public final static short ADAPTJ_FIELD_SIZE                   = ((short) 0x0040);
    public final static short ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID  = ((short) 0x0080);

    /* Object Dump */
      /* Data Len defined in Monitor Dump */
      /* Data defined in Monitor Dump */

    /* Object Free*/
      /* Obj ID defined in JNI Globalref Alloc */

    /* Object Move */
      /* Arena ID defined in Arena Delete */
      /* Obj ID defined in JNI Globalref Alloc */
    public final static short ADAPTJ_FIELD_NEW_ARENA_ID           = ((short) 0x0020);
    public final static short ADAPTJ_FIELD_NEW_OBJ_ID             = ((short) 0x0040);


    /* Raw Monitor ____ */
    public final static short ADAPTJ_FIELD_NAME                   = ((short) 0x0008);
    public final static short ADAPTJ_FIELD_ID                     = ((short) 0x0010);

    /* Thread End Event */

    /* Thread Start Event */
    public final static short ADAPTJ_FIELD_THREAD_NAME            = ((short) 0x0008);
    public final static short ADAPTJ_FIELD_GROUP_NAME             = ((short) 0x0010);
    public final static short ADAPTJ_FIELD_PARENT_NAME            = ((short) 0x0020);
    public final static short ADAPTJ_FIELD_THREAD_ID              = ((short) 0x0040);
    public final static short ADAPTJ_FIELD_THREAD_ENV_ID          = ((short) 0x0080);

    /* Instruction Start Event */
      /* Method ID defined in Compild Method Load */
    public final static short ADAPTJ_FIELD_OFFSET                 = ((short) 0x0010);
    public final static short ADAPTJ_FIELD_IS_TRUE                = ((short) 0x0020);
    public final static short ADAPTJ_FIELD_KEY                    = ((short) 0x0040);
    public final static short ADAPTJ_FIELD_LOW                    = ((short) 0x0080);
    public final static short ADAPTJ_FIELD_HI                     = ((short) 0x0100);
    public final static short ADAPTJ_FIELD_CHOSEN_PAIR_INDEX      = ((short) 0x0200);
    public final static short ADAPTJ_FIELD_PAIRS_TOTAL            = ((short) 0x0400);
    
    /* -------------------------------------------------------------------------- */
    /*                                   "Fake" Events                            */
    /* -------------------------------------------------------------------------- */
    
    /* Thread Status Event */
    public final static short ADAPTJ_FIELD_NEW_STATUS             = ((short) 0x0008);

    /* -------------------------------------------------------------------------- */
    /*                                  Other Constants                           */
    /* -------------------------------------------------------------------------- */

    public final static int ADAPTJ_FILESPLIT                    = ((int) 0x0000007F);
    public final static int ADAPTJ_COMPACT_INSTRUCTION_START    = ((int) 0x0000007E);
}
