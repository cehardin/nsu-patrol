package edu.nova.chardin.patrol.graph;

import static org.junit.Assert.*;

import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.nova.chardin.patrol.AppModule;
import org.junit.Test;

public class TspLengthCalculatorTest {

  final Injector injector = Guice.createInjector(new AppModule());
  final GraphLoader graphLoader = injector.getInstance(GraphLoader.class);
  final TspLengthCalculator tspLengthCalculator = injector.getInstance(TspLengthCalculator.class);
  
  private int calculateLength(String fileName) {
    return tspLengthCalculator.apply(graphLoader.loadGraph(Resources.getResource(getClass(), fileName)));
  }
  
  @Test
  public void testGraph1() {
    assertEquals(30, calculateLength("graph1.csv"));
  }
  
  @Test
  public void testGraph2() {
    assertEquals(10, calculateLength("graph2.csv"));
  }
  
  @Test
  public void testGraph3() {
    assertEquals(47, calculateLength("graph3.csv"));
  }
  
}
