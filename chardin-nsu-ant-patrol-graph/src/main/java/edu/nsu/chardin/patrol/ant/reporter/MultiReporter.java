package edu.nsu.chardin.patrol.ant.reporter;

import edu.nsu.chardin.patrol.ant.AntStepReporter;
import edu.nsu.chardin.patrol.graph.GraphData;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public class MultiReporter<V, E> implements AntStepReporter<V, E> {
    private final Iterable<AntStepReporter<V, E>> reporters;

    public MultiReporter(Iterable<AntStepReporter<V, E>> reporters) {
        this.reporters = reporters;
    }

    @Override
    public void report(int step, GraphData<V, E> graphData, SortedSet<Integer> locations) {
        for(final AntStepReporter<V, E> reporter : reporters) {
            reporter.report(step, graphData, locations);
        }
    }
    
    
    
    
}
