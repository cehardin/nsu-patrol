package chardin.nsu.patrol.ant.reporter;

import chardin.nsu.patrol.ant.AntStepReporter;
import chardin.nsu.patrol.graph.GraphData;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public class MultiReporter<T, V, E> implements AntStepReporter<T, V, E> {
    private final Iterable<AntStepReporter<T, V, E>> reporters;

    public MultiReporter(Iterable<AntStepReporter<T, V, E>> reporters) {
        this.reporters = reporters;
    }

    @Override
    public void report(int step, GraphData<T, V, E> graphData, SortedSet<T> locations) {
        for(final AntStepReporter<T, V, E> reporter : reporters) {
            reporter.report(step, graphData, locations);
        }
    }
    
    
    
    
}
