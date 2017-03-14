/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nova.chardin.patrol.graph;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 *
 * @author cehar
 */
public class GraphLoaderTest {
  
  @Test
  public void graph1() throws IOException {
    final URL url = Resources.getResource(getClass(), "graph1.csv");
    final GraphLoader graphLoader = new GraphLoader();
    final ImmutableValueGraph<String, Integer> graph = graphLoader.loadGraph(url);
    
    assertEquals(3, graph.nodes().size());
    assertEquals(3, graph.edges().size());
    assertEquals(ImmutableSet.of("A", "B", "C"), graph.nodes());
    assertEquals(
            ImmutableSet.of(
                    EndpointPair.unordered("A", "B"),
                    EndpointPair.unordered("B", "C"),
                    EndpointPair.unordered("A", "C")), 
            graph.edges());
    
    assertEquals(new Integer(5), graph.edgeValue("A", "B"));
    assertEquals(new Integer(5), graph.edgeValue("B", "A"));
    
    assertEquals(new Integer(10), graph.edgeValue("B", "C"));
    assertEquals(new Integer(10), graph.edgeValue("C", "B"));
    
    assertEquals(new Integer(15), graph.edgeValue("A", "C"));
    assertEquals(new Integer(15), graph.edgeValue("C", "A"));
  }
  
  @Test
  public void graph2() throws IOException {
    final URL url = Resources.getResource(getClass(), "graph2.csv");
    final GraphLoader graphLoader = new GraphLoader();
    final ImmutableValueGraph<String, Integer> graph = graphLoader.loadGraph(url);
  }
}
