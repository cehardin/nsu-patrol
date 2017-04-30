package edu.nova.chardin.patrol.graph;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.Value;

@Value
public class EdgeWeight implements Comparable<EdgeWeight> {
  
  int value;
  
  public EdgeWeight(final int value) {
    
    Preconditions.checkArgument(value > 0, "Value must be > 0 but was %s", value);
    
    this.value = value;
  }

  @Override
  public int compareTo(@NonNull final EdgeWeight o) {
    return Integer.compare(value, o.getValue());
  }
  
  
}
