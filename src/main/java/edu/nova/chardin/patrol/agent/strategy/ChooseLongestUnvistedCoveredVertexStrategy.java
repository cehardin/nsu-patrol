package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public final class ChooseLongestUnvistedCoveredVertexStrategy {
  
  public ImmutableMap<EdgeId, Double> scoreCoveredVertices(
          @NonNull final AgentContext context,
          @NonNull final ImmutableMap<VertexId, Integer> leftTimes,
          @NonNull final ImmutableMap<VertexId, Pair<EdgeId, Integer>> edgeAndArrivalTsPairs) {

    final double attackInterval = context.getAttackInterval();
    final double currentTs = context.getCurrentTimeStep();

    Preconditions.checkState(context.getCurrentTimeStep() > 0);
    Preconditions.checkState(leftTimes.keySet().equals(edgeAndArrivalTsPairs.keySet()));
    Preconditions.checkState(leftTimes.values().stream()
            .mapToInt(Integer::intValue)
            .allMatch(ts -> ts > 0));
    Preconditions.checkState(edgeAndArrivalTsPairs.values().stream()
            .map(Pair::getSecond)
            .mapToInt(Integer::intValue)
            .allMatch(ts -> ts > context.getCurrentTimeStep()));
    Preconditions.checkState(edgeAndArrivalTsPairs.entrySet().stream()
            .allMatch(e -> e.getValue().getSecond() > leftTimes.get(e.getKey())));
    
    return leftTimes.entrySet().stream()
            .filter(e -> !e.getKey().equals(context.getCurrentVertex()))
            .map(entry -> {
              final VertexId vertex = entry.getKey();
              final double leftTs = entry.getValue();
              final double currentTsFactor = (currentTs - leftTs) / attackInterval;
              final Pair<EdgeId, Integer> edgeAndArrivalTsPair = edgeAndArrivalTsPairs.get(vertex);
              final EdgeId edge = edgeAndArrivalTsPair.getFirst();
              final double arriveTs = edgeAndArrivalTsPair.getSecond();
              final double arriveTsFactor = (arriveTs - leftTs) / attackInterval;
              final double score = currentTsFactor + arriveTsFactor;

              Preconditions.checkState(
                      score > 0.0 && Double.isFinite(score), 
                      "Computed a bad score of %s. leftTs=%s; arriveTs=%s; currentTs=%s",
                      score,
                      leftTs,
                      arriveTs,
                      currentTs);
              
              return new SimpleEntry<>(edge, score);
            })
            .collect(
                    ImmutableMap.toImmutableMap(
                            Entry::getKey,
                            Entry::getValue,
                            Double::sum));
  }
}
