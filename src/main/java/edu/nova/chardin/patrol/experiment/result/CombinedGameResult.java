package edu.nova.chardin.patrol.experiment.result;

import edu.nova.chardin.patrol.adversary.AdversaryStrategyFactory;
import edu.nova.chardin.patrol.agent.AgentStrategyFactory;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class CombinedGameResult {
  
  @NonNull
  Integer numberOfGamesPerMatch;

  @NonNull
  Integer numberOfTimestepsPerGame;

  @NonNull
  PatrolGraph graph;

  @NonNull
  Integer numberOfAgents;

  @NonNull
  Integer numberOfAdversaries;

  @NonNull
  AgentStrategyFactory agentStrategyFactory;

  @NonNull
  AdversaryStrategyFactory adversaryStrategyFactory;

  @NonNull
  Integer attackInterval;

  @NonNull
  Double executionTimeMilliSeconds;
  
  @NonNull
  Double timeStepExecutionTimeMicroseconds;

  @NonNull
  Double generalEffectiveness;
  
  @NonNull
  Double deterenceEffectiveness;
  
  @NonNull
  Double patrolEffectiveness;
  
  @NonNull
  Double defenseEffectiveness;
  
  @NonNull
  Integer attackCount;
  
  @NonNull
  Integer twartedCount;
  
  @NonNull
  Integer compromisedCount;
  
  @NonNull
  Integer criticalVerticesCount;
  
  @NonNull
  Double succesfulAttackRatio;
  
  @NonNull
  Double thwartedAttackRatio;
  
}
