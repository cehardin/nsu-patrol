package edu.nova.chardin.patrol.agent;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 *
 * @author cehar
 */
public interface AgentStrategy {
    DefaultWeightedEdge process(Context context);
}
