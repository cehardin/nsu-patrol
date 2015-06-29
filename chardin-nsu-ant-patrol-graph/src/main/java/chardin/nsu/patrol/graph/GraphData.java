package chardin.nsu.patrol.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author Chad
 * @param <V>
 * @param <E>
 */
public class GraphData<V,E> implements Cloneable {
    private final Graph graph;
    private final Map<Integer,Optional<V>> vertexDataMap;
    private final Map<Set<Integer>, Optional<E>> edgeDataMap;
    
    /**
     * 
     * @param graph 
     */
    public GraphData(final Graph graph) {
        this.graph = Objects.requireNonNull(graph);
        this.vertexDataMap = new HashMap<>();
        this.edgeDataMap = new HashMap<>();
        
        for(final Integer vertex : this.graph.getVertices()) {
            vertexDataMap.put(vertex, Optional.empty());
        }
        
        for(final Set<Integer> edge : this.graph.getEdges()) {
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
    private Integer checkVertex(final Integer vertex) {
        return checkKey(graph.getVertices(), vertex);
    }
    
    /**
     * 
     * @param edge 
     */
    private Set<Integer> checkEdge(final Set<Integer> edge) {
        return checkKey(graph.getEdges(), edge);
    }

    /**
     * 
     * @return 
     */
    public Graph getGraph() {
        return graph;
    }
    
    /**
     * 
     * @param vertex
     * @return 
     */
    public Optional<V> getVertexData(final Integer vertex) {
        return vertexDataMap.get(checkVertex(vertex));
    }
    
    /**
     * 
     * @param vertex
     * @param vertexData
     * @return 
     */
    public Optional<V> setVertexData(final Integer vertex, final V vertexData) {
        return vertexDataMap.put(checkVertex(vertex), Optional.ofNullable(vertexData));
    }
    
    public Collection<Optional<V>> getVertexData() {
        return Collections.unmodifiableCollection(vertexDataMap.values());
    }
    
    public Stream<V> getVertexStream() {
        return getVertexData().stream().map(Optional::get);
    }
    
    public Stream<V> getVertexStream(final V defaultValue) {
        return getVertexData().stream().map((o) -> o.orElse(defaultValue));
    }
    
    /**
     * 
     * @param edge
     * @return 
     */
    public Optional<E> getEdgeData(final Set<Integer> edge) {
        return edgeDataMap.get(checkEdge(edge));
    }
    
    /**
     * 
     * @param edge
     * @param edgeData
     * @return 
     */
    public Optional<E> setEdgeData(final Set<Integer> edge, final E edgeData) {
        return edgeDataMap.put(checkEdge(edge), Optional.ofNullable(edgeData));
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public GraphData<V,E> clone() {
        final GraphData<V,E> clone = new GraphData<>(graph);
        
        for(final Map.Entry<Integer, Optional<V>> vertexEntry : vertexDataMap.entrySet()) {
            final Integer vertex = vertexEntry.getKey();
            final Optional<V> vertexData = vertexEntry.getValue();
            
            clone.vertexDataMap.put(vertex, vertexData);
        }
        
        for(final Map.Entry<Set<Integer>, Optional<E>> edgeEntry : edgeDataMap.entrySet()) {
            final Set<Integer> edge = edgeEntry.getKey();
            final Optional<E> edgeData = edgeEntry.getValue();
            
            clone.edgeDataMap.put(edge, edgeData);
        }
        
        return clone;
    }
    
    public double[][] getVextexValueGrid() {
        throw new UnsupportedOperationException();
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
            final GraphData<?,?> other = getClass().cast(o);
            
            equal = Objects.equals(graph, other.graph) && Objects.equals(vertexDataMap, other.vertexDataMap) && Objects.equals(edgeDataMap, other.edgeDataMap);
        }
        else {
            equal = false;
        }
        
        return equal;
    }
}
