package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;

public class WaitingAdversaryStrategy implements AdversaryStrategy {
  
  private boolean wasOccupied = false;

  @Override
  public boolean attack(int attackInterval, long timestep, boolean occupied) {
    
    final boolean attack;
    
    if (occupied) {
      attack = false;
      wasOccupied = true;
    } else {
      attack = wasOccupied;
      wasOccupied = false;
    }
    
    return attack;
  }
  
  
}
