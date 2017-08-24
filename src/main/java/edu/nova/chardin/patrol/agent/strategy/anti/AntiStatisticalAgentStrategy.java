package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AntiStatisticalAgentStrategy implements AgentStrategy {

  private final Set<EdgeId> edgesToAvoid = new HashSet<>();
  private Optional<EdgeId> lastChosenEdge = Optional.empty();

  @Override
  public void thwarted(VertexId vertex, ImmutableSet<VertexId> criticalVertices, int timestep, int attackInterval) {
  }

  @Override
  public EdgeId choose(AgentContext context) {
    final ImmutableSet<EdgeId> allEdgeIds = context.getIncidientEdgeIds();
    final ImmutableSet<EdgeId> safeEdgeIds;
    final ImmutableList<EdgeId> edgeIdsToChoose;
    final EdgeId chosenEdge;
    
    lastChosenEdge.ifPresent(edgeId -> {
      if ( context.getCriticalVertices().contains(context.getCurrentVertex())) {
        edgesToAvoid.add(edgeId);
      }
    });
    
    safeEdgeIds = ImmutableSet.copyOf(Sets.difference(allEdgeIds, edgesToAvoid));
    edgeIdsToChoose = safeEdgeIds.isEmpty() ? allEdgeIds.asList() : safeEdgeIds.asList();
    chosenEdge = edgeIdsToChoose.get(ThreadLocalRandom.current().nextInt(edgeIdsToChoose.size()));
   
    lastChosenEdge = Optional.of(chosenEdge);
    
    return chosenEdge;
   
  }
}
