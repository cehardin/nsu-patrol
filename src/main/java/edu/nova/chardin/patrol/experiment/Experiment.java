package edu.nova.chardin.patrol.experiment;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.function.Supplier;

@Value
@Builder
public class Experiment {
  
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
}
