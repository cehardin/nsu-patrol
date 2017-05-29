package edu.nova.chardin.patrol;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.nova.chardin.patrol.adversary.AdversaryStrategyFactory;
import edu.nova.chardin.patrol.adversary.HybridAdversaryStrategyFactory;
import edu.nova.chardin.patrol.adversary.SimpleAdversaryStrategyFactory;
import edu.nova.chardin.patrol.adversary.strategy.RandomAdversaryStrategy;
import edu.nova.chardin.patrol.adversary.strategy.StatisticalAdversaryStrategy;
import edu.nova.chardin.patrol.adversary.strategy.WaitingAdversaryStrategy;
import edu.nova.chardin.patrol.agent.strategy.covering.AntiRandomCoveringStrategy;
import edu.nova.chardin.patrol.agent.strategy.covering.AntiStatisticalCoveringStrategy;
import edu.nova.chardin.patrol.agent.strategy.covering.AntiWaitingCoveringStrategy;
import edu.nova.chardin.patrol.agent.strategy.covering.ClassCoveringAgentStrategyFactory;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.result.CombinedGameResult;
import edu.nova.chardin.patrol.experiment.result.CombinedMatchResult;
import edu.nova.chardin.patrol.experiment.result.ExperimentResult;
import edu.nova.chardin.patrol.experiment.runner.ExperimentRunner;
import edu.nova.chardin.patrol.graph.loader.XmlGraph;
import edu.nova.chardin.patrol.graph.loader.XmlGraphLoader;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Log
@AllArgsConstructor
public class App implements Runnable {

  public static void main(final String[] args) {
    final Options options = new Options();
    final Option samplingOption = Option.builder("s")
            .argName("Sampling")
            .longOpt("sampling")
            .desc("The sampling percentage")
            .required()
            .hasArg()
            .numberOfArgs(1)
            .type(Integer.class)
            .build();
    final CommandLineParser parser = new DefaultParser();
    
    options.addOption(samplingOption);
    
    try {
      final CommandLine command = parser.parse(options, args);
      final int sampling = Integer.parseInt(command.getOptionValue(samplingOption.getOpt()));
      final int numberOfGamesPerMatch = (100 * sampling) / 100;
      final App app = new App(numberOfGamesPerMatch);
      
      app.run();
    } catch (ParseException e) {
      final HelpFormatter helpFormatter = new HelpFormatter();
      
      System.err.println(e.getMessage());
      helpFormatter.printHelp("java", options, true);
    }
    
  }
  
  private final int gamesPerMatch;
  
  public void run() {
    final Injector injector = Guice.createInjector(new AppModule());
    final ExperimentRunner experimentRunner = injector.getInstance(ExperimentRunner.class);
    final XmlGraphLoader xmlGraphLoader = injector.getInstance(XmlGraphLoader.class);;;
    final Experiment experiment = Experiment.builder()
            .adversaryStrategyFactory(new SimpleAdversaryStrategyFactory("random", RandomAdversaryStrategy.class))
            .adversaryStrategyFactory(new SimpleAdversaryStrategyFactory("waiting", WaitingAdversaryStrategy.class))
            .adversaryStrategyFactory(new SimpleAdversaryStrategyFactory("statistical", StatisticalAdversaryStrategy.class))
            .adversaryStrategyFactory(new HybridAdversaryStrategyFactory())
            .agentStrategyFactory(new ClassCoveringAgentStrategyFactory("antiRandom", AntiRandomCoveringStrategy.class))
            .agentStrategyFactory(new ClassCoveringAgentStrategyFactory("antiWaiting", AntiWaitingCoveringStrategy.class))
            .agentStrategyFactory(new ClassCoveringAgentStrategyFactory("antiStatistical", AntiStatisticalCoveringStrategy.class))
            .graph(xmlGraphLoader.loadGraph(XmlGraph.A))
            .graph(xmlGraphLoader.loadGraph(XmlGraph.B))
            .graph(xmlGraphLoader.loadGraph(XmlGraph.Circle))
            .graph(xmlGraphLoader.loadGraph(XmlGraph.Corridor))
            .graph(xmlGraphLoader.loadGraph(XmlGraph.Grid))
            .graph(xmlGraphLoader.loadGraph(XmlGraph.Islands))
            .numberOfGamesPerMatch(gamesPerMatch)
            .timestepsPerGameFactor(100)
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
    final File matchResultsFile = new File(String.format("match-results.csv", System.currentTimeMillis()));
    final File gameResultsFile = new File(String.format("game-results.csv", System.currentTimeMillis()));
    
    log.info(String.format("Number of games per match is %d", experiment.getNumberOfGamesPerMatch()));
    experimentMonitor.startAsync().awaitRunning();
    experimentResult = experimentRunner.apply(experiment);
    experimentMonitor.stopAsync().awaitTerminated();
    
    try (final FileWriter fw = new FileWriter(matchResultsFile)) {
      try (final BufferedWriter bw = new BufferedWriter(fw)) {
        try (final PrintWriter printWriter = new PrintWriter(bw)) {
          final CombinedMatchResultsCsvWriter csvWriter = new CombinedMatchResultsCsvWriter(printWriter);

          for (final CombinedMatchResult result : experimentResult.createCombinedMatchResults()) {
            csvWriter.write(result);
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(String.format("Could not write to match results file %s", matchResultsFile.getAbsolutePath()), e);
    }
    
    log.info(String.format("Match results file is at %s", matchResultsFile.getAbsolutePath()));
    
    try (final FileWriter fw = new FileWriter(gameResultsFile)) {
      try (final BufferedWriter bw = new BufferedWriter(fw)) {
        try (final PrintWriter printWriter = new PrintWriter(bw)) {
          final CombinedGameResultsCsvWriter csvWriter = new CombinedGameResultsCsvWriter(printWriter);

          for (final CombinedGameResult result : experimentResult.createCombinedGameResults()) {
            csvWriter.write(result);
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(String.format("Could not write to game results file %s", gameResultsFile.getAbsolutePath()), e);
    }
    
    log.info(String.format("Game results file is at %s", gameResultsFile.getAbsolutePath()));
    
    
  }
  
  
}
