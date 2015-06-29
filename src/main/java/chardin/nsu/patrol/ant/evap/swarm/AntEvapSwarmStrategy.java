package chardin.nsu.patrol.ant.evap.swarm;

import chardin.nsu.patrol.ant.AntStrategy;
import chardin.nsu.patrol.graph.GraphData;
import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author Chad
 */
public class AntEvapSwarmStrategy implements AntStrategy<Double, Object> {

    private final double pheromoneLevel;

    public AntEvapSwarmStrategy(double pheromoneLevel) {
        this.pheromoneLevel = pheromoneLevel;
    }

    @Override
    public Set<Integer> calculate(Integer currentVertex, Set<Integer> occupiedVertices, GraphData<Double, Object> graphData) {
        final Set<Integer> allConnections = graphData.getGraph().getConnections().get(currentVertex);
        final Set<Integer> availableConnections = allConnections.stream().filter(c -> !occupiedVertices.contains(c)).collect(Collectors.toSet());
        final Set<Integer> occupiedConnections = allConnections.stream().filter(c -> occupiedVertices.contains(c)).collect(Collectors.toSet());
        final Set<Integer> result;

        if (availableConnections.isEmpty()) {
            result = Collections.emptySet();
        } else {
            final Set<Integer> unvisitedConnections = availableConnections.stream().filter(c -> !graphData.getVertexData(c).isPresent()).collect(Collectors.toSet());

            if (unvisitedConnections.isEmpty()) {
                if (occupiedConnections.isEmpty()) {
                    final SortedMap<Double, Integer> sortedByValue = new TreeMap<>();

                    for (final Integer availableConnection : availableConnections) {
                        final double value = graphData.getVertexData(availableConnection).orElse(Double.POSITIVE_INFINITY);

                        if (!sortedByValue.containsKey(value)) {
                            sortedByValue.put(value, availableConnection);
                        }
                    }

                    result = Collections.singleton(sortedByValue.values().iterator().next());
                } else {
                    result = Collections.emptySet();
                }
            } else {
                result = unvisitedConnections;
            }
        }

        for(final Integer r : result) {
            final double currentPheremoneLevel = graphData.getVertexData(r).orElse(0.0);
            final double newPheremoneLevel = currentPheremoneLevel + pheromoneLevel;
            
            graphData.setVertexData(r, newPheremoneLevel);
        }
        
        return result;
    }
}
