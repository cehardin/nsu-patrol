package edu.nova.chardin.patrol.adversary;

import com.google.common.collect.Iterators;
import edu.nova.chardin.patrol.adversary.strategy.RandomAdversaryStrategy;
import edu.nova.chardin.patrol.adversary.strategy.StatisticalAdversaryStrategy;
import edu.nova.chardin.patrol.adversary.strategy.WaitingAdversaryStrategy;
import lombok.NonNull;
import lombok.Value;

import java.util.Iterator;

@Value
public class HybridAdversaryStrategyFactory implements AdversaryStrategyFactory {
  
  @NonNull
  String name;
  
  @NonNull
  Iterator<AdversaryStrategyFactory> factories;
  
  public HybridAdversaryStrategyFactory() {
    name = "hybrid";
    factories = Iterators.cycle(
            new SimpleAdversaryStrategyFactory("random", RandomAdversaryStrategy.class),
            new SimpleAdversaryStrategyFactory("waiting", WaitingAdversaryStrategy.class),
            new SimpleAdversaryStrategyFactory("statistical", StatisticalAdversaryStrategy.class));
  }

  @Override
  public AdversaryStrategy get() {
    synchronized (factories) {
        return factories.next().get();
    }
  }
  
}
