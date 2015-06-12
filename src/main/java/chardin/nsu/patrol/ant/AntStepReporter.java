package chardin.nsu.patrol.ant;

import chardin.nsu.patrol.graph.GraphData;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public interface AntStepReporter<T, V, E> {

    void report(int step, GraphData<T, V, E> graphData, SortedSet<T> locations);
}
