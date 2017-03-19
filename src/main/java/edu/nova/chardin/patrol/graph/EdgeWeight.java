package edu.nova.chardin.patrol.graph;

import lombok.Value;

@Value
public class EdgeWeight implements Comparable<EdgeWeight> {
  int value;

  @Override
  public int compareTo(EdgeWeight o) {
    return Integer.compare(value, o.value);
  }
  
  
}
