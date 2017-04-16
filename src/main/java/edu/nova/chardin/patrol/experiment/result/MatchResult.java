package edu.nova.chardin.patrol.experiment.result;

import edu.nova.chardin.patrol.experiment.Match;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.IntSummaryStatistics;

@Value
@Builder
public class MatchResult {
  
  @NonNull
  Match match;
  
  @NonNull
  IntSummaryStatistics numberOfTargetVerticesCompromised;
  
  @NonNull
  IntSummaryStatistics numberOfTargetVerticesNotAttacked;
  
  @NonNull
  IntSummaryStatistics numberOfTargetVerticesDiscoveredCritical;
  
  @NonNull
  IntSummaryStatistics numberOfTargetVerticesThwartedThenCompromised;
  
  @NonNull
  IntSummaryStatistics idlenessAllVerticesStatistics;
  
  @NonNull
  IntSummaryStatistics idlenessTargetVerticesStatistics;
  
  @NonNull
  IntSummaryStatistics idlenessNonTargetVerticesStatistics;
}
