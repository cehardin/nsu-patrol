package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.Context;

/**
 *
 * @author cehar
 */
public final class RandomEdgeAgentStrategy<V, E> extends AbstractRandomAgentStrategy<V,E> {
    
    public RandomEdgeAgentStrategy(long seed) {
        super(seed);
    }

    @Override
    public E process(Context<V, E> context) {
        return selectRandomEdge(context);
    }
    
    
}
