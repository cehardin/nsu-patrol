package chardin.nsu.patrol.graph.creator;

import chardin.nsu.patrol.graph.Graph;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Chad
 */
public class GridGraphCreator {

    public Graph<Integer> create(final int rows, final int columns) {
        final Set<Integer> vertices = new HashSet<>(check(rows, "rows") * check(columns, "columns"));
        final Set<Set<Integer>> edges = new HashSet<>(2 * rows * columns);
        final int lastRow = rows - 1;
        final int lastColumn = columns - 1;

        for (int vertex = 0; vertex < rows * columns; vertex++) {
            vertices.add(vertex);
        }

        //up to last row
        for (int row = 0; row < lastRow; row++) {
            //up to last column
            for (int column = 0; column < lastColumn; column++) {
                edges.add(createEdge(offset(row, column, columns), offset(row, column + 1, columns))); //horizontal edge
                edges.add(createEdge(offset(row, column, columns), offset(row + 1, column, columns))); //vertical edge
            }
            
            //last column
            edges.add(createEdge(offset(row, lastColumn, columns), offset(row + 1, lastColumn, columns))); //vertical edge
        }

        //last row
        for (int column = 0; column < columns - 1; column++) {
            edges.add(createEdge(offset(lastRow, column, columns), offset(lastRow, column + 1, columns))); //horizontal edge
        }

        return new Graph<>(vertices, edges);
    }

    private int check(final int number, String name) {
        if (number < 2) {
            throw new IllegalArgumentException(String.format("%s must be >= 2 but was %s", name, number));
        }

        return number;
    }

    private int offset(final int row, final int column, final int columns) {
        return row * columns + column;
    }

    private Set<Integer> createEdge(final int from, final int to) {
        final Set<Integer> edge = new HashSet<>(2);

        edge.add(from);
        edge.add(to);

        return edge;
    }
}
