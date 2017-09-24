package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;

public class CoveringProbabilisticLongestUnusedEdgeChooser implements CoveringEdgeChooser {

  @Override
  public Optional<EdgeId> choose(
          @NonNull final AgentContext context,
          @NonNull final ImmutableSet<VertexId> coveredVertices,
          @NonNull final ImmutableMap<VertexId, VertexData> verticesData,
          @NonNull final ImmutableMap<EdgeId, EdgeData> edgesData,
          ImmutableSet<EdgeId> edgesToAvoid) {

    final int currentTimestep = context.getCurrentTimeStep();
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final ImmutableList<EdgeId> edges = context.getIncidientEdgeIds().stream()
                .map(e -> Pair.create(
                        e, 
                        Optional.ofNullable(edgesData.get(e))
                                .map(EdgeData::getTimestepUsed)
                                .orElse(0)))
                .map(p -> Pair.create(p.getKey(), currentTimestep - p.getValue()))
                .map(p -> edgesToAvoid.contains(p.getKey()) ? Pair.create(p.getKey(), 1) : p)
                .flatMap(p -> Collections.nCopies(p.getValue(), p.getKey()).stream())
                .collect(ImmutableList.toImmutableList());
    
    return Optional.of(edges.get(random.nextInt(edges.size())));
                
  }
}
