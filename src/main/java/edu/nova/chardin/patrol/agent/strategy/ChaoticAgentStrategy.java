package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.VertexId;

import java.util.concurrent.ThreadLocalRandom;

public class ChaoticAgentStrategy implements AgentStrategy {
  
  @Override
  public void arrived(AgentContext context) {
  }

  @Override
  public VertexId choose(AgentContext context) {
    final ImmutableList<VertexId> vertices = context.getPossibleNextVertices().asList();
    final int index = ThreadLocalRandom.current().nextInt(vertices.size());
    
    return vertices.get(index);
  }
  
}
