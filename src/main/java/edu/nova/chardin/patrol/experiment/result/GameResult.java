package edu.nova.chardin.patrol.experiment.result;

import edu.nova.chardin.patrol.experiment.Game;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class GameResult {
  
  @NonNull
  Game game;
  
  @NonNull
  Long executionTimeMilliSeconds;
  
  @NonNull
  Long timeStepExecutionTimeMicroSeconds;
  
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
  Integer targetVerticesCount;
  
  @NonNull
  Integer agentChoseToMoveCount;
  
  @NonNull
  Integer agentChoseToStayCount;
  
  @NonNull
  Integer agentTimestepsSpentMoving;
}
