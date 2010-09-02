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

import adaptj_pool.event.*;
import adaptj_pool.util.*;
import java.util.*;
import adaptj_pool.util.text.OptionStringParser;
import adaptj_pool.util.text.HelpDisplayManager;

public class Pack extends Customizable implements AdaptJContainer {// extends TimedEventBoxApplicable implements AdaptJContainer {
    private List contents;
    
    public Pack(String name, String description) {
        super(name, description);
        contents = new LinkedList();
        ////setTimed(false);
    }

    public Pack(String name, String description, AdaptJContainer parent) {
        super(name, description);
        contents = new LinkedList();
        parent.add(this);
        ////setTimed(false);
    }

    /*
    public int[] registerEvents() {
        int events[] = new int[AdaptJEvent.ADAPTJ_EVENT_COUNT];
        for (int i = 0; i < AdaptJEvent.ADAPTJ_EVENT_COUNT; i++) {
            events[i] = i;
        }

        return events;
    }
    */

    /*
    public long getTime() {
        if (isTimed()) {
            /* The timer for this pack is enabled */
            /*
            return super.getTime();
        } else {
            long result = 0L;
            
            Iterator it = iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof ITimedCustomizable) {
                    result += ((ITimedCustomizable) o).getTime();
                }
            }

            return result;
        }
    }
    */

    public void setOption(String name, String value) {
        if (name.equals("all-enabled")) {
            setAllEnabled(OptionStringParser.parseBoolean(value));
        } else {
            super.setOption(name, value);
        }
    }

    public void displayHelp(HelpDisplayManager manager) {
        manager.displayOptionHelp("all-enabled[:<boolean>]", "Recursively enables/disables the subtree contained by this pack");
    }
    
    public void preInit() {
        if (!isEnabled()) {
            return;
        }
        
        if (isVerbose()) {
            doVerbosePreInit();
        }
        
        doPreInit();
        Iterator it = contents.iterator();
        while (it.hasNext()) {
            ICustomizable customizable = (ICustomizable) it.next();
            if (customizable instanceof IInitializable) {
                if (customizable.isEnabled()) {
                    ((IInitializable) customizable).preInit();
                }
            }
            /*
            if (customizable instanceof AdaptJContainer) {
                ((AdaptJContainer) customizable).preInit();
            } else if (customizable instanceof IEventBoxApplicable) {
                ((IEventBoxApplicable) customizable).preInit();
            }
            */
        }
    }

    public void doPreInit() {

    }
    
    public void init() {
        if (!isEnabled()) {
            return;
        }
        
        if (isVerbose()) {
            doVerboseInit();
        }
        
        doInit();
        Iterator it = contents.iterator();
        while (it.hasNext()) {
            ICustomizable customizable = (ICustomizable) it.next();
            if (customizable instanceof IInitializable) {
                if (customizable.isEnabled()) {
                    ((IInitializable) customizable).init();
                }
            }
            /*
            if (customizable instanceof AdaptJContainer) {
                ((AdaptJContainer) customizable).init();
            } else if (customizable instanceof IEventBoxApplicable) {
                ((IEventBoxApplicable) customizable).init();
            }
            */
        }
    }

    public void doInit() {

    }

    /*
    public void doApply(EventBox box) {
        Iterator it = contents.iterator();

        while (it.hasNext()) {
            IEventBoxApplicable eba = (IEventBoxApplicable) it.next();

            eba.apply(box);
        }
    }
    */

    /*
    public void doApply(AdaptJEventChain chain) {
        Iterator it = contents.iterator();
        
        while (it.hasNext()) {
            IEventBoxApplicable eba = (IEventBoxApplicable) it.next();

            if (eba instanceof IEventChainApplicable) {
                ((IEventChainApplicable) eba).apply(chain);
            } else {
                EventBox box = new ChainEventBox(chain);
                
                if (eba.isEnabled()) {
                    while (box.hasNext()) {
                        eba.apply(box);
                        box.step();
                    }
                }
            }
        }
    }
    */

    public void done() {
        if (!isEnabled()) {
            return;
        }

        if (isVerbose()) {
            doVerboseDone();
        }
        
        Iterator it = contents.iterator();
        while (it.hasNext()) {
            ICustomizable customizable = (ICustomizable) it.next();
            if (customizable instanceof IInitializable) {
                if (customizable.isEnabled()) {
                    ((IInitializable) customizable).done();
                }
            }
            /*
            if (customizable instanceof AdaptJContainer) {
                ((AdaptJContainer) customizable).done();
            } else if (customizable instanceof IEventBoxApplicable) {
                ((IEventBoxApplicable) customizable).done();
            }
            */
        }
        
        doDone();
    }

    public void doDone() {

    }

    public void doVerbosePreInit() {
        
    }
    
    public void doVerboseInit() {
        
    }
    
    /*
    public void doVerboseApply(EventBox box) {

    }
    */

    /*
    public void doVerboseApply(AdaptJEventChain chain) {

    }
    */

    public void doVerboseDone() {

    }

    public void add(ICustomizable eba) {
        contents.add(eba);
    }

    public void remove(ICustomizable eba) {
        contents.remove(eba);
    }

    public void removeAll(ICustomizable eba) {
        while(contents.remove(eba)) ;
    }

    public ICustomizable getByName(String name) {
        int dot = name.indexOf('.');

        if (dot < 0) {
            Iterator it = contents.iterator();
            while (it.hasNext()) {
                ICustomizable customizable = (ICustomizable) it.next();
                String n = customizable.getName();
                if (n != null && n.equals(name)) {
                    return customizable;
                }
            }
        } else {
            String item = name.substring(0, dot);
            Iterator it = contents.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (o instanceof AdaptJContainer) {
                    AdaptJContainer container = (AdaptJContainer) o;
                    String n = container.getName();
                    if (n != null && n.equals(item)) {
                        return container.getByName(name.substring(dot + 1));
                    }
                }
            }
        }

        return null;
    }

    public Iterator iterator() {
        return contents.iterator();
    }

    public void reset() {
        contents = new LinkedList();
    }

    public void setVerbose(boolean verbose) {
        super.setVerbose(verbose);

        Iterator it = contents.iterator();
        while (it.hasNext()) {
            ICustomizable customizable = (ICustomizable) it.next();
            customizable.setVerbose(verbose);
        }
    }
    
    public void setAllEnabled(boolean enabled) {
        super.setEnabled(enabled);

        Iterator it = contents.iterator();
        while (it.hasNext()) {
            ICustomizable customizable = (ICustomizable) it.next();
            customizable.setEnabled(enabled);
        }
    }
}
