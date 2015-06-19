package chardin.nsu.patrol.ant;

import chardin.nsu.patrol.graph.GraphData;
import java.util.function.Predicate;

/**
 *
 * @author Chad
 */
public class StopWhenMinimalChangeToAverage implements Predicate<GraphData<Integer, Double, Object>> {
    private static final double MIN_CHANGE = 0.0000001;
    private double lastAverage = Double.POSITIVE_INFINITY;
    
    @Override
    public boolean test(GraphData<Integer, Double, Object> graphData) {
        final double average = graphData.getVertexStream(0.0).mapToDouble(x -> x).average().getAsDouble();
        final boolean stop;
        
        if(graphData.getVertexData().stream().allMatch(v -> v.isPresent())) {
            if(average > lastAverage) {
                final double difference = average - lastAverage;
                stop = difference < MIN_CHANGE;
            }
            else {
                stop = false;
            }
        }
        else {
            stop = false;
        }
        
        lastAverage = average;
        return stop;
    }
    
}
