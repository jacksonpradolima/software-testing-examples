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

public interface Option {
    /**
     * Returns the textual description of this option. This value
     * is used to print help about the commands.
     *
     * @return the textual description of this option.
     */
    public String getDescription();

    /**
     * Returns the valid switches for this option. Typical
     * exaples include "-s" and "--long-option", or null if
     * the option is to be ignored, in which case this option
     * will not appear in the usage and help information.
     *
     * @return a array of switches that can be used to specify this option,
     *  or <code>null</code> if none exists.
     */
    public String[] getSwitches();

    /**
     * Returns the array of <code>Argument</code> objects for this
     * option. <code>Argument</code> objects are used to specifify
     * the arguments that this option accepts.
     *
     * @return an array of <code>Argument</code> objects, or <code>null</code>
     *  if none exists.
     */
    public Argument[] getArguments();

    
    /**
     * Returns an array of argument descriptions corresponding to the
     * <code>Argument</code> objects returned by <code>getArguments</code>
     *
     * @return an array of argument descriptions, or <code>null</code> if
     * none exists.
     */
    public String[] getArgumentDescriptions();

    /**
     * Registers an argument for use with this option. In the case where
     * multiple arguments are registered, they are parsed in the same order
     * as they are added using <code>addArgument</code>.
     *
     * @param argument an <code>Argument</code> object describing
     *    an argument to use with this option.
     */
    /*public void addArgument(Argument argument);*/
}
