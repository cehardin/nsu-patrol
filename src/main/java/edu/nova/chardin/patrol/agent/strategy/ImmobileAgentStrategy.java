package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

@Value
@Getter(AccessLevel.NONE)
public class ImmobileAgentStrategy implements AgentStrategy {
  
  @Override
  public void arrived(AgentContext context) {
  }

  @Override
  public VertexId choose(AgentContext context) {
    return context.getCurrentVertex();
  }
  
}
