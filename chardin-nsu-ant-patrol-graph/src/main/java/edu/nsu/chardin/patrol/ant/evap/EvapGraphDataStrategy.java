package edu.nsu.chardin.patrol.ant.evap;

import edu.nsu.chardin.patrol.graph.GraphData;
import edu.nsu.chardin.patrol.graph.GraphDataStrategy;
import java.util.Optional;

/**
 *
 * @author Chad
 */
public class EvapGraphDataStrategy implements GraphDataStrategy<Double, Object> {
    private final double evaporationFactor;

    public EvapGraphDataStrategy(double evaporationFactor) {
        this.evaporationFactor = evaporationFactor;
    }
    
    @Override
    public void process(GraphData<Double, Object> graphData) {
        for(final Integer vertex : graphData.getGraph().getVertices()) {
            final Optional<Double> currentValue = graphData.getVertexData(vertex);
            
            if(currentValue.isPresent()) {
                double newValue = currentValue.get() * evaporationFactor;
                
                graphData.setVertexData(vertex, newValue);
            }
        }
    }
    
}
