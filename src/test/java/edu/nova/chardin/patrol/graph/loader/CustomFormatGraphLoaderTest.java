package edu.nova.chardin.patrol.graph.loader;

import edu.nova.chardin.patrol.graph.loader.CustomFormatGraphLoader;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.io.Resources;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.VertexId;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 *
 * @author cehar
 */
public class CustomFormatGraphLoaderTest {
  
  @Test
  public void graph1() throws IOException {
    final URL url = Resources.getResource(getClass(), "graph1.csv");
    final CustomFormatGraphLoader graphLoader = new CustomFormatGraphLoader();
    final ImmutableValueGraph<VertexId, EdgeWeight> graph = graphLoader.loadGraph(url);
    
    assertEquals(3, graph.nodes().size());
    assertEquals(3, graph.edges().size());
    assertEquals(ImmutableSet.of(new VertexId("A"), new VertexId("B"), new VertexId("C")), graph.nodes());
    assertEquals(
            ImmutableSet.of(
                    EndpointPair.unordered(new VertexId("A"), new VertexId("B")),
                    EndpointPair.unordered(new VertexId("B"), new VertexId("C")),
                    EndpointPair.unordered(new VertexId("A"), new VertexId("C"))), 
            graph.edges());
    
    assertEquals(new EdgeWeight(5), graph.edgeValue(new VertexId("A"), new VertexId("B")));
    assertEquals(new EdgeWeight(5), graph.edgeValue(new VertexId("B"), new VertexId("A")));
    
    assertEquals(new EdgeWeight(10), graph.edgeValue(new VertexId("B"), new VertexId("C")));
    assertEquals(new EdgeWeight(10), graph.edgeValue(new VertexId("C"), new VertexId("B")));
    
    assertEquals(new EdgeWeight(15), graph.edgeValue(new VertexId("A"), new VertexId("C")));
    assertEquals(new EdgeWeight(15), graph.edgeValue(new VertexId("C"), new VertexId("A")));
  }
  
  @Test
  public void graph2() throws IOException {
    final URL url = Resources.getResource(getClass(), "graph2.csv");
    final CustomFormatGraphLoader graphLoader = new CustomFormatGraphLoader();
    final ImmutableValueGraph<VertexId, EdgeWeight> graph = graphLoader.loadGraph(url);
  }
}
