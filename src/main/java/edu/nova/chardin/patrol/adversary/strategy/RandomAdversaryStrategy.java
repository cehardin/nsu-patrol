package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryContext;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.Random;

/**
 * The random adversary strategy.
 */
@Value
@Getter(AccessLevel.NONE)
public class RandomAdversaryStrategy implements AdversaryStrategy {

  Random random = new Random();

  @Override
  public boolean attack(AdversaryContext context) {
    return random.nextInt(context.getAttackInterval()) == 0;
  }
}
