package edu.nova.chardin.patrol.graph;

import static com.google.common.collect.Sets.difference;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Uses the https://en.wikipedia.org/wiki/Nearest_neighbour_algorithm to estimate the TSP length of a graph.
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log
class TspLengthCalculator implements Function<PatrolGraph, Integer> {

  public static final TspLengthCalculator INSTANCE = new TspLengthCalculator();
  
  @Override
  public Integer apply(@NonNull final PatrolGraph graph) {
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final ImmutableSet<VertexId> allVertices = graph.getVertices();
    final ConcurrentSkipListMap<Integer, ImmutableList<VertexId>> costMap = new ConcurrentSkipListMap<>();
    final Queue<Integer> allCosts = new ConcurrentLinkedQueue<>();
    final IntSummaryStatistics costStatistics;
    final Entry<Integer, ImmutableList<VertexId>> shortestCost;

    //try with each vertex as a starting vertex
    allVertices.parallelStream().forEach(startingVertex -> {
      final Pair<Integer, ImmutableList<VertexId>> costPath = apply(graph, startingVertex);
      final Integer cost = costPath.getFirst();
      final ImmutableList<VertexId> path = costPath.getSecond();
      
      allCosts.add(cost);
      
      costMap.putIfAbsent(cost, path);
    });

    costStatistics = allCosts.stream().mapToInt(Integer::intValue).summaryStatistics();
    shortestCost = costMap.firstEntry();
    log.info(String.format(
            "Calculated approximate TSP length of graph '%s'. Found %d costs in %d ms with a minimum of %d, a maximum of %d, and an average of %.2f. Path cost is %d with %d vertices : %s",
            graph,
            costStatistics.getCount(),
            stopwatch.stop().elapsed(TimeUnit.MILLISECONDS),
            costStatistics.getMin(),
            costStatistics.getMax(),
            costStatistics.getAverage(),
            shortestCost.getKey(),
            shortestCost.getValue().stream().map(VertexId::getValue).count(),
            shortestCost.getValue().stream().map(VertexId::getValue).collect(Collectors.toList())));

    //return the lowest score
    return shortestCost.getKey();
  }

  /**
   * Calculate using neighrest neighbors using a given starting vertex
   */
  private static Pair<Integer, ImmutableList<VertexId>> apply(
          @NonNull final PatrolGraph graph,
          @NonNull final VertexId startingVertex) {

    final Set<VertexId> unvisitedVertices = new HashSet<>(graph.getVertices());
    final List<VertexId> path = new ArrayList<>(unvisitedVertices.size() * 2);
    final Pair<Integer, ImmutableList<VertexId>> shortestReturnPathPair;
    VertexId currentVertex = startingVertex;
    int totalCost = 0;

    path.add(currentVertex);

    while (!unvisitedVertices.isEmpty()) {
      final TreeMap<Integer, Pair<VertexId, ImmutableList<VertexId>>> unvisitedVerticesDistance = new TreeMap<>();
      
      unvisitedVertices.remove(currentVertex);

      for (final VertexId unvisitedVertex : unvisitedVertices) {
        final Pair<Integer, ImmutableList<VertexId>> shortestPathPair;
        final ImmutableList<VertexId> truePath;

        shortestPathPair = ShortestPathCalculator.INSTANCE.apply(graph, currentVertex, unvisitedVertex);
        truePath = shortestPathPair.getSecond().subList(1, shortestPathPair.getSecond().size());
        unvisitedVerticesDistance.put(shortestPathPair.getFirst(), Pair.create(unvisitedVertex, truePath));
      }

      if (!unvisitedVerticesDistance.isEmpty()) {
        final Pair<VertexId, ImmutableList<VertexId>> chosenNextVertex = unvisitedVerticesDistance.firstEntry().getValue();

        totalCost += unvisitedVerticesDistance.firstEntry().getKey();
        currentVertex = chosenNextVertex.getFirst();
        path.addAll(chosenNextVertex.getSecond());
        unvisitedVertices.removeAll(chosenNextVertex.getValue());
      }
    }

    shortestReturnPathPair = ShortestPathCalculator.INSTANCE.apply(graph, currentVertex, startingVertex);

    totalCost += shortestReturnPathPair.getFirst();
    path.addAll(shortestReturnPathPair.getSecond().subList(1, shortestReturnPathPair.getSecond().size()));

    return Pair.create(totalCost, ImmutableList.copyOf(path));
  }

}
