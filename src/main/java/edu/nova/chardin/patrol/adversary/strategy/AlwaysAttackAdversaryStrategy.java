package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryContext;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 * The random adversary strategy.
 */
@Value
@Getter(AccessLevel.NONE)
public class AlwaysAttackAdversaryStrategy implements AdversaryStrategy {

  @Override
  public boolean attack(AdversaryContext context) {
    return true;
  }
}
