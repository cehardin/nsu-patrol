package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class CoveringSoftLimitEdgeChooser2 implements CoveringEdgeChooser {

  @Override
  public Optional<EdgeId> choose(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData,
          @NonNull final ImmutableSet<EdgeId> edgesToAvoid) {

    final int currentTimestep = context.getCurrentTimeStep();
    final int attackInterval = context.getAttackInterval();
    
    return context.getIncidientEdgeIds().stream()
            .map(edgeId -> {
              return Pair.create(
                      edgeId,
                      coveredVertices.stream()
                              .mapToInt(coveredVertex -> {
                                final int timeLastVisited = Optional.ofNullable(verticesData.get(coveredVertex))
                                        .map(VertexData::getTimestepVisited)
                                        .orElse(0);
                                final int distance = context.distanceToVertexThroughIncidentEdge(edgeId, coveredVertex);
                                final int arrivalTime = currentTimestep + distance;
                                final int deadlineTimestep = timeLastVisited + attackInterval;

                                return deadlineTimestep - arrivalTime;
                              })
                              .filter(timestepsLeft -> timestepsLeft < 0)
                              .summaryStatistics());
            })
            .filter(p -> !edgesToAvoid.contains(p.getKey()))
            .filter(p -> p.getValue().getCount() > 0)
            .map(p -> Pair.create(p.getKey(), p.getValue().getSum()))
            .min((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
            .map(Pair::getKey);
  }
}
