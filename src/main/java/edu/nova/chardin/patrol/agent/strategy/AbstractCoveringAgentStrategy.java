package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.AtomicDouble;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public abstract class AbstractCoveringAgentStrategy implements AgentStrategy {

  public static enum PatrolMode {
    Deterministic,
    Indeterministic
  }

  private final Map<EdgeId, Integer> incidentEdgeChosenTimestamps = new HashMap<>();
  private final Map<VertexId, Integer> coveredVerticesReturnTime = new HashMap<>();
  private final PatrolMode patrolMode;

  protected AbstractCoveringAgentStrategy(@NonNull PatrolMode patrolMode) {
    this.patrolMode = patrolMode;
  }

  @Override
  public void thwarted(
          final VertexId vertex, 
          final ImmutableSet<VertexId> criticalVertices, 
          final int timestep, 
          final int attackInterval) {
    
    if (!criticalVertices.contains(vertex)) {
      coveredVerticesReturnTime.put(vertex, calculateReturnTime(timestep, attackInterval));
    }
  }

  @Override
  public final EdgeId choose(final AgentContext context) {

    final EdgeId chosenEdge;
    final VertexId currentVertex = context.getCurrentVertex();
    final int attackInterval = context.getAttackInterval();
    final int currentTimestep = context.getCurrentTimeStep();
    final Map<VertexId, Pair<EdgeId, Integer>> shortestEdgeToCoveredVertex = new HashMap<>();
    final Map<EdgeId, Double> nextEdgeScores = new HashMap<>();

    // determine the adjacent vertex that will get to each covered vertex the quickest
    // however, try to avoid going onto ciritical vertices 
    // that are not covered by this agent. This is because those
    // vertices are covered by a different agent.
    coveredVerticesReturnTime.keySet().forEach(coveredVertex -> {
      final AtomicInteger minTimesteps = new AtomicInteger(Integer.MAX_VALUE);

      context.getIncidientEdgeIds().forEach(edgeId -> {

        final int timestepsToArrive = context.distanceToVertexThroughIncidentEdge(edgeId, coveredVertex);

        if (timestepsToArrive < minTimesteps.get()) {
          shortestEdgeToCoveredVertex.put(
                  coveredVertex,
                  Pair.create(edgeId, timestepsToArrive));
          minTimesteps.set(timestepsToArrive);
        }
      });
    });

    if (PatrolMode.Indeterministic.equals(patrolMode)) {
      final ThreadLocalRandom random = ThreadLocalRandom.current();
      
      context.getIncidientEdgeIds().stream().forEach(edgeId -> {
        nextEdgeScores.put(edgeId, random.nextDouble(2.0));
      });
    } else {
      final ImmutableMap<EdgeId, Double> edgeVacantTimesteps = context.getIncidientEdgeIds().stream()
              .collect(
                      ImmutableMap.toImmutableMap(
                              Function.identity(),
                              edgeId -> currentTimestep - (double) incidentEdgeChosenTimestamps.getOrDefault(edgeId, 0)));
      final DoubleSummaryStatistics stats = edgeVacantTimesteps.values().stream()
              .mapToDouble(Double::doubleValue)
              .summaryStatistics();
      final double count = (double) stats.getCount();
      final double sum = stats.getSum();

      edgeVacantTimesteps.forEach((edgeId, vacantTimesteps) -> {
        nextEdgeScores.put(edgeId, count * vacantTimesteps / sum);
      });
    }

    //figure out score for moving to a covered vertex through a
    //an adjacent vertex. But only consider the shortest route
    // to a particular covered vertex.
    coveredVerticesReturnTime.forEach((coveredVertex, returnTime) -> {
      Optional.ofNullable(shortestEdgeToCoveredVertex.get(coveredVertex)).ifPresent(edgeAndTimestepsToArrive -> {
        final EdgeId edgeId = edgeAndTimestepsToArrive.getFirst();
        final int timestepsToArrive = edgeAndTimestepsToArrive.getSecond();
        final int arrivalTimestep = currentTimestep + timestepsToArrive;
        final double score = score(edgeId, attackInterval, arrivalTimestep, returnTime);

        nextEdgeScores.merge(edgeId, score, Double::sum);
      });
    });

    //only update the return time for a covered vertex
    if (coveredVerticesReturnTime.keySet().contains(currentVertex)) {
      coveredVerticesReturnTime.put(
              currentVertex,
              calculateReturnTime(context.getCurrentTimeStep(), context.getAttackInterval()));
    }

    chosenEdge = pickFromScores(nextEdgeScores);

    incidentEdgeChosenTimestamps.put(chosenEdge, currentTimestep);
    
    return chosenEdge;
  }

  private EdgeId pickFromScores(@NonNull final Map<EdgeId, Double> nextEdgeScores) {
    final double total = nextEdgeScores.values().stream().mapToDouble(Double::doubleValue).sum();
    final Map<EdgeId, Range<Double>> rouletteWheel = new HashMap<>(nextEdgeScores.size());
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final AtomicDouble max = new AtomicDouble(0.0);

    nextEdgeScores.forEach((edgeId, rawScore) -> {
      final double weigtedScore = rawScore / total;
      final double lower = max.get();
      final double upper = lower + weigtedScore;
      final Range<Double> range = Range.closedOpen(lower, upper);

      rouletteWheel.put(edgeId, range);
      max.set(upper);
    });

    while (true) {
      final double number = random.nextDouble(max.get());
      final Optional<EdgeId> pick = rouletteWheel.entrySet().stream()
              .filter(e -> e.getValue().contains(number))
              .map(Entry::getKey)
              .findAny();

      if (pick.isPresent()) {
        return pick.get();
      }
    }
  }

  protected abstract int calculateReturnTime(int timestep, int attackInterval);

  protected abstract double score(
          final EdgeId edgeId,
          final double attackInterval,
          final double arrivalTimestep,
          final double returnTimestep);
}
