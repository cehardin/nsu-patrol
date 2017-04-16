/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nova.chardin.patrol;

import edu.nova.chardin.patrol.experiment.event.AbstractLifecycleEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author cehar
 */
@Value
@Getter(value = AccessLevel.NONE)
final class LifecycleCounter<T> {
  
  private final AtomicLong createdCount = new AtomicLong();
  private final AtomicLong startedCount = new AtomicLong();
  private final AtomicLong finishedCount = new AtomicLong();
  private final AtomicLong runningCount = new AtomicLong();

  public void handle(@NonNull final AbstractLifecycleEvent<T> event) {
    switch (event.getLifecycle()) {
      case Created:
        createdCount.incrementAndGet();
        break;
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

  public long getCreatedCount() {
    return createdCount.get();
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
    return calculatePercentage(getFinishedCount(), getCreatedCount());
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
