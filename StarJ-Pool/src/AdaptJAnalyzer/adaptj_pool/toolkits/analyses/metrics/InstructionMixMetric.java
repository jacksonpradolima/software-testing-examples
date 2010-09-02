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
import adaptj_pool.*;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.toolkits.*;
import adaptj_pool.event.*;
import adaptj_pool.util.*;
import adaptj_pool.util.text.OptionStringParser;
import adaptj_pool.util.text.HelpDisplayManager;
import java.util.*;
import adaptj_pool.util.xml.*;
import it.unimi.dsi.fastUtil.*;


public class InstructionMixMetric extends MetricAnalysis {
    private final static int NO_SORT = 0;
    private final static int SORT_BY_NAME = 1;
    private final static int SORT_BY_VALUE = 2;
    private final static int SORT_BY_VALUE_NO_TIE = 3;
    private List setList = null;
    private Int2ObjectOpenHashMap byteCodeToSets = null;
    private String setsFileName = null;
    private boolean showSummary = false;
    private int sortType = SORT_BY_NAME;
    
    public InstructionMixMetric(String name) {
        super(name, "Instruction Mix Metric", "Counts occurences of classes of bytecodes");
    }

    public void setOption(String name, String value) {
        if (name.equals("setsFile")) {
            if (value != null) {
                setsFileName = value;
                try {
                    InstructionSetReader reader = new InstructionSetReader(value);
                    setList = reader.getSets();
                } catch (FileNotFoundException e) {
                    throw new FileNotFoundOptionException(this, name, value);
                } catch (IOException e) {
                    throw new FileReadOptionException(this, name, value);
                } catch (InvalidSyntaxException e) {
                    Scene.v().reportError(e.toString());
                    throw new SetOptionException(this, name, e.toString());
                }
            
            } else {
                throw new InvalidOptionFileNameException(this, name, value);
            }
        } else if (name.equals("sort")) {
            if (value.equals("name")) {
                sortType = SORT_BY_NAME;
            } else if (value.equals("value")) {
                sortType = SORT_BY_VALUE;
            } else if (value.equals("valuenotie")) {
                sortType = SORT_BY_VALUE_NO_TIE;
            } else if (OptionStringParser.parseBoolean(value) == false) {
                sortType = NO_SORT;
            } else {
                throw new SetOptionException(this, "Invalid sort option: " + value);
            }
        } else if (name.equals("summary")) {
            showSummary = OptionStringParser.parseBoolean(value);
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) {
        if (name.equals("setsFile")) {
            return setsFileName;
        } else if (name.equals("sort")) {
            switch (sortType) {
                case SORT_BY_NAME:
                    return "name";
                case SORT_BY_VALUE:
                    return "value";
                case SORT_BY_VALUE_NO_TIE:
                    return "valuenotie";
                default:
                    return "off";
            }
        } else if (name.equals("summary")) {
            if (showSummary) {
                return "true";
            } else {
                return "false";
            }
        }

        return super.getOption(name);
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);

        manager.displayOptionHelp("setsFile:<file>", "Specifies the name of the file from which to read to description of the instruction sets");
        manager.displayOptionHelp("sort:<off | name | value | valuenotie>", "Specifies the sorting strategy to use for the results");
        manager.displayOptionHelp("summary[:boolean]", "Specifies whether summary information about the bytecodes should be displayed at the end of the analysis");
    }
    
    /*
    public int[] registerEvents() {
        int events[] = {
            AdaptJEvent.ADAPTJ_INSTRUCTION_START
        };

        return events;
    }
    */
    
    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {
            new EventDependency(AdaptJEvent.ADAPTJ_INSTRUCTION_START)
        };

        return deps;
    }
    
    public String[] registerOperationDependencies() {
        String[] deps = {Scene.INSTRUCTION_RESOLVER};
        return deps;
    }

    public void doInit() {
        if (setList == null) {
            setList = new ArrayList();
        }

        initMap();
    }

    private void initMap() {
        byteCodeToSets = new Int2ObjectOpenHashMap();

        Iterator it = setList.iterator();
        while (it.hasNext()) {
            InstructionSet set = (InstructionSet) it.next();
            int contents[] = set.getContents();
            if (contents != null) {
                for (int i = 0; i < contents.length; i++) {
                    int currentByteCode = contents[i];
                    List l = (List) byteCodeToSets.get(currentByteCode);
                    if (l == null) {
                        l = new ArrayList();
                        byteCodeToSets.put(currentByteCode, l);
                    }

                    l.add(set);
                }
            }
        }

        /* Convert lists to arrays */
        InstructionSet[] iSet;
        IntIterator keysIt = (IntIterator) byteCodeToSets.keySet().iterator();
        while (keysIt.hasNext()) {
            int key = keysIt.nextInt();
            List l = (List) byteCodeToSets.get(key);
            if (l != null && l.size() > 0) {
                iSet = new InstructionSet[l.size()];
                l.toArray(iSet);
            } else {
                iSet = null;
            }
            byteCodeToSets.put(key, iSet);
        }
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();
        if (event.getTypeID() == AdaptJEvent.ADAPTJ_INSTRUCTION_START) {
            int code = ((InstructionStartEvent) event).getCode();
            InstructionSet[] iSet = (InstructionSet[]) byteCodeToSets.get(code);
            
            if (iSet != null) {
                for (int i = 0; i < iSet.length; i++) {
                    iSet[i].stepCounter();
                }
            }
        }
    }

    public void computeResults() {
        /* Sort List */
        switch(sortType) {
            case SORT_BY_NAME:
                Collections.sort(setList, new NameSetComparator());
                break;
            case SORT_BY_VALUE:
                Collections.sort(setList, new ValueSetComparator(false));
                break;
            case SORT_BY_VALUE_NO_TIE:
                Collections.sort(setList, new ValueSetComparator(true));
                break;
            default:
                /* Don't sort */
        }

        if (showSummary) { 
            System.out.println("Instruction Mix Metric Results:");
            System.out.println("---------------------------------------------------");
            Iterator it = setList.iterator();
            while (it.hasNext()) {
                InstructionSet set = (InstructionSet) it.next();

                System.out.println("    " + set.getName() + ": " + set.getCounter());
            }
            System.out.println();
        }
    }

    /*
    public void outputXMLResults(XMLPrintStream out) {
        if (!isEnabled()) {
            return;
        }
        out.openTagLn("results type=\"histogram\"");
        Iterator it = setList.iterator();
        while (it.hasNext()) {
            InstructionSet set = (InstructionSet) it.next();
            out.openTagLn("value");
            out.printTaggedValueLn("label", set.getName());
            out.printTaggedValueLn("amount", set.getCounter());
            out.closeTagLn("value");
        }
        out.closeTagLn("results");
    }
    */

    class ValueSetComparator implements Comparator {
        private boolean breakTiesByName;
        
        public ValueSetComparator(boolean breakTiesByName) {
            this.breakTiesByName = breakTiesByName;    
        }
        
        public int compare(Object o1, Object o2) {
            if (o1 instanceof InstructionSet && o2 instanceof InstructionSet) {
                InstructionSet s1 = (InstructionSet) o1;
                InstructionSet s2 = (InstructionSet) o2;
                
                /* The counter values are long values. The typical subtraction strategy
                   requires a cast to int and thus can lead to erroneous results. An
                   if .. then approach is therefore used here. */
                if (s2.getCounter() > s1.getCounter()) {
                    return 1;
                } else if (s2.getCounter() < s1.getCounter()) {
                    return -1;
                }
                
                if (breakTiesByName) {
                    return s1.getName().compareTo(s2.getName());
                }
                return 0;
            }

            throw new RuntimeException("Can only compare InstructionSets!");
        }

        public boolean equals(Object obj) {
            if (obj instanceof ValueSetComparator) {
                return this.breakTiesByName == ((ValueSetComparator) obj).breakTiesByName;
            }

            return false;
        }
    }

    class NameSetComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            if (o1 instanceof InstructionSet && o2 instanceof InstructionSet) {
                InstructionSet s1 = (InstructionSet) o1;
                InstructionSet s2 = (InstructionSet) o2;

                return s1.getName().compareTo(s2.getName());
            }

            throw new RuntimeException("Can only compare InstructionSets!");
        }

        public boolean equals(Object obj) {
            if (obj instanceof NameSetComparator) {
                return true;
            }

            return false;
        }
    }

}
