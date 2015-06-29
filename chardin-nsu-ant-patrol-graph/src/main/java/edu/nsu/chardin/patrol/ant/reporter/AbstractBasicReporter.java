package edu.nsu.chardin.patrol.ant.reporter;

import edu.nsu.chardin.patrol.ant.AntStepReporter;
import edu.nsu.chardin.patrol.graph.GraphData;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public abstract class AbstractBasicReporter implements AntStepReporter<Double, Object>{
    private long cumulativeCost = 0;
    
    protected static class BasicReport<T> {
        GraphData<Double, Object> graphData;
        SortedSet<Integer> locations;
        int step;
        double average;
        double min;
        double max;
        int numAnts;
        int cost;
        long cumulativeCost;
        int numVertices;
        long visitedVertices;
        long unvisitedVertices;
    }
    
    @Override
    public final void report(int step, GraphData<Double, Object> graphData, SortedSet<Integer> locations) {
        final BasicReport basicReport = new BasicReport();
        
        basicReport.graphData = graphData;
        basicReport.locations = locations;
        basicReport.step = step;
        basicReport.average = graphData.getVertexStream(0.0).mapToDouble(x -> x).average().getAsDouble();
        basicReport.min = graphData.getVertexStream(0.0).mapToDouble(x -> x).min().getAsDouble();
        basicReport.max = graphData.getVertexStream(0.0).mapToDouble(x -> x).max().getAsDouble();
        basicReport.numAnts = locations.size();
        basicReport.cost = basicReport.numAnts;
        basicReport.numVertices = graphData.getVertexData().size();
        basicReport.visitedVertices = graphData.getVertexData().stream().filter(v -> v.isPresent()).count();
        basicReport.unvisitedVertices = graphData.getVertexData().stream().filter(v -> !v.isPresent()).count();
        
        cumulativeCost += basicReport.cost;
        basicReport.cumulativeCost = cumulativeCost;
        
        report(basicReport);
    }

    protected abstract void report(BasicReport basicReport);
}
