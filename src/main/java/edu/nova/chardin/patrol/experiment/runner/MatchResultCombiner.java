package edu.nova.chardin.patrol.experiment.runner;

import edu.nova.chardin.patrol.experiment.result.MatchResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.function.BinaryOperator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
public class MatchResultCombiner implements BinaryOperator<MatchResult> {
  
  @NonNull
  IntSummaryStatisticsCombiner intSummaryStatisticsCombiner;

  @Override
  public MatchResult apply(
          @NonNull final MatchResult t, 
          @NonNull final MatchResult u) {
    
    return MatchResult
            .builder()
            .match(t.getMatch())
            .numberOfTargetVerticesCompromised(intSummaryStatisticsCombiner.apply(t.getNumberOfTargetVerticesCompromised(), u.getNumberOfTargetVerticesCompromised()))
            .numberOfTargetVerticesDiscoveredCritical(intSummaryStatisticsCombiner.apply(t.getNumberOfTargetVerticesDiscoveredCritical(), u.getNumberOfTargetVerticesDiscoveredCritical()))
            .numberOfTargetVerticesNotAttacked(intSummaryStatisticsCombiner.apply(t.getNumberOfTargetVerticesNotAttacked(), u.getNumberOfTargetVerticesNotAttacked()))
            .numberOfTargetVerticesThwartedThenCompromised(intSummaryStatisticsCombiner.apply(t.getNumberOfTargetVerticesThwartedThenCompromised(), u.getNumberOfTargetVerticesThwartedThenCompromised()))
            .idlenessAllVerticesStatistics(intSummaryStatisticsCombiner.apply(t.getIdlenessAllVerticesStatistics(), u.getIdlenessAllVerticesStatistics()))
            .idlenessNonTargetVerticesStatistics(intSummaryStatisticsCombiner.apply(t.getIdlenessNonTargetVerticesStatistics(), u.getIdlenessNonTargetVerticesStatistics()))
            .idlenessTargetVerticesStatistics(intSummaryStatisticsCombiner.apply(t.getIdlenessTargetVerticesStatistics(), u.getIdlenessTargetVerticesStatistics()))
            .build();
  }
}
