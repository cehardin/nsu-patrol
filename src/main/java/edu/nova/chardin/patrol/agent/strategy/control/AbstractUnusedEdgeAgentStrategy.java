package edu.nova.chardin.patrol.agent.strategy.control;

import com.google.common.collect.ImmutableMap;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUnusedEdgeAgentStrategy extends AbstractControlAgentStrategy {

  private final Map<EdgeId, Integer> timestepEdgeChosen = new HashMap<>();
  
  @Override
  public final EdgeId choose(AgentContext context) {
    final EdgeId chosenEdge = choose(context, ImmutableMap.copyOf(timestepEdgeChosen));
    
    timestepEdgeChosen.put(chosenEdge, context.getCurrentTimeStep());
    
    return chosenEdge;
  }
  
  protected abstract EdgeId choose(AgentContext context, ImmutableMap<EdgeId, Integer> timestepEdgeChosen);
}
