package edu.nova.chardin.patrol;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {

  public static void main(final String[] args) {
    final Injector injector = Guice.createInjector(new AppModule());
    final ExperimentMonitor experimentMonitor = injector.getInstance(ExperimentMonitor.class);
    
    experimentMonitor.startAsync().awaitRunning();
    
  }
}
