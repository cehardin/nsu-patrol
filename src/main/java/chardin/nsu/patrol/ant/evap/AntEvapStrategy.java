package chardin.nsu.patrol.ant.evap;

import chardin.nsu.patrol.ant.AntStrategy;
import chardin.nsu.patrol.graph.GraphData;
import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Chad
 */
public class AntEvapStrategy implements AntStrategy<Object, Double, Object> {

    private final double pheromoneLevel;

    public AntEvapStrategy(double pheromoneLevel) {
        this.pheromoneLevel = pheromoneLevel;
    }
    
    @Override
    public Set<Object> calculate(Object currentVertex, Set<Object> occupiedVertices, GraphData<Object, Double, Object> graphData) {
        final Set<Object> connections = graphData.getGraph().getConnections().get(currentVertex);
        final Object result;
        final double currrentPheromoneLevel;
        final double newPheremoneLevel;
        
        connections.removeAll(occupiedVertices);

        if (connections.isEmpty()) {
            result = currentVertex;
        } else {
            final SortedMap<Double, SortedSet<Object>> sortedByValue = new TreeMap<>();

            for (final Object connection : connections) {
                final double value = graphData.getVertexData(connection).orElse(Double.POSITIVE_INFINITY);
                
                if(!sortedByValue.containsKey(value)) {
                    sortedByValue.put(value, new TreeSet<>());
                }
                
                sortedByValue.get(value).add(connection);
            }
            
            result = sortedByValue.values().iterator().next().first();
        }

        currrentPheromoneLevel = graphData.getVertexData(result).orElse(0.0);
        newPheremoneLevel = currrentPheromoneLevel + pheromoneLevel;
        graphData.setVertexData(result, newPheremoneLevel);
        
        return Collections.singleton(result);
    }
}
