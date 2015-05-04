package chardin.nsu.patrol.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Chad
 * @param <T>
 * @param <V>
 * @param <E>
 */
public class GraphData<T,V,E> implements Cloneable {
    private final Graph<T> graph;
    private final Map<T,Optional<V>> vertexDataMap;
    private final Map<Set<T>, Optional<E>> edgeDataMap;
    
    /**
     * 
     * @param graph 
     */
    public GraphData(final Graph<T> graph) {
        this.graph = Objects.requireNonNull(graph);
        this.vertexDataMap = new HashMap<>();
        this.edgeDataMap = new HashMap<>();
        
        for(final T vertex : this.graph.getVertices()) {
            vertexDataMap.put(vertex, Optional.empty());
        }
        
        for(final Set<T> edge : this.graph.getEdges()) {
            edgeDataMap.put(edge, Optional.empty());
        }
    }
    
    /**
     * 
     * @param <K>
     * @param set
     * @param key 
     */
    private <K> K checkKey(final Set<K> set, K key) {
        if(set.contains(Objects.requireNonNull(key))) {
            return key;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * 
     * @param vertex 
     */
    private T checkVertex(final T vertex) {
        return checkKey(graph.getVertices(), vertex);
    }
    
    /**
     * 
     * @param edge 
     */
    private Set<T> checkEdge(final Set<T> edge) {
        return checkKey(graph.getEdges(), edge);
    }

    /**
     * 
     * @return 
     */
    public Graph<T> getGraph() {
        return graph;
    }
    
    /**
     * 
     * @param vertex
     * @return 
     */
    public Optional<V> getVertexData(final T vertex) {
        return vertexDataMap.get(checkVertex(vertex));
    }
    
    /**
     * 
     * @param vertex
     * @param vertexData
     * @return 
     */
    public Optional<V> setVertexData(final T vertex, final V vertexData) {
        return vertexDataMap.put(checkVertex(vertex), Optional.ofNullable(vertexData));
    }
    
    /**
     * 
     * @param edge
     * @return 
     */
    public Optional<E> getEdgeData(final Set<T> edge) {
        return edgeDataMap.get(checkEdge(edge));
    }
    
    /**
     * 
     * @param edge
     * @param edgeData
     * @return 
     */
    public Optional<E> setEdgeData(final Set<T> edge, final E edgeData) {
        return edgeDataMap.put(checkEdge(edge), Optional.ofNullable(edgeData));
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public GraphData<T,V,E> clone() {
        final GraphData<T,V,E> clone = new GraphData<>(graph);
        
        for(final Map.Entry<T, Optional<V>> vertexEntry : vertexDataMap.entrySet()) {
            final T vertex = vertexEntry.getKey();
            final Optional<V> vertexData = vertexEntry.getValue();
            
            clone.vertexDataMap.put(vertex, vertexData);
        }
        
        for(final Map.Entry<Set<T>, Optional<E>> edgeEntry : edgeDataMap.entrySet()) {
            final Set<T> edge = edgeEntry.getKey();
            final Optional<E> edgeData = edgeEntry.getValue();
            
            clone.edgeDataMap.put(edge, edgeData);
        }
        
        return clone;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(graph, vertexDataMap, edgeDataMap);
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean equal;
        
        if(this == o) {
            equal = false;
        }
        else if(getClass().isInstance(o)) {
            final GraphData<?,?,?> other = getClass().cast(o);
            
            equal = Objects.equals(graph, other.graph) && Objects.equals(vertexDataMap, other.vertexDataMap) && Objects.equals(edgeDataMap, other.edgeDataMap);
        }
        else {
            equal = false;
        }
        
        return equal;
    }
}
