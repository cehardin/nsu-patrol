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
  Double tspLengthFactor;
  
  @NonNull
  Integer attackInterval;

  @NonNull
  PatrolGraph graph;

  @NonNull
  Double agentToVertexCountRatio;

  @NonNull
  Double adversaryToVertexCountRatio;
  
  @NonNull
  Integer numberOfAgents;
  
  @NonNull
  Integer numberOfAdversaries;

  @NonNull
  AgentStrategyFactory agentStrategyFactory;

  @NonNull
  AdversaryStrategyFactory adversaryStrategyFactory;

  @NonNull
  Long executionTimeMilliSeconds;
  
  @NonNull
  Long timeStepExecutionTimeMicroseconds;
  
  @NonNull
  Integer numberOfTimesteps;

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
  Integer thwartedCount;
  
  @NonNull
  Integer compromisedCount;
  
  @NonNull
  Integer criticalVerticesCount;
  
  @NonNull
  Integer targetVerticesCount;
  
  @NonNull
  Integer agentMoveCount;
  
  @NonNull
  Integer agentTimestepsSpentMoving;
  
  @NonNull
  Double ratioVerticesVisited;
  
}
