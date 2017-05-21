package edu.nova.chardin.patrol;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.ExperimentSummary;
import edu.nova.chardin.patrol.experiment.Game;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.event.GameLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.MatchLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.ScenarioLifecycleEvent;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.java.Log;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@Value
@EqualsAndHashCode(callSuper = false)
@Log
public class ExperimentMonitor extends AbstractScheduledService {

  @Inject
  static EventBus EVENT_BUS;
  
  @Inject
  static ForkJoinPool FORK_JOIN_POOL;
  
  Stopwatch stopwatch = Stopwatch.createUnstarted();
  
  @NonNull
  Experiment experiment;
  
  LifecycleCounter<Scenario> scenarioCounter;
  LifecycleCounter<Match> matchCounter;
  LifecycleCounter<Game> gameCounter;
  
  public ExperimentMonitor(@NonNull final Experiment experiment) {
    final ExperimentSummary summary = experiment.getSummary();
    
    this.experiment = experiment;
    this.scenarioCounter = new LifecycleCounter<>(summary.getTotalScenarioCount());
    this.matchCounter = new LifecycleCounter<>(summary.getTotalMatchCount());
    this.gameCounter = new LifecycleCounter<>(summary.getTotalGameCount());
  }

  @Override
  protected void startUp() throws Exception {
    stopwatch.start();
    EVENT_BUS.register(this);
  }

  @Override
  protected void shutDown() throws Exception {
    EVENT_BUS.unregister(this);
    stopwatch.stop();
    log.info(String.format("Stopped after %s", stopwatch));
  }

  @Override
  protected void runOneIteration() throws Exception {
    final double elapsedHours = (double)stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0 / 60.0 / 60.0;
    final double ratioDone = (double)gameCounter.getFinishedCount() / (double)gameCounter.getExpectedTotalCount();
    final double estimatedTotalHours = elapsedHours / ratioDone;
    final double estimatedHoursLeft = estimatedTotalHours - elapsedHours;
    
    log.fine(
            String.format(
                    "%,d submissions are queued; %,d tasks are queued; %,d threads are active; %,d threads are running; %,d tasks have been stolen", 
                    FORK_JOIN_POOL.getQueuedSubmissionCount(),
                    FORK_JOIN_POOL.getQueuedTaskCount(),
                    FORK_JOIN_POOL.getActiveThreadCount(),
                    FORK_JOIN_POOL.getRunningThreadCount(),
                    FORK_JOIN_POOL.getStealCount()));
    
    System.out.printf(
            "STATUS after %s; Scenarios : %s; Matches : %s; Games : %s%n",
            toString(stopwatch),
            toString(scenarioCounter),
            toString(matchCounter),
            toString(gameCounter));
    
    System.out.printf(
            "%,.4f hours have elasped. Estimated total time is %,.4f hours. So, %,.4f hours are left%n",
            elapsedHours,
            estimatedTotalHours,
            estimatedHoursLeft);
  }
  
  private String toString(final Stopwatch stopwatch) {
    return stopwatch.toString();
  }
  
  private String toString(final LifecycleCounter<?> counter) {
    return String.format(
            "%,d/%,d (%.1f%%) (%,d running)", 
            counter.getFinishedCount(),
            counter.getExpectedTotalCount(),
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
