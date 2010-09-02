package adaptj_pool.toolkits.aspects;

import adaptj_pool.toolkits.analyses.metrics.MetricAnalysis;
import adaptj_pool.toolkits.analyses.metrics.ProgramSizeMetric;
import adaptj_pool.util.xml.*;
import adaptj_pool.toolkits.EventDependency;
import adaptj_pool.event.*;
import adaptj_pool.Scene;

public class AspectSizeMetrics extends MetricAnalysis {
    private ProgramSizeMetric psm;

    // Computations
    private long deadSize;
    private long appDeadSize;
    private double codeCoverage;
    private double appCodeCoverage;

    public AspectSizeMetrics(String name) {
        super(name, "Aspect Size Metrics", "Measures size-related quantities related to aspects");
    }


    public EventDependency[] registerEventDependencies() {
        EventDependency[] deps = {};
        return deps;
    }

    public String[] registerOperationDependencies() {
        String[] deps = {"mp.PSM"};
        return deps;
    }
    
    public void doInit() {
        psm = (ProgramSizeMetric) Scene.v().getByName("mp.PSM");
        if (psm == null) {
            throw new NullPointerException("Failed to obtain reference to mp.PSM");
        }
    }

    public void doApply(EventBox box) {

    }

    public void computeResults() {
        long loadSize = psm.getLoadSize();
        long appLoadSize = psm.getAppLoadSize();
        long runSize = psm.getRunSize();
        long appRunSize = psm.getAppRunSize();

        deadSize = loadSize - runSize;
        appDeadSize = appLoadSize - appRunSize;

        codeCoverage = ((double) runSize) / loadSize;
        appCodeCoverage = ((double) appRunSize) / appLoadSize;
    }

    public void outputXML(XMLMetricPrinter xmlPrinter) {
        xmlPrinter.addValue("size", "deadCode", deadSize);
        xmlPrinter.addValue("size", "appDeadCode", appDeadSize);

        xmlPrinter.addValue("size", "codeCoverage", codeCoverage);
        xmlPrinter.addValue("size", "appCodeCoverage", appCodeCoverage);
    }
}

