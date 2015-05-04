package chardin.nsu.patrol.ant.evap;

import chardin.nsu.patrol.graph.GraphData;
import chardin.nsu.patrol.graph.GraphDataStrategy;
import java.util.Optional;

/**
 *
 * @author Chad
 */
public class EvapGraphDataStrategy implements GraphDataStrategy<Object, Double, Object> {
    private final double startingValue;
    private final double evapFactor;

    public EvapGraphDataStrategy(double startingValue, double evapFactor) {
        this.startingValue = startingValue;
        this.evapFactor = evapFactor;
    }
    
    @Override
    public void process(GraphData<Object, Double, Object> graphData) {
        for(final Object vertex : graphData.getGraph().getVertices()) {
            final Optional<Double> currentValue = graphData.getVertexData(vertex);
            final double newValue;
            
            if(currentValue.isPresent()) {
                newValue = currentValue.get() * evapFactor;
            }
            else {
                newValue = startingValue;
            }
            
            graphData.setVertexData(vertex, newValue);
        }
    }
    
}
