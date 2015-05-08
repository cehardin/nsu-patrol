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

    public AntProcessor(
            AntStrategy<T, V, E> antStrategy, 
            GraphDataStrategy<T, V, E> graphDataStrategy, 
            GraphData<T, V, E> graphData, 
            SortedSet<T> locations) {
        this.antStrategy = antStrategy;
        this.graphDataStrategy = graphDataStrategy;
        this.graphData = graphData;
        this.locations = locations;
    }

    public void process() {
        final int startingSize = locations.size();
        final int endingSize;
        
        graphDataStrategy.process(graphData);

        for (final T location : locations) {
            final T newLocation = antStrategy.calculate(location, locations, graphData);
            locations.remove(location);
            locations.add(newLocation);
        }
        
        endingSize = locations.size();
        
        if(startingSize != endingSize) {
            throw new IllegalStateException(String.format("Starting size of lcations (%s) differs from ending size (%d)", startingSize, endingSize));
        }
        
    }
}
