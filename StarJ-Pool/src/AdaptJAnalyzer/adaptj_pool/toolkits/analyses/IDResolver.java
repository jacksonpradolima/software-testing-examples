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

package adaptj_pool.toolkits.analyses;

import adaptj_pool.event.*;
import adaptj_pool.JVMPI.*;
import adaptj_pool.toolkits.*;
import adaptj_pool.toolkits.types.*;
import adaptj_pool.util.*;
import java.util.*;
import adaptj_pool.spec.AdaptJSpecConstants;

import it.unimi.dsi.fastUtil.*;

import adaptj_pool.Scene;

public class IDResolver extends EventOperation {
    private final static int CACHE_SIZE = 32; // NB: This has to be a power of 2!
    private final static int CACHE_MASK = 0x0000001F; // Keep 5 bits (2^5 = 32)
    
    private static IDResolver instance = new IDResolver();
    private IntHashMap idToArenaInfo;
    private IntHashMap idToObjectInfo;
    private IntHashMap idToMethodInfo;
    private IntHashMap idToMethodEntity;

    private int[] cachedMethodIDs;
    private ClassInfo[] cachedClassInfos;

    /*
    private Int2ObjectHashMap idToArenaInfo;
    private Int2ObjectHashMap idToObjectInfo;
    private Int2ObjectHashMap idToMethodInfo;
    private Int2ObjectHashMap idToMethodEntity;
    */
    
    private IDResolver() {
        super("IDResolver", "Keeps track of the JVMPI IDs and provides services for other operations");
        doInit();
    }

    public static IDResolver v() {
        return instance;
    }

    /*
    public int[] registerEvents() {
        int [] events = {
            AdaptJEvent.ADAPTJ_CLASS_LOAD,
            AdaptJEvent.ADAPTJ_CLASS_UNLOAD,
            AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
            AdaptJEvent.ADAPTJ_OBJECT_MOVE,
            AdaptJEvent.ADAPTJ_OBJECT_FREE,
            AdaptJEvent.ADAPTJ_ARENA_NEW,
            AdaptJEvent.ADAPTJ_ARENA_DELETE
        };

        return events;
    }
    */

    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_LOAD, 
                                AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_LOAD_CLASS_ID 
                                | AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_NAME
                                | AdaptJSpecConstants.ADAPTJ_FIELD_METHODS,
                                false),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_UNLOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID,
                                false),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ARENA_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OBJ_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_IS_ARRAY
                                | AdaptJSpecConstants.ADAPTJ_FIELD_SIZE
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID,
                                false),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_MOVE,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ARENA_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OBJ_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_NEW_ARENA_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_NEW_OBJ_ID,
                                false),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_FREE,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OBJ_ID,
                                false),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_ARENA_NEW,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ARENA_ID,
                                false),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_ARENA_DELETE,
                                AdaptJSpecConstants.ADAPTJ_FIELD_ARENA_ID,
                                false)
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        return null;
    }
    
    public void doInit() {
        idToObjectInfo = new IntHashMap(1024*1024, "idToObjectInfo");
        idToMethodInfo = new IntHashMap(1024*1024, "idToMethodInfo");
        idToMethodEntity = new IntHashMap(1024*1024, "idToMethodEntity");
        idToArenaInfo = new IntHashMap(1024*1024, "idToArenaInfo");
        
        /*
        idToObjectInfo = new Int2ObjectHashMap();
        idToMethodInfo = new Int2ObjectHashMap();
        idToMethodEntity = new Int2ObjectHashMap();
        idToArenaInfo = new Int2ObjectHashMap();
        */

        cachedMethodIDs = new int[CACHE_SIZE];
        cachedClassInfos = new ClassInfo[CACHE_SIZE];
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_CLASS_LOAD: {
                    ClassLoadEvent e = (ClassLoadEvent) event;

                    int class_id = e.getClassID();
                    boolean requested = e.isRequested();
                    
                    ClassInfo prevCInfo = getClassInfo(class_id);
                    if (prevCInfo != null) {
                        if (requested) {
                            /* This event was requested, and thus should not overwrite the
                               previous information */
                            box.remove();
                        }

                        if (prevCInfo.getLoadedCount() == 0) {
                            /* All previous occurences of this event were requested ones,
                               so this is the first 'real' occurence. Do not overwrite. */

                            prevCInfo.stepLoadedCount();
                            box.remove();
                        }
                    } 
                    
                    MethodInfo methods[] = null;
                    int num_methods = e.getNumMethods();
                    if (num_methods > 0) {
                        methods = new MethodInfo[num_methods];
                    }

                    ClassInfo cinfo = new ClassInfo(e.getClassName(),
                                                    e.getSourceName(),
                                                    methods,
                                                    requested);

                    if (!requested) {
                        cinfo.stepLoadedCount();
                    }
                    
                    ObjectInfo obj = getObjectInfo(class_id);
                    if (obj != null) {
                        obj.setAssociatedClass(cinfo);
                    } else {
                        if (Scene.v().isTypeDumped(AdaptJEvent.ADAPTJ_OBJECT_ALLOC)) {
                            Scene.v().showWarning("No prior Class object defined");
                        }

                        obj = new ObjectInfo(class_id, null, null, cinfo, false, 0, requested);
                        idToObjectInfo.put(class_id, obj);
                    }

                    boolean isStandardLib = cinfo.isStandardLib();

                    if (num_methods > 0) {
                        JVMPIMethod m;
                        for (int i = 0; i < num_methods; i++) {
                            m = e.getMethod(i);
                            int method_id = m.getMethodID();
                            MethodInfo minfo = new MethodInfo(method_id,
                                                              obj,
                                                              m.getMethodName(),
                                                              m.getMethodSignature());
                            methods[i] = minfo;
                            idToMethodInfo.put(method_id, minfo);
                            idToMethodEntity.put(method_id, buildMethodEntityFromInfo(minfo));

                            int key = (method_id >> 4) & CACHE_MASK;
                            if (method_id == cachedMethodIDs[key] && method_id != 0) {
                                cachedMethodIDs[key] = 0;
                                cachedClassInfos[key] = null;
                            }
                        }
                    }
                }
                break;
            case AdaptJEvent.ADAPTJ_CLASS_UNLOAD: {
                    ClassUnloadEvent e = (ClassUnloadEvent) event;
                    deleteClass(e.getClassID());
                }
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC: {
                    ObjectAllocEvent e = (ObjectAllocEvent) event;

                    int obj_id = e.getObjID();
                    int is_array = e.getIsArray();
                    boolean requested = e.isRequested();

                    ObjectInfo prevObjInfo = getObjectInfo(obj_id);
                    if (prevObjInfo != null) {
                        if (requested) {
                            /* This event was requested, and thus should not overwrite the
                               previous information */
                            box.remove();
                        }

                        if (prevObjInfo.getInstanciatedCount() == 0) {
                            /* All previous occurences of this event were requested ones,
                               so this is the first 'real' occurence. Do not overwrite. */

                            prevObjInfo.stepInstanciatedCount();
                            box.remove();
                        }
                    }
                    
                    ArenaInfo arena = getArenaInfo(e.getArenaID());
                    ObjectInfo info = null;
                    if (is_array == ObjectAllocEvent.NORMAL_OBJECT
                            || is_array == ObjectAllocEvent.OBJECT_ARRAY) {
                        ObjectInfo klass = getObjectInfo(e.getClassID());
                        info = new ObjectInfo(obj_id,
                                              arena,
                                              (klass != null ? klass.getAssociatedClass() : null),
                                              null,
                                              is_array == ObjectAllocEvent.OBJECT_ARRAY,
                                              e.getSize(),
                                              requested);
                    } else {
                        info = new ObjectInfo(obj_id,
                                              arena,
                                              is_array,
                                              e.getSize(),
                                              requested);
                    }

                    if (!requested) {
                        info.stepInstanciatedCount();
                    }
                    if (arena != null) {
                        arena.add(info);
                    }
                    idToObjectInfo.put(obj_id, info);
                }
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_MOVE: {
                    ObjectMoveEvent e = (ObjectMoveEvent) event;
                    
                    int obj_id = e.getObjID();
                    int new_obj_id = e.getNewObjID();

                    ObjectInfo obj = getObjectInfo(obj_id);
                    if (obj != null) {
                        obj.moveTo(new_obj_id, getArenaInfo(e.getNewArenaID()));
                        idToObjectInfo.remove(obj_id);
                        idToObjectInfo.put(new_obj_id, obj);
                        TypeRepository.v().moveObject(obj_id, new_obj_id);
                    }
                }
                break;
            case AdaptJEvent.ADAPTJ_OBJECT_FREE: {
                    ObjectFreeEvent e = (ObjectFreeEvent) event;
                    int obj_id = e.getObjID();
                    idToObjectInfo.remove(obj_id);
                    TypeRepository.v().invalidateObjectID(obj_id);
                }
                break;
            case AdaptJEvent.ADAPTJ_ARENA_NEW: {
                    ArenaNewEvent e = (ArenaNewEvent) event;
                    
                    int arena_id = e.getArenaID();
                    idToArenaInfo.put(arena_id,
                                      new ArenaInfo(arena_id, e.getArenaName()));
                }
                break;
            case AdaptJEvent.ADAPTJ_ARENA_DELETE: {
                    ArenaDeleteEvent e = (ArenaDeleteEvent) event;
                    deleteArena(e.getArenaID());
                }
                break;
            default:
                break;
        }
    }

    private void deleteClass(int classID) {
        ObjectInfo info = (ObjectInfo) idToObjectInfo.get(classID);
        ClassInfo cinfo;
        if (info != null && (cinfo = info.getAssociatedClass()) != null) {
            MethodInfo methods[] = cinfo.getMethods();
            if (methods != null) {
                for (int i = 0; i < methods.length; i++) {
                    int mID = methods[i].getID();
                    idToMethodInfo.remove(mID);
                    idToMethodEntity.remove(mID);
                }
            }
        }
        TypeRepository.v().invalidateObjectID(classID);
        idToObjectInfo.remove(classID);
    }

    private void deleteArena(int arenaID) {
        ArenaInfo info = (ArenaInfo) idToArenaInfo.get(arenaID);
        if (info != null) {
            Set s = info.getObjects();
            Iterator it = s.iterator();
            while (it.hasNext()) {
                ObjectInfo obj = (ObjectInfo) it.next();
                int obj_id = obj.getID();
                TypeRepository.v().invalidateObjectID(obj_id);
                idToObjectInfo.remove(obj_id);
            }
        }
        idToArenaInfo.remove(arenaID);
    }

    public ArenaInfo getArenaInfo(int arenaID) {
        return (ArenaInfo) idToArenaInfo.get(arenaID);
    }

    public ObjectInfo getObjectInfo(int objectID) {
        return (ObjectInfo) idToObjectInfo.get(objectID);
    }

    public MethodInfo getMethodInfo(int methodID) {
        return (MethodInfo) idToMethodInfo.get(methodID);
    }

    public ArenaInfo getArenaInfo(Integer arenaID) {
        return (ArenaInfo) idToArenaInfo.get(arenaID.intValue());
    }

    public ObjectInfo getObjectInfo(Integer objectID) {
        return (ObjectInfo) idToObjectInfo.get(objectID.intValue());
    }

    public MethodInfo getMethodInfo(Integer methodID) {
        return (MethodInfo) idToMethodInfo.get(methodID.intValue());
    }

    /* Utility Functions */
    public MethodEntity getMethodEntity(int methodID) {
        return (MethodEntity) idToMethodEntity.get(methodID);
        /*return getMethodEntity(getMethodInfo(methodID));*/
    }

    public MethodEntity getMethodEntity(Integer methodID) {
        return (MethodEntity) idToMethodEntity.get(methodID.intValue());
        /*return getMethodEntity(getMethodInfo(methodID));*/
    }

    //public MethodEntity getMethodEntity(MethodInfo m) {
    //    return buildMethodEntityFromInfo(m);
        /*
        MethodInfo minfo = m;
        if (minfo == null) {
            return null;
        }       
        ObjectInfo klass = minfo.getKlass();
        if (klass == null) {
            return null;
        }
        ClassInfo info = klass.getKlass();
        if (info == null) {
            return null;
        }
        return new MethodEntity(info.getName(),
                                  minfo.getName(),
                                  minfo.getSignature());
        */
    //}
    
    private MethodEntity buildMethodEntityFromInfo(MethodInfo m) {
        if (m == null) {
            return null;
        }       
        ObjectInfo klass = m.getKlass();
        if (klass == null) {
            return null;
        }
        ClassInfo info = klass.getAssociatedClass();
        if (info == null) {
            return null;
        }
        return new MethodEntity(info.getName(),
                                  m.getName(),
                                  m.getSignature(),
                                  info.isStandardLib());
    }
    
    public ClassInfo getClassInfo(int classID) {
        ObjectInfo info = getObjectInfo(classID);
        if (info == null) {
            return null;
        }

        return info.getAssociatedClass(); 
    }

    public ClassInfo getClassInfo(Integer classID) {
        return getClassInfo(classID.intValue());
        
    }
}
