package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import edu.nova.chardin.patrol.experiment.event.MatchLifecycleEvent;
import edu.nova.chardin.patrol.experiment.result.GameResult;
import edu.nova.chardin.patrol.experiment.result.MatchResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

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

    final ImmutableList<GameResult> gameResults;
    final SummaryStatistics generalEffectiveness;
    final SummaryStatistics deterenceEffectiveness;
    final SummaryStatistics patrolEffectiveness;
    final SummaryStatistics defenseEffectiveness;
    final MatchResult result;

    eventBus.post(new MatchLifecycleEvent(match, Lifecycle.Started));

    gameResults = match.getGames().parallelStream().map(gameRunner).collect(ImmutableList.toImmutableList());
    generalEffectiveness = new SummaryStatistics();
    deterenceEffectiveness = new SummaryStatistics();
    patrolEffectiveness = new SummaryStatistics();
    defenseEffectiveness = new SummaryStatistics();
    gameResults.forEach(gameResult -> {
      generalEffectiveness.addValue(gameResult.getGeneralEffectiveness());
      deterenceEffectiveness.addValue(gameResult.getDeterenceEffectiveness());
      patrolEffectiveness.addValue(gameResult.getPatrolEffectiveness());
      defenseEffectiveness.addValue(gameResult.getDefenseEffectiveness());
    });
    result = MatchResult.builder()
            .match(match)
            .build();
    
    eventBus.post(new MatchLifecycleEvent(match, Lifecycle.Finished));
    
    return result;
  }
}
