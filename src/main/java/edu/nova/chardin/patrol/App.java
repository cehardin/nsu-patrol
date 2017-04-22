package edu.nova.chardin.patrol;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.nova.chardin.patrol.adversary.strategy.RandomAdversaryStrategy;
import edu.nova.chardin.patrol.agent.strategy.RandomAgentStrategy;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.result.ExperimentResult;
import edu.nova.chardin.patrol.experiment.runner.ExperimentRunner;
import edu.nova.chardin.patrol.graph.creator.CircleGraphCreator;
import lombok.extern.java.Log;

@Log
public class App {

  public static void main(final String[] args) {
    final Injector injector = Guice.createInjector(new AppModule());
    final ExperimentRunner experimentRunner = injector.getInstance(ExperimentRunner.class);
    final CircleGraphCreator circleGraphCreator = injector.getInstance(CircleGraphCreator.class);
    final Experiment experiment = Experiment.builder()
            .adversaryStrategySupplier(RandomAdversaryStrategy::new )
            .agentStrategySupplier(RandomAgentStrategy::new )
            .graph("Small Circle", circleGraphCreator.create(10, 1))
            .graph("Large Circle", circleGraphCreator.create(50, 1))
            .numberOfGamesPerMatch(1000)
            .numberOfTimestepsPerGame(1000)
            .agentToVertexCountRatio(0.05)
            .agentToVertexCountRatio(0.10)
            .agentToVertexCountRatio(0.15)
            .agentToVertexCountRatio(0.20)
            .agentToVertexCountRatio(0.25)
            .adversaryToVertexCountRatio(0.05)
            .adversaryToVertexCountRatio(0.10)
            .adversaryToVertexCountRatio(0.15)
            .adversaryToVertexCountRatio(0.20)
            .adversaryToVertexCountRatio(0.25)
            .tspLengthFactor(0.125)
            .tspLengthFactor(0.250)
            .tspLengthFactor(0.500)
            .tspLengthFactor(1.000)
            .tspLengthFactor(2.000)
            .build();
    final ExperimentMonitor experimentMonitor = new ExperimentMonitor(experiment);
    final ExperimentResult experimentResult;
    
    
    experimentMonitor.startAsync().awaitRunning();
    experimentResult = experimentRunner.apply(experiment);
    experimentMonitor.stopAsync().awaitTerminated();
    
//    log.info(String.format("RESULTS"));
//    
//    System.out.println();
//    experimentResult.getScenarioResults()
//            .forEach(scenarioResult -> log.info("Graph '%s'", scenarioResult.getScenario()));
    
    
  }
  
  
}
