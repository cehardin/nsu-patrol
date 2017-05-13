package edu.nova.chardin.patrol.agent;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Value
public class AgentContext {
  
  @NonNull
  VertexId currentVertex;
  
  @NonNull
  ImmutableSet<VertexId> adjacentVertices;
  
  @Getter(lazy = true)
  ImmutableSet<VertexId> possibleNextVertices = createPossibleNextVertices();
  
  private ImmutableSet<VertexId> createPossibleNextVertices() {
    return ImmutableSet.<VertexId>builder().addAll(adjacentVertices).add(currentVertex).build();
  }
}
