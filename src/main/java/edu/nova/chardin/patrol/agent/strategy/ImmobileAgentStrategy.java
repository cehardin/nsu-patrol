package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.VertexId;

public class ImmobileAgentStrategy implements AgentStrategy {
  
  @Override
  public void arrived(AgentContext context) {
  }

  @Override
  public VertexId choose(AgentContext context) {
    return context.getCurrentVertex();
  }
  
}
