package edu.nova.chardin.patrol.graph;

import lombok.NonNull;
import lombok.Value;

@Value
public class EdgeId {
  
  @NonNull
  VertexId vertexA;
  
  @NonNull
  VertexId vertexB;
}
