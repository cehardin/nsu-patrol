package edu.nova.chardin.patrol.agent.strategy.covering;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.VertexId;

import java.util.Map;

public interface CoveringStrategy {

  void arrived(AgentContext context, Map<VertexId, Integer> coveredVertices);

  VertexId choose(AgentContext context, Map<VertexId, Integer> coveredVertices);
}
