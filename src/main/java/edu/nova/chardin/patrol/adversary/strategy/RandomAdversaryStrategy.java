package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;

import java.util.Random;

/**
 * The random adversary strategy.
 */
public final class RandomAdversaryStrategy implements AdversaryStrategy {

  private final Random random;
  private final double probability;

  public RandomAdversaryStrategy(final long seed, final double probability) {
    this.random = new Random(seed);
    this.probability = probability;
  }

  @Override
  public boolean attack(final int k, final boolean occupied, final int timestep) {
    return random.nextDouble() <= probability;
  }
}
