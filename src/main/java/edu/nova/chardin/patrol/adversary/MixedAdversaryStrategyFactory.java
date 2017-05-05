package edu.nova.chardin.patrol.adversary;

import com.google.common.collect.Iterators;
import lombok.NonNull;
import lombok.Value;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

@Value
public class MixedAdversaryStrategyFactory implements AdversaryStrategyFactory {
  
  @NonNull
  String name;
  
  @NonNull
  Iterator<AdversaryStrategyFactory> factories;
  
  public MixedAdversaryStrategyFactory(@NonNull final AdversaryStrategyFactory ... factories) {
    this(Arrays.asList(factories));
  }
  
  public MixedAdversaryStrategyFactory(@NonNull final Collection<AdversaryStrategyFactory> factories) {
    this.name = String.format("Mixed of %s", String.join(", ", factories.stream().map(AdversaryStrategyFactory::getName).collect(Collectors.toList())));
    this.factories = Iterators.cycle(factories);
  }

  @Override
  public AdversaryStrategy get() {
    return factories.next().get();
  }
  
}
