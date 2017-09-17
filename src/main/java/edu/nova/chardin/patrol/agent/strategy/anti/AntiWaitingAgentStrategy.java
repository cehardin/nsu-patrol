package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class AntiWaitingAgentStrategy extends AbstractCoveringAgentStrategy {

  private final Set<EdgeId> checkedEdges = new HashSet<>();
  private Optional<EdgeId> lastEdgeToCheck = Optional.empty();

  @Override
  protected EdgeId choose(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData) {
    final EdgeId chosenEdge;

    if (lastEdgeToCheck.isPresent()) {
      final EdgeId reverseEdge = lastEdgeToCheck.get().reversed();

      if (checkedEdges.contains(reverseEdge)) {
        chosenEdge = chooseBestEdge(context, coveredVertices, verticesData, edgesData);
      } else {
        chosenEdge = reverseEdge;
      }
    } else {
      chosenEdge = chooseBestEdge(context, coveredVertices, verticesData, edgesData);
    }

    if (checkedEdges.contains(chosenEdge)) {
      lastEdgeToCheck = Optional.empty();
    } else {
      lastEdgeToCheck = Optional.of(chosenEdge);
      checkedEdges.addAll(context.getIncidientEdgeIds());
    }

    return chosenEdge;

  }

  private EdgeId chooseBestEdge(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData) {
    final int currentTimestep = context.getCurrentTimeStep();
    final int attackInterval = context.getAttackInterval();

    return context.getIncidientEdgeIds().stream()
            .map(edgeId -> {
              return Pair.create(
                      edgeId,
                      coveredVertices.stream()
                              .mapToInt(coveredVertex -> {
                                final int timeLastVisited = Optional.ofNullable(verticesData.get(coveredVertex)).map(VertexData::getTimestepVisited).orElse(0);
                                final int distance = context.distanceToVertexThroughIncidentEdge(edgeId, coveredVertex);
                                final int arrivalTime = currentTimestep + distance;
                                final int deadlineTimestep = timeLastVisited + attackInterval;

                                return deadlineTimestep - arrivalTime;
                              })
                              .filter(timestepsLeft -> timestepsLeft <= attackInterval / coveredVertices.size() / 2)
                              .summaryStatistics());
            })
            .filter(p -> p.getValue().getCount() > 0)
            .min((p1, p2) -> Long.compare(p1.getValue().getSum(), p2.getValue().getSum()))
            .map(Pair::getKey)
            .orElse(
                    context.getIncidientEdgeIds().stream()
                            .map(edgeId -> Pair.create(edgeId, Optional.ofNullable(edgesData.get(edgeId)).map(EdgeData::getTimestepUsed).orElse(0)))
                            .min((p1, p2) -> Integer.compare(p1.getValue(), p2.getValue()))
                            .map(Pair::getKey)
                            .get());
  }
}
