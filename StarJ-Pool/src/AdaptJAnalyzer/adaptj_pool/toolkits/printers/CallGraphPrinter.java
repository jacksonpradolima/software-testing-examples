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

/* ========================================================================== *
 *                                   AdaptJ                                   *
 *              A Dynamic Application Profiling Toolkit for Java              *
 *                                                                            *
 *  Copyright (C) 2003-2004 Ondrej Lhotak                                     *
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

package adaptj_pool.toolkits.printers;

import adaptj_pool.Scene;
import adaptj_pool.util.*;
import adaptj_pool.util.text.OptionStringParser;
import adaptj_pool.toolkits.analyses.*;
import adaptj_pool.event.*;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.spec.AdaptJSpecConstants;
import java.util.*;
import it.unimi.dsi.fastUtil.*;

public class CallGraphPrinter extends DefaultEventPrinter {
    private Int2ObjectOpenHashMap stacks = new Int2ObjectOpenHashMap();
    private Object2ObjectOpenHashMap edges = new Object2ObjectOpenHashMap();
    private Object2IntOpenHashMap me2id = new Object2IntOpenHashMap();
    boolean dot = false;

    public CallGraphPrinter(String name, String description) {
        super(name, description, "AdaptJ.callgraph");
    }

    public CallGraphPrinter(String name, String description, String fileName) {
        super(name, description, fileName);
    }
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY,
                                AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                (AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID),
                                true),
                                
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_EXIT,
                                (AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID),
                                true)

            /*
            new EventDependency(AdaptJEvent.ADAPTJ_METHOD_ENTRY2,
                                (AdaptJSpecConstants.ADAPTJ_FIELD_ENV_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID),
                                false)
            */
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        String[] deps = {Scene.ID_RESOLVER};

        return deps;
    }

    public void doInit() {
        super.doInit();
    }
    
    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        
        switch (event.getTypeID()) {
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY:
            case AdaptJEvent.ADAPTJ_METHOD_ENTRY2:
                {
                    MethodEvent e = (MethodEvent) event;

                    MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());

                    if( me == null ) break;

                    me2id.put( me, e.getMethodID() );

                    int thread = event.getEnvID();

                    LinkedList stack = (LinkedList) stacks.get( thread );
                    if( stack == null ) {
                        stack = new LinkedList();
                        stacks.put( thread, stack );
                    }

                    if( !stack.isEmpty() ) {
                        MethodEntity top = (MethodEntity) stack.getLast();
                        ObjectOpenHashSet targets =
                            (ObjectOpenHashSet) edges.get( top );
                        if( targets == null ) {
                            targets = new ObjectOpenHashSet();
                            edges.put( top, targets );
                        }
                        targets.add( me );
                    }

                    stack.addLast( me );
                }
                break;
            case AdaptJEvent.ADAPTJ_METHOD_EXIT:
                {
                    MethodExitEvent e = (MethodExitEvent) event;

                    MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());

                    if( me == null ) break;

                    me2id.put( me, e.getMethodID() );

                    int thread = e.getEnvID();

                    LinkedList stack = (LinkedList) stacks.get( thread );
                    if( stack == null ) {
                        stack = new LinkedList();
                        stacks.put( thread, stack );
                    }

                    if( stack.isEmpty() ) {
                        System.out.println( "Stack is empty when exiting method "+me );
                        break;
                    }
                    MethodEntity top = (MethodEntity) stack.removeLast();
                    if( !top.equals( me ) ) {
                        System.out.println( "Stack is "+stack );
                        throw new RuntimeException( "Exiting method "+me+
                                " but top of stack is "+top );
                    }
                }
                break;
            default:
                break;
        }        
    }

    public void setOption(String name, String value) {
        if (name.equals("dot")) {
            dot = OptionStringParser.parseBoolean(value);
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) {
        if (name.equals("dot")) {
            return (dot ? "true" : "false");
        } else {
            return super.getOption(name);
        }
    }

    public void doDone() {
        if( dot ) {
            pw.println( "digraph CallGraph {" );
            for( Iterator srcIt = me2id.keySet().iterator(); srcIt.hasNext(); ) {
                final MethodEntity src = (MethodEntity) srcIt.next();
                pw.println( "  "+me2id.getInt( src )+
                        " [shape=box, "+
                        "label=\""+src.getClassName()+"\\n"+
                        src.getMethodName()+"\"]" );
                ObjectOpenHashSet targets = (ObjectOpenHashSet) edges.get(src);
                if( targets == null ) continue;
                for( Iterator tgtIt = targets.iterator(); tgtIt.hasNext(); ) {
                    final MethodEntity tgt = (MethodEntity) tgtIt.next();
                    pw.println( "  "+me2id.getInt(src)+" -> "+me2id.getInt(tgt) );
                }
            }
            pw.println( "}" );
        } else {
            for( Iterator srcIt = me2id.keySet().iterator(); srcIt.hasNext(); ) {
                final MethodEntity src = (MethodEntity) srcIt.next();
                ObjectOpenHashSet targets = (ObjectOpenHashSet) edges.get(src);
                if( targets == null ) continue;
                for( Iterator tgtIt = targets.iterator(); tgtIt.hasNext(); ) {
                    final MethodEntity tgt = (MethodEntity) tgtIt.next();
                    pw.println( ""+src+" -> "+tgt );
                }
            }
        }
        super.doDone();
    }


}
