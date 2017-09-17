package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class AntiRandomAgentStrategy extends AbstractCoveringAgentStrategy {

  @Override
  protected EdgeId choose(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData) {
    final double attackInterval = context.getAttackInterval();
    final int currentTimestep = context.getCurrentTimeStep();
    final Optional<EdgeId> priorityEdge;
    final EdgeId chosenEdge;

    priorityEdge = coveredVertices.stream()
            .map(coveredVertex -> {
              final int lastTimestepVisitied = Optional.ofNullable(verticesData.get(coveredVertex)).map(VertexData::getTimestepVisited).orElse(0);
              final Pair<Integer, EdgeId> bestEdgeDistance = context.bestDistanceToVertex(coveredVertex);
              final int distance = bestEdgeDistance.getFirst();
              final EdgeId edge = bestEdgeDistance.getSecond();
              final int arrivalTimestep = currentTimestep + distance;
              final double timestepsUnivisitedAfterArrival = arrivalTimestep - lastTimestepVisitied;
              final double score = timestepsUnivisitedAfterArrival / attackInterval;

              return Pair.create(edge, Pair.create(score, distance));
            })
            .filter(p -> p.getSecond().getFirst() >= 1.0)
            .map(p -> Pair.create(p.getFirst(), p.getSecond().getSecond()))
            .min((p1, p2) -> Integer.compare(p1.getSecond(), p2.getSecond()))
            .map(Pair::getFirst);

    if (priorityEdge.isPresent()) {
      chosenEdge = priorityEdge.get();
    } else {
      chosenEdge = context.getIncidientEdgeIds().stream()
              .map(edgeId -> {
                final double timestepLastChosen = Optional.ofNullable(edgesData.get(edgeId)).map(EdgeData::getTimestepUsed).orElse(0);
                final double timestepsSinceLastChosen = currentTimestep - timestepLastChosen;

                return Pair.create(edgeId, timestepsSinceLastChosen);
              })
              .max((p1, p2) -> Double.compare(p1.getValue(), p2.getValue()))
              .map(Pair::getKey)
              .get();
    }

    return chosenEdge;

  }
}
