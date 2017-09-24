package edu.nova.chardin.patrol.agent.strategy.anti;

import edu.nova.chardin.patrol.graph.VertexId;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Wither
public class EdgeData {
    
  @NonNull
  VertexId source;
  @NonNull
  VertexId destination;
  int length;
  int usedCount;
  int timestepUsed;

  public EdgeData withIncrementedUsedCount() {
    return withUsedCount(getUsedCount() + 1);
  }
    
}
