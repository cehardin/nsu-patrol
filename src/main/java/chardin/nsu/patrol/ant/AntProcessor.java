package chardin.nsu.patrol.ant;

import chardin.nsu.patrol.graph.GraphData;
import chardin.nsu.patrol.graph.GraphDataStrategy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.function.Predicate;

/**
 *
 * @author Chad
 */
public class AntProcessor<T, V, E> implements Runnable {

    private final AntStrategy<T, V, E> antStrategy;
    private final GraphDataStrategy<T, V, E> graphDataStrategy;
    private final GraphData<T, V, E> graphData;
    private final SortedSet<T> locations;
    private final Predicate<GraphData<T, V, E>> stopPredicate;
    private final AntStepReporter<T, V, E> reporter;

    public AntProcessor(
            AntStrategy<T, V, E> antStrategy,
            GraphDataStrategy<T, V, E> graphDataStrategy,
            GraphData<T, V, E> graphData,
            SortedSet<T> locations,
            Predicate<GraphData<T, V, E>> stopPredicate,
            AntStepReporter<T, V, E> reporter) {
        this.antStrategy = antStrategy;
        this.graphDataStrategy = graphDataStrategy;
        this.graphData = graphData;
        this.locations = locations;
        this.stopPredicate = stopPredicate;
        this.reporter = reporter;
    }

    @Override
    public void run() {
        int step = 1;
        do {
            final Stack<T> locationStack = new Stack<>();
            
            locationStack.addAll(locations);
            
            for (final T location : locations) {
                final Set<T> newLocations = antStrategy.calculate(location, Collections.unmodifiableSet(locations), graphData);
                locations.remove(location);
                locations.addAll(newLocations);
            }
            graphDataStrategy.process(graphData);
            reporter.report(step, graphData, locations);
            step++;
        } while( !stopPredicate.test(graphData));
    }
}
