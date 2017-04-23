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
  Integer attackCount;
  
  @NonNull
  Integer attackSuccessfulCount;
  
  @NonNull
  Integer attackThwartedCount;
  
  
//  @NonNull
//  Integer numberOfTargetVerticesThwartedThenCompromised;
//  
//  @NonNull
//  IntSummaryStatistics idlenessAllVerticesStatistics;
//  
//  @NonNull
//  IntSummaryStatistics idlenessTargetVerticesStatistics;
//  
//  @NonNull
//  IntSummaryStatistics idlenessNonTargetVerticesStatistics;
}
