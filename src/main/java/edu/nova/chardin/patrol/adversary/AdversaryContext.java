
package edu.nova.chardin.patrol.adversary;

import lombok.Value;

@Value
public class AdversaryContext {
  
  int attackInterval;
  
  long timestep;
  
  boolean occupied;
}
