package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import lombok.NonNull;

public final class ChooseRandomEdgeBiasStrategy {

  public ImmutableMap<EdgeId, Double> scoreEdgeBias(@NonNull final AgentContext context) {
    final ThreadLocalRandom random = ThreadLocalRandom.current();

    return context.getIncidientEdgeIds().stream()
            .collect(
                    ImmutableMap.toImmutableMap(
                            Function.identity(),
                            Functions.forSupplier(() -> random.nextDouble())));
  }

}
