package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class CoveringSoftLimitVertexFocusedEdgeChooser implements CoveringEdgeChooser {

  @Override
  public Optional<EdgeId> choose(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData,
          @NonNull final ImmutableSet<EdgeId> edgesToAvoid) {

    final double attackInterval = context.getAttackInterval();
    final int currentTimestep = context.getCurrentTimeStep();

    return coveredVertices.stream()
            .map(coveredVertex -> {
              final int lastTimestepVisitied = Optional.ofNullable(verticesData.get(coveredVertex))
                      .map(VertexData::getTimestepVisited)
                      .orElse(0);
              final Pair<Integer, EdgeId> bestEdgeDistance = context.bestDistanceToVertex(coveredVertex);
              final int distance = bestEdgeDistance.getFirst();
              final EdgeId edge = bestEdgeDistance.getSecond();
              final int arrivalTimestep = currentTimestep + distance;
              final double timestepsUnivisitedAfterArrival = arrivalTimestep - lastTimestepVisitied;
              final double score = timestepsUnivisitedAfterArrival / attackInterval;

              return Pair.create(edge, Pair.create(score, distance));
            })
            .filter(p -> p.getSecond().getFirst() >= 1.0)
            .filter(p -> !edgesToAvoid.contains(p.getKey()))
            .map(p -> Pair.create(p.getFirst(), p.getSecond().getSecond()))
            .min((p1, p2) -> p1.getSecond().compareTo(p2.getSecond()))
            .map(Pair::getFirst);
   
  }
}
