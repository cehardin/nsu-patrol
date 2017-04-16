package edu.nova.chardin.patrol.experiment;

import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class Game {

  @NonNull
  Match match;
  
  @NonNull
  Integer number;

  @NonNull
  @Singular
  ImmutableSet<VertexId> agentStartingPositions;
  
  @NonNull
  @Singular
  ImmutableSet<VertexId> targets;
  
  @NonNull
  Integer timesteps;
}
