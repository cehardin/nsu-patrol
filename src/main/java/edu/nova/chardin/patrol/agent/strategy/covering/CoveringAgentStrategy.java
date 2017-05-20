package edu.nova.chardin.patrol.agent.strategy.covering;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public  class CoveringAgentStrategy implements AgentStrategy {
  
  @NonNull
  @Getter(AccessLevel.NONE)
  CoveringStrategy coveringStrategy;
  
  @Getter(AccessLevel.NONE)
  Map<VertexId, Integer> coveredVertices = new HashMap<>();

  @Override
  public final void arrived(final AgentContext context) {
    final VertexId vertex = context.getCurrentVertex();
    
    if (context.getUnderAttack() && !context.getCriticalVertices().contains(vertex)) {
      coveredVertices.put(vertex, 0);
    }
    
    coveringStrategy.arrived(context, coveredVertices);
  }

  @Override
  public final VertexId choose(AgentContext context) {
    return coveringStrategy.choose(context, coveredVertices);
  }
}
