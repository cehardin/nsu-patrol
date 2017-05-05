package edu.nova.chardin.patrol.experiment;

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
import java.util.stream.Collectors;

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
    log.info("Creating Scenarios");
    final Set<Scenario> createdScenarios = ConcurrentHashMap.newKeySet(
            getGraphs().size()
            * getAgentToVertexCountRatios().size()
            * getAdversaryToVertexCountRatios().size());

    getGraphs().parallelStream().forEach(g -> {
      final int numberOfTimestepsPerGame = timestepsPerGameFactor * g.getApproximateTspLength();
      getAgentToVertexCountRatios().parallelStream().forEach(agentToVertexCountRatio -> {
        final int numberOfAgents = (int) Math.ceil(g.getVertices().size() * agentToVertexCountRatio);
        final ImmutableSet<Integer> attackIntervals
                = ImmutableSet.copyOf(
                        getTspLengthFactors().parallelStream()
                                .map(factor -> (int) (factor * ((double) g.getApproximateTspLength() / (double) numberOfAgents)))
                                .collect(Collectors.toSet()));

        getAdversaryToVertexCountRatios().parallelStream().forEach(adversaryToVertexCountRatio -> {
          final int numberOfAdversaries = (int) Math.ceil(g.getVertices().size() * adversaryToVertexCountRatio);
          createdScenarios.add(
                  Scenario.builder()
                          .experiment(this)
                          .graph(g)
                          .numberOfTimestepsPerGame(numberOfTimestepsPerGame)
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
