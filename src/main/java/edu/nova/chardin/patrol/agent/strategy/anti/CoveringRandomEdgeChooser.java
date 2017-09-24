package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.NonNull;

public class CoveringRandomEdgeChooser implements CoveringEdgeChooser {

  @Override
  public Optional<EdgeId> choose(
          @NonNull final AgentContext context, 
          @NonNull final ImmutableSet<VertexId> coveredVertices, 
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData, 
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData,
          @NonNull final ImmutableSet<EdgeId> edgesToAvoid) {

    final ImmutableSet<EdgeId> safeEdges = context.getIncidientEdgeIds().stream()
            .filter(e -> !edgesToAvoid.contains(e))
            .collect(ImmutableSet.toImmutableSet());
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final ImmutableList<EdgeId> edges;
    
    if (safeEdges.isEmpty()) {
      edges = context.getIncidientEdgeIds().asList();
    } else {
      edges = safeEdges.asList();
    }
    
    return Optional.of(edges.get(random.nextInt(edges.size())));
  }
  
}
