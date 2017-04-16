package edu.nova.chardin.patrol.agent;

import edu.nova.chardin.patrol.graph.VertexId;

public interface AgentStrategy {
  
  void arrived(AgentContext context);
  
  VertexId choose(AgentContext context);
}
