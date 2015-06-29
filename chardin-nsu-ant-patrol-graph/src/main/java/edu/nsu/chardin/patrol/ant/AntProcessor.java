package edu.nsu.chardin.patrol.ant;

import edu.nsu.chardin.patrol.graph.GraphData;
import edu.nsu.chardin.patrol.graph.GraphDataStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Predicate;

/**
 *
 * @author Chad
 */
public class AntProcessor<V, E> implements Runnable {

    private final AntStrategy<V, E> antStrategy;
    private final GraphDataStrategy<V, E> graphDataStrategy;
    private final GraphData<V, E> graphData;
    private final SortedSet<Integer> locations;
    private final Predicate<GraphData<V, E>> stopPredicate;
    private final AntStepReporter<V, E> reporter;

    public AntProcessor(
            AntStrategy<V, E> antStrategy,
            GraphDataStrategy<V, E> graphDataStrategy,
            GraphData<V, E> graphData,
            SortedSet<Integer> locations,
            Predicate<GraphData<V, E>> stopPredicate,
            AntStepReporter<V, E> reporter) {
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
            final List<Integer> locationsToProcess = new ArrayList<>(locations);
            for (final Integer locationToProcess : locationsToProcess) {
                final Set<Integer> newLocations = antStrategy.calculate(locationToProcess, Collections.unmodifiableSet(locations), graphData);
                locations.remove(locationToProcess);
                locations.addAll(newLocations);
            }
            graphDataStrategy.process(graphData);
            reporter.report(step, graphData, locations);
            step++;
        } while( !stopPredicate.test(graphData));
    }
}
