package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.graph.EdgeId;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;

public final class ChooseLongestUnvisitedEdgeBiasStrategy {
  private final Map<EdgeId, Integer> incidentEdgeChosenTimestamps = new HashMap<>();

  public ImmutableMap<EdgeId, Double> scoreEdgeBias(
          final int currentTimeStep,
          @NonNull final ImmutableSet<EdgeId> incidientEdgeIds) {
    
    Preconditions.checkState(currentTimeStep > 0);
    
    return incidientEdgeIds.stream()
            .collect(
                    ImmutableMap.toImmutableMap(
                            Function.identity(),
                            edgeId -> (double) (currentTimeStep - incidentEdgeChosenTimestamps.getOrDefault(edgeId, 0))));
  }
  
  public void edgeChosen(final int currentTimeStep, @NonNull final EdgeId edge) {
    Preconditions.checkState(currentTimeStep > 0);
    incidentEdgeChosenTimestamps.put(edge, currentTimeStep);
  }
}
