
package edu.nova.chardin.patrol.adversary;

public interface AdversaryContext {
  
  int getAttackInterval();
  
  long getTimestep();
  
  boolean isOccupied();
}
