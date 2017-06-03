package edu.nova.chardin.patrol.agent.strategy.covering;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.ArrayList;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public abstract class AbstractScoringCoveringStrategy implements CoveringStrategy {

  @Override
  public final VertexId choose(AgentContext context, Map<VertexId, Integer> coveredVertices) {

    final Map<VertexId, DoubleStream.Builder> possibleNextVerticeScores = new HashMap<>();
    final VertexId currentVertex = context.getCurrentVertex();
    final int attackInterval = context.getAttackInterval();
    final VertexId nextVertex;

    context.getPossibleNextVertices().forEach(v -> possibleNextVerticeScores.put(v, DoubleStream.builder()));

    coveredVertices.keySet().forEach(coveredVertex -> {
      context.getAdjacentVertices().forEach(adjacentVertex -> {
        final int moveArrivalTimestep = context.getCurrentTimeStep() + context.distanceToVertex(currentVertex, coveredVertex);
        final int stayArrivalTimestep = moveArrivalTimestep + 1;
        final int plannedReturnTimestep = coveredVertices.get(coveredVertex);
        final double moveScore = scoreArrival(attackInterval, moveArrivalTimestep, plannedReturnTimestep);
        final double stayScore = scoreArrival(attackInterval, stayArrivalTimestep, plannedReturnTimestep);
        
//        possibleNextVerticeScores.computeIfAbsent(adjacentVertex, v -> DoubleStream.builder());
//        possibleNextVerticeScores.computeIfAbsent(currentVertex, v -> DoubleStream.builder());
        
        possibleNextVerticeScores.get(adjacentVertex).accept(moveScore);
        possibleNextVerticeScores.get(currentVertex).accept(stayScore / (double)context.getAdjacentVertices().size());
      });
    });

    nextVertex = selectVertexFromScores(
            possibleNextVerticeScores.entrySet()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    Entry::getKey, 
                                    e -> e.getValue().build().sum())));

    if (coveredVertices.keySet().contains(context.getCurrentVertex()) && !context.getCurrentVertex().equals(nextVertex)) {
      coveredVertices.put(context.getCurrentVertex(), calculateReturnTime(context.getAttackInterval(), context.getCurrentTimeStep()));
    }

    return nextVertex;
  }

  private VertexId selectVertexFromScores(@NonNull final Map<VertexId, Double> vertexScores) {

    final Double max = vertexScores.values().stream().max(Double::compare).get();
    final List<VertexId> vertices = vertexScores.entrySet().stream()
            .filter(e -> e.getValue().equals(max))
            .map(Entry::getKey)
            .collect(Collectors.toList());
    final int vertexCount = vertices.size();
    
    switch (vertexCount) {
      case 0:
        throw new IllegalStateException();
      case 1:
        return vertices.get(0);
      default:
        return vertices.get(ThreadLocalRandom.current().nextInt(vertexCount));
    }
    
//    final Map<VertexId, Range<Double>> wheel = new HashMap<>(vertexScores.size());
//    final double pick;
//
//    {
//      final double minScore = vertexScores.values().stream().mapToDouble(Double::doubleValue).min().getAsDouble();
//
//      if (minScore < 0) {
//        vertexScores.replaceAll((v, s) -> s + Math.abs(minScore));
//      }
//    }
//
//    if (vertexScores.values().stream().anyMatch(s -> s < 0)) {
//      throw new AssertionError(String.format("Negative scores found : %s", vertexScores));
//    }
//
//    {
//      final double scoresSum = vertexScores.values().stream().mapToDouble(Double::doubleValue).sum();
//      double max = 0.0;
//
//      for (final Entry<VertexId, Double> vertexScore : vertexScores.entrySet()) {
//        final VertexId vertexId = vertexScore.getKey();
//        final double rawScore = vertexScore.getValue();
//        final double weigtedScore = scoresSum == 0.0 ? 0.0 : rawScore / scoresSum;
//        final double offset = max;
//        final Range<Double> range;
//        max += weigtedScore;
//        
//        range = offset == max ? Range.singleton(offset) : Range.closedOpen(offset, max);
//        
//        wheel.put(vertexId, range);
//      }
//      
//      if (max == 0.0) {
//        pick = 0.0;
//      } else {
//        try {
//          pick = ThreadLocalRandom.current().nextDouble(max);
//        } catch (IllegalArgumentException e) {
//          throw new IllegalStateException(
//                  String.format(
//                          "Could not get next double with a max of %f from : %s", 
//                          max, 
//                          vertexScores), 
//                  e);
//        }
//      }
//    }
//
//    try {
//      return wheel.entrySet().stream()
//            .filter(e -> e.getValue().contains(pick))
//            .map(Entry::getKey)
//            .findFirst()
//            .get();
//    } catch (NoSuchElementException e) {
//      throw new IllegalStateException(
//              String.format(
//                      "Could not find %f in %s", 
//                      pick, 
//                      wheel), 
//              e);
//    }
  }

  protected abstract double scoreArrival(int attackInterval, int arrivalTimestep, int returnTimestep);

  protected abstract int calculateReturnTime(int attackInterval, int currentTimestep);

}
