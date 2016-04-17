package edu.nova.chardin.patrol.agent;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author cehar
 */
public interface Context<V, E> {
    public int getK();
    public V getCurrentVertex();
    public Set<E> getCurrentEdges();
    public Set<V> getCriticalVertices();
    public E getEdgeToCriticalVertex(V vertex);
    public int getCriticalVerticesTimestepsUnoccupied(V vertex);
    public int getCriticalVerticesShortestPathDistace(V vertex);
    
}
