package chardin.nsu.patrol.graph;

/**
 *
 * @author Chad
 * @param <T>
 * @param <V>
 * @param <E>
 */
public interface GraphDataStrategy<T,V,E> {
    
    /**
     * 
     * @param graphData 
     */
    void process(GraphData<T,V,E> graphData);
}
