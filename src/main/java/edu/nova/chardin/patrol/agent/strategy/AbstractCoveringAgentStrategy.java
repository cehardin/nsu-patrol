package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public abstract class AbstractCoveringAgentStrategy implements AgentStrategy {

  private final Map<VertexId, Integer> coveredVerticesReturnTime = new HashMap<>();

  @Override
  public void arrived(final AgentContext context) {

    final VertexId currentVertex = context.getCurrentVertex();

    if (context.getUnderAttack() && !context.getCriticalVertices().contains(currentVertex)) {
      coveredVerticesReturnTime.putIfAbsent(currentVertex, calculateReturnTime(context));
    }
  }

  @Override
  public final VertexId choose(final AgentContext context) {

    final VertexId currentVertex = context.getCurrentVertex();
    final ImmutableSet<VertexId> othersCoveredVertices = ImmutableSet.copyOf(
            Sets.difference(
                    coveredVerticesReturnTime.keySet(),
                    coveredVerticesReturnTime.keySet()));
    final int attackInterval = context.getAttackInterval();
    final int currentTimestep = context.getCurrentTimeStep();
    final Map<VertexId, Pair<VertexId, Integer>> shortestAdjacentVertexToCoveredVertex = new HashMap<>();
    final Map<VertexId, Double> nextVertexScores = new HashMap<>();

    // determine the adjacent vertex that will get to each covered vertex the quickest
    // however, try to avoid going onto ciritical vertices 
    // that are not covered by this agent. This is because those
    // vertices are covered by a different agent.
    coveredVerticesReturnTime.keySet().forEach(coveredVertex -> {
      final AtomicInteger minTimesteps = new AtomicInteger(Integer.MAX_VALUE);

      context.getAdjacentVertices().forEach(adjacentVertex -> {

        if (!othersCoveredVertices.contains(adjacentVertex)) {
          final int timestepsToArrive = context.distanceToVertexThroughAdjacentVertex(adjacentVertex, coveredVertex);

          if (timestepsToArrive < minTimesteps.get()) {
            shortestAdjacentVertexToCoveredVertex.put(
                    coveredVertex,
                    Pair.create(adjacentVertex, timestepsToArrive));
            minTimesteps.set(timestepsToArrive);
          }
        }
      });
    });

    //initialize all scores to 1.0
    context.getAdjacentVertices().forEach(adjacentVertex -> nextVertexScores.put(adjacentVertex, 1.0));

    //figure out score for moving to a covered vertex through a
    //an adjacent vertex. But only consider the shortest route
    // to a particular covered vertex.
    coveredVerticesReturnTime.forEach((coveredVertex, returnTime) -> {
      Optional.ofNullable(shortestAdjacentVertexToCoveredVertex.get(coveredVertex)).ifPresent(adjacentVertexAndTimestepsToArrive -> {
        final VertexId adjacentVertex = adjacentVertexAndTimestepsToArrive.getFirst();
        final int timestepsToArrive = adjacentVertexAndTimestepsToArrive.getSecond();
        final int arrivalTimestep = currentTimestep + timestepsToArrive;
        final double score = score(adjacentVertex, attackInterval, arrivalTimestep, returnTime);

        nextVertexScores.merge(adjacentVertex, score, Double::sum);
      });
    });

    //only update the return time for a covered vertex
    if (coveredVerticesReturnTime.keySet().contains(currentVertex)) {
      coveredVerticesReturnTime.put(currentVertex, calculateReturnTime(context));
    }

    return pickFromScores(nextVertexScores);
  }

  private VertexId pickFromScores(@NonNull final Map<VertexId, Double> nextVertexScores) {
    final double total = nextVertexScores.values().stream().mapToDouble(Double::doubleValue).sum();
    final Map<VertexId, Range<Double>> rouletteWheel = new HashMap<>(nextVertexScores.size());
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final AtomicDouble max = new AtomicDouble(0.0);

    nextVertexScores.forEach((vertex, rawScore) -> {
      final double weigtedScore = rawScore / total;
      final double lower = max.get();
      final double upper = lower + weigtedScore;
      final Range<Double> range = Range.closedOpen(lower, upper);

      rouletteWheel.put(vertex, range);
      max.set(upper);
    });

    while (true) {
      final double number = random.nextDouble(max.get());
      final Optional<VertexId> pick = rouletteWheel.entrySet().stream().filter(e -> e.getValue().contains(number)).map(Entry::getKey).findAny();

      if (pick.isPresent()) {
        return pick.get();
      }
    }
  }

  protected abstract int calculateReturnTime(final AgentContext agentContext);

  protected double score(
          final VertexId adjacentVertex,
          final double attackInterval,
          final double arrivalTimestep,
          final double returnTimestep) {
    
    return Math.pow(-returnTimestep - arrivalTimestep, 2.0);
  }
}
