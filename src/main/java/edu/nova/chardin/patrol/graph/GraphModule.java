package edu.nova.chardin.patrol.graph;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 *
 * @author cehar
 */
public class GraphModule extends AbstractModule {

    @Override
    protected void configure() {
        
    }
    
    @Provides
    public PatrolGraph providePatrolGraph() {
        return new PatrolGraph();
    }
    
}
