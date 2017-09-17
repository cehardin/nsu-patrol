package edu.nova.chardin.patrol.agent.strategy.control;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.math3.util.Pair;

public class ProbabilisticLongestUnusedEdgeAgentStrategy extends AbstractUnusedEdgeAgentStrategy {

  @Override
  protected EdgeId choose(AgentContext context, ImmutableMap<EdgeId, Integer> timestepEdgeChosen) {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final int currentTimeStep = context.getCurrentTimeStep();
    final ImmutableList<EdgeId> edges = context.getIncidientEdgeIds().stream()
            .map(edge -> Pair.create(edge, currentTimeStep - timestepEdgeChosen.getOrDefault(edge, 0)))
            .flatMap(p -> Collections.nCopies(p.getValue(), p.getKey()).stream())
            .collect(ImmutableList.toImmutableList());
    
    return edges.get(random.nextInt(edges.size()));
  }
}
