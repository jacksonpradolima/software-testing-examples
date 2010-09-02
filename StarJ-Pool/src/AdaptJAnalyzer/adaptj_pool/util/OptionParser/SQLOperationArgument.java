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

package adaptj_pool.util.OptionParser;

import java.util.*;

public class SQLOperationArgument implements Argument {
    private boolean required;
    
    public SQLOperationArgument(boolean required) {
        this.required = required;
    }

    public Object[] parse(ArgumentQueue queue) throws OptionProcessingException {
        String val;
        if (queue == null || (val = queue.pop()) == null) {
            if (required) {
                throw new OptionProcessingException("Missing required argument");
            }
            return null;
        }

        Object[] result = new Object[1];
        if (val.equalsIgnoreCase("add") || val.equalsIgnoreCase("a")) {
            result[0] = "add";
        } else if (val.equalsIgnoreCase("update") || val.equalsIgnoreCase("u")) { 
            result[0] = "update";
        } else if (val.equalsIgnoreCase("delete") || val.equalsIgnoreCase("d")) { 
            result[0] = "delete";
        } else if (val.equalsIgnoreCase("nothing") || val.equalsIgnoreCase("n")) { 
            result[0] = "nothing";
        } else {
            throw new OptionProcessingException("Invalid argument");
        }

        return result;

    }

    public boolean isRequired() {
        return required;
    }
}
