package adaptj_pool.toolkits.aspects;

import adaptj_pool.toolkits.analyses.metrics.MetricAnalysis;
import adaptj_pool.toolkits.*;
import adaptj_pool.util.xml.*;
import adaptj_pool.event.*;
import org.apache.bcel.Constants;
import org.apache.bcel.generic.InstructionHandle;
import adaptj_pool.spec.AdaptJSpecConstants;
import adaptj_pool.util.text.OptionStringParser;
import adaptj_pool.util.text.HelpDisplayManager;
import adaptj_pool.util.BytecodeResolver;
import adaptj_pool.util.MethodEntity;
import adaptj_pool.toolkits.analyses.IDResolver;

import java.text.DecimalFormat;
import java.util.*;

import it.unimi.dsi.fastUtil.*;

public class AspectMetrics extends MetricAnalysis {
    private static final int ADV_EXEC_NEVER = 0;
    private static final int ADV_EXEC_SOMETIMES = 1;
    private static final int ADV_EXEC_ALWAYS = 2;

    private long[] kindTagCounts;
    private long[] app_kindTagCounts;
    private Map shadowAdviceInvokeCounts;
    private Map sourceAdviceInvokeCounts;
    private boolean provide_default;
    private Int2ObjectOpenHashMap envIDtoBranchInfo;
    private HashSet testInstructions;
    private long[] bins;
    
    public AspectMetrics(String name) {
        this(name, true);
    }

    public AspectMetrics(String name, boolean provide_default_tag) {
        super(name, "Aspect Metrics", "Measures various aspect-related quantities");

        this.provide_default = provide_default_tag;
    }

    public boolean getProvideDefaultTag() {
        return provide_default;
    }
    
    public void setProvideDefaultTag(boolean value) {
        this.provide_default = value;
    }

    public void setOption(String name, String value) {
        if (name.equals("provideDefaultTag")) {
            provide_default = OptionStringParser.parseBoolean(value);
        } else {
            super.setOption(name, value);
        }
    }

    public String getOption(String name) {
        if (name.equals("provideDefaultTag")) {
            if (provide_default) {
                return "true";
            } else {
                return "false";
            }
        }

        return super.getOption(name);
    }

    public void displayHelp(HelpDisplayManager manager) {
        super.displayHelp(manager);

        manager.displayOptionHelp("provideDefaultTag[:boolean]", "Specifies whether the analysis provides a default tag for untagged instructions");
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
        String[] deps = { "aspects.AspectInstTagResolver" };
        return deps;
    }
    
    public void doInit() {
        kindTagCounts = new long[AspectTagConstants.ASPECT_TAG_COUNT];
        app_kindTagCounts = new long[AspectTagConstants.ASPECT_TAG_COUNT];
        shadowAdviceInvokeCounts = new HashMap();
        sourceAdviceInvokeCounts = new HashMap();
        envIDtoBranchInfo = new Int2ObjectOpenHashMap();
        testInstructions = new HashSet();
    }

    public void doApply(EventBox box) {
        AdaptJEvent event = box.getEvent();

        InstructionStartEvent e = ((InstructionStartEvent) event);
        
        InstructionHandle ih = BytecodeResolver.v().getInstructionHandle(e.getMethodID(), e.getOffset());
        if (ih != null) {
            MethodEntity me = IDResolver.v().getMethodEntity(e.getMethodID());
            int kindTag = ih.getKindTag();
            ShadowSourceTag shadowSourceTag = ih.getShadowSourceTag();
            
            if (kindTag != AspectTagConstants.ASPECT_TAG_INVALID) {
                kindTagCounts[kindTag] += 1L;
                if (me != null && !me.isStandardLib()) {
                    app_kindTagCounts[kindTag] += 1L;
                }
            } else if (provide_default) {
                kindTagCounts[AspectTagConstants.ASPECT_TAG_REGULAR] += 1L;
                if (me != null && !me.isStandardLib()) {
                    app_kindTagCounts[AspectTagConstants.ASPECT_TAG_REGULAR] += 1L;
                }
            }
            
            if(shadowSourceTag != null) {
           		shadowSourceTag.incrInstrCount();
           		if(kindTag == AspectTagConstants.ASPECT_TAG_ADV_EXECUTION) {
           			shadowSourceTag.incrAdviceInvokeCount();
           		}
            	if (me != null && !me.isStandardLib()) {
            		shadowSourceTag.incrAppInstrCount();
               		if(kindTag == AspectTagConstants.ASPECT_TAG_ADV_EXECUTION) {
               			shadowSourceTag.incrAppAdviceInvokeCount();
               		}
            	}
            }
            	
            int env_id = e.getEnvID();
            BranchInfo binfo;
            if (!envIDtoBranchInfo.containsKey(env_id)) {
                binfo = new BranchInfo();
                envIDtoBranchInfo.put(env_id, binfo);
            } else {
                binfo = (BranchInfo) envIDtoBranchInfo.get(env_id);
            }

            if (binfo.testInst != null) {
                binfo.testInst.markTaken(ih != binfo.succeedTarget);
                binfo.testInst = null;
                binfo.succeedTarget = null;
            }

            // Check if we have a dynamic test
            if (kindTag == AspectTagConstants.ASPECT_TAG_ADV_TEST) {
                // This is a dynamic test. If this branch is taken, the
                // test has failed (advice is not executed).
                switch (e.getCode()) {
                    case Constants.IFEQ:
                    case Constants.IFNE:
                        binfo.testInst = ih;
                        binfo.succeedTarget = ih.getNext();
                        testInstructions.add(ih);
                    default:
                        // no branch. Skip.
                        break;
                }
            }
        }
    }

    public long getCount(int tag) {
        return kindTagCounts[tag];
    }

    public long getAppCount(int tag) {
        return app_kindTagCounts[tag];
    }

    public void computeResults() {
        bins = new long[3];
        Iterator it = testInstructions.iterator();
        while (it.hasNext()) {
            InstructionHandle ih = (InstructionHandle) it.next();

            if (ih.getTakenCount() > 0) {
                if (ih.getFallThroughCount() > 0) {
                    bins[ADV_EXEC_SOMETIMES] += 1L;
                } else {
                    bins[ADV_EXEC_NEVER] += 1L;
                }
            } else {
                bins[ADV_EXEC_ALWAYS] += 1L;
            }
        }
        
        // sum advice invokes for each shadow/source id
        for(Iterator i = AspectInstTagResolver.v().getShadowSourceTags().values().iterator();
            i.hasNext();)
        {
        	ShadowSourceTag sst = (ShadowSourceTag)i.next();
        	
        	Long shadowAdviceInvokeCount = (Long)shadowAdviceInvokeCounts.get(new Integer(sst.getShadowId()));
        	if(shadowAdviceInvokeCount == null) {
        		shadowAdviceInvokeCount = new Long(0L);
        	}
        	shadowAdviceInvokeCount = new Long(shadowAdviceInvokeCount.longValue() + sst.getAdviceInvokeCount());
        	shadowAdviceInvokeCounts.put(new Integer(sst.getShadowId()), shadowAdviceInvokeCount);
        	
        	Long sourceAdviceInvokeCount = (Long)sourceAdviceInvokeCounts.get(new Integer(sst.getSourceId()));
        	if(sourceAdviceInvokeCount == null) {
        		sourceAdviceInvokeCount = new Long(0L);
        	}
        	sourceAdviceInvokeCount = new Long(sourceAdviceInvokeCount.longValue() + sst.getAdviceInvokeCount());
        	sourceAdviceInvokeCounts.put(new Integer(sst.getSourceId()), sourceAdviceInvokeCount);
        }
    }

    public void analysisDone() {
        DecimalFormat format = new DecimalFormat("0.0%");
        long total = 0L;
        for (int i = 0; i < kindTagCounts.length; i++) {
            total += kindTagCounts[i];
        }
        System.out.println("Aspect Tag Mix" + (provide_default ? " (default tag mode)" : ""));
        System.out.println("---------------------");
        for (int i = 0; i < AspectTagConstants.ASPECT_TAG_COUNT; i++) {
            System.out.print((i < 10 ? " " + i : String.valueOf(i)) + ": " + kindTagCounts[i]);
            System.out.print(" (" + format.format(((double) kindTagCounts[i]) / total) + ")");
            System.out.println();
        }
        System.out.println("---------------------");

        total = 0L;
        for (int i = 0; i < app_kindTagCounts.length; i++) {
            total += app_kindTagCounts[i];
        }
        System.out.println("Aspect App Tag Mix" + (provide_default ? " (default tag mode)" : ""));
        System.out.println("---------------------");
        for (int i = 0; i < AspectTagConstants.ASPECT_TAG_COUNT; i++) {
            System.out.print((i < 10 ? " " + i : String.valueOf(i)) + ": " + app_kindTagCounts[i]);
            System.out.print(" (" + format.format(((double) app_kindTagCounts[i]) / total) + ")");
            System.out.println();
        }
        System.out.println("---------------------");

        System.out.println("Shadow/Source Tag Counts");
        System.out.println("---------------------");
        for(Iterator i = AspectInstTagResolver.v().getShadowSourceTags().values().iterator();
            i.hasNext();)
        {
        	ShadowSourceTag tag = (ShadowSourceTag)i.next();
        	System.out.println(tag.getShadowId() + "/" + tag.getSourceId() + ": " + tag.getInstrCount());
        }
        System.out.println("---------------------");
        
        System.out.println("Shadow/Source Advice Invoke Counts");
        System.out.println("---------------------");
        for(Iterator i = AspectInstTagResolver.v().getShadowSourceTags().values().iterator();
            i.hasNext();)
        {
        	ShadowSourceTag tag = (ShadowSourceTag)i.next();
        	System.out.println(tag.getShadowId() + "/" + tag.getSourceId() + ": " + tag.getAdviceInvokeCount());
        }
        System.out.println("---------------------");
                
        System.out.println("Shadow/Source App Advice Invoke Counts");
        System.out.println("---------------------");
        for(Iterator i = AspectInstTagResolver.v().getShadowSourceTags().values().iterator();
            i.hasNext();)
        {
        	ShadowSourceTag tag = (ShadowSourceTag)i.next();
        	System.out.println(tag.getShadowId() + "/" + tag.getSourceId() + ": " + tag.getAppAdviceInvokeCount());
        }
        System.out.println("---------------------");
                
    }

    public void outputXML(XMLMetricPrinter xmlPrinter) {
        double dblVal;
        long total;
        
        total = 0L;
        for (int i = 0; i < kindTagCounts.length; i++) {
            total += kindTagCounts[i];
        }
        // aspects.tagMix.bin
        xmlPrinter.addBin("aspects", "tagMix");
        for (int i = 0; i < AspectTagConstants.ASPECT_TAG_COUNT; i++) {
            dblVal = ((double) kindTagCounts[i]) / total;
            xmlPrinter.addBinRange("aspects", "tagMix", i, i, dblVal);
        }
        // aspects.executionOverhead.value
        dblVal = 1.0 - ((double) kindTagCounts[AspectTagConstants.ASPECT_TAG_REGULAR]
                + kindTagCounts[AspectTagConstants.ASPECT_TAG_ADVICE_BODY]) / total;
        xmlPrinter.addValue("aspects", "executionOverhead", dblVal);

        // aspects.appTagMix.bin
        total = 0L;
        for (int i = 0; i < app_kindTagCounts.length; i++) {
            total += app_kindTagCounts[i];
        }
        xmlPrinter.addBin("aspects", "appTagMix");
        for (int i = 0; i < AspectTagConstants.ASPECT_TAG_COUNT; i++) {
            dblVal = ((double) app_kindTagCounts[i]) / total;
            xmlPrinter.addBinRange("aspects", "appTagMix", i, i, dblVal);
        }
        // aspects.appExecutionOverhead.value
        dblVal = 1.0 - ((double) app_kindTagCounts[AspectTagConstants.ASPECT_TAG_REGULAR]
                + app_kindTagCounts[AspectTagConstants.ASPECT_TAG_ADVICE_BODY]) / total;
        xmlPrinter.addValue("aspects", "appExecutionOverhead", dblVal);

        total = 0L;
        for (int i = 0; i < bins.length; i++) {
            total += bins[i];
        }
        
        // aspects.adviceExecution.bin
        xmlPrinter.addBin("aspects", "adviceExecution");
        for (int i = 0; i < bins.length; i++) {
            dblVal = ((double) bins[i]) / total;
            xmlPrinter.addBinRange("aspects", "adviceExecution", i, i, dblVal);
        }
        // aspects.adviceExecution.bin
        xmlPrinter.addBin("aspects", "adviceExecutionCounts");
        for (int i = 0; i < bins.length; i++) {
            dblVal = (double) bins[i];
            xmlPrinter.addBinRange("aspects", "adviceExecutionCounts", i, i, dblVal);
        }
        
        // aspects.adviceExecutionPerShadow.bin
        xmlPrinter.addBin("aspects", "adviceExecutionPerShadow");
        for(Iterator i = shadowAdviceInvokeCounts.entrySet().iterator();
        	i.hasNext();)
        {
        	Map.Entry entry = (Map.Entry)i.next();
        	int shadowId = ((Integer)entry.getKey()).intValue();
        	long count = ((Long)entry.getValue()).longValue();
        	xmlPrinter.addBinRange("aspects", "adviceExecutionPerShadow", shadowId, shadowId, count);
        }
        
        // aspects.adviceExecutionPerShadow.bin
        xmlPrinter.addBin("aspects", "adviceExecutionPerSource");
        for(Iterator i = sourceAdviceInvokeCounts.entrySet().iterator();
        	i.hasNext();)
        {
        	Map.Entry entry = (Map.Entry)i.next();
        	int sourceId = ((Integer)entry.getKey()).intValue();
        	long count = ((Long)entry.getValue()).longValue();
        	xmlPrinter.addBinRange("aspects", "adviceExecutionPerSource", sourceId, sourceId, count);
        }
    }

    class BranchInfo {
        public InstructionHandle testInst;
        public InstructionHandle succeedTarget;
    }
}
