package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SelfPheromoneAgentStrategy implements AgentStrategy {
  private final Map<EdgeId, Integer> incidentEdgeUsedTimestamps = new HashMap<>();

  @Override
  public void thwarted(VertexId vertex, ImmutableSet<VertexId> criticalVertices, int timestep, int attackInterval) {
  }

  @Override
  public EdgeId choose(AgentContext context) {
    final TreeMap<Integer, EdgeId> scores = new TreeMap<>();
    final EdgeId chosenEdge;
    
    context.getIncidientEdgeIds().stream()
            .forEach(v -> scores.put(incidentEdgeUsedTimestamps.getOrDefault(v, 0), v));
    
    chosenEdge = scores.firstEntry().getValue();
    incidentEdgeUsedTimestamps.put(chosenEdge, context.getCurrentTimeStep());
    
    return chosenEdge;
  }
  
  
}
