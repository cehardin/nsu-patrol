package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.agent.Context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cehar
 */
public abstract class AbstractRandomAgentStrategy<V,E> implements AgentStrategy<V,E> {
    private final Random random;
    
    protected AbstractRandomAgentStrategy(long seed) {
        this.random = new Random(seed);
    }

    protected final Random getRandom() {
        return random;
    }
    
    protected final <T> T selectRandomElement(Collection<T> c) {
        final List<T> list = new ArrayList<>(c);
        final int index = random.nextInt(list.size());
        
        return list.get(index);
    }
    
    protected final E selectRandomEdge(Context<V, E> context) {
        return selectRandomElement(context.getCurrentEdges());
    }
}
