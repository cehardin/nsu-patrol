package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryContext;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;

public class WaitingAdversaryStrategy implements AdversaryStrategy {
  
  private boolean wasOccupied = false;

  @Override
  public boolean attack(AdversaryContext context) {
    
    final boolean attack;
    
    if (context.isOccupied()) {
      attack = false;
      wasOccupied = true;
    } else {
      if (wasOccupied) {
        attack = true;
      } else {
        attack = false;
      }
      wasOccupied = false;
    }
    
    return attack;
  }
  
  
}
