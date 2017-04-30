package edu.nova.chardin.patrol.graph;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.Value;

@Value
public class VertexId implements Comparable<VertexId> {
  
  @NonNull
  String value;
  
  public VertexId(@NonNull final String value) {
    this.value = value.trim();
    
    Preconditions.checkArgument(value.length() > 0, "Value was empty");
  }

  @Override
  public int compareTo(@NonNull final VertexId o) {
    return value.compareTo(o.value);
  }
}
