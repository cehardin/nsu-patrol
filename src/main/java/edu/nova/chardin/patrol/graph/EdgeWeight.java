package edu.nova.chardin.patrol.graph;

import lombok.NonNull;
import lombok.Value;

@Value
public class EdgeWeight implements Comparable<EdgeWeight> {
  
  int value;

  @Override
  public int compareTo(@NonNull final EdgeWeight o) {
    return Integer.compare(value, o.getValue());
  }
  
  
}
