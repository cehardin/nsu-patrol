package edu.nova.chardin.patrol.experiment;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Match {
  
  @NonNull
  Scenario scenario;
  
  @NonNull
  Class<? extends AgentStrategy> agentStrategyType;
  
  @NonNull
  Class<? extends AdversaryStrategy> adversaryStrategyType;
  
  @NonNull
  Integer attackInterval;
  
}
