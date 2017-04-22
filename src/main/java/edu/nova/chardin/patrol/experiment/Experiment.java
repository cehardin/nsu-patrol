package edu.nova.chardin.patrol.experiment;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.TspLengthCalculator;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Builder
@Getter
@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor
@Log
public class Experiment {

  @Inject
  static TspLengthCalculator TSP_LENGTH_CALCULATOR;

  @NonNull
  @Singular
  ImmutableMap<String, ImmutableValueGraph<VertexId, EdgeWeight>> graphs;

  @NonNull
  @Singular
  ImmutableSet<Supplier<? extends AgentStrategy>> agentStrategySuppliers;

  @NonNull
  @Singular
  ImmutableSet<Supplier<? extends AdversaryStrategy>> adversaryStrategySuppliers;

  @NonNull
  @Singular
  ImmutableSet<Double> agentToVertexCountRatios;

  @NonNull
  @Singular
  ImmutableSet<Double> adversaryToVertexCountRatios;

  @NonNull
  @Singular
  ImmutableSet<Double> tspLengthFactors;

  @NonNull
  Integer numberOfGamesPerMatch;

  @NonNull
  Integer numberOfTimestepsPerGame;

  @Getter(lazy = true)
  ExperimentSummary summary = createSummary();

  @Getter(lazy = true)
  ImmutableSet<Scenario> scenarios = createScenarios();

  private ExperimentSummary createSummary() {
    final AtomicInteger scenarioCount = new AtomicInteger();
    final AtomicInteger matchCount = new AtomicInteger();
    final AtomicInteger gameCount = new AtomicInteger();
    
    getScenarios().parallelStream().forEach(scenario -> {
      scenarioCount.incrementAndGet();
      scenario.getMatches().parallelStream().forEach(match -> {
        matchCount.incrementAndGet();
        gameCount.addAndGet(match.getGames().size());
      });
    });
    
    return ExperimentSummary.builder()
            .totalScenarioCount(scenarioCount.get())
            .totalMatchCount(matchCount.get())
            .totalGameCount(gameCount.get())
            .build();
  }
  
  private ImmutableSet<Scenario> createScenarios() {
    log.info("Creating Scenarios");
    final ImmutableMap<ImmutableValueGraph<VertexId, EdgeWeight>, Integer> tspLengths;
    final Set<Scenario> createdScenarios = ConcurrentHashMap.newKeySet(
            getGraphs().size()
            * getAgentToVertexCountRatios().size()
            * getAdversaryToVertexCountRatios().size());
 
    tspLengths = ImmutableMap.copyOf(
            getGraphs().values().parallelStream().collect(
                    Collectors.toMap(
                            Function.identity(),
                            TSP_LENGTH_CALCULATOR)));

    getGraphs().values().parallelStream().forEach(g -> {
      getAgentToVertexCountRatios().parallelStream().forEach(agentToVertexCountRatio -> {
        final int numberOfAgents = (int) Math.ceil(g.nodes().size() * agentToVertexCountRatio);
        final ImmutableSet<Integer> attackIntervals
                = ImmutableSet.copyOf(
                        getTspLengthFactors().parallelStream()
                                .map(factor -> (int) (factor * ((double) numberOfAgents / tspLengths.get(g))))
                                .collect(Collectors.toSet()));

        getAdversaryToVertexCountRatios().parallelStream().forEach(adversaryToVertexCountRatio -> {
          final int numberOfAdversaries = (int) Math.ceil(g.nodes().size() * adversaryToVertexCountRatio);
          createdScenarios.add(
                  Scenario.builder()
                          .experiment(this)
                          .graph(g)
                          .numberOfAgents(numberOfAgents)
                          .numberOfAdversaries(numberOfAdversaries)
                          .attackIntervals(attackIntervals)
                          .build());
        });
      });
    });
    
    log.info("Scenarios created");
    return ImmutableSet.copyOf(createdScenarios);
  }

}
