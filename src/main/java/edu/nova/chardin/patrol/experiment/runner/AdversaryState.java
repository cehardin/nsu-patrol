package edu.nova.chardin.patrol.experiment.runner;

import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

@Data
final class AdversaryState {
  
  @NonNull
  private final VertexId target;
  
  @Setter(AccessLevel.NONE)
  private boolean attacking = false;
  
  @Setter(AccessLevel.NONE)
  private int attackingTimeStepCount = 0;
  
  @Setter(AccessLevel.NONE)
  private int attackCount = 0;
  
  @Setter(AccessLevel.NONE)
  private int attackThwartedCount = 0;
  
  @Setter(AccessLevel.NONE)
  private int attackSuccessfulCount = 0;
  
  
  public void beginAttack() {
    
    if (attacking) {
      throw new IllegalStateException("Already attacking");
    } else {
      attacking = true;
      attackingTimeStepCount = 0;
      attackCount++;
    }
  }
  
  public void endAttack(boolean succesful) {
    if (attacking) {
      attacking = false;
      attackingTimeStepCount = 0;
      
      if (succesful) {
        attackSuccessfulCount++;
      } else {
        attackThwartedCount++;
      }
    } else {
      throw new IllegalStateException("Not attacking");
    }
  }
  
  public void timestep() {
    if (attacking) {
      attackingTimeStepCount++;
    }
  }
}
