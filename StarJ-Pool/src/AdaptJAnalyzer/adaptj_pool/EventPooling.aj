package adaptj_pool;

import adaptj_pool.event.*;

public aspect EventPooling pertarget(event_alloc(int)) {
    private AdaptJEvent[][] eventPool;
    private int current_pool;
    
    public EventPooling() {
        this.initializeEventPool();
        this.current_pool = 0;
    }
    
    private void initializeEventPool() {
        this.eventPool = new AdaptJEvent[AdaptJEvent.ADAPTJ_EVENT_COUNT][2];

        eventPool[AdaptJEvent.ADAPTJ_ARENA_DELETE][0]                  = new ArenaDeleteEvent();
        eventPool[AdaptJEvent.ADAPTJ_ARENA_NEW][0]                     = new ArenaNewEvent();
        eventPool[AdaptJEvent.ADAPTJ_CLASS_LOAD][0]                    = new ClassLoadEvent();
        eventPool[AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK][0]               = null; //new ClassLoadHookEvent();
        eventPool[AdaptJEvent.ADAPTJ_CLASS_UNLOAD][0]                  = new ClassUnloadEvent();
        eventPool[AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD][0]          = new CompiledMethodLoadEvent();
        eventPool[AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD][0]        = new CompiledMethodUnloadEvent();
        eventPool[AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST][0]             = null; //new DataDumpRequestEvent();
        eventPool[AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST][0]            = null; //new DataResetRequestEvent();
        eventPool[AdaptJEvent.ADAPTJ_GC_FINISH][0]                     = new GCFinishEvent();
        eventPool[AdaptJEvent.ADAPTJ_GC_START][0]                      = new GCStartEvent();
        eventPool[AdaptJEvent.ADAPTJ_HEAP_DUMP][0]                     = null; //new HeapDumpEvent();
        eventPool[AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC][0]           = null; //new JNIGlobalRefAllocEvent();
        eventPool[AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE][0]            = null; //new JNIGlobalRefFreeEvent();
        eventPool[AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC][0]      = null; //new JNIWeakGlobalRefAllocEvent();
        eventPool[AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE][0]       = null; //new JNIWeakGlobalRefFreeEvent();
        eventPool[AdaptJEvent.ADAPTJ_JVM_INIT_DONE][0]                 = new JVMInitDoneEvent();
        eventPool[AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN][0]                 = new JVMShutDownEvent();
        eventPool[AdaptJEvent.ADAPTJ_METHOD_ENTRY][0]                  = new MethodEntryEvent();
        eventPool[AdaptJEvent.ADAPTJ_METHOD_ENTRY2][0]                 = new MethodEntry2Event();
        eventPool[AdaptJEvent.ADAPTJ_METHOD_EXIT][0]                   = new MethodExitEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER][0]       = new MonitorContendedEnterEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED][0]     = new MonitorContendedEnteredEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT][0]        = new MonitorContendedExitEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_DUMP][0]                  = new MonitorDumpEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_WAIT][0]                  = new MonitorWaitEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_WAITED][0]                = new MonitorWaitedEvent();
        eventPool[AdaptJEvent.ADAPTJ_OBJECT_ALLOC][0]                  = new ObjectAllocEvent();
        eventPool[AdaptJEvent.ADAPTJ_OBJECT_DUMP][0]                   = new ObjectDumpEvent();
        eventPool[AdaptJEvent.ADAPTJ_OBJECT_FREE][0]                   = new ObjectFreeEvent();
        eventPool[AdaptJEvent.ADAPTJ_OBJECT_MOVE][0]                   = new ObjectMoveEvent();
        eventPool[AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER][0]   = new RawMonitorContendedEnterEvent();
        eventPool[AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED][0] = new RawMonitorContendedEnteredEvent();
        eventPool[AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT][0]    = new RawMonitorContendedExitEvent();
        eventPool[AdaptJEvent.ADAPTJ_THREAD_END][0]                    = new ThreadEndEvent();
        eventPool[AdaptJEvent.ADAPTJ_THREAD_START][0]                  = new ThreadStartEvent();
        eventPool[AdaptJEvent.ADAPTJ_INSTRUCTION_START][0]             = new InstructionStartEvent();
        eventPool[AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE][0]          = new ThreadStatusChangeEvent();

        eventPool[AdaptJEvent.ADAPTJ_ARENA_DELETE][1]                  = new ArenaDeleteEvent();
        eventPool[AdaptJEvent.ADAPTJ_ARENA_NEW][1]                     = new ArenaNewEvent();
        eventPool[AdaptJEvent.ADAPTJ_CLASS_LOAD][1]                    = new ClassLoadEvent();
        eventPool[AdaptJEvent.ADAPTJ_CLASS_LOAD_HOOK][1]               = null; //new ClassLoadHookEvent();
        eventPool[AdaptJEvent.ADAPTJ_CLASS_UNLOAD][1]                  = new ClassUnloadEvent();
        eventPool[AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD][1]          = new CompiledMethodLoadEvent();
        eventPool[AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD][1]        = new CompiledMethodUnloadEvent();
        eventPool[AdaptJEvent.ADAPTJ_DATA_DUMP_REQUEST][1]             = null; //new DataDumpRequestEvent();
        eventPool[AdaptJEvent.ADAPTJ_DATA_RESET_REQUEST][1]            = null; //new DataResetRequestEvent();
        eventPool[AdaptJEvent.ADAPTJ_GC_FINISH][1]                     = new GCFinishEvent();
        eventPool[AdaptJEvent.ADAPTJ_GC_START][1]                      = new GCStartEvent();
        eventPool[AdaptJEvent.ADAPTJ_HEAP_DUMP][1]                     = null; //new HeapDumpEvent();
        eventPool[AdaptJEvent.ADAPTJ_JNI_GLOBALREF_ALLOC][1]           = null; //new JNIGlobalRefAllocEvent();
        eventPool[AdaptJEvent.ADAPTJ_JNI_GLOBALREF_FREE][1]            = null; //new JNIGlobalRefFreeEvent();
        eventPool[AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_ALLOC][1]      = null; //new JNIWeakGlobalRefAllocEvent();
        eventPool[AdaptJEvent.ADAPTJ_JNI_WEAK_GLOBALREF_FREE][1]       = null; //new JNIWeakGlobalRefFreeEvent();
        eventPool[AdaptJEvent.ADAPTJ_JVM_INIT_DONE][1]                 = new JVMInitDoneEvent();
        eventPool[AdaptJEvent.ADAPTJ_JVM_SHUT_DOWN][1]                 = new JVMShutDownEvent();
        eventPool[AdaptJEvent.ADAPTJ_METHOD_ENTRY][1]                  = new MethodEntryEvent();
        eventPool[AdaptJEvent.ADAPTJ_METHOD_ENTRY2][1]                 = new MethodEntry2Event();
        eventPool[AdaptJEvent.ADAPTJ_METHOD_EXIT][1]                   = new MethodExitEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTER][1]       = new MonitorContendedEnterEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_ENTERED][1]     = new MonitorContendedEnteredEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_CONTENDED_EXIT][1]        = new MonitorContendedExitEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_DUMP][1]                  = new MonitorDumpEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_WAIT][1]                  = new MonitorWaitEvent();
        eventPool[AdaptJEvent.ADAPTJ_MONITOR_WAITED][1]                = new MonitorWaitedEvent();
        eventPool[AdaptJEvent.ADAPTJ_OBJECT_ALLOC][1]                  = new ObjectAllocEvent();
        eventPool[AdaptJEvent.ADAPTJ_OBJECT_DUMP][1]                   = new ObjectDumpEvent();
        eventPool[AdaptJEvent.ADAPTJ_OBJECT_FREE][1]                   = new ObjectFreeEvent();
        eventPool[AdaptJEvent.ADAPTJ_OBJECT_MOVE][1]                   = new ObjectMoveEvent();
        eventPool[AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTER][1]   = new RawMonitorContendedEnterEvent();
        eventPool[AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_ENTERED][1] = new RawMonitorContendedEnteredEvent();
        eventPool[AdaptJEvent.ADAPTJ_RAW_MONITOR_CONTENDED_EXIT][1]    = new RawMonitorContendedExitEvent();
        eventPool[AdaptJEvent.ADAPTJ_THREAD_END][1]                    = new ThreadEndEvent();
        eventPool[AdaptJEvent.ADAPTJ_THREAD_START][1]                  = new ThreadStartEvent();
        eventPool[AdaptJEvent.ADAPTJ_INSTRUCTION_START][1]             = new InstructionStartEvent();
        eventPool[AdaptJEvent.ADAPTJ_THREAD_STATUS_CHANGE][1]          = new ThreadStatusChangeEvent();
    }

    private pointcut event_alloc(int id):
        call(AdaptJEvent IAEFReader+.newEvent(int)) && args(id);

    AdaptJEvent around(int id): event_alloc(id) {
        AdaptJEvent event = this.eventPool[id][this.current_pool];
        this.current_pool = 1 - this.current_pool;
        if (event == null) {
            throw new RuntimeException("Invalid event ID: " + id);
        }

        return event;
    }
}
