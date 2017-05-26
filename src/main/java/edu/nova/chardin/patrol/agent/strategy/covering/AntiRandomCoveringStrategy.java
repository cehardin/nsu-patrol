package edu.nova.chardin.patrol.agent.strategy.covering;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.VertexId;

import java.util.Map;

public class AntiRandomCoveringStrategy extends AbstractScoringCoveringStrategy {

  @Override
  public void arrived(AgentContext context, Map<VertexId, Integer> coveredVertices) {
  }

  @Override
  protected double scoreArrival(
          final int attackInterval, 
          final int arrivalTimestep,
          final int returnTimestep) {
    
    final double difference = returnTimestep - arrivalTimestep;
    
    return 1.0 / (1.0 + Math.abs(difference));
  }

  @Override
  protected int calculateReturnTime(int attackInterval, int currentTimestep) {
    return currentTimestep + attackInterval;
  }

  
}
