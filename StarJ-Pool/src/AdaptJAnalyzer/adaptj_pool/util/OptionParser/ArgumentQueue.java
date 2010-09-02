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

class ArgumentQueue {
        private String[] args;
        private int p;
        
        public ArgumentQueue(String[] args) {
            this.args = args;
            p = 0;
        }

        public boolean empty() {
            return (args == null || p == args.length);
        }

        public String pop() {
            if (args != null && p < args.length) {
                return args[p++];
            }

            return null;
        }

        public String top() {
            if (args != null && p < args.length) {
                return args[p];
            }

            return null;
        }

        public void pushBack() {
            if (p > 0) {
                p--;
            }
        }

        public void push(String arg) {
            String[] newArgs;
            if (args == null) {
                args = new String[1];
                args[0] = arg;
                p = 0;
                return;
            } else {
                newArgs = new String[args.length + 1];
            }

            if (p > 0) {
                System.arraycopy(args, 0, newArgs, 0, p);
            }

            newArgs[p] = arg;
            if (p < args.length) {
                System.arraycopy(args, p, newArgs, p + 1, args.length - p);
            }
            args = newArgs;
        }

        public int size() {
            if (args == null) {
                return 0;
            }

            return args.length;
        }

        public int position() {
            return p;
        }

        public int available() {
            if (args == null) {
                return 0;
            }

            return args.length - p;
        }
    }
