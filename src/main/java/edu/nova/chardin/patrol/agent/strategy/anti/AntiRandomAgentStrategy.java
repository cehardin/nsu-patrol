package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class AntiRandomAgentStrategy extends AbstractCoveringAgentStrategy {

  private final Map<EdgeId, Integer> timestepEdgeChosen = new HashMap<>();

  @Override
  protected EdgeId choose(
          @NonNull final AgentContext context, 
          @NonNull final ImmutableMap<VertexId, Integer> coveredVertices) {
    final double attackInterval = context.getAttackInterval();
    final int currentTimestep = context.getCurrentTimeStep();
    final Optional<EdgeId> priorityEdge;
    final EdgeId chosenEdge;

    priorityEdge = coveredVertices.entrySet().stream()
            .map(entry -> {
              final VertexId coveredVertex = entry.getKey();
              final int lastTimestepVisitied = entry.getValue();
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
                final double timestepLastChosen = timestepEdgeChosen.getOrDefault(edgeId, 0);
                final double timestepsSinceLastChosen = currentTimestep - timestepLastChosen;

                return Pair.create(edgeId, timestepsSinceLastChosen);
              })
              .max((p1, p2) -> Double.compare(p1.getValue(), p2.getValue()))
              .map(Pair::getKey)
              .get();
    }
    
    timestepEdgeChosen.put(chosenEdge, currentTimestep);

    return chosenEdge;

  }
}
