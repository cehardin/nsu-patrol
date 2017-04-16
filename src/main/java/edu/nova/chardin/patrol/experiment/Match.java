package edu.nova.chardin.patrol.experiment;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Supplier;

@Value
@Builder
public class Match {
  
  @NonNull
  Scenario scenario;
  
  @NonNull
  Supplier<? extends AgentStrategy> agentStrategySupplier;
  
  @NonNull
  Supplier<? extends AdversaryStrategy> adversaryStrategySupplier;
  
  @NonNull
  Integer attackInterval;
  
}
