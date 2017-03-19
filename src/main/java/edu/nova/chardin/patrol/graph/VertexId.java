package edu.nova.chardin.patrol.graph;

import lombok.Value;

@Value
public class VertexId implements Comparable<VertexId> {
  String value;

  @Override
  public int compareTo(VertexId o) {
    return value.compareTo(o.value);
  }
}
