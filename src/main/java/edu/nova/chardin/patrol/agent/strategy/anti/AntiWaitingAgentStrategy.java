package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class AntiWaitingAgentStrategy extends AbstractCoveringAgentStrategy {

  final Set<VertexId> verticesChecked = new HashSet<>();
  Optional<EdgeId> returnOnEdge = Optional.empty();
  boolean checking = false;

  @Override
  protected EdgeId choose(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData) {

    final int currentTimestep = context.getCurrentTimeStep();
    final VertexId currentVertex = context.getCurrentVertex();
    final EdgeId chosenEdge;

    verticesChecked.addAll(context.getCriticalVertices());

    if (checking) {
      verticesChecked.add(currentVertex);
      checking = false;
    }

    if (returnOnEdge.isPresent()) {
      chosenEdge = returnOnEdge.get();
      checking = true;

      if (verticesChecked.contains(currentVertex)) {
        returnOnEdge = Optional.empty();
      } else {
        returnOnEdge = Optional.of(chosenEdge.reversed());
      }
    } else {
      final DoubleSummaryStatistics edgeLengthStatistics = edgesData.values().stream()
              .mapToDouble(EdgeData::getLength)
              .summaryStatistics();
      final ImmutableMap<VertexId, Pair<Integer, EdgeId>> distances = coveredVertices.stream()
              .map(v -> Pair.create(v, context.bestDistanceToVertex(v)))
              .collect(ImmutableMap.toImmutableMap(Pair::getKey, Pair::getValue));
      
      chosenEdge = coveredVertices.stream()
              .map(v -> Pair.create(
                      v, 
                      Optional.ofNullable(verticesData.get(v))
                              .map(VertexData::getTimestepVisited)
                              .orElse(0)))
              .map(p -> Pair.create(
                      p.getKey(), 
                      p.getValue().doubleValue() + context.getAttackInterval()))
              .map(p -> Pair.create(
                      p.getKey(), 
                      p.getValue() - (currentTimestep + 2 * edgeLengthStatistics.getMax() + distances.get(p.getKey()).getFirst())))
              .filter(p -> p.getValue() <= 0)
              .map(p -> Pair.create(p.getKey(), distances.get(p.getKey()).getFirst()))
              .min((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
              .map(Pair::getKey)
              .map(v -> context.bestDistanceToVertex(v).getSecond())
              .orElseGet(() -> context.getIncidientEdgeIds().stream()
                .map(e -> Pair.create(
                        e, 
                        Optional.ofNullable(edgesData.get(e))
                                .map(EdgeData::getTimestepUsed)
                                .orElse(0)))
                .min((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
                .map(Pair::getKey)
                .get());

      if (verticesChecked.contains(currentVertex)) {
        returnOnEdge = Optional.empty();
      } else {
        returnOnEdge = Optional.of(chosenEdge.reversed());
      }
    }

    return chosenEdge;
  }
}
