package edu.nova.chardin.patrol.agent;

import lombok.NonNull;
import lombok.Value;

import java.util.function.Supplier;

@Value
public class ClassAgentStrategyFactory implements AgentStrategyFactory {
  
  @NonNull
  String name;
          
  @NonNull
  Class<? extends AgentStrategy> agentStrategyClass;

  @Override
  public AgentStrategy get() {
    try {
      return agentStrategyClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Could not create agent strategy", e);
    }
  }
}
