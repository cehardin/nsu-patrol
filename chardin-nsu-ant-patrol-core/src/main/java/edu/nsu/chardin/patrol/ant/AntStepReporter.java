package edu.nsu.chardin.patrol.ant;

import edu.nsu.chardin.patrol.graph.GraphData;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public interface AntStepReporter<V, E> {

    void report(int step, GraphData<V, E> graphData, SortedSet<Integer> locations);
}
