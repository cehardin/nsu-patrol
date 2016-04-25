package edu.nova.chardin.patrol.agent;

import java.util.Set;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 *
 * @author cehar
 */
public interface Context {
    public int getK();
    public Integer getVertex();
    public Set<DefaultWeightedEdge> getEdges();
    public Set<Integer> getCriticalVertices();
    public int getDistance(DefaultWeightedEdge edge, Integer criticalVertex);
    public int getTimestepsUnoccupied(Integer criticalVertex);
    
}
