package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.graph.EdgeId;

public class AntiWaitingAgentStrategy extends AbstractCoveringAgentStrategy {

  public AntiWaitingAgentStrategy() {
    super(PatrolMode.Deterministic);
  }
  
  @Override
  protected int calculateReturnTime(final int timestep, final int attackInterval) {
    return timestep + attackInterval;
  }
  
  @Override
  protected double score(
          final EdgeId edgeId,
          final double attackInterval,
          final double arrivalTimestep, 
          final double returnTimestep) {
    
    final double score;
    
    if (returnTimestep >= arrivalTimestep) {
      score = Math.pow(-returnTimestep - arrivalTimestep, 2.0);
    } else {
      score = 0.0;
    }
    
    return score;
  }
}
