package edu.nova.chardin.patrol;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import lombok.extern.java.Log;

import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;

@Log
public class AppModule extends AbstractModule {

  @Override
  protected void configure() {
    final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
//    final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

    bind(ForkJoinPool.class).toInstance(forkJoinPool);
//    bind(ListeningExecutorService.class).toInstance(listeningExecutorService);
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
  }
  
}
