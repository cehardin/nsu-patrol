package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class CoveringHardLimitEdgeChooser implements CoveringEdgeChooser {

  @Override
  public Optional<EdgeId> choose(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData,
          @NonNull final ImmutableSet<EdgeId> edgesToAvoid) {

    final double localEdgeMax = context.getIncidientEdgeIds().stream()
            .mapToDouble(e -> Optional.ofNullable(edgesData.get(e))
                    .map(EdgeData::getLength)
                    .orElse(Integer.MAX_VALUE))
            .max()
            .getAsDouble();
    final double globalEdgeMax = edgesData.values().stream()
            .mapToDouble(EdgeData::getLength)
            .max()
            .orElse(Integer.MAX_VALUE);
    final ImmutableMap<VertexId, Pair<Integer, EdgeId>> distances = coveredVertices.stream()
            .map(v -> Pair.create(v, context.bestDistanceToVertex(v)))
            .collect(ImmutableMap.toImmutableMap(Pair::getKey, Pair::getValue));
    final double currentTimestep = context.getCurrentTimeStep();
    
    
    return coveredVertices.stream()
              .map(v -> Pair.create(
                      v, 
                      Optional.ofNullable(verticesData.get(v))
                              .map(VertexData::getTimestepVisited)
                              .orElse(0)))
              .map(p -> Pair.create(
                      p.getKey(), 
                      p.getValue().doubleValue() + (double)context.getAttackInterval()))
              .map(p -> Pair.create(
                      p.getKey(), 
                      p.getValue() - (currentTimestep + localEdgeMax + globalEdgeMax + (double)distances.get(p.getKey()).getFirst())))
              .filter(p -> p.getValue() <= 0.0)
              .map(p -> Pair.create(p.getKey(), distances.get(p.getKey()).getFirst()))
              .min((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
              .map(Pair::getKey)
              .map(v -> context.bestDistanceToVertex(v).getSecond());
  }
}
