package edu.nova.chardin.patrol.adversary;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An adversary, which uses an adversary strategy
 */
@RequiredArgsConstructor
@Getter
public class Adversary {

  @NonNull
  private final AdversaryStrategy strategy;

  @NonNull
  private final String targetVertex;

  private boolean attacking = false;
  private int timeStepsAttacting = 0;

  public void beginAttack() {
    Preconditions.checkState(!attacking, "already attacking");
    attacking = true;
    timeStepsAttacting = 0;
  }

  public void incrementAttack() {
    Preconditions.checkState(attacking, "not attacking");
    timeStepsAttacting++;
  }

  public void endAttack() {
    Preconditions.checkState(attacking, "not attacking");
    attacking = false;
    timeStepsAttacting = 0;
  }

}
