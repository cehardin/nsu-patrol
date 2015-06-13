package chardin.nsu.patrol.ant.evap;

import chardin.nsu.patrol.graph.GraphData;
import chardin.nsu.patrol.graph.GraphDataStrategy;
import java.util.Optional;

/**
 *
 * @author Chad
 */
public class EvapGraphDataStrategy<T> implements GraphDataStrategy<T, Double, Object> {
    private final double evaporationFactor;

    public EvapGraphDataStrategy(double evaporationFactor) {
        this.evaporationFactor = evaporationFactor;
    }
    
    @Override
    public void process(GraphData<T, Double, Object> graphData) {
        for(final T vertex : graphData.getGraph().getVertices()) {
            final Optional<Double> currentValue = graphData.getVertexData(vertex);
            
            if(currentValue.isPresent()) {
                double newValue = currentValue.get() * evaporationFactor;
                
                graphData.setVertexData(vertex, newValue);
            }
        }
    }
    
}
