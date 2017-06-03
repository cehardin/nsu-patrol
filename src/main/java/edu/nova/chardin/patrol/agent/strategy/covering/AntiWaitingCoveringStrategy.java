package edu.nova.chardin.patrol.agent.strategy.covering;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.VertexId;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class AntiWaitingCoveringStrategy extends AbstractScoringCoveringStrategy {

  private final Set<VertexId> checkedVertices = new HashSet<>();
  private Optional<VertexId> lastVertex = Optional.empty();
  
  @Override
  public void arrived(
          final AgentContext context, 
          final Map<VertexId, Integer> coveredVertices) {
    
    final VertexId currentVertex = context.getCurrentVertex();
    
    if (checkedVertices.add(currentVertex)) {
      lastVertex = Optional.of(currentVertex);
    } else {
      lastVertex = Optional.empty();
    }
  }

  @Override
  protected double scoreArrival(
          final VertexId via, 
          final int arrivalTimestep,
          final int returnTimestep) {
    
    final double score;
    
    if (lastVertex.filter(Predicate.isEqual(via)).isPresent()) {
      score = Double.MAX_VALUE;
    } else {
      final double difference = returnTimestep - arrivalTimestep;
    
      score = difference < 0.0 ? 0.0 : 1.0 / (1.0 + difference);
    }
    
    return score;
  }

  @Override
  protected int calculateReturnTime(int attackInterval, int currentTimestep) {
    return currentTimestep + attackInterval;
  }

  
}
