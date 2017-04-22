package edu.nova.chardin.patrol;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import lombok.extern.java.Log;

import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;

@Log
public class AppModule extends AbstractModule {

  @Override
  protected void configure() {
    final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

    bind(ForkJoinPool.class).toInstance(forkJoinPool);
    bind(EventBus.class).toInstance(new EventBus((exception, context) -> {
      log.log(
              Level.SEVERE,
              String.format(
                      "Fatal error on event bus with event %s with subscriber %s : %s",
                      context.getEvent(),
                      context.getSubscriber(),
                      context.getSubscriberMethod()),
              exception);
      System.exit(1);
    }));
    requestStaticInjection(ExperimentMonitor.class);
  }
  
}
