package edu.nova.chardin.patrol.agent.strategy.control;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class PeekBackAgentStrategy implements AgentStrategy {

  private final Stack<EdgeId> returnEdges = new Stack<>();
  private final Set<EdgeId> edgesToTargetVertices = new HashSet<>();
  private final Set<EdgeId> edgesToNonTargetVertices = new HashSet<>();
  private Optional<EdgeId> lastEdge = Optional.empty();

  @Override
  public void thwarted(VertexId vertex, ImmutableSet<VertexId> criticalVertices, int timestep, int attackInterval) {
    lastEdge.ifPresent(edge -> edgesToTargetVertices.add(edge));
  }

  @Override
  public EdgeId choose(final AgentContext context) {
    
    final ImmutableSet<EdgeId> edges = context.getIncidientEdgeIds();
    final EdgeId chosenEdge;
    
    lastEdge.filter(edge -> !edgesToTargetVertices.contains(edge))
            .ifPresent(edge -> edgesToNonTargetVertices.add(edge));

    if (returnEdges.isEmpty()) {
      final ImmutableSet<EdgeId> nonTargetEdges = edges.stream()
              .filter(edge -> !edgesToTargetVertices.contains(edge))
              .collect(ImmutableSet.toImmutableSet());

      if (nonTargetEdges.isEmpty()) {
        chosenEdge = pickRandomEdge(edges);
      } else {
        final ImmutableSet<EdgeId> unknownEdges = edges.stream()
                .filter(edge -> !edgesToNonTargetVertices.contains(edge))
                .collect(ImmutableSet.toImmutableSet());

        if (unknownEdges.isEmpty()) {
          chosenEdge = pickRandomEdge(nonTargetEdges);
        } else {
          chosenEdge = pickRandomEdge(unknownEdges);
        }
      }
    } else {
      chosenEdge = returnEdges.pop();
    }

    if (edgesToTargetVertices.contains(chosenEdge) || !edgesToNonTargetVertices.contains(chosenEdge)) {
      returnEdges.push(chosenEdge.reversed());
    }
    
    lastEdge = Optional.of(chosenEdge);

    return chosenEdge;
  }

  private static EdgeId pickRandomEdge(ImmutableCollection<EdgeId> edges) {
    final List<EdgeId> edgesToPickFrom = edges.asList();
    final int index = ThreadLocalRandom.current().nextInt(edgesToPickFrom.size());

    return edgesToPickFrom.get(index);
  }

}
