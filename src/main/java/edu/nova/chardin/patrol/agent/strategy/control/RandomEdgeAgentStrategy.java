package edu.nova.chardin.patrol.agent.strategy.control;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEdgeAgentStrategy extends AbstractControlAgentStrategy {

  @Override
  public EdgeId choose(AgentContext context) {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final ImmutableList<EdgeId> edgeIds = context.getIncidientEdgeIds().asList();
    
    return edgeIds.get(random.nextInt(edgeIds.size()));
  }
}
