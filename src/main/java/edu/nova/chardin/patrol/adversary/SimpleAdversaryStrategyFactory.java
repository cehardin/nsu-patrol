package edu.nova.chardin.patrol.adversary;

import lombok.NonNull;
import lombok.Value;

@Value
public class SimpleAdversaryStrategyFactory implements AdversaryStrategyFactory {
  
  @NonNull
  String name;
  
  @NonNull
  Class<? extends AdversaryStrategy> adversaryStrategyClass;

  @Override
  public AdversaryStrategy get() {
    try {
      return adversaryStrategyClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Could not create adversary strategy", e);
    }
  }
}
