package edu.nova.chardin.patrol.graph;

import com.google.common.graph.ImmutableValueGraph;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides the experiment graphs. Loads them if necessary then caches them.
 */
@Singleton
@Log
public class Graphs {
  
  private final GraphLoader graphLoader;
  private final Map<GraphTopology, ImmutableValueGraph<String, Integer>> graphs;
  
  @Inject
  Graphs(@NonNull final GraphLoader graphLoader) {
    this.graphLoader = graphLoader;
    this.graphs = new HashMap<>(GraphTopology.values().length);
  }
  
  /**
   * Get the graph. Loads it if necessary then caches it for future calls.
   * @param graphTopology The graph type
   * @return The graph.
   */
  public ImmutableValueGraph<String, Integer> getGraph(@NonNull final GraphTopology graphTopology) {
    
    final ImmutableValueGraph<String, Integer> graph;
    
    synchronized (graphs) {
      if (graphs.containsKey(graphTopology)) {
        graph = graphs.get(graphTopology);
      } else {
        log.info(String.format("Loading graph %s", graphTopology));
        graph = graphLoader.loadGraph(graphTopology);
        graphs.put(graphTopology, graph);
        log.info(String.format("Loaded graph %s", graphTopology));
      }
    }
    
    return graph;
  }
  
}
