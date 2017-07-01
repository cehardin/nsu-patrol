package edu.nova.chardin.patrol.agent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.graph.EdgeId;
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
  ImmutableSet<VertexId> criticalVertices;
  
  @NonNull
  @Getter(AccessLevel.PRIVATE)
  ImmutableMap<EdgeId, VertexId> incidientEdgeIdToAdjacentVertexMap;
  
  @NonNull
  Integer currentTimeStep;
  
  @NonNull
  @Getter(AccessLevel.NONE)
  PatrolGraph graph;
  
  @Getter(lazy = true)
  ImmutableSet<EdgeId> incidientEdgeIds = createIncidientEdgeIds();
  
   private ImmutableSet<EdgeId> createIncidientEdgeIds() {
     return incidientEdgeIdToAdjacentVertexMap.keySet();
  }
  
  public Integer distanceToVertexThroughIncidentEdge(
          @NonNull final EdgeId edgeId, 
          @NonNull final VertexId destinationVertex) {
    
    final VertexId adjacentVertex = getIncidientEdgeIdToAdjacentVertexMap().get(edgeId);
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
