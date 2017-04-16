package edu.nova.chardin.patrol.experiment;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({@Inject}))
@Value
@Getter(AccessLevel.NONE)
public class AttackIntervalsCalculator {
  ImmutableSet<Double> factors = ImmutableSet.of(1.0/ 8.0, 1.0 / 4.0, 1.0 / 2.0, 1.0, 2.0);
  
  @NonNull
  AttackIntervalCalculator attackIntervalCalculator;
  
  public Map<Double, Integer> apply(
          @NonNull final ImmutableValueGraph<VertexId, EdgeWeight> graph,
          final int numAgents) {
    
    return factors
            .parallelStream()
            .collect(
                    Collectors
                            .toMap(
                                    Function.identity(),
                                    f -> attackIntervalCalculator.apply(graph, numAgents, f)));
    
  }
}
