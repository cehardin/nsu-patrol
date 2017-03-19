package edu.nova.chardin.patrol.graph;

import com.google.common.base.Stopwatch;
import com.google.common.graph.ImmutableValueGraph;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Gets the TSP lengths for the experiment graphs.
 */
@Singleton
@Log
public class GraphTopologyTspLength {
  
  private final TspLengthCalculator tspLengthCalculator;
  private final Graphs graphs;
  private final Map<GraphTopology, Integer> graphLengths = new HashMap<>();

  @Inject
  GraphTopologyTspLength(@NonNull final TspLengthCalculator tspLengthCalculator, @NonNull final Graphs graphs) {
    this.tspLengthCalculator = tspLengthCalculator;
    this.graphs = graphs;
  }
  
  /**
   * Gets the TSP length for a graph. Caches the result for layer access.
   * @param graphTopology The graph.
   * @return The TSP length.
   */
  public int getTspLength(@NonNull final GraphTopology graphTopology) {
    final int length;
    
    synchronized (graphLengths) {
      if (graphLengths.containsKey(graphTopology)) {
        length = graphLengths.get(graphTopology);
      } else {
        final ImmutableValueGraph<VertexId, EdgeWeight> graph = graphs.getGraph(graphTopology);
        final Stopwatch stopwatch = Stopwatch.createStarted();
        
        log.info(String.format("Calculating TSP length for graph %s", graphTopology));
        length = tspLengthCalculator.apply(graph);
        graphLengths.put(graphTopology, length);
        log.info(
                String.format(
                        "Calculated a TSP length of %d for graph %s in %d ms", 
                        length, 
                        graphTopology, 
                        stopwatch.elapsed(TimeUnit.MILLISECONDS)));
      }
    }
    
    return length;
  }
          
}
