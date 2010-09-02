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

import java.io.*;

public class OptionFileArgument implements Argument {
    private boolean required;

    public OptionFileArgument(boolean required) {
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

    public Object[] parse(ArgumentQueue queue) throws OptionProcessingException {
        String val;
        if (queue == null || (val = queue.pop()) == null) {
            if (required) {
                throw new OptionProcessingException("Missing required argument");
            }
            return null;
        }

        enqueue(val, queue);
        return null;
    }

    private void enqueue(String filename, ArgumentQueue queue) throws OptionProcessingException {
        StreamTokenizer tokenizer;
        try {
            FileInputStream fis = new FileInputStream(filename);
            Reader r = new BufferedReader(new InputStreamReader(fis));
            tokenizer = new StreamTokenizer(r);    
        } catch (FileNotFoundException e) {
            throw new OptionProcessingException("File not found: \"" + filename + "\"");
        } catch (IOException e) {
            throw new OptionProcessingException("Error reading file: \"" + filename + "\"");
        }
         
        tokenizer.resetSyntax();
        tokenizer.whitespaceChars(0, 32);
        tokenizer.wordChars(33, Character.MAX_VALUE);
        tokenizer.quoteChar('"');
        tokenizer.quoteChar('\'');
        tokenizer.eolIsSignificant(false);
        tokenizer.commentChar('#');

        ArgumentQueue tmpQueue = new ArgumentQueue(null);

        int token;
        try {
            while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
                switch (token) {
                    case StreamTokenizer.TT_WORD:
                    case '\"':
                    case '\'':
                        tmpQueue.push(tokenizer.sval);
                    default:
                }
            }

            /* Reverse the order of the arguments, ensuring that they appear
             * in the correct order in the queue. */
            while (tmpQueue.available() > 0) {
                String s = tmpQueue.pop();
                queue.push(s);
            }

        } catch (IOException e) {
            throw new OptionProcessingException("Error reading file: \"" + filename + "\"");
        }
    }
}
