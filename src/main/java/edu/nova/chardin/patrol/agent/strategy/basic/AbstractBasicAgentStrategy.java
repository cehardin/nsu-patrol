package edu.nova.chardin.patrol.agent.strategy.basic;

import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.VertexId;

public abstract class AbstractBasicAgentStrategy implements AgentStrategy {

  @Override
  public void arrived(AgentContext context) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public VertexId choose(AgentContext context) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
}
