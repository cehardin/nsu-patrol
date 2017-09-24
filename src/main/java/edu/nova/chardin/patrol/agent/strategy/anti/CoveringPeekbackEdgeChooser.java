package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.math3.util.Pair;

public class CoveringPeekbackEdgeChooser implements CoveringEdgeChooser {

  @Value
  static class ReturnOnEdgeToCheckVertex {
    @NonNull
    EdgeId edgeToReturnOn;
    @NonNull
    VertexId toCheckVertex;
  }
  
  final Set<VertexId> verticesChecked = new HashSet<>();
  final Set<EdgeId> edgesChecked = new HashSet<>();
  Optional<ReturnOnEdgeToCheckVertex> returnOnEdgeToCheckVertex = Optional.empty();
  Optional<VertexId> checkingVertex = Optional.empty();

  @Override
  public Optional<EdgeId> choose(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData,
          @NonNull final ImmutableSet<EdgeId> edgesToAvoid) {

    final VertexId currentVertex = context.getCurrentVertex();
    final AtomicReference<EdgeId> chosenEdge = new AtomicReference<>();

    verticesChecked.addAll(context.getCriticalVertices());
    
    checkingVertex.ifPresent(v -> {
      if (currentVertex.equals(v)) {
        verticesChecked.add(v);
      }
      checkingVertex = Optional.empty();
    });
    
    returnOnEdgeToCheckVertex.ifPresent(c -> {
      if (context.getIncidientEdgeIds().contains(c.getEdgeToReturnOn())) {
        chosenEdge.set(c.getEdgeToReturnOn());
        checkingVertex = Optional.of(c.getToCheckVertex());
      }
      returnOnEdgeToCheckVertex = Optional.empty();
    });
    
    if (chosenEdge.get() == null) {
      if (!verticesChecked.contains(currentVertex)) {
        final Optional<EdgeId> edge = context.getIncidientEdgeIds().stream()
                .filter(e -> !edgesChecked.contains(e))
                .map(e -> Pair.create(e, Optional.ofNullable(edgesData.get(e)).map(EdgeData::getDestination)))
                .map(p -> Pair.create(p.getKey(), p.getValue().map(v -> coveredVertices.contains(v)).orElse(false)))
                .filter(p -> p.getValue() == false)
                .filter(p -> !edgesToAvoid.contains(p.getKey()))
                .map(Pair::getKey)
                .findAny();
        
        edge.ifPresent(e -> {
          returnOnEdgeToCheckVertex = Optional.of(
                  new ReturnOnEdgeToCheckVertex(
                          e.reversed(), 
                          currentVertex));
          chosenEdge.set(e);
        });
        
      }
    }
    
    if (chosenEdge.get() != null) {
      edgesChecked.add(chosenEdge.get());
    }
    
    return Optional.ofNullable(chosenEdge.get());
    
  }
}
