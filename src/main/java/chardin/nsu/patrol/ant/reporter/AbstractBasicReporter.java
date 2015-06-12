package chardin.nsu.patrol.ant.reporter;

import chardin.nsu.patrol.ant.AntStepReporter;
import chardin.nsu.patrol.graph.GraphData;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public abstract class AbstractBasicReporter implements AntStepReporter<Object, Double, Object>{

    @Override
    public final void report(int step, GraphData<Object, Double, Object> graphData, SortedSet<Object> locations) {
        final double average = graphData.getVertexStream(0.0).mapToDouble(x -> x).average().getAsDouble();
        final double min = graphData.getVertexStream(0.0).mapToDouble(x -> x).min().getAsDouble();
        final double max = graphData.getVertexStream(0.0).mapToDouble(x -> x).max().getAsDouble();
        
        report(step, step, max, min, max);
    }

    protected abstract void report(int step, int numAnts, double avg, double min, double max);
    
    
}
