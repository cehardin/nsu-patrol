
package edu.nova.chardin.patrol.graph.creator;

import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
public class CircleGraphCreator {
  
  public PatrolGraph create(final int numNodes, final int edgeWeight) {
    final MutableValueGraph<VertexId, EdgeWeight> graph = ValueGraphBuilder.undirected().allowsSelfLoops(false).build();
    final List<VertexId> vertices = IntStream
            .range(0, numNodes)
            .mapToObj(Integer::toString)
            .map(VertexId::new )
            .collect(Collectors.toList());
    
    vertices.forEach(v -> graph.addNode(v));
    
    IntStream.range(0, vertices.size()).forEach(i -> {
      final EdgeWeight weight = new EdgeWeight(edgeWeight);
      final VertexId from = vertices.get(i);
      final VertexId to = vertices.get((i + 1) % vertices.size());
      
      graph.putEdgeValue(from, to, weight);
    });
    
    return new PatrolGraph(ImmutableValueGraph.copyOf(graph));
  }
}
