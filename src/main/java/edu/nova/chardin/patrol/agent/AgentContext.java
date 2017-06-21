package edu.nova.chardin.patrol.agent;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
public class AgentContext {
  
  @NonNull
  Integer attackInterval;
  
  @NonNull
  VertexId currentVertex;
  
  @NonNull
  ImmutableSet<VertexId> adjacentVertices;
  
  @NonNull
  ImmutableSet<VertexId> criticalVertices;
  
  @NonNull
  Boolean underAttack;
  
  @NonNull
  Integer currentTimeStep;
  
  @NonNull
  @Getter(AccessLevel.NONE)
  PatrolGraph graph;
  
  @Getter(lazy = true)
  ImmutableSet<VertexId> possibleNextVertices = createPossibleNextVertices();
  
  private ImmutableSet<VertexId> createPossibleNextVertices() {
    return ImmutableSet.<VertexId>builder().addAll(adjacentVertices).add(currentVertex).build();
  }
  
  public Integer distanceToVertexThroughAdjacentVertex(
          @NonNull final VertexId adjacentVertex, 
          @NonNull final VertexId destinationVertex) {
    
    final int distanceToAdjacentVertex = graph.edgeWeight(currentVertex, adjacentVertex).getValue();
    final int distance;
    
    if (adjacentVertex.equals(destinationVertex)) {
      distance = distanceToAdjacentVertex;
    } else {
      final int distaceToDestinationVertex = graph.shortestPath(adjacentVertex, destinationVertex).getFirst();
      
      distance = distanceToAdjacentVertex + distaceToDestinationVertex;
    }
    
    return distance;
  }
}
