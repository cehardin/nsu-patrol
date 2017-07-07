package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.collect.ImmutableMap;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.math3.util.Pair;

public class AntiStatisticalAgentStrategy2 extends AbstractCoveringAgentStrategy2 {

  private final ChooseRandomEdgeBiasStrategy edgeBiasStrategy = new ChooseRandomEdgeBiasStrategy();
  private final ChooseLongestUnvistedCoveredVertexStrategy coveredVertexStrategy = new ChooseLongestUnvistedCoveredVertexStrategy();
  private final Map<VertexId, Double> vertexReturnTsFactors = new HashMap<>();

  public AntiStatisticalAgentStrategy2(double coveredVertexFactor) {
    super(coveredVertexFactor);
  }

  @Override
  protected void leavingVertex(AgentContext context, VertexId vertex) {
    vertexReturnTsFactors.put(vertex, ThreadLocalRandom.current().nextDouble(-1.0, 1.0));
  }

  @Override
  protected ImmutableMap<EdgeId, Double> scoreEdgeBias(AgentContext context) {
    return validateScores(edgeBiasStrategy.scoreEdgeBias(context));
  }

  @Override
  protected ImmutableMap<EdgeId, Double> scoreCoveredVertices(
          AgentContext context,
          ImmutableMap<VertexId, Integer> leftTimes,
          ImmutableMap<VertexId, Pair<EdgeId, Integer>> edgeAndArrivalTs) {

    final int currentTs = context.getCurrentTimeStep();
    final double attackInterval = context.getAttackInterval();
    final ImmutableMap<VertexId, Integer> adjustedLeftTimes = leftTimes.entrySet().stream()
            .map(entry -> {
              final VertexId vertex = entry.getKey();
              final double originalLeftTime = entry.getValue();
              final double factor = vertexReturnTsFactors.getOrDefault(vertex, 0.0);
              final int adjustedLeftTime = (int)(originalLeftTime + Math.ceil(factor * attackInterval));
              final int properLeftTime = Math.min(currentTs, Math.max(1, adjustedLeftTime));
              return new SimpleEntry<>(vertex, properLeftTime);

            })
            .collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));

    return validateScores(
            coveredVertexStrategy.scoreCoveredVertices(
              context,
              adjustedLeftTimes,
              edgeAndArrivalTs));
  }

  @Override
  protected void edgeChosen(AgentContext context, EdgeId edge) {
  }
}
