package edu.nova.chardin.patrol.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 *
 * @author cehar
 */
@Singleton
public class PatrolGraphGenerator {

    private final Random random;
    private final Provider<PatrolGraph> graphProvider;
    private final int widthLength;
    private final int amountOfVertices;
    private final double averageConnectedness;

    @Inject
    PatrolGraphGenerator(long seed, Provider<PatrolGraph> graphProvider, int widthLength, int amountOfVertices, double averageConnectedness) {
        this.random = new Random(seed);
        this.graphProvider = graphProvider;
        this.widthLength = widthLength;
        this.amountOfVertices = amountOfVertices;
        this.averageConnectedness = averageConnectedness;
    }

    public PatrolGraph create() {
        final PatrolGraph graph = graphProvider.get();
        final Map<Integer, List<Integer>> vertexNumberPoints = new HashMap<>();

        {
            final Set<List<Integer>> points = new HashSet<>(amountOfVertices);
            int vertexNumber;
            
            while (points.size() < amountOfVertices) {
                final int x = random.nextInt(widthLength);
                final int y = random.nextInt(widthLength);
                final List<Integer> point = Arrays.asList(x, y);

                points.add(point);
            }
            
            vertexNumber = 1;
            
            for (final List<Integer> point : points) {
                graph.addVertex(vertexNumber);
                vertexNumberPoints.put(vertexNumber, point);
                vertexNumber++;
            }
        }

        vertexNumberPoints.entrySet().stream().forEach(e1 -> {
            final int n1 = e1.getKey();
            final int x1 = e1.getValue().get(0);
            final int y1 = e1.getValue().get(1);

            vertexNumberPoints.entrySet().stream().filter(e -> !e.getKey().equals(n1)).forEach(e2 -> {
                final int n2 = e2.getKey();
                if (!graph.containsEdge(n1, n2)) {
                    final int x2 = e2.getValue().get(0);
                    final int y2 = e2.getValue().get(1);
                    final double xd = Math.pow(x1 - x2, 2);
                    final double yd = Math.pow(y1 - y2, 2);
                    final double length = Math.sqrt(xd + yd);

                    graph.setEdgeWeight(graph.addEdge(n1, n2), length);
                }
            });
        });

        while (graph.getAverageDegree() > averageConnectedness) {
            final List<Integer> vertices = new ArrayList<>(graph.vertexSet());
            final int v1 = vertices.get(random.nextInt(vertices.size()));
            final int v2 = vertices.get(random.nextInt(vertices.size()));
            
            if(v1 != v2) {
                if(graph.degreeOf(v1) > 1 && graph.degreeOf(v2) > 1) {
                    graph.removeEdge(v1, v2);
                }
            }
        }

        return graph;
    }
}
