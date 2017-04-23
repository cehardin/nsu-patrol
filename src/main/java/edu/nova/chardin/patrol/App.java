package edu.nova.chardin.patrol;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.nova.chardin.patrol.adversary.strategy.AlwaysAttackAdversaryStrategy;
import edu.nova.chardin.patrol.adversary.strategy.RandomAdversaryStrategy;
import edu.nova.chardin.patrol.agent.strategy.ImmobileAgentStrategy;
import edu.nova.chardin.patrol.agent.strategy.RandomAgentStrategy;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.result.ExperimentResult;
import edu.nova.chardin.patrol.experiment.result.SuperResult;
import edu.nova.chardin.patrol.experiment.runner.ExperimentRunner;
import edu.nova.chardin.patrol.graph.creator.CircleGraphCreator;
import edu.nova.chardin.patrol.graph.creator.GridGraphCreator;
import lombok.extern.java.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

@Log
public class App {

  public static void main(final String[] args) {
    final Injector injector = Guice.createInjector(new AppModule());
    final ExperimentRunner experimentRunner = injector.getInstance(ExperimentRunner.class);
    final CircleGraphCreator circleGraphCreator = injector.getInstance(CircleGraphCreator.class);
    final GridGraphCreator gridGraphCreator = injector.getInstance(GridGraphCreator.class);
    final Experiment experiment = Experiment.builder()
            .adversaryStrategyType(RandomAdversaryStrategy.class)
            .adversaryStrategyType(AlwaysAttackAdversaryStrategy.class)
            .agentStrategyType(RandomAgentStrategy.class)
            .agentStrategyType(ImmobileAgentStrategy.class)
            .graph(circleGraphCreator.create(25, 1))
            .graph(circleGraphCreator.create(50, 1))
            .graph(gridGraphCreator.create(5, 5, 1))
            .graph(gridGraphCreator.create(8, 8, 1))
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
    final ImmutableList<SuperResult> results;
    final File file = new File(String.format("data-out-%d.csv", System.currentTimeMillis()));
    
    
    experimentMonitor.startAsync().awaitRunning();
    experimentResult = experimentRunner.apply(experiment);
    experimentMonitor.stopAsync().awaitTerminated();
    results = experimentResult.createSuperResults();
    
    try (final FileWriter fw = new FileWriter(file)) {
      try (final BufferedWriter bw = new BufferedWriter(fw)) {
        bw.write(SuperResult.COMMA_JOINER.apply(SuperResult.CSV_HEAD));
        bw.newLine();
        for (final SuperResult result : results) {
          bw.write(SuperResult.COMMA_JOINER.apply(SuperResult.TO_CSV_LINE.apply(result)));
          bw.newLine();
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(String.format("Could not write to output file %s", file.getAbsolutePath()), e);
    }
    
    log.info(String.format("Output file is at %s", file.getAbsolutePath()));
    
    
  }
  
  
}
