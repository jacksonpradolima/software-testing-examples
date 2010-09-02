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

package adaptj_pool.debug;

import adaptj_pool.util.*;
import java.io.*;

/**
 * Reads the output from the standard AdaptJ JVMPI agent and outputs it to the
 * standard output or a file. Warning: if the standard output is used, unpredictable
 * results can be obtained in the terminal, for the data is in binary format. For
 * example, under UN*X systems, the terminal might stop displaying characters correctly
 * and may need to be reset.
 */
public class AdaptJPipeDump {
    /**
     * Reads input from the file specified as the first argument, and outputs the data
     * to the file specified as the second argument OR the standard output, if no file was
     * specified. THe input file can be a regular file or a special FIFO (named pipe) file.
     *
     * @param args the command line arguments passes to this application. <code>args[0]</code>
     *             must be the name of an input file. <code>args[1]</code> can optionally be
     *             specified, in which case it represents the name of the file used to write
     *             the output.
     */
    public static void main(String args[]) {
        if (args.length < 1) {
            System.err.println("Usage: java AdaptJPipeDump <file> [outfile]");
            System.exit(1);
        }

        try {
            BufferedFileReader reader = new BufferedFileReader(args[0]);
            PrintStream outStream = System.out;
            if (args.length > 1) {
                outStream = new PrintStream(new FileOutputStream(args[1]));
            }
            int b;
            while ((b = reader.read()) >= 0) {
                outStream.write(b);
            }
        } catch (IOException e) {
            System.err.println("Error occured: " + e);
        }


        System.err.println("Done...");
    }
}
