package edu.nova.chardin.patrol.graph;

import lombok.NonNull;
import lombok.Value;

@Value
public class VertexId implements Comparable<VertexId> {
  
  @NonNull
  String value;

  @Override
  public int compareTo(@NonNull final VertexId o) {
    return value.compareTo(o.value);
  }
}
