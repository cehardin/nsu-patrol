package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BiCoveringEdgeChooser implements CoveringEdgeChooser {
  @NonNull
  private final CoveringEdgeChooser a;
  
  @NonNull
  private final CoveringEdgeChooser b;

  @Override
  public Optional<EdgeId> choose(
          @NonNull final AgentContext context, 
          @NonNull final ImmutableSet<VertexId> coveredVertices, 
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData, 
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData,
          @NonNull final ImmutableSet<EdgeId> edgesToAvoid) {

    final Optional<EdgeId> r;
    final Optional<EdgeId> ar = a.choose(context, coveredVertices, verticesData, edgesData, edgesToAvoid);
    
    if (ar.isPresent()) {
      r = ar;
    } else {
      r = b.choose(context, coveredVertices, verticesData, edgesData, edgesToAvoid);
    }
    
    return r;
  }
  
  
}
