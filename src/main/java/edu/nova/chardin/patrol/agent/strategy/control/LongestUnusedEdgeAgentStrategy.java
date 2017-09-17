package edu.nova.chardin.patrol.agent.strategy.control;

import com.google.common.collect.ImmutableMap;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import org.apache.commons.math3.util.Pair;

public class LongestUnusedEdgeAgentStrategy extends AbstractUnusedEdgeAgentStrategy {

  @Override
  protected EdgeId choose(AgentContext context, ImmutableMap<EdgeId, Integer> timestepEdgeChosen) {
    return context.getIncidientEdgeIds().stream()
            .map(edge -> Pair.create(edge, timestepEdgeChosen.getOrDefault(edge, 0)))
            .min((p1, p2) -> p1.getSecond().compareTo(p2.getSecond()))
            .map(Pair::getFirst)
            .get();
  }
}
