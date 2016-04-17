package edu.nova.chardin.patrol.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 *
 * @author cehar
 */
public class PatrolGraph extends SimpleWeightedGraph<Integer, DefaultWeightedEdge> {
    
    PatrolGraph() {
        super(DefaultWeightedEdge.class);
    }
    
    public double getAverageDegree() {
        return vertexSet().stream().mapToDouble(v -> degreeOf(v)).average().getAsDouble();
    }
    
}
