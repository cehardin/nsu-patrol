package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class CoveringLongestUnusedEdgeChooser implements CoveringEdgeChooser {

  @Override
  public Optional<EdgeId> choose(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData,
          @NonNull final ImmutableSet<EdgeId> edgesToAvoid) {
  
    return context.getIncidientEdgeIds().stream()
                .map(e -> Pair.create(
                        e, 
                        Optional.ofNullable(edgesData.get(e))
                                .map(EdgeData::getTimestepUsed)
                                .orElse(0)))
                .map(p -> edgesToAvoid.contains(p.getKey()) ? Pair.create(p.getKey(), Integer.MAX_VALUE) : p)
                .min((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
                .map(Pair::getKey);
  }
}
