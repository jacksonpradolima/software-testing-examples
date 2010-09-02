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

import adaptj_pool.toolkits.*;

public class EventBoxApplicableGraphNode {
    private IEventBoxApplicable target;
    private EventBoxApplicableGraphNode next;
    private byte colour;

    public EventBoxApplicableGraphNode(IEventBoxApplicable target) {
        this.target = target;
        this.next = null;
    }

    public EventBoxApplicableGraphNode(IEventBoxApplicable target, EventBoxApplicableGraphNode next) {
        this.target = target;
        this.next = next;
    }
    
    public void setNext(EventBoxApplicableGraphNode next) {
        this.next = next;
    }

    public EventBoxApplicableGraphNode getNext() {
        return next;
    }

    public IEventBoxApplicable getTarget() {
        return target;
    }

    public byte getColour() {
        return colour;
    }

    public void setColour(byte colour) {
        this.colour = colour;
    }
}

