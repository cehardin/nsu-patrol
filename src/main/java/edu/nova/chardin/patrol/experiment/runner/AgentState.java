package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.base.Preconditions;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
public class AgentState {
  
  @Setter(AccessLevel.NONE)
  private boolean isMoving = false;
  
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private int movingTimeStepRequired = 0;
  
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @NonNull
  private VertexId currentVertexId;
  
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private VertexId nextVertexId = null;
  
  @Setter(AccessLevel.NONE)
  private int moveCount = 0;
  
  @Setter(AccessLevel.NONE)
  private int timestepsSpentMoving = 0;
  
  public void startMove(@NonNull final VertexId destination, final int timeStepsRequired) {
    if (isMoving) {
      throw new IllegalStateException(String.format("Cannot move to %s, already moving to %s", destination, nextVertexId));
    } else {
      Preconditions.checkArgument(timeStepsRequired > 0, "timeStepsRequired must be > 0 but was %s", timeStepsRequired);
      isMoving = true;
      movingTimeStepRequired = timeStepsRequired;
      nextVertexId = destination;
      currentVertexId = null;
      moveCount++;
    }
  }
  
  public void timestep() {
    if (isMoving) {
      movingTimeStepRequired--;
      timestepsSpentMoving++;
      if (movingTimeStepRequired == 0) {
        isMoving = false;
        currentVertexId = nextVertexId;
        nextVertexId = null;
      }
    }
  }
  
  public VertexId getCurrentVertex() {
    if(isMoving) {
      throw new IllegalStateException(
              String.format(
                      "Not at a vertex, moving from %s to %s, %d timesteps left", 
                      currentVertexId, 
                      nextVertexId, 
                      movingTimeStepRequired));
    } else {
      return currentVertexId;
    }
  }
  
  public boolean isAtVertex() {
    return !isMoving;
  }
  
}
