package edu.nova.chardin.patrol.experiment;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import edu.nova.chardin.patrol.graph.EdgeWeight;
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

@Getter
@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Log
public class Scenario {

  @NonNull
  Experiment experiment;

  @NonNull
  ImmutableValueGraph<VertexId, EdgeWeight> graph;

  @NonNull
  Integer numberOfAgents;

  @NonNull
  Integer numberOfAdversaries;

  @NonNull
  @Singular
  ImmutableSet<Integer> attackIntervals;
  
  @Getter(lazy = true)
  ImmutableSet<Match> matches = createMatches();
  
  private ImmutableSet<Match> createMatches() {

    final Set<Match> createdMatches = ConcurrentHashMap.newKeySet(
            experiment.getAgentStrategySuppliers().size() 
                    * experiment.getAdversaryStrategySuppliers().size() 
                    * getAttackIntervals().size());

    experiment.getAgentStrategySuppliers().parallelStream().forEach(agentStrategySupplier -> {
      experiment.getAdversaryStrategySuppliers().parallelStream().forEach(adversaryStrategySupplier -> {
        getAttackIntervals().parallelStream().forEach(attackInterval -> {
          createdMatches.add(Match.builder()
                          .scenario(this)
                          .agentStrategySupplier(agentStrategySupplier)
                          .adversaryStrategySupplier(adversaryStrategySupplier)
                          .attackInterval(attackInterval)
                          .build());
        });
      });
    });
    
    return ImmutableSet.copyOf(createdMatches);
  }
}
