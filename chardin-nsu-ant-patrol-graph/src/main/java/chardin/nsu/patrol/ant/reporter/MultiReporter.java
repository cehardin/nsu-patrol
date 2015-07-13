package chardin.nsu.patrol.ant.reporter;

import chardin.nsu.patrol.ant.AntStepReporter;
import chardin.nsu.patrol.graph.GraphData;
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
