package edu.nova.chardin.patrol.graph;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.graph.ImmutableValueGraph;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Calculates the shortest path in a graph.
 */
@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
public class ShortestPathCalculator implements 
        BiFunction<ImmutableValueGraph<VertexId, EdgeWeight>, Pair<VertexId, VertexId>, Pair<Integer, ImmutableList<VertexId>>>{
  /**
   * Calculate from source vertex to destination vertex, returning the full path (including the source and destination vertices).
   * @param graph The graph
   * @param sourceAndDestinationVertices The source and destination vertices
   * @return A pair of the weighted path length and the vertices in the path
   */
  @Override
  public Pair<Integer, ImmutableList<VertexId>> apply(
          @NonNull final ImmutableValueGraph<VertexId, EdgeWeight> graph, 
          @NonNull final Pair<VertexId, VertexId> sourceAndDestinationVertices) {
    
    final VertexId sourceVertex = Objects.requireNonNull(sourceAndDestinationVertices.getFirst());
    final VertexId destinationVertex = Objects.requireNonNull(sourceAndDestinationVertices.getSecond());
    final ImmutableSet<VertexId> vertices = ImmutableSet.copyOf(graph.nodes());
    final Set<VertexId> unvisited = new HashSet<>(vertices.size());
    final Map<VertexId, Integer> distance = new HashMap<>(vertices.size());
    final Map<VertexId, VertexId> previous = new HashMap<>(vertices.size());

    if (!vertices.contains(sourceVertex)) {
      throw new IllegalArgumentException(String.format("Source vertex '%s' is not in the graph", sourceVertex));
    }

    if (!vertices.contains(destinationVertex)) {
      throw new IllegalArgumentException(String.format("Destination vertex '%s' is not in the graph", sourceVertex));
    }

    for (final VertexId vertex : vertices) {
      distance.put(vertex, Integer.MAX_VALUE);
      previous.put(vertex, null);
      unvisited.add(vertex);
    }

    distance.put(sourceVertex, 0);

    while (!unvisited.isEmpty()) {
      final TreeMap<Integer, VertexId> minVertices = new TreeMap<>();
      final VertexId vertex;

      if (!unvisited.contains(destinationVertex)) {
        break;
      }
      
      for (final Entry<VertexId, Integer> entry : Maps.filterKeys(distance, k -> unvisited.contains(k)).entrySet()) {
        minVertices.put(entry.getValue(), entry.getKey());
      }

      vertex = minVertices.firstEntry().getValue();
      unvisited.remove(vertex);

      for (final VertexId neighbor : Sets.intersection(graph.adjacentNodes(vertex), unvisited)) {
        final int altDistance = distance.get(vertex) + graph.edgeValue(vertex, neighbor).getValue();

        if (altDistance < distance.get(neighbor)) {
          distance.put(neighbor, altDistance);
          previous.put(neighbor, vertex);
        }
      }
    }

    return Pair.create(distance.get(destinationVertex), path(previous, destinationVertex));
  }

  /**
   * After the path has been found, this method follows the previous map to get the path in reverse, then flips
   * it into the normal order.
   * @param previous The previous map.
   * @param destinationVertex The desitnation vertex
   * @return The path.
   */
  private ImmutableList<VertexId> path(
          @NonNull final Map<VertexId, VertexId> previous,
          @NonNull final VertexId destinationVertex) {
    
    final List<VertexId> path = new ArrayList<>(previous.size());
    VertexId vertex = destinationVertex;

    while (vertex != null) {
      path.add(vertex);
      vertex = previous.get(vertex);
    }

    Collections.reverse(path);
    
    return ImmutableList.copyOf(path);
  }

}
