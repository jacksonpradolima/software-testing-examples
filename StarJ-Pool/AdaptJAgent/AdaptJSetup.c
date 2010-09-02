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

extern JVMPI_Interface *jvmpi_interface;

extern FILE *outputFile;
extern char fileName[];
extern int splitThreshold;
extern int gcInterval;
#ifdef ADAPTJ_ENABLE_PIPE
extern boolean pipeMode;
#endif
extern boolean optMode;
extern boolean verboseMode;
extern boolean threadStatus;
extern boolean needMSBConversion;
extern jshort (*JShortMSB)(jshort);
extern jint (*JIntMSB)(jint);
extern jlong (*JLongMSB)(jlong);

extern jshort eventInfo[];
extern jlong counters[];
extern AdaptJEvent charToEvent[];
extern AdaptJEvent JVMPIToAdaptJEvent[];
extern char eventToChar[];
extern char eventNames[ADAPTJ_MAX_EVENT + 1][42];
extern jint adaptjEventToJVMPI[];

extern HashMap_t methodIDtoBytecode;

extern AdaptJIDSet_t knownObjectIDs;
extern AdaptJIDSet_t knownThreadIDs;
extern AdaptJIDSet_t knownMethodIDs;
extern AdaptJIDSetMap_t arenaIDtoObjects;
extern AdaptJIDSetMap_t classIDtoMethods;

extern char **cp;
extern int cpSize;

void printUsage() {
    fprintf(stdout, "AdaptJ Agent Options\n");
    fprintf(stdout, "===================\n\n");
    fprintf(stdout, " file=<output_file>       Specifies the name of the output file.\n");
    fprintf(stdout, "                          Defaults to \"AdaptJ.dat\"\n");
    fprintf(stdout, " events=<event_char>+     Specifies the events to be recorded\n");
    fprintf(stdout, " counters=<event_char>+   Specifies the events to be counted\n");
    fprintf(stdout, "                          Usage \"help=events\" to get a list of event chars\n");
    fprintf(stdout, " split=<size>             Specifies the threshold at which a new file is to be started\n");
    fprintf(stdout, " gc=<size>                Specifies at which interval to force garbage collection\n");         
    fprintf(stdout, "                          <size> is a number, optionally followed by one of 'k', 'm'\n");
    fprintf(stdout, "                          or 'g', which stand for 'kilobytes', 'megabytes' and\n");
    fprintf(stdout, "                          'gigabytes', respectively. THIS FEATURE IS CURRENTLY BROKEN.\n");
#ifdef ADAPTJ_ENABLE_PIPE
    fprintf(stdout, " pipe                     Output data to a named piped instead of a regular file\n");
#endif
    fprintf(stdout, " quiet                    Turns off agent messages\n");
    fprintf(stdout, " opt                      Attempts to reduce the size of the trace file by using\n");
    fprintf(stdout, "                          compression techniques\n");
    fprintf(stdout, " cp=<path1>+              Specifies the classpath to use for resolving classes\n");   
    fprintf(stdout, " cp+=<path1>+             Specifies the classpath to use for resolving classes.\n");
    fprintf(stdout, "                          The value of the environment variable CLASSPATH is append to\n");
    fprintf(stdout, "                          the specified list\n");
    fprintf(stdout, " help[=events]            Prints this help message. If 'events' is specified, a list\n");
    fprintf(stdout, "                          of event chars is output\n");
}

void printEventChars() {
    fprintf(stdout, "AdaptJ Event Mappings\n");
    fprintf(stdout, "====================\n\n");
    fprintf(stdout, "JVMPI_EVENT_ARENA_DELETE                    A\n");
    fprintf(stdout, "JVMPI_EVENT_ARENA_NEW                       a\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_CLASS_LOAD                      c\n");
    fprintf(stdout, "JVMPI_EVENT_CLASS_LOAD_HOOK                 k\n");
    fprintf(stdout, "JVMPI_EVENT_CLASS_UNLOAD                    C\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_COMPILED_METHOD_LOAD            l\n");
    fprintf(stdout, "JVMPI_EVENT_COMPILED_METHOD_UNLOAD          L\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_DATA_DUMP_REQUEST               q\n");
    fprintf(stdout, "JVMPI_EVENT_DATA_RESET_REQUEST              Q\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_GC_FINISH                       G\n");
    fprintf(stdout, "JVMPI_EVENT_GC_START                        g\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_HEAP_DUMP                       h\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_JNI_GLOBALREF_ALLOC             j\n");
    fprintf(stdout, "JVMPI_EVENT_JNI_GLOBALREF_FREE              J\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_JNI_WEAK_GLOBALREF_ALLOC        w\n");
    fprintf(stdout, "JVMPI_EVENT_JNI_WEAK_GLOBALREF_FREE         W\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_JVM_INIT_DONE                   v\n");
    fprintf(stdout, "JVMPI_EVENT_JVM_SHUT_DOWN                   V\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_METHOD_ENTRY                    b\n");
    fprintf(stdout, "JVMPI_EVENT_METHOD_ENTRY2                   m\n");
    fprintf(stdout, "JVMPI_EVENT_METHOD_EXIT                     M\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_MONITOR_CONTENDED_ENTER         d\n");
    fprintf(stdout, "JVMPI_EVENT_MONITOR_CONTENDED_ENTERED       e\n");
    fprintf(stdout, "JVMPI_EVENT_MONITOR_CONTENDED_EXIT          D\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_MONITOR_DUMP                    x\n");
    fprintf(stdout, "JVMPI_EVENT_MONITOR_WAIT                    y\n");
    fprintf(stdout, "JVMPI_EVENT_MONITOR_WAITED                  Y\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_OBJECT_ALLOC                    o\n");
    fprintf(stdout, "JVMPI_EVENT_OBJECT_DUMP                     p\n");
    fprintf(stdout, "JVMPI_EVENT_OBJECT_FREE                     O\n");
    fprintf(stdout, "JVMPI_EVENT_OBJECT_MOVE                     P\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTER     r\n");
    fprintf(stdout, "JVMPI_EVENT_RAW_MONITOR_CONTENDED_ENTERED   E\n");
    fprintf(stdout, "JVMPI_EVENT_RAW_MONITOR_CONTENDED_EXIT      R\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_THREAD_END                      T\n");
    fprintf(stdout, "JVMPI_EVENT_THREAD_START                    t\n");
    fprintf(stdout, "\n");
    fprintf(stdout, "JVMPI_EVENT_INSTRUCTION_START               i\n");
}

void addCPLength(char *cp) {
    char *c;
    
    if (cp == NULL) {
        return;
    }

    cpSize++;
    c = cp;
    while (*c != '\0') {
        if (*c++ == ':') {
            cpSize++;
        }
    }
}

void addToClassPath(char *s, char **cp, int *index) {
    char *c;
    char *d;
    char *tmp;
    
    if (s == NULL) {
        return;
    }
    
    c = s;
    d = c + 1;
    while (*d != '\0') {
        if (*d == ':') {
            tmp = (char *) malloc(d - c + 1);
            strncpy(tmp, c, d - c);
            tmp[d - c] = '\0';
            cp[(*index)++] = tmp;
            c = d + 1;
        }
        d++;
    }

    if (d != c) {
        tmp = (char *) malloc(d - c + 1);
        strncpy(tmp, c, d - c);
        tmp[d - c] = '\0';
        cp[(*index)++] = tmp;
    }
}

void parseClassPath(char *additionalCP, boolean includeEnv) {
    int index = 0;
    char *envCP;
    
    cpSize = 0;
    cp = NULL;

    if ((additionalCP == NULL) && (!includeEnv)) {
        return;
    }
    
    /* Set the value of the CLASSPATH environment variable */
    envCP = (includeEnv ? getenv("CLASSPATH") : NULL);
    
    /* Compute the number of strings in CLASSPATH */
    addCPLength(additionalCP);
    addCPLength(envCP);

    /* Allocate memory for an array of cpSize strings */
    cp = NEW_ARRAY(char *, cpSize);

    addToClassPath(additionalCP, cp, &index);
    if (includeEnv) {
        addToClassPath(envCP, cp, &index);
    }
}

unsigned int intHash(jint i) {
    return (unsigned int) i;
}

void freeBytecode(bytecode_t *bc) {
    if (bc != NULL) {
        if (bc->code != NULL) {
            free(bc->code);
        }
        free(bc);
    }
}

int jintEq(jint i1, jint i2) {
    return (i1 == i2);
}

jint initTables() {
    int i;

    /* Byte order */
    needMSBConversion = !isMSB();
    if (needMSBConversion) {
        JShortMSB = JShortLSB2MSB;
        JIntMSB = JIntLSB2MSB;
        JLongMSB = JLongLSB2MSB;
    } else {
        JShortMSB = JShortMSB2MSB;
        JIntMSB = JIntMSB2MSB;
        JLongMSB = JLongMSB2MSB;
    }
    
    /* mark all events as not selected +
     * initialize all counters to 0 */
    for (i = 0; i <= ADAPTJ_MAX_EVENT; i++) {
        eventInfo[i] = ((jshort) 0);
        counters[i] = (jlong) 0;
    }

    /* Initialize the char <-> event conversion table and
     * the option <-> event conversion table */
    for (i = 0; i < 128; i++) {
        charToEvent[i] = ADAPTJ_INVALID_EVENT;
    }

    for (i = 0; i <= JVMPI_MAX_EVENT_TYPE_VAL; i++) {
        JVMPIToAdaptJEvent[i] = ADAPTJ_INVALID_EVENT;
    }

    for (i = 0; i <= ADAPTJ_MAX_EVENT; i++) {
        char c = (char) eventToChar[i];
        jint e  = adaptjEventToJVMPI[i];
        charToEvent[(int)c] = (AdaptJEvent) i;
        JVMPIToAdaptJEvent[e] = (AdaptJEvent) i;
    }

    if (initHashMap(&methodIDtoBytecode, 64, 0.7, intHash, jintEq, NULL,
            freeBytecode) != HASH_MAP_OK) {
        return JNI_ERR;
    }

    if (initIDSet(&knownObjectIDs, 128, 0.7) != ID_SET_OK) {
        return JNI_ERR;
    }

    if (initIDSet(&knownThreadIDs, 128, 0.7) != ID_SET_OK) {
        return JNI_ERR;
    }

    if (initIDSet(&knownMethodIDs, 128, 0.7) != ID_SET_OK) {
        return JNI_ERR;
    }

    if (initIDSetMap(&arenaIDtoObjects, 64, 0.7) != ID_SET_MAP_OK) {
        return JNI_ERR;
    }

    if (initIDSetMap(&classIDtoMethods, 128, 0.7) != ID_SET_MAP_OK) {
        return JNI_ERR;
    }

    return JNI_OK;
}

jint processOptions(char *optionString) {
    char opt[BUFF_SIZE];
    char val[BUFF_SIZE];
    char *p;
    char *q;
    boolean processedCP = false;
    boolean explicitOpt = false;
    
    /* Set default values */
    fileName[0] = '\0';
    optMode = true;
#ifdef ADAPTJ_ENABLE_PIPE
    pipeMode = false;
#endif
    verboseMode = true;
    threadStatus = false;
    cp = NULL;
    cpSize = 0;
    gcInterval = -1;
    
    if (optionString == NULL) {
        reportWarning("No event selected");
        return JNI_OK;
    }
    
    p = optionString;
    while (*p) {
        p = getNextOption(p, opt, val);
        if (!strcmp(opt, "file")) {
            /* FILE option */
            q = stripQuotes(val);
            if (q[0] == '\0') {
                reportError("missing file name");
                return JNI_ERR;
            }
            strcpy(fileName, q);
        } else if (!strcmp(opt, "specFile")) {
            q = stripQuotes(val);
            if (q[0] == '\0') {
                reportError("missing file name");
                return JNI_ERR;
            }
            if (AdaptJProcessSpecFile(q) != JNI_OK) {
                return JNI_ERR;
            }
        } else if (!strcmp(opt, "events")) {
            /* EVENTS option */
            processEventString(val, ADAPTJ_RECORD_ALL_FIELDS);
        } else if (!strcmp(opt, "counters")) {
            /* COUNTERS option */
            processEventString(val, ADAPTJ_FIELD_COUNTED);
        } else if (!strcmp(opt, "split")) {
            double d;
            char *p;
            
            q = stripQuotes(val);
            d = strtod(q, &p);
                
            if (!strcmp(p, "k")) {
                d *= 1024;
            } else if (!strcmp(p, "m")) {
                d *= 1024 * 1024;
            } else if (!strcmp(p, "g")) {
                d *= 1024 * 1024 * 1024;
            } else if (*p != '\0') {
                reportError2("Unrecognized size modifier: %s\n", p);
                return JNI_ERR;
            }

            splitThreshold = (int) d;
            showMessageInt("File size threshold: %d\n", splitThreshold);
#ifdef ADAPTJ_ENABLE_PIPE
        } else if (!strcmp(opt, "pipe")) {
            if (val[0] == '\0') {
                pipeMode = true;
                showMessage("Pipe Mode Enabled");
            } else {
                reportError("\"pipe\" option does not require a value");
                return JNI_ERR;
            }
#endif
        } else if (!strcmp(opt, "help")) {
                if (val[0] == '\0') {
                    printUsage();
                } else if (!strcmp(val, "events")) {
                    printEventChars();
                } else {
                    reportError2("Unrecognized help option: %s\n", val);
                    return JNI_ERR;
                }       
                jvmpi_interface->ProfilerExit((jint)0);
        } else if (!strcmp(opt, "quiet")) {
            if (val[0] == '\0') {
                verboseMode = false;
            } else {
                boolean b;
                if (!parseBoolean(val, &b)) {
                    return JNI_ERR;
                }

                verboseMode = !b;
            }
        } else if (!strcmp(opt, "opt")) {
            if (val[0] == '\0') {
                optMode = true;
            } else {
                boolean b;
                if (!parseBoolean(val, &b)) {
                    reportError2("Invalid boolean value: %s\n", val);
                    return JNI_ERR;
                }

                optMode = b;
            }
            explicitOpt = true;
        } else if (!strcmp(opt, "gc")) {
            double d;
            char *p;
            
            q = stripQuotes(val);
            d = strtod(q, &p);

            if (!strcmp(p, "k")) {
                d *= 1024;
            } else if (!strcmp(p, "m")) {
                d *= 1024 * 1024;
            } else if (!strcmp(p, "g")) {
                d *= 1024 * 1024 * 1024;
            } else if (*p != '\0') {
                reportError2("Unrecognized size modifier: %s\n", p);
                return JNI_ERR;
            }

            gcInterval = (int) d;

            showMessageInt("Will force GC after %d bytes allocated\n", gcInterval);
        } else if (!strcmp(opt, "cp+")) {
            parseClassPath(val, true);
            processedCP = true;
        } else if (!strcmp(opt, "cp")) {
            parseClassPath(val, false);
            processedCP = true;
        } else {
            reportError2("Unrecognized option: \"%s\"\n", opt);
            return JNI_ERR;
        }
    }

    if (!processedCP) {
        parseClassPath(NULL, true);
    }

#ifdef ADAPTJ_PRINT_CLASSPATH
    {
        int cpIndex;
        fprintf(stderr, "AdaptJ Agent> Debugging CLASSPATH Info Follows:\n");
        for (cpIndex = 0; cpIndex < cpSize; cpIndex++) {
            fprintf(stderr, "AdaptJ Agent> CP[%d] = %s\n", cpIndex, cp[cpIndex]);
        }
        fprintf(stderr, "AdaptJ Agent> Debugging CLASSPATH Info End\n");
    }
#endif
    
    initMap((const char **)cp, cpSize);
    threadStatus = eventInfo[ADAPTJ_THREAD_STATUS_CHANGE] & ADAPTJ_FIELD_RECORDED;
    if (threadStatus) {
        showMessage("Will record thread status");
    }

    if (optMode) {
        if (eventInfo[ADAPTJ_INSTRUCTION_START] & ADAPTJ_FIELD_RECORDED) {
            /* Make sure that we have the required events */
            short classFields = (short) (ADAPTJ_FIELD_RECORDED
                                | ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID
                                | ADAPTJ_FIELD_CLASS_NAME
                                | ADAPTJ_FIELD_METHODS);
            if ((eventInfo[ADAPTJ_CLASS_LOAD] & classFields) != classFields) {
                reportError("Optimization requires that the JVMPI_CLASS_LOAD be recorded with the appropriate fields\n");
                return JNI_ERR;
            } 
        } else {
            optMode = false;
            if (explicitOpt) {
                reportWarning("Optimization has been requested, but is useless when INSTRUCTION_START events are not recorded.");
                reportWarning("Will turn off optimization");
            }
        }
    }

    return JNI_OK;
}

jint openFile() {
    struct stat fileStat;
    
    if (fileName[0] == '\0') {
        reportWarning2("No file name specified, using default: \"%s\"\n", DEFAULT_FILE_NAME);
        strcpy(fileName, DEFAULT_FILE_NAME);
    }

    if (stat(fileName, &fileStat) >= 0) {
#ifdef ADAPTJ_ENABLE_PIPE
        if (S_ISFIFO(fileStat.st_mode)) {
            if (!pipeMode) {
                reportWarning2("File \"%s\" is a FIFO. Will switch to 'pipe' mode\n", fileName);
                pipeMode = true;
            }
        } else if (pipeMode) {
                reportError2("File \"%s\" already exists, but is not a FIFO\n", fileName);
                return JNI_ERR;
        } else {
#endif
            if (!S_ISREG(fileStat.st_mode)) {
                reportError2("File \"%s\" exists and is not a regular file\n", fileName);
                return JNI_ERR;
            }
#ifdef ADAPTJ_ENABLE_PIPE
        }
#endif
    }

#ifdef ADAPTJ_ENABLE_PIPE
    if (pipeMode && (mkfifo(fileName, 0600) == -1)) {
        if (errno != EEXIST) {
            perror("AdaptJ Agent> Pipe error");
            return JNI_ERR;
        }
        /*
        if (errno == EEXIST) {
            if (stat(fileName, &fileStat) == 0) {
                if (!S_ISFIFO(fileStat.st_mode)) {
                    reportError2("File \"%s\" already exists, but is not a FIFO\n", fileName);
                    return JNI_ERR;
                }
            } else {
                reportError("Unknown pipe error");
                return JNI_ERR;
            }
        } else {
            perror("Pipe error");
            return JNI_ERR;
        }
        */
    }
#endif
    
    outputFile = fopen(fileName, "wb");
    if (outputFile == NULL) {
        reportError2("Unable to open file \"%s\"\n", fileName);
        return JNI_ERR;
    }

    return JNI_OK;
}


jint writeHeader() {
    void *blank;
    jint blankSize;
    int numCounters = 0;
    jint magic;
    int i;
    jshort agentOptions = (jshort) 0;
    
    /* Calculate the number of counted events */
#ifdef ADAPTJ_ENABLE_PIPE
    if (!pipeMode) {
#endif
        for (i = 0; i <= ADAPTJ_MAX_EVENT; i++) {
            if (eventInfo[i] & ADAPTJ_FIELD_COUNTED) {
                numCounters++;
            }
        }
#ifdef ADAPTJ_ENABLE_PIPE
    }
#endif
    
    /* Write Magic string + version */
    magic = (jint)(ADAPTJ_MAGIC | ADAPTJ_VERSION);
    
    magic = JIntMSB(magic);
    fwrite(&magic, 4, 1, outputFile);
    
#ifdef ADAPTJ_ENABLE_PIPE    
    if (pipeMode) {
        agentOptions |= ADAPTJ_ISPIPED;
    }
#endif

    /* Write Agent Options */
    agentOptions = JShortMSB(agentOptions);
    fwrite(&agentOptions, sizeof(jshort), 1, outputFile);
    
    /* Leave space for option info and counters */
    /* Format is: option ID (AdaptJOption) + Info (jshort) [ + counter (jlong) ] */
#ifdef ADAPTJ_ENABLE_PIPE
    if (pipeMode) {
        AdaptJEvent e;
        byte byteBuff[(sizeof(AdaptJEvent) + sizeof(jshort)) * (ADAPTJ_MAX_EVENT + 1)];
        byte *b;
        size_t bLength = 0;

        b = byteBuff;
        for (e = 0; e <= ADAPTJ_MAX_EVENT; e++) {
            ADAPTJ_WRITE_BYTE(((byte)e), b, bLength);
            ADAPTJ_WRITE_JSHORT((eventInfo[e] & ~ADAPTJ_FIELD_COUNTED), b, bLength);
        }

        fwrite(byteBuff, 1, bLength, outputFile);
    } else {
#endif
        blankSize = (sizeof(AdaptJEvent) + sizeof(jshort)) * (ADAPTJ_MAX_EVENT + 1) + (sizeof(jlong) * numCounters);
        blank = calloc(blankSize, 1);
        fwrite(blank, 1, blankSize, outputFile);
        free(blank);

#ifdef ADAPTJ_ENABLE_PIPE
    }
#endif

    return JNI_OK;
}

jint enableEvents() {
    AdaptJEvent requiredEvents[] = {
        ADAPTJ_JVM_INIT_DONE,
        ADAPTJ_JVM_SHUT_DOWN
    };
    int numRequiredEvents = sizeof(requiredEvents);
    int i;

    /* Enable all requested events */
    for (i = 1; i <= ADAPTJ_MAX_EVENT; i++) {
        jshort s = eventInfo[i];
        if ((s & ADAPTJ_FIELD_RECORDED) || (s & ADAPTJ_FIELD_COUNTED)) {
            jint jvmpiEvent = adaptjEventToJVMPI[i];

            if (jvmpiEvent < 0) {
                continue;
            }
            if (jvmpi_interface->EnableEvent(jvmpiEvent, NULL) != JVMPI_SUCCESS) {
                reportWarning2("Failed to enable event: %s\n", eventNames[i]);
                if (i == ADAPTJ_JVM_SHUT_DOWN) {
                    return JNI_ERR;
                }
            } else {
                showMessage2("%s enabled\n", eventNames[i]);
            }
        }
    }

    /* Enable required events */
    for (i = 0; i < numRequiredEvents; i++) {
        AdaptJEvent j = requiredEvents[i];
        jshort s = eventInfo[j];
        s |= ADAPTJ_FIELD_REQUIRED;
        eventInfo[j] = s;
        if (!((s & ADAPTJ_FIELD_RECORDED) || (s & ADAPTJ_FIELD_COUNTED)) && (jvmpi_interface->EnableEvent(adaptjEventToJVMPI[j], NULL) != JVMPI_SUCCESS)) {
            reportWarning2("Failed to enable event: %s\n", eventNames[j]);
            return JNI_ERR;
        }
    }

    /* Other dependencies */
    /* FIXME eg GC for ObjFree, etc. */

    /* Agent Dependencies */
    /* Turn on instruction events if we must GC at specific intervals */
    i = ADAPTJ_INSTRUCTION_START;
    if ((gcInterval > 0) && !(eventInfo[i] & ADAPTJ_FIELD_RECORDED)) {
        eventInfo[i] |= ADAPTJ_FIELD_REQUIRED;
        if (jvmpi_interface->EnableEvent(adaptjEventToJVMPI[i], NULL) != JVMPI_SUCCESS) {
            reportWarning2("Failed to enable event: %s\n", eventNames[i]);
            return JNI_ERR;
        }
    }
    
    return JNI_OK;
}

jint AdaptJInit(char *optionString) {
    if (initTables() != JNI_OK) {
        reportError("Failed to initialize tables");
        return JNI_ERR;
    }

    if (processOptions(optionString) != JNI_OK) {
        reportError("Failed to process options");
        return JNI_ERR;
    }
    
    if (openFile() != JNI_OK) {
        reportError("Failed to open file");
        return JNI_ERR;
    }

    if (writeHeader() != JNI_OK) {
        reportError("Failed to write file header");
        fclose(outputFile);
        return JNI_ERR;
    }

    if (enableEvents() != JNI_OK) {
        fclose(outputFile);
        reportError("Failed to enable JVMPI events");
        return JNI_ERR;
    }
    
    showMessage("Initialization completed");

    return JNI_OK; 
}
