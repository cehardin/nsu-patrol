package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.Context;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author cehar
 */
public final class RandomCriticalVertexAgentStrategy<V, E> extends AbstractRandomAgentStrategy<V,E> {

    private Optional<V> targetVertex = Optional.empty();

    public RandomCriticalVertexAgentStrategy(long seed) {
        super(seed);
    }

    @Override
    public E process(Context<V, E> context) {
        if (targetVertex.isPresent()) {
            if (targetVertex.get().equals(context.getVertex())) {
                targetVertex = Optional.of(selectRandomCriticalVertex(context));
            }

            return nextEdgeToTargetVertex(context);
        } else if (context.getCriticalVertices().isEmpty()) {
            return selectRandomEdge(context);
        } else {
            targetVertex = Optional.of(selectRandomCriticalVertex(context));
            return nextEdgeToTargetVertex(context);
        }
    }

    private V selectRandomCriticalVertex(Context<V, E> context) {
        return selectRandomElement(context.getCriticalVertices());
    }

    private E nextEdgeToTargetVertex(Context<V, E> context) {
        final SortedMap<Integer, E> distances = new TreeMap<>();
        
        for(final E edge : context.getEdges()) {
            int distance = context.getDistance(edge, targetVertex.get());
            
            distances.putIfAbsent(distance, edge);
        }
        
        return distances.values().iterator().next();
    }

}
