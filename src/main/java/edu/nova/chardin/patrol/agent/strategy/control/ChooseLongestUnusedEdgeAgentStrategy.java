package edu.nova.chardin.patrol.agent.strategy.control;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.util.Pair;

public class ChooseLongestUnusedEdgeAgentStrategy implements AgentStrategy {

  private final Map<EdgeId, Integer> timestepEdgeChosen = new HashMap<>();
  
  @Override
  public void thwarted(VertexId vertex, ImmutableSet<VertexId> criticalVertices, int timestep, int attackInterval) {
  }

  @Override
  public EdgeId choose(AgentContext context) {
    final EdgeId chosenEdge = context.getIncidientEdgeIds().stream()
            .map(edge -> Pair.create(edge, timestepEdgeChosen.getOrDefault(edge, 0)))
            .min((p1, p2) -> p1.getSecond().compareTo(p2.getSecond()))
            .map(Pair::getFirst)
            .get();
    
    timestepEdgeChosen.put(chosenEdge, context.getCurrentTimeStep());
    
    return chosenEdge;
  }

  
}
