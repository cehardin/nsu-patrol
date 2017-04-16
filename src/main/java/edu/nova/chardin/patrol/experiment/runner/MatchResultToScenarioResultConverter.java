package edu.nova.chardin.patrol.experiment.runner;

import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.result.MatchResult;
import edu.nova.chardin.patrol.experiment.result.ScenarioResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
public final class MatchResultToScenarioResultConverter implements Function<MatchResult, ScenarioResult> {

  @NonNull
  Scenario scenario;

  @Override
  public ScenarioResult apply(@NonNull final MatchResult mr) {
    return ScenarioResult.builder()
            .scenario(scenario)
            .idlenessTargetVerticesStatistics(mr.getIdlenessTargetVerticesStatistics())
            .idlenessNonTargetVerticesStatistics(mr.getIdlenessNonTargetVerticesStatistics())
            .idlenessAllVerticesStatistics(mr.getIdlenessAllVerticesStatistics())
            .numberOfTargetVerticesThwartedThenCompromised(mr.getNumberOfTargetVerticesThwartedThenCompromised())
            .numberOfTargetVerticesNotAttacked(mr.getNumberOfTargetVerticesNotAttacked())
            .numberOfTargetVerticesDiscoveredCritical(mr.getNumberOfTargetVerticesDiscoveredCritical())
            .numberOfTargetVerticesCompromised(mr.getNumberOfTargetVerticesCompromised())
            .build();
  }

}
