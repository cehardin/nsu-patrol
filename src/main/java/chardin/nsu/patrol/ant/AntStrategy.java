package chardin.nsu.patrol.ant;

import chardin.nsu.patrol.graph.GraphData;
import java.util.Set;

/**
 *
 * @author Chad
 * @param <V>
 * @param <E>
 */
public interface AntStrategy<V, E> {
    
    /**
     * 
     * @param currentVertex
     * @param occupiedVertices
     * @param graphData
     * @return 
     */
    Set<Integer> calculate(Integer currentVertex, Set<Integer> occupiedVertices, GraphData<V,E> graphData);
    
}
