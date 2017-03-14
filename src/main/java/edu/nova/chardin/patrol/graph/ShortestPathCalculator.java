package edu.nova.chardin.patrol.graph;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.graph.ImmutableValueGraph;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Calculates the shortest path in a graph.
 */
@Singleton
public class ShortestPathCalculator {

  @Inject
  ShortestPathCalculator() {
  }

  /**
   * Calculate from source vertex to destination vertex, returning the full path (including the source and destination vertices).
   * @param graph The graph
   * @param sourceVertex The source vertex
   * @param destinationVertex The destination vertex
   * @return A pair of the weighted path length and the vertices in the path
   */
  public Pair<Integer, ImmutableList<String>> calculate(
          @NonNull final ImmutableValueGraph<String, Integer> graph,
          @NonNull final String sourceVertex,
          @NonNull final String destinationVertex) {

    final ImmutableSet<String> vertices = ImmutableSet.copyOf(graph.nodes());
    final Set<String> unvisited = new HashSet<>(vertices.size());
    final Map<String, Integer> distance = new HashMap<>(vertices.size());
    final Map<String, String> previous = new HashMap<>(vertices.size());

    if (!vertices.contains(sourceVertex)) {
      throw new IllegalArgumentException(String.format("Source vertex '%s' is not in the graph", sourceVertex));
    }

    if (!vertices.contains(destinationVertex)) {
      throw new IllegalArgumentException(String.format("Destination vertex '%s' is not in the graph", sourceVertex));
    }

    for (final String vertex : vertices) {
      distance.put(vertex, Integer.MAX_VALUE);
      previous.put(vertex, null);
      unvisited.add(vertex);
    }

    distance.put(sourceVertex, 0);

    while (!unvisited.isEmpty()) {
      final TreeMap<Integer, String> minVertices = new TreeMap<>();
      final String vertex;

      for (final Entry<String, Integer> entry : Maps.filterKeys(distance, k -> unvisited.contains(k)).entrySet()) {
        minVertices.put(entry.getValue(), entry.getKey());
      }

      vertex = minVertices.firstEntry().getValue();
      unvisited.remove(vertex);

      for (final String neighbor : Sets.intersection(graph.adjacentNodes(vertex), unvisited)) {
        final int altDistance = distance.get(vertex) + graph.edgeValue(vertex, neighbor);

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
  private ImmutableList<String> path(
          @NonNull final Map<String, String> previous,
          @NonNull final String destinationVertex) {
    
    final List<String> path = new ArrayList<>(previous.size());
    String vertex = destinationVertex;

    while (vertex != null) {
      path.add(vertex);
      vertex = previous.get(vertex);
    }

    Collections.reverse(path);
    
    return ImmutableList.copyOf(path);
  }

}
