package chardin.nsu.patrol.ant;

import chardin.nsu.patrol.graph.GraphData;
import chardin.nsu.patrol.graph.GraphDataStrategy;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public class AntProcessor<T, V, E> {
    private final AntStrategy<T, V, E> antStrategy;
    private final GraphDataStrategy<T, V, E> graphDataStrategy;
    private final GraphData<T, V, E> graphData;
    private final SortedSet<T> locations;

    public AntProcessor(AntStrategy<T, V, E> antStrategy, GraphDataStrategy<T, V, E> graphDataStrategy, GraphData<T, V, E> graphData, SortedSet<T> locations) {
        this.antStrategy = antStrategy;
        this.graphDataStrategy = graphDataStrategy;
        this.graphData = graphData;
        this.locations = locations;
    }
    
    
    
    public void process() {
        final Set<T> newLocations = new HashSet<>();
        
        newLocations.addAll(locations);
        
        for(final T location : locations) {
            final T newLocation = antStrategy.calculate(location, newLocations, graphData);
            newLocations.remove(location);
            newLocations.add(newLocation);
        }
        
        locations.clear();
        locations.addAll(newLocations);
        
        graphDataStrategy.process(graphData);
    }
}
