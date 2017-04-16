package edu.nova.chardin.patrol.experiment;

import com.google.common.graph.ImmutableValueGraph;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.TspLengthCalculator;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({@Inject}))
@Value
@Getter(AccessLevel.NONE)
public class AttackIntervalCalculator {
  
  @NonNull
  TspLengthCalculator tspLengthCalculator;
  
  public int apply(
          @NonNull final ImmutableValueGraph<VertexId, EdgeWeight> graph,
          final int numAgents,
          final double factor) {
    
    return (int)(factor * ((double)numAgents / tspLengthCalculator.apply(graph)));
    
  }
}
