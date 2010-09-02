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

package adaptj_pool.toolkits.analyses.metrics;

import java.io.*;
import java.util.*;
import it.unimi.dsi.fastUtil.*;

public class InstructionSetReader {
    private StreamTokenizer tokenizer;
    private Object2ObjectOpenHashMap sets = new Object2ObjectOpenHashMap();
    private ObjectOpenHashSet exportedSets = new ObjectOpenHashSet();

    /* Parser states */
    private static final int STATE_WAIT_DEFNAME     =  0;  /* Waiting for a new set name     */
    private static final int STATE_WAIT_EQ          =  1;  /* Waiting for '='                */
    private static final int STATE_WAIT_SET         =  2;  /* Waiting for a set (id or enum) */
    private static final int STATE_STARTED_ENUM     =  3;  /* Processed '{' as last token    */
    private static final int STATE_GOT_INST         =  4;  /* Processed inst as last token   */
    private static final int STATE_GOT_COMMA        =  5;  /* Processed ',' as last token    */
    private static final int STATE_GOT_SET          =  6;  /* Processed a whole set as last
                                                              token (id or enum)             */
    private static final int STATE_GOT_EXPORT       =  7;  /* Processed "export" as last
                                                              token, waiting for set ID      */
    private static final int STATE_GOT_EXPORT_ID    =  8;  /* Processed an ID as last token
                                                              and this ID is to be exported.
                                                              Waiting for ';', '=' or another
                                                              ID */
    private static final int STATE_GOT_EXPORT_COMMA =  9;  /* This is a list of export IDs.
                                                              Waiting for another ID         */
    private static final int STATE_GOT_EXPORT_ID2   = 10;  /* Processed an ID as last token
                                                              and this ID is to be exported.
                                                              Waiting for ';' or another ID.
                                                              (This is a list of exports, so
                                                              '=' is now illegal)            */

    
    public InstructionSetReader(String fileName) throws FileNotFoundException, IOException,
            InvalidSyntaxException {
        FileInputStream fis = new FileInputStream(fileName);
        init(fis);
    }

    public InstructionSetReader(InputStream stream) throws IOException, InvalidSyntaxException {
        init(stream);
    }

    public List getSets() {
        return new ArrayList(exportedSets);
    }

    private void init(InputStream stream) throws IOException, InvalidSyntaxException {
        Reader r = new BufferedReader(new InputStreamReader(stream));
        
        /* Get a Stream Tokenizer to parse the input */
        tokenizer = new StreamTokenizer(r);
    
        tokenizer.resetSyntax();                       /* Clear all rules */
        tokenizer.whitespaceChars(0, 32);              /* Typical setting */
        tokenizer.wordChars(33, Character.MAX_VALUE);  /* We want as much words as possible */
        tokenizer.eolIsSignificant(false);             /* For counting line numbers */
        tokenizer.ordinaryChar('{');                   /* Set begin */
        tokenizer.ordinaryChar('}');                   /* Set end */
        tokenizer.ordinaryChar('=');                   /* Set definition */
        tokenizer.ordinaryChar(',');                   /* Set element delimiter */
        tokenizer.ordinaryChar(';');                   /* End of set definition, used
                                                          to avoid errors caused by forgetting
                                                          a ',' character */
        tokenizer.ordinaryChar('+');                   /* Set union */
        tokenizer.commentChar('#');                    /* Single line comment character */

        /* Do the parsing */
        readInstructionSets();
    }

    private void readInstructionSets() throws IOException, InvalidSyntaxException {
        /* Attempt to parse the input to create InstructionSet objects */
        
        int token;                             /* The current token value */
        InstructionSet newSet = null;          /* The cumulative union of sets in a definition */
        int currentState = STATE_WAIT_DEFNAME; /* The current state (initially waiting for a set name) */

        while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
            switch (token) {
                case StreamTokenizer.TT_WORD:
                    /* Parsed an identifier */
                    if (tokenizer.sval.equals("export")) {
                        currentState = STATE_GOT_EXPORT;
                    } else {
                        if (currentState == STATE_GOT_EXPORT) {
                            String setName = tokenizer.sval;
                            boolean setExists = sets.containsKey(setName);
                            int lineNumber = tokenizer.lineno();
                            token = tokenizer.nextToken();
                            tokenizer.pushBack();
                            if (token == '=') {
                                if (setExists) {
                                    throw new InvalidSyntaxException(lineNumber, "Set " + setName + " is already defined");
                                }

                                newSet = new InstructionSet(setName);
                                exportedSets.add(newSet);
                            } else {
                                if (setExists) {
                                    InstructionSet set = (InstructionSet) sets.get(setName);
                                    exportedSets.add(set);
                                } else {
                                    throw new InvalidSyntaxException(lineNumber, "Set " + setName + " is not defined");
                                }
                            }
                            currentState = STATE_GOT_EXPORT_ID;
                        } else if (currentState == STATE_WAIT_DEFNAME) {
                            /* Identifier is the name of a new set. Check if this is a duplicate */
                            if (sets.containsKey(tokenizer.sval)) {
                                throw new InvalidSyntaxException(tokenizer.lineno(), "Set " + tokenizer.sval + " is already defined");
                            }
                            newSet = new InstructionSet(tokenizer.sval);
                            /* Next token has to be '=' */
                            currentState = STATE_WAIT_EQ;
                        } else if (currentState == STATE_WAIT_SET) {
                            /* Identifier is another, predefined set. Perform union. */
                            if (sets.containsKey(tokenizer.sval)) {
                                newSet.setUnion((InstructionSet) sets.get(tokenizer.sval));
                            } else {
                                throw new InvalidSyntaxException(tokenizer.lineno(), "Undefined set: " + tokenizer.sval);
                            }
                            /* Next token has to be ';' or '+' */
                            currentState = STATE_GOT_SET;
                        } else if (currentState == STATE_STARTED_ENUM
                                   || currentState == STATE_GOT_COMMA) {
                            /* Identifier is a bytecode mnemonic. Add to growing set. */
                            if (!newSet.add(tokenizer.sval)) {
                                throw new InvalidSyntaxException(tokenizer.lineno(), "Unknown bytecode mnemonic: " + tokenizer.sval);
                            }
                            currentState = STATE_GOT_INST;
                        } else if (currentState == STATE_GOT_EXPORT_COMMA) {
                            if (sets.containsKey(tokenizer.sval)) {
                                exportedSets.add(sets.get(tokenizer.sval));
                            }
                            currentState = STATE_GOT_EXPORT_ID2;
                        } else {
                            throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: " + tokenizer.sval);
                        }
                    }
                    break;
                case '{':
                    if (currentState == STATE_WAIT_SET) {
                        currentState = STATE_STARTED_ENUM;
                    } else {
                        throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: {");
                    }
                    break;
                case '}':
                    if (currentState == STATE_STARTED_ENUM
                            || currentState == STATE_GOT_INST) {
                        currentState = STATE_GOT_SET;
                    } else {
                        throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: }");
                    }
                    break;
                case '=':
                    if (currentState == STATE_WAIT_EQ || currentState == STATE_GOT_EXPORT_ID) {
                        currentState = STATE_WAIT_SET;
                    } else {
                        throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: =");
                    }
                    break;
                case ',':
                    if (currentState == STATE_GOT_EXPORT_ID || currentState == STATE_GOT_EXPORT_ID2) {
                        currentState = STATE_GOT_EXPORT_COMMA;
                    } else if (currentState == STATE_GOT_INST) {
                        currentState = STATE_GOT_COMMA;
                    } else {
                        throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: ,");
                    }
                    break;
                case ';':
                    if (currentState == STATE_GOT_SET) {
                        sets.put(newSet.getName(), newSet);
                        currentState = STATE_WAIT_DEFNAME;
                    } else if (currentState == STATE_GOT_EXPORT_ID || currentState == STATE_GOT_EXPORT_ID2) {
                        currentState = STATE_WAIT_DEFNAME;
                    } else {
                        throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: ;");
                    }
                    break;
                case '+':
                    if (currentState == STATE_GOT_SET) {
                        currentState = STATE_WAIT_SET;
                    } else {
                        throw new InvalidSyntaxException(tokenizer.lineno(), "Invalid token: +");
                    }
                    break;
                default:
                    throw new InvalidSyntaxException(tokenizer.lineno(), "Unrecognized token type: " + token);
            }
        }

        if (currentState != STATE_WAIT_DEFNAME) {
            throw new InvalidSyntaxException(tokenizer.lineno(), "Unterminated set definition");
        }
    }
}
