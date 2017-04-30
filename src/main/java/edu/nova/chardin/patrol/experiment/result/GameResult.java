package edu.nova.chardin.patrol.experiment.result;

import edu.nova.chardin.patrol.experiment.Game;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.IntSummaryStatistics;

@Value
@Builder
public class GameResult {
  
  @NonNull
  Game game;
  
  @NonNull
  Long executionTimeNanoSeconds;
  
  @NonNull
  Double generalEffectiveness;
  
  @NonNull
  Double deterenceEffectiveness;
  
  @NonNull
  Double patrolEffectiveness;
  
  @NonNull
  Double defenseEffectiveness;
}
