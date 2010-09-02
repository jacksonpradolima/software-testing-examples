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

package adaptj_pool.toolkits;

import adaptj_pool.event.AdaptJEvent;
import adaptj_pool.spec.AdaptJSpecConstants;

public class EventDependency {
    private int eventID;
    private int alternateID;
    private short eventInfo;
    private boolean required;
    
    public EventDependency(int eventID) {
        this(eventID, (short) 0, true);
    }
    
    public EventDependency(int eventID, boolean required) {
        this(eventID, (short) 0, required);
    }
    
    public EventDependency(int eventID, int eventInfo) {
        this(eventID, AdaptJEvent.ADAPTJ_UNKNOWN, (short) (eventInfo & 0x0000FFFF), true);
    }
    
    public EventDependency(int eventID, int alternateID, int eventInfo) {
        this(eventID, alternateID, (short) (eventInfo & 0x0000FFFF), true);
    }
    
    public EventDependency(int eventID, int eventInfo, boolean required) {
        this(eventID, AdaptJEvent.ADAPTJ_UNKNOWN, (short) (eventInfo & 0x0000FFFF), required);
    }
    
    public EventDependency(int eventID, int alternateID, int eventInfo, boolean required) {
        this(eventID, alternateID, (short) (eventInfo & 0x0000FFFF), required);
    }
    
    public EventDependency(int eventID, short eventInfo) {
        this(eventID, AdaptJEvent.ADAPTJ_UNKNOWN, eventInfo, true);
    }

    public EventDependency(int eventID, int alternateID, short eventInfo) {
        this(eventID, alternateID, eventInfo, true);
    }
    
    public EventDependency(int eventID, short eventInfo, boolean required) {
        this(eventID, AdaptJEvent.ADAPTJ_UNKNOWN, eventInfo, required);
    }
    
    public EventDependency(int eventID, int alternateID, short eventInfo, boolean required) {
        if (eventID < 0 || eventID >= AdaptJEvent.ADAPTJ_EVENT_COUNT) {
            throw new RuntimeException("Invalid event ID");
        }

        this.eventID = eventID;
        this.alternateID = alternateID;
        this.eventInfo = (short) (((eventInfo | AdaptJSpecConstants.ADAPTJ_FIELD_RECORDED)
                            & getEventMask(eventID)));
        if (alternateID != AdaptJEvent.ADAPTJ_UNKNOWN) {
            this.eventInfo &= getEventMask(alternateID);
        }
        this.required = required;
    }
    
    public int getEventID() {
        return eventID;
    }
    
    public int getAlternateEventID() {
        return alternateID;
    }
    
    public short getEventInfo() {
        return eventInfo;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean checkInfoAgainst(short mask) {
        /* Make sure that each bit that is set in 'eventInfo'
         * is also set in 'mask' */


        int i = ((int) eventInfo) & 0x0000FFFF;
        return ((i & mask) == i);
    }

    public static int getEventMask(int eventID) {
        int numBits = 3;

        switch (eventID) {
            case AdaptJEvent.ADAPTJ_ARENA_DELETE:
                numBits += 1;
                break;
            case AdaptJEvent.ADAPTJ_ARENA_NEW:
                numBits += 2;
                break;
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                numBits += 10;
                break;
            case AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK:
                // FIXME
                break;
            case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                numBits += 1;
                break;
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD:
                numBits += 5;
                break;
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD:
                numBits += 1;
                break;
            case AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST:
            case AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST:
                /* No event specific information */
                break;
            case AdaptJEvent.ADAPTJ_GC_FINISH:
                numBits += 3;
                break;
            case AdaptJEvent.ADAPTJ_GC_START:
                /* No event specific information */
                break;
            case AdaptJEvent.ADAPTJ_HEAP_DUMP:
                // TODO
                break;
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC:
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC:
                numBits += 2;
                break;
            case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE:
            case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE:
                numBits += 1;
                break;
            case AdaptJEvent.ADAPTJ_JVM_INIT_DONE:
            case AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN:
                /* No event specific information */
                break;
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
            case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                numBits += 1;
                break;
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                numBits += 2;
                break;
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER:
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED:
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT:
                numBits += 1;
                break;
            case AdaptJEvent.ADAPTJ_MONITOR_DUMP:
                numBits += 4;
                break;
            case AdaptJEvent.ADAPTJ_MONITOR_WAIT:
            case AdaptJEvent.ADAPTJ_MONITOR_WAITED:
                numBits += 2;
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                numBits += 5;
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_DUMP:
                numBits += 2;
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_FREE:
                numBits += 1;
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_MOVE:
                numBits += 4;
                break;
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER:
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED:
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT:
                numBits += 2;
                break;
            case AdaptJEvent.ADAPTJ_THREAD_END:
                /* No event specific information */
                break;
            case AdaptJEvent.ADAPTJ_THREAD_START:
                numBits += 5;
                break;
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                numBits += 8;
                break;
            case AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE:
                numBits += 1;
                break;
            default:
                throw new RuntimeException("Invalid Event ID: " + eventID);
        }

        if (numBits == 0) {
            return 0;
        }

        int result = 1;
        for (int i = 0; i < numBits - 1; i++) {
            result = (result << 1) | 0x00000001;
        }

        return result;
    }
}

