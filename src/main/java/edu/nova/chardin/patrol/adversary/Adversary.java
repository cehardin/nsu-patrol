package edu.nova.chardin.patrol.adversary;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An adversary, which uses an adversary strategy
 */
@RequiredArgsConstructor
@Getter
public final class Adversary {

  @NonNull
  private final AdversaryStrategy strategy;

  @NonNull
  private final String targetVertex;
  
  private final int k;

  private boolean attacking = false;
  private int timeStepsAttacting = 0;
  private int attackCount = 0;
  private int succesfulAttackCount = 0;
  private int notAttackCount = 0;

  public void decide(final boolean occupied, final int timestep) {
    
    if (attacking) {
      timeStepsAttacting++;
      
      if (occupied) {
        attacking = false;
        timeStepsAttacting = 0;
      } else if (timeStepsAttacting == k) {
        succesfulAttackCount++;
        attacking = false;
        timeStepsAttacting = 0;
      } else {
        timeStepsAttacting++;
      }
    } else if (strategy.attack(k, occupied, timestep)) {
      attacking = true;
      timeStepsAttacting = 0;
      attackCount++;
    } else {
      attacking = false;
      timeStepsAttacting = 0;
      notAttackCount++;
    }
  }
}
