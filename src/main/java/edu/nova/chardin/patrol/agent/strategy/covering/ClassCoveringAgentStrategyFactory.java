package edu.nova.chardin.patrol.agent.strategy.covering;

import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.agent.AgentStrategyFactory;
import lombok.NonNull;
import lombok.Value;

@Value
public class ClassCoveringAgentStrategyFactory implements AgentStrategyFactory {
  
  @NonNull
  String name;
  
  Class<? extends CoveringStrategy> coveringStrategyClass;

  @Override
  public AgentStrategy get() {
    try {
      return new CoveringAgentStrategy(coveringStrategyClass.newInstance());
    } catch (IllegalAccessException | InstantiationException e) {
      throw new RuntimeException("Could not create covering strategy", e);
    }
  }
}
