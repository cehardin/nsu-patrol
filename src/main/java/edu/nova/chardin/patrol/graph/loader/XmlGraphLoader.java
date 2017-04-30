package edu.nova.chardin.patrol.graph.loader;

import com.google.common.base.Charsets;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

/**
 * Loads graphs from text files where each line is a comma-separated triple of edges with a weight in the order of: fromVertex, toVertex,
 * weight. The vertex identifers are strings and the edge weights are integers.
 */
@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
@Log
public class XmlGraphLoader {

  public PatrolGraph loadGraph(@NonNull final XmlGraph xmlGraph) {
    final String name = xmlGraph.name();
    final String fileName = xmlGraph.fileName();
    final URL url = Resources.getResource(XmlGraphLoader.class, fileName);

    return loadGraph(name, url);

  }

  /**
   * Load a graph at a particular URL.
   *
   * @param name The name of the graph.
   * @param url The url that contains the graph file.
   * @return The loaded graph
   */
  public PatrolGraph loadGraph(@NonNull final String name, @NonNull final URL url) {

    try (final InputStream is = url.openStream()) {
      final MutableValueGraph<VertexId, EdgeWeight> graph = ValueGraphBuilder.undirected().allowsSelfLoops(false).build();
      final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
      final NodeList vertexNodes = document.getElementsByTagName("node");
      final NodeList edgeNodes = document.getElementsByTagName("edge");

      IntStream.range(0, vertexNodes.getLength())
              .mapToObj(index -> vertexNodes.item(index))
              .filter(Element.class::isInstance)
              .map(Element.class::cast)
              .map(entity -> entity.getAttribute("label"))
              .filter(Predicates.notNull())
              .map(VertexId::new)
              .forEach(vertexId -> graph.addNode(vertexId));

      
      IntStream.range(0, edgeNodes.getLength())
              .mapToObj(index -> edgeNodes.item(index))
              .filter(Element.class::isInstance)
              .map(Element.class::cast)
              .forEach(element -> {
                final VertexId source = new VertexId(element.getAttribute("source"));
                final VertexId target = new VertexId(element.getAttribute("target"));
                final EdgeWeight weight = new EdgeWeight(
                        Math.max(
                                1,
                                (int) Math.ceil(Double.parseDouble(element.getAttribute("length")))));
                
                graph.putEdgeValue(source, target, weight);
              });
      
      return new PatrolGraph(
            ImmutableValueGraph.copyOf(graph),
            name);
    } catch (IOException | SAXException | ParserConfigurationException e) {
      throw new RuntimeException(String.format("Could not read xml graph '%s' at %s", name, url), e);
    }
  }
  
  public static void main(String[] args) {
    final Injector injector = Guice.createInjector();
    final XmlGraphLoader xmlGraphLoader = injector.getInstance(XmlGraphLoader.class);
    
    for (final XmlGraph xmlGraph : XmlGraph.values()) {
      try {
        final PatrolGraph graph = xmlGraphLoader.loadGraph(xmlGraph);
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final int tspLength = graph.getApproximateTspLength();
      
        stopwatch.stop();
      
        System.out.printf(
              "Calculated in %d seconds that graph '%s' has %d vertices, %d edges, and a TSP length of %d%n", 
              stopwatch.elapsed(TimeUnit.SECONDS),
              graph.getName(),
              graph.getVertices().size(),
              graph.getEdges().size(),
              tspLength);
      } catch (RuntimeException e) {
        throw new RuntimeException(String.format("Could not load graph %s", xmlGraph), e);
      }
      
    }
    
  }
}
