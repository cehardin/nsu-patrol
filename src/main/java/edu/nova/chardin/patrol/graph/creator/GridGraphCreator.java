
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

import java.util.ArrayList;
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
public class GridGraphCreator {
  
  public PatrolGraph create(final int width, final int height, final int edgeWeight) {
    final MutableValueGraph<VertexId, EdgeWeight> graph = ValueGraphBuilder.undirected().allowsSelfLoops(false).build();
    final List<List<VertexId>> vertices = new ArrayList<>();
    
    //create the vertices
    IntStream.range(0, height).forEach(y -> {
      vertices.add(
              IntStream.range(0, width)
                      .mapToObj(x -> String.format("v-%d-%d", x, y))
                      .map(VertexId::new)
                      .collect(Collectors.toList()));
    });
    
    //add the vertices to the graph
    vertices.forEach(line -> line.forEach(vertex -> graph.addNode(vertex)));
    
    //connect the vertices vertically
    IntStream.range(0, height - 1).forEach(y -> {
      final List<VertexId> currentLine = vertices.get(y);
      final List<VertexId> nextLine = vertices.get(y + 1);
      IntStream.range(0, width).forEach(x -> {
        final VertexId vertexA = currentLine.get(x);
        final VertexId vertexB = nextLine.get(x);
        graph.putEdgeValue(vertexA, vertexB, new EdgeWeight(edgeWeight));
      });
    });
    
    //connect the vertices horizontally
    IntStream.range(0, height).forEach(y -> {
      final List<VertexId> line = vertices.get(y);
      IntStream.range(0, width - 1).forEach(x -> {
        final VertexId vertexA = line.get(x);
        final VertexId vertexB = line.get(x + 1);
        graph.putEdgeValue(vertexA, vertexB, new EdgeWeight(edgeWeight));
      });
    });
    
    return new PatrolGraph(
            ImmutableValueGraph.copyOf(graph), 
            String.format(
                    "%d x %d grid with edge weight %d", 
                    width,
                    height,
                    edgeWeight));
  }
}
