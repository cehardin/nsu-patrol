package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The random adversary strategy.
 */
public class RandomAdversaryStrategy implements AdversaryStrategy {

  @Override
  public boolean attack(int attackInterval, long timestep, boolean occupied) {
    return ThreadLocalRandom.current().nextInt(attackInterval) == 0;
  }
}
