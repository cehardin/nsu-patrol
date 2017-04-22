package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.eventbus.EventBus;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.result.ExperimentResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({@Inject}))
@Value
@Getter(AccessLevel.NONE)
public class ExperimentRunner implements Function<Experiment, ExperimentResult> {
  
  EventBus eventBus;
  ScenarioRunner scenarioRunner;

  @Override
  public ExperimentResult apply(@NonNull final Experiment experiment) {
    
    return ExperimentResult.builder()
            .experiment(experiment)
            .scenarioResults(
                    experiment.getScenarios().parallelStream()
                            .map(scenarioRunner)
                            .collect(Collectors.toList()))
            .build();
  }
}
