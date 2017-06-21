package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryContext;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The random adversary strategy.
 */
public class RandomAdversaryStrategy implements AdversaryStrategy {

  @Override
  public boolean attack(AdversaryContext context) {
    return ThreadLocalRandom.current().nextInt(context.getAttackInterval()) == 0;
  }
}
