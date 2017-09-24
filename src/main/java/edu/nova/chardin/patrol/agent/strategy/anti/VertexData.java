package edu.nova.chardin.patrol.agent.strategy.anti;

import lombok.Value;
import lombok.experimental.Wither;

@Value
@Wither
public class VertexData {
    
  int visitCount;
  int timestepVisited;

  public VertexData withIncrementedVisitCount() {
    return withVisitCount(getVisitCount() + 1);
  }
    
}
