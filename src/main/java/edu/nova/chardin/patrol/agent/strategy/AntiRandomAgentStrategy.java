package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.VertexId;

public class AntiRandomAgentStrategy extends AbstractCoveringAgentStrategy {

  @Override
  protected int calculateReturnTime(final AgentContext agentContext) {
    return agentContext.getCurrentTimeStep() + agentContext.getAttackInterval();
  }

  @Override
  protected double score(
          VertexId adjacentVertex, 
          double attackInterval, 
          double arrivalTimestep, 
          double returnTimestep) {
    
    final double halfAttackInterval = attackInterval / 2.0;
    final double score;
    
    if (returnTimestep < (arrivalTimestep + halfAttackInterval)) {
      score = super.score(
              adjacentVertex, 
              attackInterval, 
              arrivalTimestep - halfAttackInterval, 
              returnTimestep);
    } else {
      score = 0.0;
    }
    
    return score;
  }
  
  
  
}
