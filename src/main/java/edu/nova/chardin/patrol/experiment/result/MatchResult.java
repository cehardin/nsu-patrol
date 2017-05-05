package edu.nova.chardin.patrol.experiment.result;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.experiment.Match;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

@Value
@Builder
public class MatchResult {
  
  @NonNull
  Match match;
  
  @NonNull
  ImmutableList<GameResult> gameResults;
  
  @NonNull
  StatisticalSummary executionTimeNanoSeconds;
  
  @NonNull
  StatisticalSummary generalEffectiveness;
  
  @NonNull
  StatisticalSummary deterenceEffectiveness;
  
  @NonNull
  StatisticalSummary patrolEffectiveness;
  
  @NonNull
  StatisticalSummary defenseEffectiveness;
}
