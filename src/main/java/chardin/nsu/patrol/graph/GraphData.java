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
    private final Map<T,VertexData<V>> vertexDataMap;
    private final Map<Set<T>, EdgeData<E>> edgeDataMap;
    
    /**
     * 
     * @param graph 
     */
    public GraphData(final Graph<T> graph) {
        this.graph = Objects.requireNonNull(graph);
        this.vertexDataMap = new HashMap<>();
        this.edgeDataMap = new HashMap<>();
    }
    
    /**
     * 
     * @param <K>
     * @param set
     * @param key 
     */
    private <K> void checkKey(final Set<K> set, K key) {
        if(!set.contains(Objects.requireNonNull(key))) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * 
     * @param vertex 
     */
    private void checkVertex(final T vertex) {
        checkKey(graph.getVertices(), vertex);
    }
    
    /**
     * 
     * @param edge 
     */
    private void checkEdge(final Set<T> edge) {
        checkKey(graph.getEdges(), edge);
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
    public Optional<VertexData<V>> getVertexData(final T vertex) {
        checkVertex(vertex);
        return Optional.ofNullable(vertexDataMap.get(vertex));
    }
    
    /**
     * 
     * @param vertex
     * @param vertexData
     * @return 
     */
    public Optional<VertexData<V>> setVertexData(final T vertex, final VertexData<V> vertexData) {
        checkVertex(vertex);
        return Optional.ofNullable(vertexDataMap.put(vertex, vertexData));
    }
    
    /**
     * 
     * @param edge
     * @return 
     */
    public Optional<EdgeData<E>> getEdgeData(final Set<T> edge) {
        checkEdge(edge);
        return Optional.ofNullable(edgeDataMap.get(edge));
    }
    
    /**
     * 
     * @param edge
     * @param edgeData
     * @return 
     */
    public Optional<EdgeData<E>> setEdgeData(final Set<T> edge, final EdgeData<E> edgeData) {
        checkEdge(edge);
        final Set<T> key = new HashSet<>(edge);
        return Optional.ofNullable(edgeDataMap.put(key, edgeData));
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public GraphData<T,V,E> clone() {
        final GraphData<T,V,E> clone = new GraphData<>(graph);
        
        for(final Map.Entry<T, VertexData<V>> vertexEntry : vertexDataMap.entrySet()) {
            final T vertex = vertexEntry.getKey();
            final VertexData<V> vertexData = vertexEntry.getValue().clone();
            
            clone.vertexDataMap.put(vertex, vertexData);
        }
        
        for(final Map.Entry<Set<T>, EdgeData<E>> edgeEntry : edgeDataMap.entrySet()) {
            final Set<T> edge = edgeEntry.getKey();
            final EdgeData<E> edgeData = edgeEntry.getValue().clone();
            
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
