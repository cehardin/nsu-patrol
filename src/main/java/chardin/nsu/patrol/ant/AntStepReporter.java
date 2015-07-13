package chardin.nsu.patrol.ant;

import chardin.nsu.patrol.graph.GraphData;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public interface AntStepReporter<V, E> {

    void report(int step, GraphData<V, E> graphData, SortedSet<Integer> locations);
}
