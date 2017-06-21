package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AntiWaitingPeekBackAgentStrategy extends AntiWaitingAgentStrategy {

  private final Set<VertexId> peekedBackVertices = new HashSet<>();
  Optional<VertexId> checkAgain = Optional.empty();
  
  @Override
  public void arrived(AgentContext context) {
    super.arrived(context);
    
    if (peekedBackVertices.add(context.getCurrentVertex())) {
      checkAgain = Optional.of(context.getCurrentVertex());
    }
  }

  @Override
  protected double score(
          final VertexId adjacentVertex,
          final double attackInterval,
          final double arrivalTimestep, 
          final double returnTimestep) {
    
    final double score;
    
    if (checkAgain.isPresent() && checkAgain.get().equals(adjacentVertex)) {
      score = Double.MAX_VALUE;
      checkAgain = Optional.empty();
    }
    else {
      score = super.score(adjacentVertex, attackInterval, arrivalTimestep, returnTimestep);
    }
    
    return score;
  }
}
