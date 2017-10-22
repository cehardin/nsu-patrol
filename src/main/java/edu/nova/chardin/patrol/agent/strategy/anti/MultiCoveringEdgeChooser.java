package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MultiCoveringEdgeChooser implements CoveringEdgeChooser {
  
  @NonNull
  private final ImmutableList<CoveringEdgeChooser> edgeChoosers;

  @Override
  public Optional<EdgeId> choose(
          @NonNull final AgentContext context, 
          @NonNull final ImmutableSet<VertexId> coveredVertices, 
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData, 
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData,
          @NonNull final ImmutableSet<EdgeId> edgesToAvoid) {

    
    for (final CoveringEdgeChooser edgeChooser : edgeChoosers) {
      final Optional<EdgeId> result = edgeChooser.choose(
              context, 
              coveredVertices, 
              verticesData, 
              edgesData, 
              edgesToAvoid);
      
      if (result.isPresent()) {
        return result;
      }
    }
    
    throw new IllegalStateException(String.format("No edge choosers chose an edge : %s", edgeChoosers));
  }
  
  
}
