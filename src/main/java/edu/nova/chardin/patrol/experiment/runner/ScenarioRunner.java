package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import edu.nova.chardin.patrol.experiment.event.MatchLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.ScenarioLifecycleEvent;
import edu.nova.chardin.patrol.experiment.result.ScenarioResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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

    final ImmutableSet<Match> matches;
    final ScenarioResult result;
    
    eventBus.post(new ScenarioLifecycleEvent(scenario, Lifecycle.Started));
    
    matches = createMatches(scenario);
    
    matches.parallelStream()
            .map(m -> new MatchLifecycleEvent(m, Lifecycle.Created))
            .forEach(e -> eventBus.post(e));
    
    result = matches.parallelStream()
            .map(matchRunner)
            .reduce(matchResultCombiner)
            .map(new MatchResultToScenarioResultConverter(scenario))
            .get();
    
    eventBus.post(new ScenarioLifecycleEvent(scenario, Lifecycle.Finished));
    
    return result;
  }

  private ImmutableSet<Match> createMatches(@NonNull final Scenario scenario) {

    final Experiment experiment = scenario.getExperiment();
    final Set<Match> matches = ConcurrentHashMap.newKeySet(
            experiment.getAgentStrategySuppliers().size() 
                    * experiment.getAdversaryStrategySuppliers().size() 
                    * scenario.getAttackIntervals().size());

    experiment.getAgentStrategySuppliers().parallelStream().forEach(agentStrategySupplier -> {
      experiment.getAdversaryStrategySuppliers().parallelStream().forEach(adversaryStrategySupplier -> {
        scenario.getAttackIntervals().parallelStream().forEach(attackInterval -> {
          matches.add(Match.builder()
                          .scenario(scenario)
                          .agentStrategySupplier(agentStrategySupplier)
                          .adversaryStrategySupplier(adversaryStrategySupplier)
                          .attackInterval(attackInterval)
                          .build());
        });
      });
    });

    return ImmutableSet.copyOf(matches);
  }

}
