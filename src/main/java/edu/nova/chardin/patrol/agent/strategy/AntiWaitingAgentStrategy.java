package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.VertexId;

public class AntiWaitingAgentStrategy extends AbstractCoveringAgentStrategy {

  @Override
  protected int calculateReturnTime(final AgentContext agentContext) {
    return agentContext.getCurrentTimeStep() + agentContext.getAttackInterval();
  }
  
  @Override
  protected double score(
          final VertexId adjacentVertex,
          final double attackInterval,
          final double arrivalTimestep, 
          final double returnTimestep) {
    
    final double score;
    
    if (returnTimestep >= arrivalTimestep) {
      score = super.score(adjacentVertex, attackInterval, arrivalTimestep, returnTimestep);
    } else {
      score = 0.0;
    }
    
    return score;
  }
}
