package edu.nova.chardin.patrol.agent.strategy.covering;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractScoringCoveringStrategy implements CoveringStrategy {

  @Override
  public final VertexId choose(AgentContext context, Map<VertexId, Integer> coveredVertices) {

    final Map<VertexId, Double> possibleNextVerticeScores = new HashMap<>();
    final VertexId currentVertex = context.getCurrentVertex();
    final int attackInterval = context.getAttackInterval();
    final VertexId nextVertex;

    context.getPossibleNextVertices().forEach(v -> possibleNextVerticeScores.put(v, 0.0));

    coveredVertices.keySet().forEach(coveredVertex -> {
      context.getAdjacentVertices().forEach(adjacentVertex -> {
        final int moveArrivalTimestep = context.getCurrentTimeStep() + context.distanceToVertex(currentVertex, coveredVertex);
        final int stayArrivalTimestep = moveArrivalTimestep + 1;
        final int plannedReturnTimestep = coveredVertices.get(coveredVertex);
        final double moveScore = scoreArrival(attackInterval, moveArrivalTimestep, plannedReturnTimestep);
        final double stayScore = scoreArrival(attackInterval, stayArrivalTimestep, plannedReturnTimestep);
        
        possibleNextVerticeScores.compute(adjacentVertex, (v, s) -> Math.max(moveScore, s));
        possibleNextVerticeScores.compute(currentVertex, (v, s) -> Math.max(stayScore, s));
      });
    });

    nextVertex = selectVertexFromScores(possibleNextVerticeScores);

    if (coveredVertices.keySet().contains(context.getCurrentVertex()) && !context.getCurrentVertex().equals(nextVertex)) {
      coveredVertices.put(context.getCurrentVertex(), calculateReturnTime(context.getAttackInterval(), context.getCurrentTimeStep()));
    }

    return nextVertex;
  }

  private VertexId selectVertexFromScores(@NonNull final Map<VertexId, Double> vertexScores) {

            .map(Entry::getKey)
    }
  }

  protected abstract double scoreArrival(int attackInterval, int arrivalTimestep, int returnTimestep);

  protected abstract int calculateReturnTime(int attackInterval, int currentTimestep);

}
