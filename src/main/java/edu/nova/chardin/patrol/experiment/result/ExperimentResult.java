package edu.nova.chardin.patrol.experiment.result;

import com.google.common.collect.ImmutableList;
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

  public ImmutableList<CombinedMatchResult> createCombinedMatchResults() {

    final ImmutableList.Builder<CombinedMatchResult> combinedMatchResults = ImmutableList.builder();

    scenarioResults.forEach(scenarioResult -> {
      scenarioResult.getMatchResults().forEach(matchResult -> {
        combinedMatchResults.add(
                CombinedMatchResult.builder()
                        .numberOfGamesPerMatch(experiment.getNumberOfGamesPerMatch())
                        .numberOfTimestepsPerGame(scenarioResult.getScenario().getNumberOfTimestepsPerGame())
                        .graph(scenarioResult.getScenario().getGraph())
                        .numberOfAgents(scenarioResult.getScenario().getNumberOfAgents())
                        .numberOfAdversaries(scenarioResult.getScenario().getNumberOfAdversaries())
                        .adversaryStrategyFactory(matchResult.getMatch().getAdversaryStrategyFactory())
                        .agentStrategyFactory(matchResult.getMatch().getAgentStrategyFactory())
                        .attackInterval(matchResult.getMatch().getAttackInterval())
                        .executionTimeMilliSeconds(matchResult.getExecutionTimeMilliSeconds())
                        .generalEffectiveness(matchResult.getGeneralEffectiveness())
                        .deterenceEffectiveness(matchResult.getDeterenceEffectiveness())
                        .patrolEffectiveness(matchResult.getPatrolEffectiveness())
                        .defenseEffectiveness(matchResult.getDefenseEffectiveness())
                        .build());
      });
    });

    return combinedMatchResults.build();
  }

  public ImmutableList<CombinedGameResult> createCombinedGameResults() {

    final ImmutableList.Builder<CombinedGameResult> combinedGameResults = ImmutableList.builder();

    scenarioResults.forEach(scenarioResult -> {
      scenarioResult.getMatchResults().forEach(matchResult -> {
        matchResult.getGameResults().forEach(gameResult -> {
          combinedGameResults.add(
                  CombinedGameResult.builder()
                          .numberOfGamesPerMatch(experiment.getNumberOfGamesPerMatch())
                          .numberOfTimestepsPerGame(scenarioResult.getScenario().getNumberOfTimestepsPerGame())
                          .graph(scenarioResult.getScenario().getGraph())
                          .numberOfAgents(scenarioResult.getScenario().getNumberOfAgents())
                          .numberOfAdversaries(scenarioResult.getScenario().getNumberOfAdversaries())
                          .adversaryStrategyFactory(matchResult.getMatch().getAdversaryStrategyFactory())
                          .agentStrategyFactory(matchResult.getMatch().getAgentStrategyFactory())
                          .attackInterval(matchResult.getMatch().getAttackInterval())
                          .executionTimeMilliSeconds(gameResult.getExecutionTimeMilliSeconds())
                          .timeStepExecutionTimeMicroseconds(gameResult.getTimeStepExecutionTimeMicroSeconds())
                          .generalEffectiveness(gameResult.getGeneralEffectiveness())
                          .deterenceEffectiveness(gameResult.getDeterenceEffectiveness())
                          .patrolEffectiveness(gameResult.getPatrolEffectiveness())
                          .defenseEffectiveness(gameResult.getDefenseEffectiveness())
                          .attackCount(gameResult.getAttackCount())
                          .compromisedCount(gameResult.getCompromisedCount())
                          .twartedCount(gameResult.getTwartedCount())
                          .criticalVerticesCount(gameResult.getCriticalVerticesCount())
                          .succesfulAttackRatio(gameResult.getSuccesfulAttackRatio())
                          .thwartedAttackRatio(gameResult.getThwartedAttackRatio())
                          .build());
        });
      });
    });

    return combinedGameResults.build();
  }

}
