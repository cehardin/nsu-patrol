package edu.nova.chardin.patrol.agent;

import lombok.NonNull;
import lombok.Value;

import java.util.function.Supplier;

@Value
public class SupplierAgentStrategyFactory implements AgentStrategyFactory {
  
  @NonNull
  String name;
          
  @NonNull
  Supplier<? extends AgentStrategy> agentStrategySupplier;

  @Override
  public AgentStrategy get() {
    return agentStrategySupplier.get();
  }
}
