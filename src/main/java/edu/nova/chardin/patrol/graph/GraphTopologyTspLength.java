package edu.nova.chardin.patrol.graph;

import com.google.common.base.Stopwatch;
import com.google.common.graph.ImmutableValueGraph;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
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
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
@Log
public class GraphTopologyTspLength {
  
  @NonNull
  TspLengthCalculator tspLengthCalculator;
  
  @NonNull
  Graphs graphs;
  
  Map<GraphTopology, Integer> graphLengths = new HashMap<>();
  
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
