package chardin.nsu.patrol.graph.loader;

import chardin.nsu.patrol.graph.Graph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 *
 * @author Chad
 */
public class SimpleGraphLoader {
    private static final int AVAILALBE_CHAR = '.';
    private static final int BLOCKED_CHAR = 'X';
    
    public Graph load(final Path path) throws IOException {
        final List<List<Boolean>> map = new ArrayList<>();
        final AtomicReference<Integer> width = new AtomicReference<>(null);
        final boolean[] values;
        
        Files.lines(path).sequential().forEach((String line) -> {
            final List<Boolean> spots = line.chars().mapToObj(c -> {
                switch(c) {
                    case AVAILALBE_CHAR:
                        return true;
                    case BLOCKED_CHAR:
                        return false;
                    default:
                        throw new IllegalStateException(String.format("Illegal character '%c', only 'X' and '.' are allowed", (char)c));
                }
            }).collect(Collectors.toList());
            
            if(!width.compareAndSet(null, spots.size())) {
                if(width.get().equals(spots.size())) {
                    map.add(Collections.unmodifiableList(spots));
                }
                else {
                    throw new IllegalStateException(String.format("The first line had a width of %d but a later line had a width of %d", width.get(), spots.size()));
                }
            }
        });
        
        values = new boolean[map.size() * width.get()];
        
        for(int y = 0; y < map.size(); y++) {
            final List<Boolean> row = map.get(y);
            
            for(int x = 0; x < width.get(); x++) {
                final int offset = y * width.get() + x;
                
                values[offset] = row.get(x);
            }
        }
        
        return new Graph(values, width.get());
        
    }
}
