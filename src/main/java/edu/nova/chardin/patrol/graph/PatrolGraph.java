package edu.nova.chardin.patrol.graph;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableValueGraph;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.math3.util.Pair;

import java.util.Collections;
import java.util.concurrent.ExecutionException;


@Value
public class PatrolGraph {
  @Getter(AccessLevel.NONE)
  ImmutableValueGraph<VertexId, EdgeWeight> graph;
  
  @NonNull
  String name;
  
  @Getter(lazy = true)
  Integer approximateTspLength = calculateApproximateTspLength();
  
  @Getter(AccessLevel.NONE)
  ImmutableMap<VertexId, ImmutableSet<VertexId>> adjacentVertices;
  
  @Getter(AccessLevel.NONE)
  ImmutableSet<VertexId> vertices;
  
  @Getter(AccessLevel.NONE)
  ImmutableSet<EndpointPair<VertexId>> edges;

  public PatrolGraph(
          @NonNull final ImmutableValueGraph<VertexId, EdgeWeight> graph, 
          @NonNull final String name) {
    
    Preconditions.checkArgument(!graph.isDirected(), "Graph cannot be directed");
    final ImmutableMap.Builder<VertexId, ImmutableSet<VertexId>> adjacentVerticesBuilder = ImmutableMap.builder();
    
    for (final VertexId vertexId : graph.nodes()) {
      Preconditions.checkArgument(
              !graph.adjacentNodes(vertexId).isEmpty(), 
              "Graph has an unconnected vertex : %s", 
              vertexId);
      Preconditions.checkArgument(
              !graph.adjacentNodes(vertexId).equals(Collections.singleton(vertexId)), 
              "Graph has a vertex that is only connected to itself : %s", 
              vertexId);
      adjacentVerticesBuilder.put(vertexId, ImmutableSet.copyOf(graph.adjacentNodes(vertexId)));
    }
    
    this.graph = graph;
    this.name = name.trim();
    this.adjacentVertices = adjacentVerticesBuilder.build();
    this.vertices = ImmutableSet.copyOf(graph.nodes());
    this.edges = ImmutableSet.copyOf(graph.edges());
    
    Preconditions.checkArgument(!name.isEmpty(), "Name cannot be empty");
  }
  
  LoadingCache<Pair<VertexId, VertexId>, Pair<Integer, ImmutableList<VertexId>>> shortestPathCache = CacheBuilder.newBuilder().build(new CacheLoader<Pair<VertexId, VertexId>, Pair<Integer, ImmutableList<VertexId>>>() {
    @Override
    public Pair<Integer, ImmutableList<VertexId>> load(Pair<VertexId, VertexId> key) throws Exception {
      return ShortestPathCalculator.INSTANCE.apply(PatrolGraph.this, key.getFirst(), key.getSecond());
    }
  });
  
  public ImmutableSet<VertexId> getVertices() {
    return vertices;
  }
  
  public ImmutableSet<EndpointPair<VertexId>> getEdges() {
    return edges;
  }
  
  public ImmutableSet<VertexId> adjacentVertices(@NonNull final VertexId vertexId) {
    return adjacentVertices.get(vertexId);
  }
  
  public EdgeWeight edgeWeight(@NonNull final VertexId vertex1, @NonNull final VertexId vertex2) {
    return graph.edgeValue(vertex1, vertex2);
  }
  
  public Pair<Integer, ImmutableList<VertexId>> shortestPath(final VertexId source, final VertexId destination) {
    try {
      return shortestPathCache.get(Pair.create(source, destination));
    } catch (ExecutionException e) {
      throw new RuntimeException("Could not calculate shortest path", e);
    }
  }
  
  private Integer calculateApproximateTspLength() {
    return TspLengthCalculator.INSTANCE.apply(this);
  }
  
  @Override
  public String toString() {
    return String.format(
            "%s (%d vertices and %d edges)", 
            getName(), 
            getVertices().size(), 
            getEdges().size());
  }
}
