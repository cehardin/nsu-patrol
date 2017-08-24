package edu.nova.chardin.patrol.agent.strategy.control;

import edu.nova.chardin.patrol.agent.strategy.anti.*;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ChooseLongestUnusedEdgeAgentStrategy implements AgentStrategy {

  private final Map<EdgeId, Integer> timestepEdgeChosen = new HashMap<>();
  
  @Override
  public void thwarted(VertexId vertex, ImmutableSet<VertexId> criticalVertices, int timestep, int attackInterval) {
  }

  @Override
  public EdgeId choose(AgentContext context) {
    final EdgeId chosenEdge = context.getIncidientEdgeIds().stream()
            .collect(
                    Collectors.toMap(
                            edgeId -> timestepEdgeChosen.getOrDefault(edgeId, 0), 
                            edgeId -> ImmutableSet.of(edgeId), 
                            (s1, s2) -> ImmutableSet.<EdgeId>builder().addAll(s1).addAll(s2).build(), 
                            TreeMap::new))
            .firstEntry()
            .getValue()
            .stream()
            .findAny()
            .get();
    
    timestepEdgeChosen.put(chosenEdge, context.getCurrentTimeStep());
    
    return chosenEdge;
  }

  
}
