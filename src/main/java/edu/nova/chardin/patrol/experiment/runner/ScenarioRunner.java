  package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import edu.nova.chardin.patrol.experiment.event.ScenarioLifecycleEvent;
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

  @Override
  public ScenarioResult apply(@NonNull final Scenario scenario) {

    final ScenarioResult result;
    
    
    eventBus.post(new ScenarioLifecycleEvent(scenario, Lifecycle.Started));
    
    result = ScenarioResult.builder()
            .scenario(scenario)
            .matchResults(scenario.getMatches().parallelStream().map(matchRunner).collect(ImmutableList.toImmutableList()))
            .build();
    
    eventBus.post(new ScenarioLifecycleEvent(scenario, Lifecycle.Finished));
    
    return result;
  }
}
