package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.List;
import java.util.Random;

@Value
@Getter(AccessLevel.NONE)
public class RandomAgentStrategy implements AgentStrategy {

  Random random = new Random();
  
  @Override
  public void arrived(AgentContext context) {
  }

  @Override
  public VertexId choose(AgentContext context) {
    final List<VertexId> vertices = ImmutableList.copyOf(context.getPossibleNextVertices());
    final int index = random.nextInt(vertices.size());
    
    return vertices.get(index);
  }
  
}
