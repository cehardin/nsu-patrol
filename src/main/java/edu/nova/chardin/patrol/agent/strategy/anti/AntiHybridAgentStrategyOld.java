package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntPredicate;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;

@Log
public class AntiHybridAgentStrategyOld implements AgentStrategy {

  @Value
  static class EdgeDecision {

    @NonNull
    VertexId previousVertex;
    @NonNull
    EdgeId edgeChosen;
    int timestepChosen;
  }

  @Data
  static class EdgeData {

    @NonNull
    final VertexId source;
    @NonNull
    final VertexId destination;
    final int length;
    final AtomicInteger usedCount = new AtomicInteger();
    int timestepUsed = 0;
  }

  @Data
  static class VertexData {

    final Set<EdgeId> edges = new HashSet<>();
    final AtomicInteger visitCount = new AtomicInteger();
    int timestepVisited = 0;
  }

  @Data
  static class PeekbackState {

    final Set<VertexId> verticesChecked = new HashSet<>();
    @NonNull
    Optional<EdgeId> returnOnEdge = Optional.empty();
    boolean checking = false;
  }

  private final Set<VertexId> coveredVertices = new HashSet<>();
  private final Map<VertexId, VertexData> verticesData = new HashMap<>();
  private final Map<EdgeId, EdgeData> edgesData = new HashMap<>();
  private Optional<EdgeDecision> previousEdgeDecision = Optional.empty();
  private Optional<PeekbackState> peekBackState = Optional.of(new PeekbackState());

  @Override
  public void thwarted(
          @NonNull final VertexId vertex,
          @NonNull final ImmutableSet<VertexId> criticalVertices,
          final int timestep,
          final int attackInterval) {

    if (!criticalVertices.contains(vertex)) {
      coveredVertices.add(vertex);
    }
  }

  @Override
  public EdgeId choose(final AgentContext context) {
    final int currentTimestep = context.getCurrentTimeStep();
    final VertexId currentVertex = context.getCurrentVertex();
    final boolean foundNewEdge;
    final boolean foundNewVertex;
    final EdgeId chosenEdge;

    if (previousEdgeDecision.isPresent()) {
      final EdgeDecision decision = previousEdgeDecision.get();
      final EdgeData edgeData;
      final VertexData vertexData;

      if (edgesData.containsKey(decision.getEdgeChosen())) {
        edgeData = edgesData.get(decision.getEdgeChosen());
        foundNewEdge = false;
      } else {
        edgeData = new EdgeData(
                decision.getPreviousVertex(),
                currentVertex,
                currentTimestep - decision.getTimestepChosen());
        edgesData.put(decision.getEdgeChosen(), edgeData);
        foundNewEdge = true;
      }

      edgeData.setTimestepUsed(decision.getTimestepChosen());
      edgeData.getUsedCount().incrementAndGet();

      if (verticesData.containsKey(decision.getPreviousVertex())) {
        vertexData = verticesData.get(decision.getPreviousVertex());
        foundNewVertex = false;
      } else {
        vertexData = new VertexData();
        verticesData.put(decision.getPreviousVertex(), vertexData);
        foundNewVertex = true;
      }

      vertexData.setTimestepVisited(decision.getTimestepChosen());
      vertexData.getVisitCount().incrementAndGet();
      vertexData.getEdges().add(decision.getEdgeChosen());

    } else {
      foundNewEdge = true;
      foundNewVertex = true;
    }

    if (peekBackState.isPresent()) {
      final PeekbackState state = peekBackState.get();

      state.getVerticesChecked().addAll(context.getCriticalVertices());
      
      if (state.isChecking()) {
        state.getVerticesChecked().add(currentVertex);
        state.setChecking(false);
      }

      if (state.getReturnOnEdge().isPresent()) {
        chosenEdge = state.getReturnOnEdge().get();
        state.setChecking(true);

        if (state.getVerticesChecked().contains(currentVertex)) {
          state.setReturnOnEdge(Optional.empty());
        } else {
          state.setReturnOnEdge(Optional.of(chosenEdge.reversed()));
        }
      } else {
        chosenEdge = computeEdgeTimestampUsed(context).entrySet().stream()
                .min((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .map(Entry::getKey)
                .get();

        if (state.getVerticesChecked().contains(currentVertex)) {
          
          state.setReturnOnEdge(Optional.empty());
          
          if (!foundNewEdge && !foundNewVertex && edgesData.containsKey(chosenEdge)) {
            peekBackState = Optional.empty();
            edgesData.values().forEach(data -> data.getUsedCount().set(0));
            verticesData.values().forEach(data -> data.getVisitCount().set(0));
          }
        } else {
          state.setReturnOnEdge(Optional.of(chosenEdge.reversed()));
        }
      }
    } else {
      final DoubleSummaryStatistics edgeLengthStatistics = edgesData.values().stream()
              .mapToDouble(EdgeData::getLength)
              .summaryStatistics();
      final ImmutableMap<VertexId, Pair<Integer, EdgeId>> distances = computeDistanceToCoveredVertices(context);
      final Optional<VertexId> atRiskVertex = coveredVertices.stream()
              .map(v -> Pair.create(v, Optional.ofNullable(verticesData.get(v)).map(VertexData::getTimestepVisited).orElse(0)))
              .map(p -> Pair.create(p.getKey(), p.getValue().doubleValue() + context.getAttackInterval()))
              .map(p -> Pair.create(p.getKey(), p.getValue() - (currentTimestep + 2 * edgeLengthStatistics.getMax() + distances.get(p.getKey()).getFirst())))
              .filter(p -> p.getValue() <= 0)
              .map(p -> Pair.create(p.getKey(), distances.get(p.getKey()).getFirst()))
              .min((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
              .map(Pair::getKey);

      if (atRiskVertex.isPresent()) {
        chosenEdge = distances.get(atRiskVertex.get()).getSecond();
      } else {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final ImmutableSet<VertexId> notMyCriticalVertices = context.getCriticalVertices().stream()
                .filter(v -> !coveredVertices.contains(v))
                .collect(ImmutableSet.toImmutableSet());
        final ImmutableList<EdgeId> edgesToPick = computeEdgeTimestampUsed(context).entrySet().stream()
                .flatMap(e -> {
                  final EdgeId edge = e.getKey();
                  final int length = e.getValue();
                  final Optional<VertexId> vertex = Optional.ofNullable(edgesData.get(edge))
                          .map(EdgeData::getDestination);
                  final int score;
                  
                  if (vertex.isPresent() && notMyCriticalVertices.contains(vertex.get())) {
                    score = 1;
                  } else {
                    score = currentTimestep - length;
                  }
                  
                  return Collections.nCopies(score, edge).stream();
                })
                .collect(ImmutableList.toImmutableList());

        chosenEdge = edgesToPick.get(random.nextInt(edgesToPick.size()));
      }
    }

    previousEdgeDecision = Optional.of(new EdgeDecision(currentVertex, chosenEdge, currentTimestep));

    return chosenEdge;
  }

  private ImmutableMap<EdgeId, Integer> computeEdgeTimestampUsed(@NonNull final AgentContext context) {
    return context.getIncidientEdgeIds().stream()
            .map(edge -> Pair.create(edge, Optional.ofNullable(edgesData.get(edge)).map(EdgeData::getTimestepUsed).orElse(0)))
            .collect(ImmutableMap.toImmutableMap(Pair::getKey, Pair::getValue));
  }

  private ImmutableMap<VertexId, Pair<Integer, EdgeId>> computeDistanceToCoveredVertices(@NonNull final AgentContext context) {
    return coveredVertices.stream()
            .map(v -> Pair.create(v, context.bestDistanceToVertex(v)))
            .collect(ImmutableMap.toImmutableMap(Pair::getKey, Pair::getValue));
  }
}
