package edu.nova.chardin.patrol.adversary;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.adversary.strategy.RandomAdversaryStrategy;
import edu.nova.chardin.patrol.adversary.strategy.StatisticalAdversaryStrategy;
import edu.nova.chardin.patrol.adversary.strategy.WaitingAdversaryStrategy;
import lombok.NonNull;
import lombok.Value;

import java.util.concurrent.ThreadLocalRandom;

@Value
public class HybridAdversaryStrategyFactory implements AdversaryStrategyFactory {
  
  @NonNull
  String name;
  
  @NonNull
  ImmutableList<AdversaryStrategyFactory> factories;
  
  public HybridAdversaryStrategyFactory() {
    name = "hybrid";
    factories = ImmutableList.of(
            new SimpleAdversaryStrategyFactory("random", RandomAdversaryStrategy.class),
            new SimpleAdversaryStrategyFactory("waiting", WaitingAdversaryStrategy.class),
            new SimpleAdversaryStrategyFactory("statistical", StatisticalAdversaryStrategy.class));
  }

  @Override
  public AdversaryStrategy get() {
    return factories.get(ThreadLocalRandom.current().nextInt(factories.size())).get();
  }
  
}
