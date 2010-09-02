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

package adaptj_pool.toolkits.transformers;

import adaptj_pool.Scene;
import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.toolkits.*;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.util.text.OptionStringParser;
import adaptj_pool.util.text.HelpDisplayManager;
import java.util.*;

import adaptj_pool.Scene;

public class EventDistiller extends EventOperation {
    String[] excludes;
    String[] includes;
    List listExcludes;
    List listIncludes;

    public static boolean isStandardLib(String className) {
        if (className == null) {
            return false;
        }
        return (className.startsWith("[")
                || className.startsWith("java.")
                || className.startsWith("javax.")
                || className.startsWith("sun.")
                || className.startsWith("com.sun.")
                || className.startsWith("com.ibm.")
                || className.startsWith("org.xml.")
                || className.startsWith("org.w3c.")
                || className.startsWith("org.apache."));
    }

    public static boolean isStandardLib(AdaptJEvent event) {
        if (event instanceof MethodEvent) {
            MethodEvent e = (MethodEvent) event;
            
            MethodInfo info = IDResolver.v().getMethodInfo(e.getMethodID());
            return (info != null ? info.isStandardLib() : false);
            /*return isStandardLib(e.getMethodID());*/
        }
        
        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_CLASS_LOAD:
                {
                    ClassLoadEvent e = (ClassLoadEvent) event;
                    ClassInfo info = IDResolver.v().getClassInfo(e.getClassID());
                    return (info != null ? info.isStandardLib() : false);
                    //return isStandardLib(e.getClassName());
                }
            case AdaptJEvent.ADAPTJ_CLASS_UNLOAD:
                {
                    ClassUnloadEvent e = (ClassUnloadEvent) event;
                    ClassInfo info = IDResolver.v().getClassInfo(e.getClassID());
                    return (info != null ? info.isStandardLib() : false);
                    /*
                    String s = (cinfo != null ? cinfo.getName() : null);
                    return isStandardLib(s);
                    */
                }
            case AdaptJEvent.ADAPTJ_OBJECT_ALLOC:
                {
                    ObjectAllocEvent e = (ObjectAllocEvent) event;
                    ClassInfo info = IDResolver.v().getClassInfo(e.getClassID());
                    return (info != null ? info.isStandardLib() : false);
                    /*
                    String s = (cinfo != null ? cinfo.getName() : null);
                    return isStandardLib(s);
                    */
                }
            default:
                break;
        }  

        return false;
    }

    public static boolean isStandardLib(int methodID) {
        MethodInfo info = IDResolver.v().getMethodInfo(methodID);
        return (info != null ? info.isStandardLib() : false);
        
        /*
        if (info == null) {
            return false;
        } else {
            ObjectInfo obj = info.getKlass();
            if (obj == null) { 
                return false;
            }
            
            ClassInfo declaredClassInfo = obj.getDeclaredClass();
            ClassInfo associatedClassInfo = obj.getAssociatedClass();
            if (associatedClassInfo != null) {
                return isStandardLib(associatedClassInfo.getName());
            }
            
            return (declaredClassInfo != null && isStandardLib(declaredClassInfo.getName()));
        }
        */
    }

    public EventDistiller(String name, String description) {
        super(name, description);
        excludes = null;
        includes = null;
        listIncludes = new ArrayList();
        listExcludes = new ArrayList();
    }

    public void setOption(String name, String value) {
        if (name.equals("exclude")) {
            if (!listExcludes.contains(value)) {
                listExcludes.add(value);
            }
        } else if (name.equals("include")) {
            if (!listIncludes.contains(value)) {
                listIncludes.add(value);
            }

        } else if (name.equals("excludeLib")) {
            if (OptionStringParser.parseBoolean(value)) {
                listExcludes.add("[");
                listExcludes.add("java.");
                listExcludes.add("javax.");
                listExcludes.add("sun.");
                listExcludes.add("com.sun.");
            }
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) {
        if (name.equals("excludes")) {
            return listExcludes.toString();
        } else if (name.equals("includes")) {
            return listIncludes.toString();
        } else {
            return super.getOption(name);
        }
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);
        
        manager.displayOptionHelp("exclude:<string>", "Specifies that events corresponding to classes whose names start with <string> are to be filtered out");
        manager.displayOptionHelp("include:<string>", "Specifies that events corresponding to classes whose names start with <string> are to be passed on to subsequent operations");
    }

    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_CLASS_LOAD,
            AdaptJEvent.ADAPTJ_CLASS_UNLOAD,
            AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD,
            AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD,
            AdaptJEvent.ADAPTJ_METHOD_ENTRY,
            AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
            AdaptJEvent.ADAPTJ_METHOD_EXIT,
            AdaptJEvent.ADAPTJ_INSTRUCTION_START,
            AdaptJEvent.ADAPTJ_OBJECT_ALLOC
        };

        return events;
    }
    */

    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_LOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_NAME,
                                false),

            new EventDependency(AdaptJEvent.ADAPTJ_CLASS_UNLOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_CLASS_UNLOAD_CLASS_ID,
                                false),
            new EventDependency(AdaptJEvent.ADAPTJ_COMPILED_METHOD_LOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID,
                                false),
            new EventDependency(AdaptJEvent.ADAPTJ_COMPILED_METHOD_UNLOAD,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID,
                                false),
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY,
                                AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID,
                                false),
            /*
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID,
                                false),
            */
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_EXIT,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID,
                                false),
            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID,
                                false),
            new EventDependency(AdaptJEvent.ADAPTJ_OBJECT_ALLOC,
                                AdaptJSpecConstants.ADAPTJ_FIELD_OBJECT_ALLOC_CLASS_ID,
                                false)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER};
        return deps;
    }

    public void doPreInit() {
        if (listExcludes.size() <= 0) {
            setEnabled(false);
        }
    }

    public void doInit() {
        /* We have at least one thing to exclude, otherwise
         * doPreInit() would have disabled us */
        excludes = new String[listExcludes.size()];
        listExcludes.toArray(excludes);

        if (listIncludes.size() > 0) {
            includes = new String[listIncludes.size()];
            listIncludes.toArray(includes);
        }
    }

    public void doApply(EventBox box) {
        boolean remove = false;

        AdaptJEvent event = box.getEvent();
        if (event instanceof ClassLoadEvent) {
            ClassLoadEvent e = (ClassLoadEvent) event;
            remove = excluded(e.getClassName());
        } else if (event instanceof ClassUnloadEvent) {
            ClassUnloadEvent e = (ClassUnloadEvent) event;
            ClassInfo cinfo = IDResolver.v().getClassInfo(e.getClassID());
            String s = (cinfo != null ? cinfo.getName() : null);
            remove = excluded(s);
        } else if (event instanceof MethodEvent) {
            MethodEvent e = (MethodEvent) event;
            remove = excluded(e.getMethodID());
        } else if (event instanceof ObjectAllocEvent) {
            ObjectAllocEvent e = (ObjectAllocEvent) event;
            ClassInfo cinfo = IDResolver.v().getClassInfo(e.getClassID());
            String s = (cinfo != null ? cinfo.getName() : null);
            remove = excluded(s);
        }  

        if (remove) {
            box.remove(); // This will stop the processing chain here and
                          // move to the next event
                          // NOTE: remove() throws an exception, so do
                          //       not add code after the invocation,
                          //       or it will never be executed.
        }
    }

    private boolean excluded(int methodID) {
        MethodInfo info = IDResolver.v().getMethodInfo(methodID);
        if (info == null) {
            /* Can't exclude what we don't know */
            return false;
        } else {
            ObjectInfo obj = info.getKlass();
            if (obj == null) { 
                /* Can't exclude what we don't know */
                return false;
            }
            
            ClassInfo declaredClassInfo = obj.getDeclaredClass();
            ClassInfo associatedClassInfo = obj.getAssociatedClass();

            if (associatedClassInfo != null) {
                return excluded(associatedClassInfo.getName());
            }
            
            return (declaredClassInfo != null && excluded(declaredClassInfo.getName()));
        }
    }

    private boolean excluded(String className) {
        boolean exclude = false;
    
        if (className == null) {
            return exclude;
        }

        /* Check the exclude list for a match */
        if (excludes != null) {
            for (int i = 0; i < excludes.length; i++) {
                if (className.startsWith(excludes[i])) {
                    exclude = true;
                    break;
                }
            }
        }
        /***
        Iterator exclIt = excludes.iterator();
        while (exclIt.hasNext()) {
            String s = (String) exclIt.next();
            if (className.startsWith(s)) {
                exclude = true;
            }
        }
        ***/

        /* If we have a match, check the include list for a match
           to override the exclude option */
        if (exclude && includes != null) {
            for (int i = 0; i < includes.length; i++) {
                if (className.startsWith(includes[i])) {
                    exclude = false;
                    break;
                }
            }
            /***
            while (inclIt.hasNext()) {
                String s = (String) inclIt.next();
                if (className.startsWith(s)) {
                    exclude = false;
                    break;
                }
            }
            ***/
        }

        return exclude;
    }
}
