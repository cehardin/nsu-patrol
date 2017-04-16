package edu.nova.chardin.patrol.graph;

import static com.google.common.collect.Sets.difference;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
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
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Uses the https://en.wikipedia.org/wiki/Nearest_neighbour_algorithm to estimate the TSP length of a graph.
 *
 */
@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
@Log
public class TspLengthCalculator implements Function<ImmutableValueGraph<VertexId, EdgeWeight>, Integer> {

  @NonNull
  ShortestPathCalculator shortestPathCalculator;

  @Override
  public Integer apply(@NonNull final ImmutableValueGraph<VertexId, EdgeWeight> graph) {
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final ImmutableSet<VertexId> allVertices = ImmutableSet.copyOf(graph.nodes());
    final TreeMap<Integer, ImmutableList<VertexId>> costMap = new TreeMap<>();
    final List<Integer> allCosts = new ArrayList<>(allVertices.size());
    final IntSummaryStatistics costStatistics;
    final Entry<Integer, ImmutableList<VertexId>> shortestCost;

    //try with each vertex as a starting vertex
    for (final VertexId startingVertex : allVertices) {
      final Pair<Integer, ImmutableList<VertexId>> costPath = apply(graph, startingVertex);
      final Integer cost = costPath.getFirst();
      final ImmutableList<VertexId> path = costPath.getSecond();
      
      allCosts.add(cost);
      
      if (costMap.containsKey(cost)) {
        if (costMap.get(cost).size() > path.size()) {
          costMap.put(cost, path);
        }
      } else {
        costMap.put(cost, path);
      }
    }

    costStatistics = allCosts.stream().mapToInt(Integer::intValue).summaryStatistics();
    shortestCost = costMap.firstEntry();
    log.info(String.format(
            "Calculated %d costs in %d ms with a minimum of %d, a maximum of %d, and an average of %.2f. Path cost is %d : %s.",
            costStatistics.getCount(),
            stopwatch.stop().elapsed(TimeUnit.MILLISECONDS),
            costStatistics.getMin(),
            costStatistics.getMax(),
            costStatistics.getAverage(),
            shortestCost.getKey(),
            shortestCost.getValue().stream().map(VertexId::getValue).collect(Collectors.toList())));

    //return the lowest score
    return shortestCost.getKey();
  }

  /**
   * Calculate using neighrest neighbors using a given starting vertex
   */
  private Pair<Integer, ImmutableList<VertexId>> apply(
          @NonNull final ImmutableValueGraph<VertexId, EdgeWeight> graph,
          @NonNull final VertexId startingVertex) {

    final ImmutableSet<VertexId> allVertices = ImmutableSet.copyOf(graph.nodes());
    final Set<VertexId> visitedVertices = new HashSet<>(allVertices.size());
    final List<VertexId> path = new ArrayList<>(allVertices.size());
    final Pair<Integer, ImmutableList<VertexId>> shortestReturnPathPair;
    VertexId currentVertex = startingVertex;
    int totalCost = 0;

    path.add(currentVertex);

    while (!visitedVertices.containsAll(allVertices)) {
      final TreeMap<Integer, Pair<VertexId, ImmutableList<VertexId>>> unvisitedVerticesDistance = new TreeMap<>();

      visitedVertices.add(currentVertex);

      for (final VertexId unvisitedVertex : difference(allVertices, visitedVertices)) {
        final Pair<Integer, ImmutableList<VertexId>> shortestPathPair;
        final ImmutableList<VertexId> truePath;

        shortestPathPair = shortestPathCalculator.apply(graph, Pair.create(currentVertex, unvisitedVertex));
        truePath = shortestPathPair.getSecond().subList(1, shortestPathPair.getSecond().size());
        unvisitedVerticesDistance.put(shortestPathPair.getFirst(), Pair.create(unvisitedVertex, truePath));
      }

      if (!unvisitedVerticesDistance.isEmpty()) {
        final Pair<VertexId, ImmutableList<VertexId>> chosenNextVertex = unvisitedVerticesDistance.firstEntry().getValue();

        totalCost += unvisitedVerticesDistance.firstEntry().getKey();
        currentVertex = chosenNextVertex.getFirst();
        path.addAll(chosenNextVertex.getSecond());
        visitedVertices.addAll(chosenNextVertex.getSecond());
      }
    }

    shortestReturnPathPair = shortestPathCalculator.apply(graph, Pair.create(currentVertex, startingVertex));

    totalCost += shortestReturnPathPair.getFirst();
    path.addAll(shortestReturnPathPair.getSecond());

    return Pair.create(totalCost, ImmutableList.copyOf(path));
  }

}
