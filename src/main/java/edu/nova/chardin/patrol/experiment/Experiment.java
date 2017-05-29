package edu.nova.chardin.patrol.experiment;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.adversary.AdversaryStrategyFactory;
import edu.nova.chardin.patrol.agent.AgentStrategyFactory;
import edu.nova.chardin.patrol.graph.PatrolGraph;
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

@Builder
@Getter
@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor
@Log
public class Experiment {

  @NonNull
  @Singular
  ImmutableSet<PatrolGraph> graphs;

  @NonNull
  @Singular
  ImmutableSet<AgentStrategyFactory> agentStrategyFactories;

  @NonNull
  @Singular
  ImmutableSet<AdversaryStrategyFactory> adversaryStrategyFactories;

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
  Integer timestepsPerGameFactor;

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
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final Set<Scenario> createdScenarios = ConcurrentHashMap.newKeySet();
    final ImmutableSet<Scenario> result;
    
    getGraphs().parallelStream().forEach(g -> {
      getAgentToVertexCountRatios().parallelStream().forEach(agentToVertexCountRatio -> {
        getAdversaryToVertexCountRatios().parallelStream().forEach(adversaryToVertexCountRatio -> {
          getTspLengthFactors().parallelStream().forEach(tspLengthFactor -> {
            createdScenarios.add(
                  Scenario.builder()
                          .experiment(this)
                          .graph(g)
                          .agentToVertexCountRatio(agentToVertexCountRatio)
                          .adversaryToVertexCountRatio(adversaryToVertexCountRatio)
                          .tspLengthFactor(tspLengthFactor)
                          .build());
          });
        });
      });
    });
    
    result = ImmutableSet.copyOf(createdScenarios);
    log.info(String.format("Scenarios created in %s", stopwatch));
    return result;
  }

}
