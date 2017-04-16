package edu.nova.chardin.patrol.experiment.runner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.IntSummaryStatistics;
import java.util.function.BinaryOperator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
public class IntSummaryStatisticsCombiner implements BinaryOperator<IntSummaryStatistics> {

  @Override
  public IntSummaryStatistics apply(
          @NonNull final IntSummaryStatistics t, 
          @NonNull final IntSummaryStatistics u) {
    
    final IntSummaryStatistics r = new IntSummaryStatistics();
    
    r.combine(t);
    r.combine(u);
    
    return r;
  }
}
