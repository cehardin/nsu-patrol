package edu.nova.chardin.patrol.agent.strategy.anti;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Optional;

public interface CoveringEdgeChooser {

  Optional<EdgeId> choose(
          AgentContext context,
          ImmutableSet<VertexId> coveredVertices,
          ImmutableMap<VertexId, VertexData> verticesData,
          ImmutableMap<EdgeId, EdgeData> edgesData,
          ImmutableSet<EdgeId> edgesToAvoid);
}
