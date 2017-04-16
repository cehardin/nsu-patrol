package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.Scenario;
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
public class ScenarioRunner implements Function<Scenario, ScenarioResult> {

  EventBus eventBus;
  MatchRunner matchRunner;
  MatchResultCombiner matchResultCombiner;

  @Override
  public ScenarioResult apply(@NonNull final Scenario scenario) {

    return createMatches(scenario)
            .parallelStream()
            .map(matchRunner)
            .reduce(matchResultCombiner)
            .map(new MatchResultToScenarioResultConverter(scenario))
            .get();
  }

  public ImmutableSet<Match> createMatches(@NonNull final Scenario scenario) {

    final Experiment experiment = scenario.getExperiment();
    final ImmutableSet.Builder<Match> matches = ImmutableSet.builder();

    experiment.getAgentStrategyTypes().forEach(agentStrategyType -> {
      experiment.getAdversaryStrategyTypes().forEach(adversaryStrategyType -> {
        scenario.getAttackIntervals().forEach(attackInterval -> {
          matches.add(
                  Match.builder()
                          .scenario(scenario)
                          .agentStrategyType(agentStrategyType)
                          .adversaryStrategyType(adversaryStrategyType)
                          .attackInterval(attackInterval)
                          .build());
        });
      });
    });

    return matches.build();
  }

}
