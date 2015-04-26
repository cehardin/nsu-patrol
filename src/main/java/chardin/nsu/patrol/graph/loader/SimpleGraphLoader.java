package chardin.nsu.patrol.graph.loader;

import chardin.nsu.patrol.graph.Graph;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author Chad
 */
public class SimpleGraphLoader {
    
    Graph<Integer> load(final Path path) throws IOException {
        final Set<Integer> vertices = new HashSet<>();
        final Set<Set<Integer>> edges = new HashSet<>();

        Files.lines(path).forEach((String line) -> {
            final String[] parts = line.split(",");
            
            if(parts.length == 2) {
                final Integer vertex1 = Integer.parseInt(parts[0]);
                final Integer vertex2 = Integer.parseInt(parts[1]);
                final Set<Integer> edge = new HashSet<>();
                
                edge.add(vertex1);
                edge.add(vertex2);
                
                vertices.add(vertex1);
                vertices.add(vertex2);
                edges.add(edge);
            }
            else {
                throw new IllegalStateException();
            }
        });
        
        return new Graph<Integer>(vertices, edges);
    }
}
