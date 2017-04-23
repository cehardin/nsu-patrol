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
  
  public ImmutableList<SuperResult> createSuperResults() {
    
    final ImmutableList.Builder<SuperResult> superResults = ImmutableList.builder();
    
    scenarioResults.forEach(scenarioResult -> {
      scenarioResult.getMatchResults().forEach(matchResult -> {
        matchResult.getGameResults().forEach(gameResult -> {
          superResults.add(
                  SuperResult.builder()
                          .numberOfGamesPerMatch(experiment.getNumberOfGamesPerMatch())
                          .numberOfTimestepsPerGame(experiment.getNumberOfTimestepsPerGame())
                          .graph(scenarioResult.getScenario().getGraph())
                          .numberOfAgents(scenarioResult.getScenario().getNumberOfAgents())
                          .numberOfAdversaries(scenarioResult.getScenario().getNumberOfAdversaries())
                          .adversaryStrategyType(matchResult.getMatch().getAdversaryStrategyType())
                          .agentStrategyType(matchResult.getMatch().getAgentStrategyType())
                          .attackInterval(matchResult.getMatch().getAttackInterval())
                          .executionTimeNanoSeconds(gameResult.getExecutionTimeNanoSeconds())
                          .attackCount(gameResult.getAttackCount())
                          .attackThwartedCount(gameResult.getAttackThwartedCount())
                          .attackSuccessfulCount(gameResult.getAttackSuccessfulCount())
                          .build());
        });
      });
    });
    
    return superResults.build();
  }
  
}
