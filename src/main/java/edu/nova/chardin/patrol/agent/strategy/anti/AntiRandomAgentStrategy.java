package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

public class AntiRandomAgentStrategy implements AgentStrategy {

  private final Map<EdgeId, Integer> timestepEdgeChosen = new HashMap<>();
  private final Map<VertexId, Integer> coveredVertices = new HashMap<>();

  @Override
  public void thwarted(VertexId vertex, ImmutableSet<VertexId> criticalVertices, int timestep, int attackInterval) {
    if (!criticalVertices.contains(vertex)) {
      coveredVertices.putIfAbsent(vertex, timestep);
    }
  }

  @Override
  public EdgeId choose(final AgentContext context) {
    final double attackInterval = context.getAttackInterval();
    final int currentTimestep = context.getCurrentTimeStep();
    final EdgeId chosenEdge;
    
    coveredVertices.computeIfPresent(context.getCurrentVertex(), (v, ts) -> currentTimestep);
    
    chosenEdge= context.getIncidientEdgeIds().stream().map(edgeId -> {
      final double timeSinceLastChosen = currentTimestep - timestepEdgeChosen.getOrDefault(edgeId, 0);
      final double coveredVertexBoost = coveredVertices.entrySet().stream().mapToDouble(entry -> {
        final double arrivalTime = currentTimestep + context.distanceToVertexThroughIncidentEdge(edgeId, entry.getKey());

        return (arrivalTime - entry.getValue()) / attackInterval;
      }).average().orElse(1.0);

      return Pair.create(edgeId, timeSinceLastChosen * coveredVertexBoost);
    }).max((p1, p2) -> Double.compare(p1.getValue(), p2.getValue()))
            .map(Pair::getKey)
            .get();

    timestepEdgeChosen.put(chosenEdge, currentTimestep);

    return chosenEdge;

  }
}
