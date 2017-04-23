package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import edu.nova.chardin.patrol.experiment.event.MatchLifecycleEvent;
import edu.nova.chardin.patrol.experiment.result.MatchResult;
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
public class MatchRunner implements Function<Match, MatchResult> {

  EventBus eventBus;
  GameRunner gameRunner;

  @Override
  public MatchResult apply(@NonNull final Match match) {

    final MatchResult result;

    eventBus.post(new MatchLifecycleEvent(match, Lifecycle.Started));

    result = MatchResult.builder()
            .match(match)
            .gameResults(match.getGames().parallelStream().map(gameRunner).collect(ImmutableList.toImmutableList()))
            .build();
    
    eventBus.post(new MatchLifecycleEvent(match, Lifecycle.Finished));
    
    return result;
  }
}
