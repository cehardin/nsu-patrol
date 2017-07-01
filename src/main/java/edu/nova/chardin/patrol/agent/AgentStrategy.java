package edu.nova.chardin.patrol.agent;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;


public interface AgentStrategy {
  
  void thwarted(
          VertexId vertex, 
          ImmutableSet<VertexId> criticalVertices,
          int timestep, 
          int attackInterval);
  
  EdgeId choose(AgentContext context);
}
