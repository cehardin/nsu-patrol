package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.Pair;

public class AntiRandomAgentStrategy implements AgentStrategy {

  private final Map<EdgeId, Integer> timestepEdgeChosen = new HashMap<>();
  private final Map<VertexId, Integer> coveredVertices = new HashMap<>();
  
  @Override
  public void thwarted(VertexId vertex, ImmutableSet<VertexId> criticalVertices, int timestep, int attackInterval) {
    if (!criticalVertices.contains(vertex)) {
      coveredVertices.put(vertex, timestep);
    }
  }

  @Override
  public EdgeId choose(final AgentContext context) {
    final double attackInterval = context.getAttackInterval();
    final int currentTimestep = context.getCurrentTimeStep();
    final Map<EdgeId, Double> edgeScores  = context.getIncidientEdgeIds().stream().map(edgeId -> {
      final double timeSinceLastUsed = currentTimestep - timestepEdgeChosen.getOrDefault(edgeId, 0);
      final double coveredVertexBoost = 1.0 + coveredVertices.entrySet().stream().mapToDouble(entry -> {
        final double arrivalTime = currentTimestep + context.distanceToVertexThroughIncidentEdge(edgeId, entry.getKey());
        final double riskTime = entry.getValue() + attackInterval;
        
        return Math.max(0.0, (attackInterval + arrivalTime - riskTime) / attackInterval);
      }).sum();
      
      return Pair.create(edgeId, timeSinceLastUsed * coveredVertexBoost);
    }).collect(
            Collectors.toMap(
                    Pair::getKey, 
                    Pair::getValue,
                    Double::sum));
    final EdgeId chosenEdge = edgeScores.entrySet().stream()
            .collect(
                    Collectors.toMap(
                            Entry::getValue, 
                            Entry::getKey, 
                            (v1, v2) -> v1, 
                            TreeMap::new))
            .lastEntry()
            .getValue();
    
    coveredVertices.computeIfPresent(context.getCurrentVertex(), (v,ts) -> currentTimestep);
    timestepEdgeChosen.put(chosenEdge, currentTimestep);
    
    return chosenEdge;
    
  }
}
