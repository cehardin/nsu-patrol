package edu.nova.chardin.patrol.adversary;

import java.util.function.Supplier;

public interface AdversaryStrategyFactory extends Supplier<AdversaryStrategy> {
  
  public String getName();
}
