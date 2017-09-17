package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class AntiStatisticalAgentStrategy extends AbstractCoveringAgentStrategy {

  @Override
  protected EdgeId choose(
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
                                final int timeLastVisited = Optional.ofNullable(verticesData.get(coveredVertex))
                                        .map(VertexData::getTimestepVisited)
                                        .orElse(0);
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
            .orElseGet(() -> {
              final ImmutableList<EdgeId> edges = context.getIncidientEdgeIds().asList();

              return edges.get(ThreadLocalRandom.current().nextInt(edges.size()));
            });

  }
}
