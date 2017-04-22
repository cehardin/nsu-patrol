package edu.nova.chardin.patrol.experiment;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ExperimentSummary {
  
  @NonNull
  Integer totalScenarioCount;
  
  @NonNull
  Integer totalMatchCount;
  
  @NonNull
  Integer totalGameCount;
}
