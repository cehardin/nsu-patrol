package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

public abstract class AbstractCoveringAgentStrategy implements AgentStrategy {

  /**
   * The covered vertices mapped to the last timestep it was visited.
   */
  private final Map<VertexId, Integer> coveredVertices = new HashMap<>();

  @Override
  public final void thwarted(
          @NonNull final VertexId vertex, 
          @NonNull final ImmutableSet<VertexId> criticalVertices, 
          final int timestep, 
          final int attackInterval) {
    
    if (!criticalVertices.contains(vertex)) {
      coveredVertices.putIfAbsent(vertex, timestep);
    }
  }

  @Override
  public final EdgeId choose(@NonNull final AgentContext context) {
    
    coveredVertices.computeIfPresent(context.getCurrentVertex(), (v, ts) -> context.getCurrentTimeStep());
    return choose(context, ImmutableMap.copyOf(coveredVertices));
  }
  
  protected abstract EdgeId choose(AgentContext context, ImmutableMap<VertexId, Integer> coveredVertices);

}
