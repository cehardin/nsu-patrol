package edu.nova.chardin.patrol.agent;

/**
 *
 * @author cehar
 */
public interface AgentStrategy<V,E> {
    E process(Context<V,E> context);
}
