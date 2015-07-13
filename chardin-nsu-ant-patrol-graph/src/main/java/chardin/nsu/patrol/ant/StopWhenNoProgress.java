package chardin.nsu.patrol.ant;

import chardin.nsu.patrol.graph.GraphData;
import java.util.function.Predicate;

/**
 *
 * @author Chad
 */
public class StopWhenNoProgress implements Predicate<GraphData<Double, Object>> {
    private double lastAverage = Double.POSITIVE_INFINITY;
    
    @Override
    public boolean test(GraphData<Double, Object> graphData) {
        final double average = graphData.getVertexStream(0.0).mapToDouble(x -> x).average().getAsDouble();
        final boolean stop;
        
        if(graphData.getVertexData().stream().allMatch(v -> v.isPresent())) {
            stop = average == lastAverage;
        }
        else {
            stop = false;
        }
        
        lastAverage = average;
        return stop;
    }
    
}
