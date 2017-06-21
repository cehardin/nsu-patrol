package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SelfPheromoneAgentStrategy implements AgentStrategy {
  private final Map<VertexId, Integer> vertexArrivedTimestamps = new HashMap<>();

  @Override
  public void arrived(AgentContext context) {
    vertexArrivedTimestamps.put(context.getCurrentVertex(), context.getCurrentTimeStep());
  }

  @Override
  public VertexId choose(AgentContext context) {
    final TreeMap<Integer, VertexId> scores = new TreeMap<>();
    
    context.getAdjacentVertices().stream()
            .forEach(v -> scores.put(vertexArrivedTimestamps.getOrDefault(v, 0), v));
    
    return scores.firstEntry().getValue();
  }
  
  
}
