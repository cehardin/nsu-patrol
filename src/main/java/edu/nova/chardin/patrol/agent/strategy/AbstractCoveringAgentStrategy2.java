package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.AtomicDouble;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCoveringAgentStrategy2 implements AgentStrategy {

  private final Map<VertexId, Integer> coveredVerticesLeftTime = new HashMap<>();
  private final double coveredVertexFactor;
  
  
  @Override
  public final void thwarted(
          final VertexId vertex,
          final ImmutableSet<VertexId> criticalVertices,
          final int timestep,
          final int attackInterval) {

    if (!criticalVertices.contains(vertex)) {
      Preconditions.checkState(!coveredVerticesLeftTime.containsKey(vertex));
      coveredVerticesLeftTime.put(vertex, timestep);
    }
  }

  @Override
  public final EdgeId choose(final @NonNull AgentContext context) {
    final ImmutableMap<EdgeId, Double> edgeBiasScores;
    final ImmutableMap<EdgeId, Double> coveredVertexScores;
    final ImmutableMap<VertexId, Pair<EdgeId, Integer>> bestEdgeAndArrivalTsToCoveredVertices;
    final ImmutableMap<EdgeId, Double> rawCombinedScores;
    final ImmutableMap<EdgeId, Double> normalizeCombinedScores;
    final EdgeId chosenEdge;

    Preconditions.checkState(!context.getIncidientEdgeIds().isEmpty());
    edgeBiasScores = normalizeScores(scoreEdgeBias(context));
    Preconditions.checkState(edgeBiasScores.keySet().equals(context.getIncidientEdgeIds()));
    Preconditions.checkState(
            edgeBiasScores.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .allMatch(v -> v > 0),
            "Bad edge bias scores : %s",
            edgeBiasScores);
    
    bestEdgeAndArrivalTsToCoveredVertices = coveredVerticesLeftTime.keySet().stream()
            .collect(
                    ImmutableMap.toImmutableMap(
                            Function.identity(), 
                            v -> pickEdgeToVertexWithSoonestArrivalTime(context, v)));
    coveredVertexScores = normalizeScores(
            scoreCoveredVertices(
                    context, 
                    ImmutableMap.copyOf(coveredVerticesLeftTime), 
                    bestEdgeAndArrivalTsToCoveredVertices));
    Preconditions.checkState(
            coveredVertexScores.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .allMatch(v -> v > 0),
            "Bad covered vertex scores : %s",
            coveredVertexScores);
    
    rawCombinedScores = context.getIncidientEdgeIds().stream().collect(
            ImmutableMap.toImmutableMap(
                    Function.identity(), 
                    edgeId -> (1.0 - coveredVertexFactor) * edgeBiasScores.getOrDefault(edgeId, 0.0) + coveredVertexFactor * coveredVertexScores.getOrDefault(edgeId, 0.0)));
    Preconditions.checkState(rawCombinedScores.keySet().equals(context.getIncidientEdgeIds()));
    Preconditions.checkState(
            rawCombinedScores.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .allMatch(v -> v >= 0),
            "Bad raw combined scores : %s",
            rawCombinedScores);
    
    normalizeCombinedScores = normalizeScores(rawCombinedScores);
    
    chosenEdge = pickFromScores(normalizeCombinedScores);

    edgeChosen(context, chosenEdge);
    
    //only update the left time for a covered vertex
    if (coveredVerticesLeftTime.keySet().contains(context.getCurrentVertex())) {
      coveredVerticesLeftTime.put(
              context.getCurrentVertex(),
              context.getCurrentTimeStep());
    }

    leavingVertex(context, context.getCurrentVertex());
    
    return chosenEdge;
  }
  
   protected ImmutableMap<EdgeId, Double> validateScores(final @NonNull ImmutableMap<EdgeId, Double> scores) {

    Preconditions.checkState(
              scores.values().stream()
                      .mapToDouble(Double::doubleValue)
                      .allMatch(v -> v >= 0.0),
              "Found negative values in scores : %s", 
              scores);
    
    Preconditions.checkState(
              scores.values().stream()
                      .mapToDouble(Double::doubleValue)
                      .allMatch(Double::isFinite),
              "Found non-finite values in scores : %s", 
              scores);
    
    return scores;
    
  }
  
  private ImmutableMap<EdgeId, Double> normalizeScores(final @NonNull ImmutableMap<EdgeId, Double> rawScores) {
    
    final ImmutableMap<EdgeId, Double> normalizedScores;

    validateScores(rawScores);
    
    if (rawScores.isEmpty()) {
      normalizedScores = ImmutableMap.of();
    } else {
      final double total = rawScores.values().stream().mapToDouble(Double::doubleValue).sum();
      
      if (total > 0.0) {
        normalizedScores = rawScores.entrySet().stream()
                .collect(
                        ImmutableMap.toImmutableMap(
                                Entry::getKey, 
                                e -> e.getValue() / total));
      } else if (total == 0.0) {
        final double value = 1.0 / rawScores.size();

        normalizedScores = rawScores.entrySet().stream()
                .collect(
                        ImmutableMap.toImmutableMap(
                                Entry::getKey, 
                                Functions.constant(value)));
      } else {
        throw new IllegalStateException(String.format("Total was not >= 0.0 : %f", total));
      }
    }
    
//    if (!normalizedScores.isEmpty()) {
//      final double normalizedTotal = normalizedScores.values().stream().mapToDouble(Double::doubleValue).sum();
//
//      Preconditions.checkState(Range.normalizedTotal == 1.0, "Normalized total did not equal 1.0 : %s", normalizedTotal);
//    }

    return normalizedScores;
  }
  

  private Pair<EdgeId, Integer> pickEdgeToVertexWithSoonestArrivalTime(
          @NonNull final AgentContext context,
          @NonNull final VertexId vertex) {

    final ImmutableSet<EdgeId> incidentEdgeIds = context.getIncidientEdgeIds();
    Pair<EdgeId, Integer> picked = null;

    Preconditions.checkArgument(!incidentEdgeIds.isEmpty(), "no incident edges");
    
    for (final EdgeId edgeId : incidentEdgeIds) {
      final int arriveTs = context.getCurrentTimeStep() + context.distanceToVertexThroughIncidentEdge(edgeId, vertex);

      if (picked == null || arriveTs < picked.getSecond()) {
        picked = Pair.create(edgeId, arriveTs);
      }
    }

    Preconditions.checkState(picked != null, "Did not pick edge");
    
    return picked;
  }

  private EdgeId pickFromScores(@NonNull final ImmutableMap<EdgeId, Double> scores) {

    if (scores.isEmpty()) {
      throw new IllegalArgumentException("Scores was empty");
    } else {
      final double total = scores.values().stream().mapToDouble(Double::doubleValue).sum();

      validateScores(scores);
      
      if (total > 0.0) {
        final Map<EdgeId, Range<Double>> rouletteWheel = new HashMap<>(scores.size());
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final AtomicDouble max = new AtomicDouble(0.0);

        scores.forEach((edgeId, rawScore) -> {
          final double weigtedScore = rawScore / total;
          final double lower = max.get();
          final double upper = lower + weigtedScore;
          final Range<Double> range = Range.closedOpen(lower, upper);

          rouletteWheel.put(edgeId, range);
          max.set(upper);
        });

        while (true) {
          final double number = random.nextDouble(max.get());
          final Optional<EdgeId> pick = rouletteWheel.entrySet().stream()
                  .filter(e -> e.getValue().contains(number))
                  .map(Entry::getKey)
                  .findAny();

          if (pick.isPresent()) {
            return pick.get();
          }
        }
      } else {
        throw new IllegalArgumentException(String.format("Sum of scores was <= 0.0 : %f", total));
      }
    }
  }

  protected abstract void edgeChosen(AgentContext context, EdgeId edge);
  
  protected abstract void leavingVertex(AgentContext context, VertexId vertex);

  protected abstract ImmutableMap<EdgeId, Double> scoreEdgeBias(AgentContext context);
  
  protected abstract ImmutableMap<EdgeId, Double> scoreCoveredVertices(
          AgentContext context,
          ImmutableMap<VertexId, Integer> leftTimes,
          ImmutableMap<VertexId, Pair<EdgeId, Integer>> bestEdgeAndArrivalTsToCoveredVertices);
  
  
}
