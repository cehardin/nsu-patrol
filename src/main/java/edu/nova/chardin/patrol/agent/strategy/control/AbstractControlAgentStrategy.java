package edu.nova.chardin.patrol.agent.strategy.control;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.VertexId;

public abstract class AbstractControlAgentStrategy implements AgentStrategy {

  @Override
  public final void thwarted(
          VertexId vertex, 
          ImmutableSet<VertexId> criticalVertices, 
          int timestep, 
          int attackInterval) {
    // do nothing
  }
  
}
