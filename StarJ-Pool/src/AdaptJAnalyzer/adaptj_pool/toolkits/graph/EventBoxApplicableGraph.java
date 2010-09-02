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

package adaptj_pool.toolkits.graph;

import adaptj_pool.Scene;
import adaptj_pool.toolkits.*;

import java.util.*;
import it.unimi.dsi.fastUtil.*;

public class EventBoxApplicableGraph {
    private HashMap nameToICust;
    private HashMap icustToNeighbours;
    private int numEdges;

    public static final byte NODE_COLOUR_WHITE = 0;
    public static final byte NODE_COLOUR_GRAY  = 1;
    public static final byte NODE_COLOUR_BLACK = 2;
    public static final byte NODE_COLOUR_GREEN = 3;

    private EventBoxApplicableGraph() {
        numEdges = 0;
        nameToICust = new HashMap();
        icustToNeighbours = new HashMap();
    }

    public EventBoxApplicableGraph(AdaptJContainer root) {
        numEdges = 0;
        walkICustomizableHierarchy(root);
        buildGraph();
    }

    public int getEdgeCount() {
        return numEdges;
    }

    public int getVertexCount() {
        return icustToNeighbours.size();
    }

    public EventBoxApplicableGraph transpose() {
        EventBoxApplicableGraph result = new EventBoxApplicableGraph();
        result.nameToICust.putAll(this.nameToICust);

        /* Reverse all edges */
        Iterator it = icustToNeighbours.keySet().iterator();
        while (it.hasNext()) {
            IEventBoxApplicable eba = (IEventBoxApplicable) it.next();
            EventBoxApplicableGraphNode n = (EventBoxApplicableGraphNode) icustToNeighbours.get(eba);

            for ( ; n != null; n = n.getNext()) {
                result.add(n.getTarget(), eba);
            }
        }

        return result;
    }

    private void walkICustomizableHierarchy(AdaptJContainer root) {
        nameToICust = new HashMap();

        Iterator it = root.iterator();
        while (it.hasNext()) {
            ICustomizable cust = (ICustomizable) it.next();
            String name = cust.getName();

            if (cust instanceof IEventBoxApplicable) {
                nameToICust.put(name, cust);
            }

            if (cust instanceof AdaptJContainer) {
                walkICustomizableHierarchy((AdaptJContainer) cust, name);
            }
        }
    }

    private void walkICustomizableHierarchy(AdaptJContainer root, String packName) {
        Iterator it = root.iterator();
        while (it.hasNext()) {
            ICustomizable cust = (ICustomizable) it.next();
            String fullName = packName + "." + cust.getName();

            if (cust instanceof IEventBoxApplicable) {
                nameToICust.put(fullName, cust);
            }

            if (cust instanceof AdaptJContainer) {
                walkICustomizableHierarchy((AdaptJContainer) cust, fullName);
            }
        }
    }

    private void buildGraph() {
        icustToNeighbours = new HashMap();

        Iterator it = nameToICust.values().iterator();
        while (it.hasNext()) {
            IEventBoxApplicable eba = (IEventBoxApplicable) it.next();
            String[] deps = eba.registerOperationDependencies();
            if (deps != null) {
                for (int i = 0; i < deps.length; i++) {
                    IEventBoxApplicable dep = (IEventBoxApplicable) nameToICust.get(deps[i]);
                    if (dep != null) {
                        add(eba, dep);
                    } else {
                        /* Can't find a dependency */
                        Scene.v().handleDependencyFailure(eba, "\"" + deps[i] + "\" not found");
                    }
                }
            }
        }

        // Debugging: Print the graph
        /*
        it = icustToNeighbours.keySet().iterator();
        while (it.hasNext()) {
            ICustomizable tmp = (ICustomizable) it.next();

            System.err.println("Node " + tmp.getName() + (!tmp.isEnabled() ? " (D)" : ""));
            EventBoxApplicableGraphNode n = (EventBoxApplicableGraphNode) icustToNeighbours.get(tmp);
            while (n != null) {
                System.err.println("  -> " + n.getTarget().getName());
                n = n.getNext();
            }
        }
        */
    }

    public void add(IEventBoxApplicable source, IEventBoxApplicable dest) {
        EventBoxApplicableGraphNode n;

        if (!icustToNeighbours.containsKey(dest)) {
            icustToNeighbours.put(dest, null);
        }
        
        n = new EventBoxApplicableGraphNode(dest, (EventBoxApplicableGraphNode) icustToNeighbours.get(source));
        icustToNeighbours.put(source, n);

        numEdges++;
    }

    public void checkDependencies() {
        Set nodeSet = new HashSet(nameToICust.values());
        Object2ByteOpenHashMap colourMap = new Object2ByteOpenHashMap();
        EventBoxApplicableGraph gt = transpose();

        /* Mark all vertices as undiscovered */
        setVertexColours(nodeSet, NODE_COLOUR_WHITE, colourMap);
        
        EventBoxApplicableGraphNodeSet sccs = getSCCs(nodeSet, colourMap, gt);
        EventBoxApplicableGraphNodeSet tmp = sccs;

        //DEBUG: print SCCs
        /*
        for ( ; tmp != null; tmp = tmp.getNext()) {
            System.err.println("SCC {");
            EventBoxApplicableGraphNode n = tmp.getNodes();
            for ( ; n != null; n = n.getNext()) {
                IEventBoxApplicable target = n.getTarget();
                System.err.println("  " + target.getName() + (!target.isEnabled() ? " (D)" : ""));
            }
            System.err.println("}");
        } 
        tmp = sccs;
        */
        // END BEBUG
        
        for ( ; tmp != null; tmp = tmp.getNext()) {
            EventBoxApplicableGraphNode n = tmp.getNodes();
            for ( ; n != null; n = n.getNext()) {
                IEventBoxApplicable target = n.getTarget();
                if (!target.isEnabled()) {
                    //System.err.println("$$ Target \"" + target.getName() + "\" is disabled");
                    //if (tmp.getNodes() == n && n.getNext() == null) {
                        /* This SCC contains only 1 operation. Skip it. */
                    //    continue;
                    //}
                    disableEBAChain(tmp.getNodes(), gt, colourMap);
                    break;
                }
            }
        }
    }

    private void disableEBAChain(EventBoxApplicableGraphNode chain, EventBoxApplicableGraph g,
            Object2ByteOpenHashMap colourMap) {
        for ( ; chain != null; chain = chain.getNext()) {
            IEventBoxApplicable n = chain.getTarget();
            if (colourMap.getByte(n) == NODE_COLOUR_GREEN) {
                // We have already processed this node
                continue;
            }
            if (n.isEnabled()) {
                n.setEnabled(false);
                Scene.v().handleDependencyFailure(n, null);
            }
            colourMap.put(n, NODE_COLOUR_GREEN);
            disableEBAChain((EventBoxApplicableGraphNode) g.icustToNeighbours.get(n), g, colourMap);
        }
    }

    private static void setVertexColours(Set nodeSet, byte colour, Object2ByteOpenHashMap colourMap) {
        colourMap.clear();
        colourMap.setDefRetValue(NODE_COLOUR_WHITE);
    }

    private EventBoxApplicableGraphNodeSet getSCCs(Set nodeSet, Object2ByteOpenHashMap colourMap, EventBoxApplicableGraph gt) {
        /* First DFS - Compute the initial component forest */
        EventBoxApplicableGraphNodeSet head = null;
        Iterator it = nodeSet.iterator();
        while (it.hasNext()) {
            IEventBoxApplicable n = (IEventBoxApplicable) it.next();

            if (colourMap.getByte(n) == NODE_COLOUR_WHITE) {
                head = new EventBoxApplicableGraphNodeSet(null, head);
                DFSVisit(n, head, colourMap);
            }
        }

        /* Second DFS - Compute Strongly Connected Components */

        EventBoxApplicableGraphNodeSet result = null;
        
        /* Mark all vertices as undiscovered */
        setVertexColours(nodeSet, NODE_COLOUR_WHITE, colourMap);

        /* For each vertex, in the given order */
        EventBoxApplicableGraphNodeSet order;
        for (order = head; order != null; order = order.getNext()) {
            EventBoxApplicableGraphNode n;

            for (n = order.getNodes(); n != null; n = n.getNext()) {
                IEventBoxApplicable eba = n.getTarget();
                if (colourMap.getByte(n.getTarget()) == NODE_COLOUR_WHITE) {
                    result = new EventBoxApplicableGraphNodeSet(null, result);

                    gt.DFSVisit(n.getTarget(), result, colourMap);
                }
            }
        }

        return result;
    }

    private void DFSVisit(IEventBoxApplicable node, EventBoxApplicableGraphNodeSet set,
            Object2ByteOpenHashMap colourMap) {
        /* Mark this vertex as discovered */
        colourMap.put(node, NODE_COLOUR_GRAY);

        /* Recursively visit all neighbours */
        EventBoxApplicableGraphNode neighbours = (EventBoxApplicableGraphNode) icustToNeighbours.get(node);
        for ( ; neighbours != null; neighbours = neighbours.getNext()) {
            IEventBoxApplicable target = neighbours.getTarget();
            
            if (colourMap.getByte(target) == NODE_COLOUR_WHITE) {
                DFSVisit(target, set, colourMap);
            }
        }

        /* Mark this vertex as finished */
        colourMap.put(node, NODE_COLOUR_BLACK);

        set.setNodes(new EventBoxApplicableGraphNode(node, set.getNodes()));
    }
}

