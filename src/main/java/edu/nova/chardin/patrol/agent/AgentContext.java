package edu.nova.chardin.patrol.agent;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.graph.VertexId;

import java.util.Set;

public interface AgentContext {
  
  VertexId getCurrentVertex();
  
  Set<VertexId> getAdjacentVertices();
  
  default Set<VertexId> getPossibleNextVertices() {
    return ImmutableSet.<VertexId>builder()
            .addAll(getAdjacentVertices())
            .add(getCurrentVertex())
            .build();
  }
}
