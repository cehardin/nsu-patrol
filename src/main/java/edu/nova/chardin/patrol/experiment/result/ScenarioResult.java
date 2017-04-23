package edu.nova.chardin.patrol.experiment.result;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.experiment.Scenario;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ScenarioResult {
  
  @NonNull
  Scenario scenario;        
  
  @NonNull
  ImmutableList<MatchResult> matchResults;
}
