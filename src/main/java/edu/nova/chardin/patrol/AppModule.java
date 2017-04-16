package edu.nova.chardin.patrol;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppModule extends AbstractModule {

  @Override
  protected void configure() {
    final ExecutorService executorService = Executors.newWorkStealingPool();
    final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);
    
    bind(ExecutorService.class).toInstance(executorService);
    bind(ListeningExecutorService.class).toInstance(listeningExecutorService);
    bind(EventBus.class).toInstance(new EventBus());
  }
  
}
