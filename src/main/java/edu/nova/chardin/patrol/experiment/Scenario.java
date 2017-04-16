package edu.nova.chardin.patrol.experiment;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
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
}
