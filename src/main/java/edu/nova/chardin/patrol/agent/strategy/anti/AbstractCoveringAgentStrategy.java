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
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;

public abstract class AbstractCoveringAgentStrategy implements AgentStrategy {

  @Value
  private static class EdgeDecision {

    @NonNull
    VertexId previousVertex;
    @NonNull
    EdgeId edgeChosen;
    int timestepChosen;
  }

  @Value
  @Wither
  protected static class EdgeData {

    @NonNull
    VertexId source;
    @NonNull
    VertexId destination;
    int length;
    int usedCount;
    int timestepUsed;
    
    public EdgeData withIncrementedUsedCount() {
      return withUsedCount(getUsedCount() + 1);
    }
  }

  @Value
  @Wither
  protected static class VertexData {

    int visitCount;
    int timestepVisited;
    
    public VertexData withIncrementedVisitCount() {
      return withVisitCount(getVisitCount() + 1);
    }
  }

  private final Set<VertexId> coveredVertices = new HashSet<>();
  private final Map<VertexId, VertexData> verticesData = new HashMap<>();
  private final Map<EdgeId, EdgeData> edgesData = new HashMap<>();
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
    
    chosenEdge = choose(
            context, 
            ImmutableSet.copyOf(coveredVertices),
            ImmutableMap.copyOf(verticesData),
            ImmutableMap.copyOf(edgesData));

    previousEdgeDecision = Optional.of(new EdgeDecision(currentVertex, chosenEdge, currentTimestep));

    return chosenEdge;
  }

  protected abstract EdgeId choose(
          AgentContext context, 
          ImmutableSet<VertexId> coveredVertices,
          ImmutableMap<VertexId, VertexData> verticesData,
          ImmutableMap<EdgeId, EdgeData> edgesData);

}
