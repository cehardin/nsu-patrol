package edu.nova.chardin.patrol.graph.loader;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.google.common.io.CharStreams;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

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
public class CustomFormatGraphLoader {


  /**
   * Load a graph at a particular URL.
   * @param url The url that contains the graph file.
   * @return The loaded graph
   */
  public PatrolGraph loadGraph(@NonNull final URL url) {

    try (final Reader r = new InputStreamReader(url.openStream(), Charsets.UTF_8)) {
      final Set<VertexId> nodes = new HashSet<>();
      final Map<Pair<VertexId, VertexId>, EdgeWeight> edges = new HashMap<>();
      final Splitter splitter = Splitter.on(',').trimResults().omitEmptyStrings();
      final MutableValueGraph<VertexId, EdgeWeight> graph = ValueGraphBuilder.undirected().allowsSelfLoops(false).build();

      for (final String line : CharStreams.readLines(r)) {
        final String trimmedLine = line.trim();

        // skip comments and blank lines
        if (!trimmedLine.isEmpty()) {
          if (trimmedLine.startsWith("#")) {
            log.fine(String.format("Read comment : %s", trimmedLine));
          } else {
            try {
              final List<String> elements = splitter.splitToList(trimmedLine);

              if (elements.size() == 3) {
                final VertexId from = new VertexId(elements.get(0).trim());
                final VertexId to = new VertexId(elements.get(1).trim());
                final EdgeWeight weight = new EdgeWeight(Integer.parseInt(elements.get(2)));
                final Pair<VertexId, VertexId> edge;

                // order the edge from smallest vertex to largest vertex and don't allow self loops
                if (from.equals(to)) {
                  throw new IOException(String.format("The 'from' vertex '%s' and the 'to' vertex '%s' cannot be the same", from, to));
                } else if (from.compareTo(to) < 0) {
                  edge = Pair.create(from, to); //keep the order
                } else {
                  edge = Pair.create(to, from); //flip it
                }

                nodes.add(from);
                nodes.add(to);

                if (edges.put(edge, weight) == null) {
                  log.fine(String.format("Read edge %s with weight %s", edge, weight));
                } else {
                  throw new IOException(String.format("Edge %s has already been encountered", edge));
                }
              } else {
                throw new IOException(String.format("Line was %d elements instead of %d elements", elements.size(), 3));
              }
            } catch (IOException e) {
              throw new IOException(String.format("Could not process line : '%s'", trimmedLine), e);
            }
          }
        }
      }

      for (final VertexId node : nodes) {
        log.fine(String.format("Added vertex '%s'", node));
        graph.addNode(node);
      }

      for (final Entry<Pair<VertexId, VertexId>, EdgeWeight> edgeEntry : edges.entrySet()) {
        final Pair<VertexId, VertexId> edge = edgeEntry.getKey();
        final EdgeWeight weight = edgeEntry.getValue();
        final VertexId from = edge.getFirst();
        final VertexId to = edge.getSecond();

        log.fine(String.format("Added edge %s with weight %s", edge, weight));
        graph.putEdgeValue(from, to, weight);
      }

      log.info(String.format("Loaded graph with %d vertices and %d edges from %s", graph.nodes().size(), graph.edges().size(), url));

      return new PatrolGraph(
              ImmutableValueGraph.copyOf(graph), 
              String.format("Loaded from %s", url));
    } catch (IOException | RuntimeException e) {
      throw new RuntimeException(String.format("Could not load graph from %s", url), e);
    }
  }
}
