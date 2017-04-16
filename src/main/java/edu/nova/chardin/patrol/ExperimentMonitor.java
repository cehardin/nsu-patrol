package edu.nova.chardin.patrol;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import edu.nova.chardin.patrol.experiment.Game;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.event.GameLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.MatchLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.ScenarioLifecycleEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.java.Log;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@Value
@Getter(AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Log
public class ExperimentMonitor extends AbstractScheduledService {

  Stopwatch stopwatch;
  
  @NonNull
  EventBus eventBus;
  
  @NonNull
  ForkJoinPool forkJoinPool;

  LifecycleCounter<Scenario> scenarioCounter = new LifecycleCounter<>();
  LifecycleCounter<Match> matchCounter = new LifecycleCounter<>();
  LifecycleCounter<Game> gameCounter = new LifecycleCounter<>();

  @Override
  protected void startUp() throws Exception {
    stopwatch.start();
    eventBus.register(this);
  }

  @Override
  protected void shutDown() throws Exception {
    eventBus.unregister(this);
    stopwatch.stop();
    log.info(String.format("Stopped after %s", stopwatch));
  }

  @Override
  protected void runOneIteration() throws Exception {
    log.info(
            String.format(
                    "%,d submissions are queued; %,d tasks are queued; %,d threads are active; %,d threads are running; %,d tasks have been stolen", 
                    forkJoinPool.getQueuedSubmissionCount(),
                    forkJoinPool.getQueuedTaskCount(),
                    forkJoinPool.getActiveThreadCount(),
                    forkJoinPool.getRunningThreadCount(),
                    forkJoinPool.getStealCount()));
    log.info(
            String.format(
                    "STATUS after %s; Scenarios : %s; Matches : %s; Games : %s",
                    toString(stopwatch),
                    toString(scenarioCounter),
                    toString(matchCounter),
                    toString(gameCounter)));
  }
  
  private String toString(final Stopwatch stopwatch) {
    return stopwatch.toString();
//    return String.format("%,d minutes / %,d hours", stopwatch.el)
  }
  
  private String toString(final LifecycleCounter<?> counter) {
    return String.format(
            "%,d/%,d (%.1f%%) (%,d running)", 
            counter.getFinishedCount(),
            counter.getCreatedCount(),
            counter.getCreatedFinishedPercentage(),
            counter.getRunningCount());
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedDelaySchedule(10, 10, TimeUnit.SECONDS);
  }

  @Subscribe
  void scenarioLifececycle(@NonNull final ScenarioLifecycleEvent event) {
    scenarioCounter.handle(event);
  }
  
  @Subscribe
  void matchLifececycle(@NonNull final MatchLifecycleEvent event) {
    matchCounter.handle(event);
  }
  
  @Subscribe
  void gameLifececycle(@NonNull final GameLifecycleEvent event) {
    gameCounter.handle(event);
  }
  
  
}
