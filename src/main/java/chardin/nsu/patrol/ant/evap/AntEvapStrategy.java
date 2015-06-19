package chardin.nsu.patrol.ant.evap;

import chardin.nsu.patrol.ant.AntStrategy;
import chardin.nsu.patrol.graph.GraphData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Chad
 */
public class AntEvapStrategy<T> implements AntStrategy<T, Double, Object> {

    private final Random random = new Random(0);
    private final double pheromoneLevel;

    public AntEvapStrategy(double pheromoneLevel) {
        this.pheromoneLevel = pheromoneLevel;
    }
    
    @Override
    public Set<T> calculate(T currentVertex, Set<T> occupiedVertices, GraphData<T, Double, Object> graphData) {
        final Set<T> connections = new HashSet<>(graphData.getGraph().getConnections().get(currentVertex));
        final T result;
        final double currrentPheromoneLevel;
        final double newPheremoneLevel;
        
        connections.removeAll(occupiedVertices);

        if (connections.isEmpty()) {
            result = currentVertex;
        } else {
            final SortedMap<Double, List<T>> sortedByValue = new TreeMap<>();
            final List<T> possibleSelections;
            final int offset;
            
            for (final T connection : connections) {
                final double value = graphData.getVertexData(connection).orElse(0.0);
                
                if(!sortedByValue.containsKey(value)) {
                    sortedByValue.put(value, new ArrayList<>());
                }
                
                sortedByValue.get(value).add(connection);
            }
            
            possibleSelections = sortedByValue.values().iterator().next();
            offset = random.nextInt(possibleSelections.size());
            result = possibleSelections.get(offset);
        }

        currrentPheromoneLevel = graphData.getVertexData(result).orElse(0.0);
        newPheremoneLevel = currrentPheromoneLevel + pheromoneLevel;
        graphData.setVertexData(result, newPheremoneLevel);
        
        return Collections.singleton(result);
    }
}
