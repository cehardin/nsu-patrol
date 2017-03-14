package edu.nova.chardin.patrol.graph;

import static com.google.common.collect.Sets.difference;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.util.concurrent.ListeningExecutorService;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Uses the https://en.wikipedia.org/wiki/Nearest_neighbour_algorithm to estimate the TSP length of a graph.
 *
 */
@Singleton
@Log
public class TspLengthCalculator implements Function<ImmutableValueGraph<String, Integer>, Integer> {

  private final ShortestPathCalculator shortestPathCalculator;

  @Inject
  TspLengthCalculator(@NonNull final ShortestPathCalculator shortestPathCalculator) {

    this.shortestPathCalculator = shortestPathCalculator;
  }

  @Override
  public Integer apply(@NonNull final ImmutableValueGraph<String, Integer> graph) {
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final ImmutableSet<String> allVertices = ImmutableSet.copyOf(graph.nodes());
    final TreeMap<Integer, ImmutableList<String>> costMap = new TreeMap<>();
    final List<Integer> allLengths = new ArrayList<>(allVertices.size());
    final IntSummaryStatistics lengthStatistics;
    final Entry<Integer, ImmutableList<String>> shortestCost;

    //try with each vertex as a starting vertex
    for (final String startingVertex : allVertices) {
      final Pair<Integer, ImmutableList<String>> costPath = apply(graph, startingVertex);
      final Integer cost = costPath.getFirst();
      final ImmutableList<String> path = costPath.getSecond();
      
      allLengths.add(path.size());
      
      if (costMap.containsKey(cost)) {
        if (costMap.get(cost).size() > path.size()) {
          costMap.put(cost, path);
        }
      } else {
        costMap.put(cost, path);
      }
    }

    lengthStatistics = allLengths.stream().mapToInt(Integer::intValue).summaryStatistics();
    shortestCost = costMap.firstEntry();
    log.info(String.format(
            "Calculated %d lengths in %d ms with a minimum of %d, a maximum of %d, and an average of %.2f. Path cost is %d : %s.",
            lengthStatistics.getCount(),
            stopwatch.stop().elapsed(TimeUnit.MILLISECONDS),
            lengthStatistics.getMin(),
            lengthStatistics.getMax(),
            lengthStatistics.getAverage(),
            shortestCost.getKey(),
            shortestCost.getValue()));

    //return the lowest score
    return shortestCost.getKey();
  }

  /**
   * Calculate using neighrest neighbors using a given starting vertex
   */
  private Pair<Integer, ImmutableList<String>> apply(
          @NonNull final ImmutableValueGraph<String, Integer> graph,
          @NonNull final String startingVertex) {

    final ImmutableSet<String> allVertices = ImmutableSet.copyOf(graph.nodes());
    final Set<String> visitedVertices = new HashSet<>(allVertices.size());
    final List<String> path = new ArrayList<>(allVertices.size());
    final Pair<Integer, ImmutableList<String>> shortestReturnPathPair;
    String currentVertex = startingVertex;
    int totalCost = 0;

    path.add(currentVertex);

    while (!visitedVertices.containsAll(allVertices)) {
      final TreeMap<Integer, Pair<String, ImmutableList<String>>> unvisitedVerticesDistance = new TreeMap<>();

      visitedVertices.add(currentVertex);

      for (final String unvisitedVertex : difference(allVertices, visitedVertices)) {
        final Pair<Integer, ImmutableList<String>> shortestPathPair;
        final ImmutableList<String> truePath;

        shortestPathPair = shortestPathCalculator.calculate(graph, currentVertex, unvisitedVertex);
        truePath = shortestPathPair.getSecond().subList(1, shortestPathPair.getSecond().size());
        unvisitedVerticesDistance.put(shortestPathPair.getFirst(), Pair.create(unvisitedVertex, truePath));
      }

      if (!unvisitedVerticesDistance.isEmpty()) {
        final Pair<String, ImmutableList<String>> chosenNextVertex = unvisitedVerticesDistance.firstEntry().getValue();

        totalCost += unvisitedVerticesDistance.firstEntry().getKey();
        currentVertex = chosenNextVertex.getFirst();
        path.addAll(chosenNextVertex.getSecond());
        visitedVertices.addAll(chosenNextVertex.getSecond());
      }
    }

    shortestReturnPathPair = shortestPathCalculator.calculate(graph, currentVertex, startingVertex);

    totalCost += shortestReturnPathPair.getFirst();
    path.addAll(shortestReturnPathPair.getSecond());

    return Pair.create(totalCost, ImmutableList.copyOf(path));
  }

}
