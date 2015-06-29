package chardin.nsu.patrol.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Chad
 */
public class Graph {

    private static int getVertex(int width, int x, int y) {
        return y * width + x;
    }

    private final boolean[] values;
    private final int width;
    private final int height;
    private final Set<Integer> vertices;
    private final Map<Integer, Set<Integer>> connections;
    private final Set<Set<Integer>> edges;
    private final List<List<Boolean>> mask;

    public Graph(final boolean[] values, final int width) {

        final int length;

        this.values = values.clone();
        length = this.values.length;
        this.width = width;
        this.height = this.values.length / width;

        if (this.values.length % width != 0) {
            throw new IllegalStateException(String.format("Width of values (%d) is not a multiple of the width (%d)", length, width));
        }

        //store the vertices
        {
            final Set<Integer> mutableVertixes = new HashSet<>(width * height);

            for (int i = 0; i < length; i++) {
                mutableVertixes.add(i);
            }

            this.vertices = Collections.unmodifiableSet(mutableVertixes);
        }

        //store the connections annd mask
        {
            final Map<Integer, Set<Integer>> mutableConnections = new HashMap<>(vertices.size());
            final List<List<Boolean>> mutableMask = new ArrayList<>(height);

            for (int y = 0; y < height; y++) {
                final List<Boolean> mutableMaskRow = new ArrayList<>(width);

                for (int x = 0; x < width; x++) {
                    final int vertex = getVertex(width, x, y);
                    final boolean maskValue = this.values[y * width + x];
                    final Set<Integer> mutableVertexConnections = new HashSet<>();

                    mutableMaskRow.add(maskValue);

                    if (y > 0) {
                        final int connectionVertex = getVertex(width, x, y - 1);
                        if (this.values[connectionVertex]) {
                            mutableVertexConnections.add(connectionVertex);
                        }
                    }

                    if (y < this.height - 1) {
                        final int connectionVertex = getVertex(width, x, y + 1);
                        if (this.values[connectionVertex]) {
                            mutableVertexConnections.add(connectionVertex);
                        }
                    }

                    if (x > 0) {
                        final int connectionVertex = getVertex(width, x - 1, y);
                        if (this.values[connectionVertex]) {
                            mutableVertexConnections.add(connectionVertex);
                        }
                    }

                    if (x < this.width - 1) {
                        final int connectionVertex = getVertex(width, x + 1, y);
                        if (this.values[connectionVertex]) {
                            mutableVertexConnections.add(connectionVertex);
                        }
                    }

                    mutableConnections.put(vertex, Collections.unmodifiableSet(mutableVertexConnections));
                }

                mutableMask.add(Collections.unmodifiableList(mutableMaskRow));
            }

            this.connections = Collections.unmodifiableMap(mutableConnections);
            this.mask = Collections.unmodifiableList(mutableMask);
        }

        //store the edges
        {
            final Set<Set<Integer>> mutableEdges = new HashSet<>();
            for (final Map.Entry<Integer, Set<Integer>> connectionEntry : this.connections.entrySet()) {
                final Integer from = connectionEntry.getKey();

                for (final Integer to : connectionEntry.getValue()) {
                    final Set<Integer> edge = new HashSet<>(2);

                    edge.add(from);
                    edge.add(to);

                    mutableEdges.add(Collections.unmodifiableSet(edge));
                }
            }

            this.edges = Collections.unmodifiableSet(mutableEdges);
        }

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<List<Boolean>> getMask() {
        return mask;
    }

    private boolean getValue(int x, int y) {
        return values[getVertex(width, x, y)];
    }

    public boolean isAllowed(int x, int y) {
        return getValue(x, y);
    }

    public boolean isBlocked(int x, int y) {
        return !isAllowed(x, y);
    }

    public Set<Integer> getVertices() {
        return vertices;
    }

    public Map<Integer, Set<Integer>> getConnections() {
        return connections;
    }

    public Set<Set<Integer>> getEdges() {
        return edges;
    }
}
