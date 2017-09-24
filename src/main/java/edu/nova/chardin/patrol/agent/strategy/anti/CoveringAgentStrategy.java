package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class CoveringAgentStrategy implements AgentStrategy {

  @Value
  private static class EdgeDecision {

    @NonNull
    VertexId previousVertex;
    @NonNull
    EdgeId edgeChosen;
    int timestepChosen;
  }

  private final Set<VertexId> coveredVertices = new HashSet<>();
  private final Map<VertexId, VertexData> verticesData = new HashMap<>();
  private final Map<EdgeId, EdgeData> edgesData = new HashMap<>();
  
  @NonNull
  private final CoveringEdgeChooser edgeChooser;
  private Optional<EdgeDecision> previousEdgeDecision = Optional.empty();

  @Override
  public final void thwarted(
          @NonNull final VertexId vertex,
          @NonNull final ImmutableSet<VertexId> criticalVertices,
          final int timestep,
          final int attackInterval) {

    if (!criticalVertices.contains(vertex)) {
      coveredVertices.add(vertex);
    }
  }

  @Override
  public final EdgeId choose(@NonNull final AgentContext context) {
    final int currentTimestep = context.getCurrentTimeStep();
    final VertexId currentVertex = context.getCurrentVertex();
    final EdgeId chosenEdge;
    final ImmutableSet<VertexId> othersCoveredVertices;
    final ImmutableSet<EdgeId> edgesToAvoid;

    if (previousEdgeDecision.isPresent()) {
      final EdgeDecision decision = previousEdgeDecision.get();
      
      edgesData.computeIfAbsent(
              decision.getEdgeChosen(), 
              edge -> new EdgeData(
                      decision.getPreviousVertex(), 
                      currentVertex, 
                      currentTimestep - decision.getTimestepChosen(), 
                      0, 
                      0));
      
      verticesData.computeIfAbsent(
              decision.getPreviousVertex(), 
              vertex -> new VertexData(0, 0));
     
      edgesData.computeIfPresent(
              decision.getEdgeChosen(), 
              (edge, data) -> data.withTimestepUsed(decision.getTimestepChosen()).withIncrementedUsedCount());
      
      verticesData.computeIfPresent(
              decision.getPreviousVertex(), 
              (vertex, data) -> data.withTimestepVisited(decision.getTimestepChosen()).withIncrementedVisitCount());
    }
    
    othersCoveredVertices = context.getCriticalVertices().stream()
            .filter(v -> !coveredVertices.contains(v))
            .collect(ImmutableSet.toImmutableSet());
    edgesToAvoid = edgesData.entrySet().stream()
            .filter(e -> context.getIncidientEdgeIds().contains(e.getKey()))
            .filter(e -> e.getValue().getSource().equals(currentVertex))
            .filter(e -> othersCoveredVertices.contains(e.getValue().getDestination()))
            .map(Entry::getKey)
            .collect(ImmutableSet.toImmutableSet());
    
    chosenEdge = edgeChooser.choose(
            context, 
            ImmutableSet.copyOf(coveredVertices), 
            ImmutableMap.copyOf(verticesData), 
            ImmutableMap.copyOf(edgesData),
            ImmutableSet.copyOf(edgesToAvoid))
            .get();

    previousEdgeDecision = Optional.of(new EdgeDecision(currentVertex, chosenEdge, currentTimestep));

    return chosenEdge;
  }
}
