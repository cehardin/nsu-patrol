package chardin.nsu.patrol.ant;

import chardin.nsu.patrol.graph.GraphData;
import java.util.Set;

/**
 *
 * @author Chad
 * @param <T>
 * @param <V>
 * @param <E>
 */
public interface AntStrategy<T, V, E> {
    
    /**
     * 
     * @param currentVertex
     * @param occupiedVertices
     * @param graphData
     * @return 
     */
    T calculate(T currentVertex, Set<T> occupiedVertices, GraphData<T,V,E> graphData);
    
}
