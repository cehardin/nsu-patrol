package edu.nova.chardin.patrol.agent;

import com.google.common.base.Preconditions;
import java.util.Optional;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 *
 * @author cehar
 */
public class Agent {
    private final AgentStrategy strategy;
    private Optional<DefaultWeightedEdge> currentEdge = Optional.empty();
    private int vertex;
    private int timestepsOnCurrentEdge = 0;
    private int edgeWeight = 0;

    public Agent(AgentStrategy strategy, int vertex) {
        this.strategy = strategy;
        this.vertex = vertex;
    }

    public AgentStrategy getStrategy() {
        return strategy;
    }

    public DefaultWeightedEdge getCurrentEdge() {
        return currentEdge.get();
    }
    
    public boolean isOnEdge() {
        return currentEdge.isPresent();
    }

    public int getVertex() {
        return vertex;
    }

    public int getTimestepsOnCurrentEdge() {
        return timestepsOnCurrentEdge;
    }

    public void moveToEdge(DefaultWeightedEdge edge, int edgeWeight, int destinationVertex) {
        Preconditions.checkState(!isOnEdge(), "Already on edge");
        currentEdge = Optional.of(edge);
        vertex = destinationVertex;
        this.edgeWeight = edgeWeight;
        timestepsOnCurrentEdge = 0;
    }
    
    public boolean incrementOnEdge() {
        final boolean done;
        
        Preconditions.checkState(isOnEdge(), "not on edge");
        timestepsOnCurrentEdge++;
        
        if(timestepsOnCurrentEdge == edgeWeight) {
            currentEdge = Optional.empty();
            edgeWeight = 0;
            timestepsOnCurrentEdge = 0;
            done = true;
        }
        else {
            done = false;
        }
        
        return done;
    }
}
