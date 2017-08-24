package edu.nova.chardin.patrol.agent.strategy.control;

import edu.nova.chardin.patrol.agent.strategy.anti.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.concurrent.ThreadLocalRandom;

public class RandomMovementAgentStrategy implements AgentStrategy {

  @Override
  public void thwarted(VertexId vertex, ImmutableSet<VertexId> criticalVertices, int timestep, int attackInterval) {
  }

  @Override
  public EdgeId choose(AgentContext context) {
    final ImmutableList<EdgeId> edgeIds = context.getIncidientEdgeIds().asList();
    return edgeIds.get(ThreadLocalRandom.current().nextInt(edgeIds.size()));
  }
}
