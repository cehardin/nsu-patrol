package edu.nova.chardin.patrol.experiment;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Log
public class Scenario {

  @NonNull
  Experiment experiment;

  @NonNull
  PatrolGraph graph;
  
  @NonNull
  Double agentToVertexCountRatio;

  @NonNull
  Double adversaryToVertexCountRatio;

  @NonNull
  Double tspLengthFactor;
  
  @NonNull
  @Getter(lazy = true)
  ImmutableSet<Match> matches = createMatches();
  
  private ImmutableSet<Match> createMatches() {

    final Set<Match> createdMatches = ConcurrentHashMap.newKeySet();

    getExperiment().getAgentStrategyFactories().parallelStream().forEach(agentStrategyFactory -> {
      getExperiment().getAdversaryStrategyFactories().parallelStream().forEach(adversaryStrategyFactory -> {
          createdMatches.add(Match.builder()
                          .scenario(this)
                          .agentStrategyFactory(agentStrategyFactory)
                          .adversaryStrategyFactory(adversaryStrategyFactory)
                          .build());
      });
    });
    
    return ImmutableSet.copyOf(createdMatches);
  }
  
  @NonNull
  @Getter(lazy = true)
  Integer attackInterval = createAttackInterval();
  
  private Integer createAttackInterval() {
    return (int)Math.ceil(getGraph().getApproximateTspLength() * getTspLengthFactor());
  }
  
  @NonNull
  @Getter(lazy = true)
  Integer numberOfAgents = createNumberOfAgents();
  
  private Integer createNumberOfAgents() {
    return (int)Math.ceil(getGraph().getVertices().size() * getAgentToVertexCountRatio());
  }
  
  @NonNull
  @Getter(lazy = true)
  Integer numberOfAdversaries = createNumberOfAdversaries();
  
  private Integer createNumberOfAdversaries() {
    return (int)Math.ceil(getGraph().getVertices().size() * getAdversaryToVertexCountRatio());
  }
  
  @NonNull
  @Getter(lazy = true)
  Integer numberOfTimestepsPerGame = createNumberOfTimestepsPerGame();
  
  private Integer createNumberOfTimestepsPerGame() {
    return getGraph().getApproximateTspLength() * getExperiment().getTimestepsPerGameFactor();
  }
}
