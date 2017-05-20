package edu.nova.chardin.patrol.agent;

import java.util.function.Supplier;

public interface AgentStrategyFactory extends Supplier<AgentStrategy> {
  
  String getName();
}
