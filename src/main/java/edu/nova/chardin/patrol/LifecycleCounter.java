package edu.nova.chardin.patrol;

import edu.nova.chardin.patrol.experiment.event.AbstractLifecycleEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.concurrent.atomic.AtomicLong;

@Value
@Getter(value = AccessLevel.NONE)
final class LifecycleCounter<T> {
  
  private final long expectedTotalCount;
  private final AtomicLong startedCount = new AtomicLong();
  private final AtomicLong finishedCount = new AtomicLong();
  private final AtomicLong runningCount = new AtomicLong();

  public void handle(@NonNull final AbstractLifecycleEvent<T> event) {
    switch (event.getLifecycle()) {
      case Started:
        startedCount.incrementAndGet();
        runningCount.incrementAndGet();
        break;
      case Finished:
        finishedCount.incrementAndGet();
        runningCount.decrementAndGet();
        break;
      default:
        throw new IllegalStateException();
    }
  }

  public long getExpectedTotalCount() {
    return expectedTotalCount;
  }
  
  public long getStartedCount() {
    return startedCount.get();
  }

  public long getFinishedCount() {
    return finishedCount.get();
  }

  public long getRunningCount() {
    return runningCount.get();
  }
  
  public double getCreatedFinishedPercentage() {
    return calculatePercentage(getFinishedCount(), expectedTotalCount);
  }
  
  private double calculateRatio(final long numerator, final long denominator) {
    final double ratio;
    
    if (denominator == 0) {
      ratio = 0.0;
    } else {
      ratio = (double)numerator / (double)denominator;
    }
    
    return Double.max(0.0, ratio);
  }
  
  private double calculatePercentage(final long numerator, final long denominator) {
    return calculateRatio(numerator, denominator) * 100.0;
  }
}
