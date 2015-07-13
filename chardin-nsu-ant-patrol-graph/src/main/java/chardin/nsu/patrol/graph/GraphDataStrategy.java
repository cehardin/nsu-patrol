package chardin.nsu.patrol.graph;

/**
 *
 * @author Chad
 * @param <V>
 * @param <E>
 */
public interface GraphDataStrategy<V,E> {
    
    /**
     * 
     * @param graphData 
     */
    void process(GraphData<V,E> graphData);
}
