package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.graph.EdgeId;

public class AntiRandomAgentStrategy extends AbstractCoveringAgentStrategy {

  public AntiRandomAgentStrategy() {
    super(PatrolMode.Deterministic);
  }

  
  @Override
  protected int calculateReturnTime(final int timestep, final int attackInterval) {
    return timestep + attackInterval;
  }

  @Override
  protected double score(
          EdgeId edgeId, 
          double attackInterval, 
          double arrivalTimestep, 
          double returnTimestep) {
    
    final double halfAttackInterval = attackInterval / 2.0;
    final double score;
    
    if (returnTimestep < (arrivalTimestep + halfAttackInterval)) {
      score = Math.pow(returnTimestep - arrivalTimestep - halfAttackInterval, 2.0);
    } else {
      score = 0.0;
    }
    
    return score;
  }
}
