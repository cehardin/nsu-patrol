package edu.nova.chardin.patrol.graph;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;

import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import lombok.NonNull;
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
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Loads graphs from text files where each line is a comma-separated triple of edges with a weight in the order of: fromVertex, toVertex,
 * weight. The vertex identifers are strings and the edge weights are integers.
 */
@Singleton
@Log
public class GraphLoader {

  @Inject
  GraphLoader() {
  }

  /**
   * Load the specific graph topology.
   * @param graphTopology The graph to load
   * @return The loaded graph
   */
  public ImmutableValueGraph<String, Integer> loadGraph(@NonNull final GraphTopology graphTopology) {
    final String fileName = graphTopology.getFileName();
    final URL url = Resources.getResource(fileName);

    Objects.requireNonNull(url, String.format("Could not find resource file %s for graph tolopogy %s", fileName, graphTopology));

    log.info(String.format("Reading graph toplogy for graph %s at %s", graphTopology, url));
    try {
      return loadGraph(url);
    } catch (RuntimeException e) {
      throw new RuntimeException(String.format("Could not load graph topology %s", graphTopology), e);
    }
  }

  /**
   * Load a graph at a particular URL.
   * @param url The url that contains the graph file.
   * @return The loaded graph
   */
  public ImmutableValueGraph<String, Integer> loadGraph(@NonNull final URL url) {

    try (final Reader r = new InputStreamReader(url.openStream(), Charsets.UTF_8)) {
      final Set<String> nodes = new HashSet<>();
      final Map<Pair<String, String>, Integer> edges = new HashMap<>();
      final Splitter splitter = Splitter.on(',').trimResults().omitEmptyStrings();
      final MutableValueGraph graph = ValueGraphBuilder.undirected().allowsSelfLoops(false).build();

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
                final String from = elements.get(0).trim();
                final String to = elements.get(1).trim();
                final int weight = Integer.parseInt(elements.get(2));
                final Pair<String, String> edge;

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
                  log.fine(String.format("Read edge %s with weight %d", edge, weight));
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

      for (final String node : nodes) {
        log.fine(String.format("Added vertex '%s'", node));
        graph.addNode(node);
      }

      for (final Entry<Pair<String, String>, Integer> edgeEntry : edges.entrySet()) {
        final Pair<String, String> edge = edgeEntry.getKey();
        final Integer weight = edgeEntry.getValue();
        final String from = edge.getFirst();
        final String to = edge.getSecond();

        log.fine(String.format("Added edge %s with weight %d", edge, weight));
        graph.putEdgeValue(from, to, weight);
      }

      log.info(String.format("Loaded graph with %d vertices and %d edges from %s", graph.nodes().size(), graph.edges().size(), url));

      return ImmutableValueGraph.copyOf(graph);
    } catch (IOException | RuntimeException e) {
      throw new RuntimeException(String.format("Could not load graph from %s", url), e);
    }
  }
}
