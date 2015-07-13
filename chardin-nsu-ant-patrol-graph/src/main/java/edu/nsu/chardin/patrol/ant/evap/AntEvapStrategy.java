package edu.nsu.chardin.patrol.ant.evap;

import edu.nsu.chardin.patrol.ant.AntStrategy;
import edu.nsu.chardin.patrol.graph.GraphData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Chad
 */
public class AntEvapStrategy implements AntStrategy<Double, Object> {

    private final Random random = new Random(0);
    private final double pheromoneLevel;

    public AntEvapStrategy(double pheromoneLevel) {
        this.pheromoneLevel = pheromoneLevel;
    }
    
    @Override
    public Set<Integer> calculate(Integer currentVertex, Set<Integer> occupiedVertices, GraphData<Double, Object> graphData) {
        final Set<Integer> connections = new HashSet<>(graphData.getGraph().getConnections().get(currentVertex));
        final Integer result;
        final double currrentPheromoneLevel;
        final double newPheremoneLevel;
        
        connections.removeAll(occupiedVertices);

        if (connections.isEmpty()) {
            result = currentVertex;
        } else {
            final SortedMap<Double, List<Integer>> sortedByValue = new TreeMap<>();
            final List<Integer> possibleSelections;
            final int offset;
            
            for (final Integer connection : connections) {
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
