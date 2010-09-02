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

package adaptj_pool.event;

import adaptj_pool.*;
import java.io.IOException;
import java.util.NoSuchElementException;

public class SingleEventBox implements EventBox {
    private IAEFReader reader;
    private AdaptJEvent event;
    private AdaptJEvent nextEvent;

    public SingleEventBox(IAEFReader reader) {
        this.reader = reader;
        nextEvent = null;
        step();
    }

    public AdaptJEvent getEvent() {
        return event;
    }

    public void setEvent(AdaptJEvent event) {
        this.event = event;
    }

    public AdaptJEvent nextEvent() {
        if (nextEvent != null) {
            return nextEvent;
        }
        
        try {
            nextEvent = reader.getNextEvent();
        } catch (NoSuchElementException e) {
            nextEvent = null;
        } catch (IOException e) {
            Scene.v().showDebug("IOException received from AEFReader");
            nextEvent = null;
        } catch (AEFFormatException e) {
            throw new RuntimeException("AdaptJ Event File has an invalid format or is corrupted");
        }

        return nextEvent;
    }

    public boolean hasNext() {
        return (nextEvent != null) || reader.hasMoreEvents();
    }

    public void remove() {
        //step();
        throw new EventSkipException();
    }

    public boolean step() {
        event = nextEvent();
        nextEvent = null;
        return (event != null);
    }

    public long count() {
        return reader.getEventCount();
    }
}
