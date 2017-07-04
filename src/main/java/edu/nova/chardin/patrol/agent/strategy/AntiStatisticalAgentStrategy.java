package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.graph.EdgeId;
import java.util.concurrent.ThreadLocalRandom;

public final class AntiStatisticalAgentStrategy extends AbstractCoveringAgentStrategy {

   public AntiStatisticalAgentStrategy() {
    super(PatrolMode.Indeterministic);
  }
  @Override
  protected int calculateReturnTime(
          final int timestep, 
          final int attackInterval) {

    return timestep
            + (int) Math.ceil(
                    ThreadLocalRandom.current().nextDouble(2.0)
                    * (double) attackInterval);
  }  

  @Override
  protected double score(
          final EdgeId edgeId, 
          final double attackInterval, 
          final double arrivalTimestep, 
          final double returnTimestep) {
    
    final double score;
    
    if (returnTimestep < (arrivalTimestep + 1)) {
      score = Math.pow(returnTimestep - arrivalTimestep - 1, 2.0);
    } else {
      score = 0.0;
    }
    
    return score;
  }
  
}
