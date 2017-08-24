package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

public class AntiWaitingAgentStrategy implements AgentStrategy {

  /**
   * The covered vertices mapped to the last timestep it was visited.
   */
  private final Map<VertexId, Integer> coveredVertices = new HashMap<>();

  /**
   * The last timestep that an edge was chosen.
   */
  private final Map<EdgeId, Integer> timestepEdgeChosen = new HashMap<>();

  @Override
  public void thwarted(VertexId vertex, ImmutableSet<VertexId> criticalVertices, int timestep, int attackInterval) {
    if (!criticalVertices.contains(vertex)) {
      coveredVertices.put(vertex, timestep);
    }
  }

  @Override
  public EdgeId choose(final AgentContext context) {
    final int currentTimestep = context.getCurrentTimeStep();
    final int attackInterval = context.getAttackInterval();
    final EdgeId chosenEdge = context.getIncidientEdgeIds().stream()
            .map(edgeId -> {
              return Pair.create(
                      edgeId,
                      coveredVertices.entrySet().stream()
                              .mapToInt(entry -> {
                                final VertexId coveredVertex = entry.getKey();
                                final int timeLastVisited = entry.getValue();
                                final int distance = context.distanceToVertexThroughIncidentEdge(edgeId, coveredVertex);
                                final int arrivalTime = currentTimestep + distance;
                                final int deadlineTimestep = timeLastVisited + attackInterval;

                                return deadlineTimestep - arrivalTime;
                              })
                              .filter(timestepsLeft -> timestepsLeft >= 0)
                              .filter(timestepsLeft -> timestepsLeft <= attackInterval / coveredVertices.size())
                              .summaryStatistics());
            })
            .filter(p -> p.getValue().getCount() > 0)
            .min((p1, p2) -> Long.compare(p1.getValue().getSum(), p2.getValue().getSum()))
            .map(Pair::getKey)
            .orElse(
                    context.getIncidientEdgeIds().stream()
                            .map(edgeId -> Pair.create(edgeId, timestepEdgeChosen.getOrDefault(edgeId, 0)))
                            .min((p1, p2) -> Integer.compare(p1.getValue(), p2.getValue()))
                            .map(Pair::getKey)
                            .get());

    coveredVertices.computeIfPresent(context.getCurrentVertex(), (v, ts) -> currentTimestep);
    timestepEdgeChosen.put(chosenEdge, currentTimestep);

    return chosenEdge;

  }
}
