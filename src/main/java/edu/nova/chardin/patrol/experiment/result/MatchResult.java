package edu.nova.chardin.patrol.experiment.result;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.experiment.Match;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class MatchResult {
  
  @NonNull
  Match match;
  
  @NonNull
  ImmutableList<GameResult> gameResults;
  
//  @NonNull
//  IntSummaryStatistics attackStatistics;
//  
//  @NonNull
//  IntSummaryStatistics attackSuccessfulStatistics;
//  
//  @NonNull
//  IntSummaryStatistics attackThwartedStatistics;
  
//  @NonNull
//  IntSummaryStatistics numberOfTargetVerticesThwartedThenCompromised;
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
