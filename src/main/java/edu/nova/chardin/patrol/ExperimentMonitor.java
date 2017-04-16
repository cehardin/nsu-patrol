package edu.nova.chardin.patrol;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractScheduledService;
import edu.nova.chardin.patrol.experiment.Game;
import edu.nova.chardin.patrol.experiment.event.GameLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.Console;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import javax.inject.Inject;

@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({@Inject}))
public class ExperimentMonitor extends AbstractScheduledService {
  
  @NonNull
  EventBus eventBus;
  
  @NonNull
  Console console;
  
  @NonNull
  ConcurrentMap<Lifecycle, Long> gameLifecycles = new ConcurrentHashMap<>() ;

  
  
  @Override
  protected void startUp() throws Exception {
    eventBus.register(this);
  }
  
  @Override
  protected void shutDown() throws Exception {
    eventBus.unregister(this);
  }

  

  @Override
  protected void runOneIteration() throws Exception {

    console.printf(
            "%d out of %d games have finished%n", 
            gameLifecycles.computeIfAbsent(Lifecycle.Finished, Functions.constant(1L)), 
            gameLifecycles.computeIfAbsent(Lifecycle.Created, Functions.constant(1L)));
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedDelaySchedule(10, 10, TimeUnit.SECONDS);
  }
  
  
  
  @Subscribe
  void gameLifececycle(@NonNull final GameLifecycleEvent event) {
    gameLifecycles.merge(event.getLifecycle(), 1L, Long::sum);
  }
}
