package edu.nova.chardin.patrol.experiment.result;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.experiment.Experiment;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class ExperimentResult {
  
  @NonNull
  Experiment experiment;
  
  @NonNull
  @Singular
  ImmutableSet<ScenarioResult> scenarioResults;
}
