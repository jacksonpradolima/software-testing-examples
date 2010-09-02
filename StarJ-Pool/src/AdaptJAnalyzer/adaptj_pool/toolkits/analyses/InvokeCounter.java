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

import adaptj_pool.toolkits.*;
import adaptj_pool.event.*;
import adaptj_pool.Scene;
import org.apache.bcel.Constants;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.util.text.OptionStringParser;
import adaptj_pool.util.text.HelpDisplayManager;
import adaptj_pool.toolkits.transformers.EventDistiller;

public class InvokeCounter extends EventOperation {
    private long numInvokeVirtuals;
    private long numInvokeInterfaces;
    private long numInvokeSpecials;
    private long numInvokeStatics;

    private boolean appOnly;
    
    public InvokeCounter(String name, String description) {
        this(name, description, true);
    }

    public InvokeCounter(String name, String description, boolean appOnly) {
        super(name, description);
        this.appOnly = appOnly;
    }

    public boolean getAppOnly() {
        return appOnly;
    }
    
    public void setAppOnly(boolean appOnly) {
        this.appOnly = appOnly;
    }

    public void setOption(String name, String value) {
        if (name.equals("appOnly")) {
            appOnly = OptionStringParser.parseBoolean(value);
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) {
        if (name.equals("appOnly")) {
            if (appOnly) {
                return "true";
            } else {
                return "false";
            }
        }

        return super.getOption(name);
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);

        manager.displayOptionHelp("appOnly[:boolean]", "Specifies whether the analysis only applies to the application classes or to the entire program");
    }

    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START,
                                AdaptJSpecConstants.ADAPTJ_FIELD_METHOD_ID
                                | AdaptJSpecConstants.ADAPTJ_FIELD_OFFSET,
                                true)
        };

        return deps;
    }

    public String[] registerOperationDependencies() {
        String[] deps = { Scene.INSTRUCTION_RESOLVER };
        return deps;
    }
    
    public void doInit() {
        numInvokeVirtuals = 0L;
        numInvokeInterfaces = 0L;
        numInvokeSpecials = 0L;
        numInvokeStatics = 0L;
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        InstructionStartEvent e = ((InstructionStartEvent) event);
        if (appOnly && EventDistiller.isStandardLib(e.getMethodID())) {
            return;
        }
        
        switch (e.getCode()) {
            case Constants.INVOKEVIRTUAL:
                numInvokeVirtuals += 1L;
                break;
            case Constants.INVOKEINTERFACE:
                numInvokeInterfaces += 1L;
                break;
            case Constants.INVOKESTATIC:
                numInvokeStatics += 1L;
                break;
            case Constants.INVOKESPECIAL:
                numInvokeSpecials += 1L;
                break;
            default:
                break;
        }
    }

    public void doDone() {
        long subtotal = numInvokeVirtuals + numInvokeInterfaces;
        long total = subtotal + numInvokeSpecials + numInvokeStatics;
        Scene.v().showMessage(getName() + " results " + (appOnly ? "(Application Only)" : "") + ":");
        Scene.v().showMessage("    invokeVirtual   = " + numInvokeVirtuals);
        Scene.v().showMessage("    invokeInterface = " + numInvokeInterfaces);
        Scene.v().showMessage("    Subtotal        = " + subtotal);
        Scene.v().showMessage("    invokeSpecial   = " + numInvokeSpecials);
        Scene.v().showMessage("    invokeStatic    = " + numInvokeStatics);
        Scene.v().showMessage("    ---------------   -----------------");
        Scene.v().showMessage("    Total           = " + total);
    }

                    
}
