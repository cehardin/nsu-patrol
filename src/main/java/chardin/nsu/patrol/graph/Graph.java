package chardin.nsu.patrol.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Chad
 * @param <T>
 */
public class Graph<T> {
    private final Set<T> vertices;
    private final Set<Set<T>> edges;
    private final Map<T,Set<T>> connections;
    
    /**
     * 
     * @param vertices
     * @param edges 
     */
    public Graph(final Set<T> vertices, final Set<Set<T>> edges) {
        final Set<T> mutableVertices = new HashSet<>(vertices.size());
        final Set<Set<T>> mutableEdges = new HashSet<>(edges.size());
        final Map<T,Set<T>> mutableConnections = new HashMap<>();
        
        for(final T vertex : vertices) {
            mutableVertices.add(Objects.requireNonNull(vertex));
        }
        
        this.vertices = Collections.unmodifiableSet(mutableVertices);
        
        for(final Set<T> edge : edges) {
            if(edge.size() == 2) {
                final Iterator<T> edgeIterator = edge.iterator();
                final T vertex1 = Objects.requireNonNull(edgeIterator.next());
                final T vertex2 = Objects.requireNonNull(edgeIterator.next());
                final Set<T> mutableEdge = new HashSet<>();
                
                mutableEdge.add(vertex1);
                mutableEdge.add(vertex2);
                mutableConnections.putIfAbsent(vertex1, new HashSet<>());
                mutableConnections.putIfAbsent(vertex2, new HashSet<>());
                mutableConnections.get(vertex1).add(vertex2);
                mutableConnections.get(vertex2).add(vertex1);
                mutableEdges.add(Collections.unmodifiableSet(mutableEdge));
            }
            else {
                throw new IllegalArgumentException();
            }
        }
        
        for(final Map.Entry<T, Set<T>> connection : mutableConnections.entrySet()) {
            connection.setValue(Collections.unmodifiableSet(connection.getValue()));
        }
        
        this.edges = Collections.unmodifiableSet(mutableEdges);
        this.connections = Collections.unmodifiableMap(mutableConnections);
    }

    /**
     * 
     * @return 
     */
    public Set<T> getVertices() {
        return vertices;
    }

    /**
     * 
     * @return 
     */
    public Set<Set<T>> getEdges() {
        return edges;
    }

    /**
     * 
     * @return 
     */
    public Map<T, Set<T>> getConnections() {
        return connections;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        return Objects.hash(vertices, edges);
    }
    
    /**
     * 
     * @param o
     * @return 
     */
    @Override
    public boolean equals(final Object o) {
        final boolean equal;
        
        if(this == o) {
            equal = true;
        }
        else if(getClass().isInstance(o)) {
            final Graph<?> other = Graph.class.cast(o);
            equal = Objects.equals(vertices, other.vertices) && Objects.equals(edges, other.edges);
        }
        else {
            equal = false;
        }
        
        return equal;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return String.format("Graph{vertices=%s, edges=%s}", vertices, edges);
    }
}
