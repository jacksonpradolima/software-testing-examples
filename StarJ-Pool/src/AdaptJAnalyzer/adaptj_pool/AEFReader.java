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

package adaptj_pool;

import java.io.*;
import adaptj_pool.event.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.JVMPI.*;
import adaptj_pool.util.*;
import java.util.NoSuchElementException;
import org.apache.bcel.generic.InstructionHandle;

/**
 * The <code>AEFReader</code> class verifies and parses an AdaptJ Event File (AEF). It first parses the entire
 * file to check its format. At this point, the header information of the file is available. The <code>AEFReader</code>
 * then repositions itself for reading the events from the file, one at a time. 
 *
 * @author Bruno Dufour
 */

public class AEFReader implements AdaptJSpecConstants, IAEFReader {
    /** Default buffer size for the underlying BufferedInputStream */
    private final static int BUF_SIZE = 2048;

    private BufferedFileReader input;
    private int version;
    private long rewindMark = -1L;
    private short eventInfo[] = new short[AdaptJEvent.ADAPTJ_EVENT_COUNT];
    private long counters[] = new long[AdaptJEvent.ADAPTJ_EVENT_COUNT];
    private long parsedEventCounts[] = new long[AdaptJEvent.ADAPTJ_EVENT_COUNT];
    private long eventCount = 0L;
    private long retrievedEvents = 0L;
    private boolean isPiped = false;
    private boolean parsingDone = false;
    private AdaptJEvent nextEvent = null;
    private int pendingISEs = 0;
    private int pendingISEEnvID;
    private int pendingISEMethodID;
    private InstructionHandle pendingISEHandle;

    /**
     * Constructs a new AEFReader which takes its input from <code>fileName</code> 
     * 
     *  @param fileName The name of a file to read from. This file must exist and have the correct format
     *                  of an AdaptJ Event File (see above).
     *  @throws FileNotFoundException  if <code>fileName</code> does not exists
     *  @throws IOException            if an error occurs while reading <code>fileName</code>
     *  @throws AEFFormatException     if the format of <code>fileName</code> appears to be invalid
     */
    public AEFReader(String fileName) throws IOException, AEFFormatException {
        this(fileName, false);
    }

    public AEFReader(String fileName, boolean pipedMode) throws IOException, AEFFormatException {
        input = new BufferedFileReader(fileName);
        isPiped = pipedMode;
        readFully();
    }

    public AEFReader(File file) throws IOException, AEFFormatException {
        this(file, false);
    }

    public AEFReader(File file, boolean pipedMode) throws IOException, AEFFormatException {
        input = new BufferedFileReader(file);
        isPiped = pipedMode;
        readFully();
    }
    
    private AdaptJEvent newEvent(int id) {
        switch (id) {
            case AdaptJEvent.ADAPTJ_ARENA_DELETE:
                return new ArenaDeleteEvent();
            case AdaptJEvent.ADAPTJ_ARENA_NEW:
                return new ArenaNewEvent();
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                return new ClassLoadEvent();
            //case AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK:
            //    return new ClassLoadHookEvent();
            case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                return new ClassUnloadEvent();
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD:
                return new CompiledMethodLoadEvent();
            case AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD:
                return new CompiledMethodUnloadEvent();
            //case AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST:
            //    return new DataDumpRequestEvent();
            //case AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST:
            //    return new DataResetRequestEvent();
            case AdaptJEvent.ADAPTJ_GC_FINISH:
                return new GCFinishEvent();
            case AdaptJEvent.ADAPTJ_GC_START:
                return new GCStartEvent();
            //case AdaptJEvent.ADAPTJ_HEAP_DUMP:
            //    return new HeapDumpEvent();
            //case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC:
            //    return new JNIGlobalRefAllocEvent();
            //case AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE:
            //    return new JNIGlobalRefFreeEvent();
            //case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC:
            //    return new JNIWeakGlobalRefAllocEvent();
            //case AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE:
            //    return new JNIWeakGlobalRefFreeEvent();
            case AdaptJEvent.ADAPTJ_JVM_INIT_DONE:
                return new JVMInitDoneEvent();
            case AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN:
                return new JVMShutDownEvent();
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
                return new MethodEntryEvent();
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                return new MethodEntry2Event();
            case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                return new MethodExitEvent();
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER:
                return new MonitorContendedEnterEvent();
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED:
                return new MonitorContendedEnteredEvent();
            case AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT:
                return new MonitorContendedExitEvent();
            case AdaptJEvent.ADAPTJ_MONITOR_DUMP:
                return new MonitorDumpEvent();
            case AdaptJEvent.ADAPTJ_MONITOR_WAIT:
                return new MonitorWaitEvent();
            case AdaptJEvent.ADAPTJ_MONITOR_WAITED:
                return new MonitorWaitedEvent();
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                return new ObjectAllocEvent();
            case AdaptJEvent.ADAPTJ_OBJECT_DUMP:
                return new ObjectDumpEvent();
            case AdaptJEvent.ADAPTJ_OBJECT_FREE:
                return new ObjectFreeEvent();
            case AdaptJEvent.ADAPTJ_OBJECT_MOVE:
                return new ObjectMoveEvent();
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER:
                return new RawMonitorContendedEnterEvent();
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED:
                return new RawMonitorContendedEnteredEvent();
            case AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT:
                return new RawMonitorContendedExitEvent();
            case AdaptJEvent.ADAPTJ_THREAD_END:
                return new ThreadEndEvent();
            case AdaptJEvent.ADAPTJ_THREAD_START:
                return new ThreadStartEvent();
            case AdaptJEvent.ADAPTJ_INSTRUCTION_START:
                return new InstructionStartEvent();
            case AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE:
                return new ThreadStatusChangeEvent();
            default:
                throw new RuntimeException("Invalid event ID: " + id);
        }
    }

    /**
     * Constructs a new AEFReader which takes its input from <code>input</code> 
     * 
     *  @param input A <code>DataInput</code> object from which to read. The data returned
     *                by <code>input</code> must have the correct format of an AdaptJ Event File (see above).
     *  @throws IOException            if an error occurs while reading <code>input</code>
     *  @throws AEFFormatException     if the format of <code>input</code> appears to be invalid
     */
    /*
    public AEFReader(DataInput input) throws AEFFormatException, IOException {
        this.input = input;
        readFully();
    }
    */

    /*
    private DataInput openStream(String filename) throws IOException {
        FileInputStream fs = new FileInputStream(filename);
        return new DataInputStream(new BufferedInputStream(fs, BUF_SIZE));
    }
    */

    /**
     * Returns the version number of the AdaptJ Event File being read. This is a number between 0 and 255, 0 being
     * reserved for testing versions of the AdaptJ framework.
     *  
     *  @return The version number of the AdaptJ Event File being read. 
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns an array of counters for this AdaptJ Event File. The counters represent the number of times a particular event
     * has occured. This information can be calculated by a JVMPI agent (or another source of profiling data) and incorporated
     * into the AdaptJ Event File, or is calculated by this AEFReader when the event is recorded. When no information is available
     * for a particular event is known, the counter value is less than 0.
     *
     * @return an array of counters for all AdaptJ Event, indexed by the AdaptJ Event type constant.
     * @see adaptj_pool.event.AdaptJEvent
     * @see #getDumpedTypes()
     */
    public long[] getStaticCounters() {
        return counters;
    }
    
    public long[] getRuntimeCounters() {
        return parsedEventCounts;
    }
    
    public short[] getEventInfo() {
        return eventInfo;
    }

    /** 
     * Parses the whole AdaptJ Event File, calculating the value for missing counters and making sure that the
     * file format is valid. The position of the file is reset after the parsing phase so that events can be read next.
     *
     * @throws AEFFormatException when the format of the file appears to be invalid (or the file is corrupted)
     * @throws IOException when an error occurs which prevents reading from the specified file
     */
    private void readFully() throws AEFFormatException, IOException {
        Scene.v().showMessage("Loading AdaptJ Trace File...");
        //Scene.v().showDebug("Reading magic number...");
        readMagic();
        //Scene.v().showDebug("Reading AdaptJ header...");
        readHeader();
        //Scene.v().showDebug("Setting rewind mark...");
        if (isPiped) {
            Scene.v().showMessage("File opened in Piped mode");
            rewindMark = -1L;
        } else {
            rewindMark = input.getFilePointer();
        }
        Scene.v().showMessage("File successfully loaded");
    }
    
    /** 
     * Reads the magic number and version number from the AdaptJ Event File.
     *
     * @throws AEFFormatException when the magic number of the AdaptJ Event File is invalid
     * @throws IOException when an error occurs which prevents reading from the specified file
     */
    private void readMagic() throws AEFFormatException, IOException {
        int magic = input.readInt();
        version = Scene.v().checkVersion(magic, ADAPTJ_MAGIC);
        if (!Scene.v().supports(version)) {
            throw new AEFFormatException("Cannot handle file version: " + version);
        }
        
        /*
        int version = magic & VERSION_MASK;

        magic = magic & (~VERSION_MASK); // clear version information from magic

        if (magic != ADAPTJ_MAGIC) {
            throw new AEFFormatException("Magic");
        }
        */
    }

    /** 
     * Reads the counter values and the Event Specification from the AdaptJ Event File. For each event,
     * the Event Specification (a bit field of 2 bytes) is read and store. This bit field indicates
     * which part of the event structure are recorded, and whether the event is recorded and/or counted.
     *
     * @throws AEFFormatException when the AdaptJ Event File seems to have an invalid format or is corrupted
     * @throws IOException when an error occurs which prevents reading from the specified file
     */
    private void readHeader() throws AEFFormatException, IOException {
        byte eventID;
        short info;

        /* Read Agent Options */
        short agentOptions = input.readShort();
        if ((agentOptions & ADAPTJ_ISPIPED) != 0) {
            Scene.v().showMessage("Pipe mode bit is set in the trace file");
            if (!isPiped) {
                Scene.v().showMessage("Switching to pipe mode");
            }
            isPiped = true;
        }

        /* Read Event Information */
        for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) { // for each event
            eventID = input.readByte(); // the ID is also the index of the event in the arrays
            info = input.readShort();   // get the information

            /* Update the counter values */
            if (((info & ADAPTJ_FIELD_COUNTED) != ((short) 0))) {
                counters[eventID] = input.readLong();
            } else {
                counters[eventID] = -1L;
            }

            parsedEventCounts[i] = 0L;

            /* Store the information for later use */
            eventInfo[eventID] = info;
        }
    }

    public void rewind() throws IOException {
        if (!isPiped) {
            if (rewindMark >= 0L) {
                input.seek(rewindMark);
            } else {
                Scene.v().reportError("Rewind mark was not set");
            }
        } else {
            Scene.v().showWarning("Cowardly refusing to reward in pipe mode");
        }
    }

    public void preparse() throws AEFFormatException, IOException {
        if (parsingDone) {
            Scene.v().showWarning("File is already preparsed. Skipping.");
            return;
        }

        if (isPiped) {
            Scene.v().showWarning("Cannot preparse a pipe. Skipping");
            return;
        }

        if (retrievedEvents != 0L) {
            Scene.v().showWarning("Cannot preparse a file once events have been retrieved. Skipping");
            return;
        }
        
        long mark = input.getFilePointer(); // remember this location
        if (rewindMark >= 0L) {
            input.seek(rewindMark);
        } else {
            Scene.v().reportError("Rewinding mark is not set");
        }
        Scene.v().showMessage("Preparsing file...");
        parseEvents();
        Scene.v().showMessage("Preparsing successfully completed");
        input.seek(mark); // go back to where we were
        parsingDone = true;
    }
    
    /** 
     * Reads the events from an AdaptJ Event File, making sure that the format is valid. For events
     * which are recorded but for which the Agent has not included a count, the counter value is
     * computed. The total number of events is also recorded.
     *
     * @throws AEFFormatException when the AdaptJ Event File seems to have an invalid format or is corrupted
     * @throws IOException when an error occurs which prevents reading from the specified file
     */
    private void parseEvents() throws AEFFormatException, IOException {
        AdaptJEvent event;
        BufferedFileReader originalInput = input;
        
        /* Read events, updating the appropriate counter */
        while ((event = parseNextEvent()) != null) {
            parsedEventCounts[event.getTypeID()] += 1L;
            eventCount++;
            event = null; // release event object
        }

        /* Restore Stream, in case the AEF spanned multiple physical files */
        input = originalInput;

        /* Update the counters */
        for (int i = 0; i < counters.length; i++) {
            if (counters[i] < 0) {
                /* This event was not counted. Simply update the information. */
                counters[i] = parsedEventCounts[i];
            } else {
                boolean recorded = (eventInfo[i] & ADAPTJ_FIELD_RECORDED) != 0;
                if (recorded && (counters[i] != parsedEventCounts[i])) {
                    /* Counter Mismatch detected */
                    Scene.v().showWarning("Event counter mismatch for typeID " + i);
                }
            }
        }
    }

    /**
     * Returns the total number of events found in this AdaptJ Event File.
     *
     * @return The total number of AdaptJEvent objects that are found in this AdaptJ Event File.
     */
    public long getEventCount() {
        if (isPiped || !parsingDone) {
            return -1L;
        }
        return eventCount;
    }

    /**
     * Returns <code>true</code> if this AEFReader has more elements. (In other words,
     * returns <code>true</code> if <code>getNextEvent</code> would return an event rather
     * than throwing an exception.)
     *
     * @return <code>true</code> if this AEFReader has more elements; <code>false</code> otherwise.
     */
    public boolean hasMoreEvents() {
        if (isPiped || !parsingDone) {
            if (nextEvent != null) {
                return true;
            }
            try {
                nextEvent = parseNextEvent();
            } catch (AEFFormatException e) {
                try {
                    throw new RuntimeException("File format error: " + e
                                               + " (file offset=" + input.getFilePointer() + ")");
                } catch (IOException ioe) {
                    throw new RuntimeException("File format error (file offset=?)");
                }
            } catch (IOException e) {
                try {
                    throw new RuntimeException("File format error"
                                           + " (file offset=" + input.getFilePointer() + ")");
                } catch (IOException ioe) {
                    throw new RuntimeException("File format error (file offset=?)");
                }
            }
            return (nextEvent != null);
        }
        return retrievedEvents < eventCount;
    }

    public boolean isPiped() {
        return isPiped;
    }

    /**
     * Returns the next available <code>AdaptJEvent</code> in the AdaptJ Event File.
     *
     * @throws NoSuchElementException if there are no more events to read
     * @return The next available <code>AdaptJEvent</code> in the AdaptJ Event File.
     */
    public AdaptJEvent getNextEvent() throws AEFFormatException, IOException {
        boolean behaveAsPipe = isPiped || !parsingDone;
        if (behaveAsPipe) {
            if (nextEvent != null) {
                AdaptJEvent result = nextEvent;
                nextEvent = null;
                retrievedEvents++;
                parsedEventCounts[result.getTypeID()] += 1L;
                return result;
            }
        } else {
            if (retrievedEvents >= eventCount) {
                /* No more events left */
                throw new NoSuchElementException();
            }
        }
            
        //try {
            AdaptJEvent result = parseNextEvent();
            if (behaveAsPipe && result != null) {
                parsedEventCounts[result.getTypeID()] += 1L;
            }
            retrievedEvents++;
            return result;
        //} catch (AEFFormatException e) {
        //    throw new RuntimeException("File format error\n");
        //}
    }
    
    /**
     * Returns the next available <code>AdaptJEvent</code> in the AdaptJ Event File, or <code>null</code>
     * if there is no such element.
     *
     * @throws NoSuchElementException if there are no more events to read
     * @return The next available <code>AdaptJEvent</code> in the AdaptJ Event File.
     */
    private AdaptJEvent parseNextEvent() throws AEFFormatException, IOException {
        boolean behaveAsPipe = isPiped || !parsingDone;

        if (pendingISEs > 0) {
            InstructionStartEvent ise = (InstructionStartEvent)
                    this.newEvent(AdaptJEvent.ADAPTJ_INSTRUCTION_START);
            ise.setEnvID(pendingISEEnvID);
            ise.setMethodID(pendingISEMethodID);
            ise.setCode(-1);
            
            if (pendingISEHandle == null) {
                throw new RuntimeException("Cannot locate code for an encoded instruction sequence. Please check the CLASSPATH. (Internal Code: 2)");
            }
            
            pendingISEHandle = pendingISEHandle.getNext();
            ise.setOffset(pendingISEHandle.getPosition());
            pendingISEs--;

            if (behaveAsPipe) {
                if (counters[AdaptJEvent.ADAPTJ_INSTRUCTION_START] == -1L) {
                    counters[AdaptJEvent.ADAPTJ_INSTRUCTION_START] = 1L;
                } else {
                    counters[AdaptJEvent.ADAPTJ_INSTRUCTION_START] += 1L;
                }
            }

            return ise;
        }
    
        int typeID = input.read();
        boolean requested = ((typeID & AdaptJEvent.ADAPTJ_REQUESTED_EVENT) != 0);
        if (typeID != -1) {
            typeID = (typeID & ~AdaptJEvent.ADAPTJ_REQUESTED_EVENT) & 0x000000FF; /* Clear 'requested' bit */
        }

        if (typeID == -1) {
            /* Failed reading the next byte */
            return null;
        }

        AdaptJEvent newEvent = null;
        
        /* Check for a split in the ADAPTJ Event File */
        while (typeID == ADAPTJ_FILESPLIT) {
            /* Open the next file */
            String newFileName = input.readUTF();
            Scene.v().showDebug("AEF continued on file: \"" + newFileName + "\"");
            input = new BufferedFileReader(newFileName);
            /* Get the next type ID from the new file */
            typeID = input.read();
            if (typeID == -1) {
                /* Failed reading the next byte */
                return null;
            }
            
            requested = ((typeID & AdaptJEvent.ADAPTJ_REQUESTED_EVENT) != 0);
            typeID = (typeID & ~AdaptJEvent.ADAPTJ_REQUESTED_EVENT) & 0x000000FF; /* Clear 'requested' bit */
        }

        if (typeID == ADAPTJ_COMPACT_INSTRUCTION_START) {
            pendingISEEnvID = input.readInt();
            pendingISEMethodID = input.readInt();
            int instOffset = input.readInt();
            pendingISEHandle = BytecodeResolver.v().getInstructionHandle(pendingISEMethodID, instOffset);
            pendingISEs = input.readInt() - 1;

            if (pendingISEHandle == null) {
                throw new RuntimeException("Cannot locate code for an encoded instruction sequence. Please check the CLASSPATH.");
            }
            
            InstructionStartEvent ise = (InstructionStartEvent)
                    this.newEvent(AdaptJEvent.ADAPTJ_INSTRUCTION_START);
            ise.setEnvID(pendingISEEnvID);
            ise.setMethodID(pendingISEMethodID);
            ise.setOffset(instOffset);
            ise.setCode(-1);

            if (behaveAsPipe) {
                if (counters[AdaptJEvent.ADAPTJ_INSTRUCTION_START] == -1L) {
                    counters[AdaptJEvent.ADAPTJ_INSTRUCTION_START] = 1L;
                } else {
                    counters[AdaptJEvent.ADAPTJ_INSTRUCTION_START] += 1L;
                }
            }

            ise.setRequested(requested);

            return ise;
        }

        if (typeID < 0 || typeID >= AdaptJEvent.ADAPTJ_EVENT_COUNT) {
            throw new AEFFormatException("Unrecognized ADAPTJ ID: " + typeID
                                         + " (file offset=" + input.getFilePointer() + ")");
        }

        try {
            /* Get the Event Specification for this Event ID */
            short info = eventInfo[typeID];

            /* Get the appropriate event from the event pool */
            newEvent = this.newEvent(typeID);

            /* Read the file and fill in the event fields */
            newEvent.readFromStream(input, info);
        } catch (ArrayIndexOutOfBoundsException except) {
            Scene.v().reportError("Unsupported event received (type ID = " + typeID + ")");
            return null;
        }

        if (behaveAsPipe) {
            if (counters[typeID] == -1L) {
                counters[typeID] = 1L;
            } else {
                counters[typeID] += 1L;
            }
        }
        
        newEvent.setRequested(requested);

        if (typeID == AdaptJEvent.ADAPTJ_CLASS_LOAD) {
            BytecodeResolver.v().loadClass((ClassLoadEvent) newEvent);
        } else if (typeID == AdaptJEvent.ADAPTJ_CLASS_UNLOAD) {
            BytecodeResolver.v().unloadClass((ClassUnloadEvent) newEvent);
        } 
        

        return newEvent;
    }
}
