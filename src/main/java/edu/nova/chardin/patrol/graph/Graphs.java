package edu.nova.chardin.patrol.graph;

import com.google.common.graph.ImmutableValueGraph;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provides the experiment graphs. Loads them if necessary then caches them.
 */
@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
@Log
public class Graphs {
  
  @NonNull
  GraphLoader graphLoader;
  Map<GraphTopology, ImmutableValueGraph<VertexId, EdgeWeight>> graphs = new HashMap<>();
  
  /**
   * Get the graph. Loads it if necessary then caches it for future calls.
   * @param graphTopology The graph type
   * @return The graph.
   */
  public ImmutableValueGraph<VertexId, EdgeWeight> getGraph(
          @NonNull final GraphTopology graphTopology) {
    
    final ImmutableValueGraph<VertexId, EdgeWeight> graph;
    
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
