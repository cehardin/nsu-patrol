package chardin.nsu.patrol.graph.grid;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Chad
 */
public class GridGraph {
    
    private static int getVertex(int width, int x, int y) {
        return y * width + x;
    }
    
    private final boolean[] values;
    private final int width;
    private final int height;
    private final Set<Integer> vertices;
    private final Map<Integer, Set<Integer>> edges;
    
    public GridGraph(final boolean[] values, final int width) {
        final Set<Integer> mutableVertixes = new HashSet<>();
        final Map<Integer, Set<Integer>> mutableEdges = new HashMap<>();
        final int length;
        
        this.values = values.clone();
        length = this.values.length;
        this.width = width;
        this.height = this.values.length / width;
        
        if( this.values.length % width != 0) {
            throw new IllegalStateException(String.format("Width of values (%d) is not a multiple of the width (%d)", length, width));
        }
        
        for(int i=0; i < length; i++) {
            mutableVertixes.add(i);    
        }
        this.vertices = Collections.unmodifiableSet(mutableVertixes);
        
        for(int y=0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                final int vertex = getVertex(width, x, y);
                final Set<Integer> connections = new HashSet<>();
                
                if(y > 0) {
                    final int connectionVertex = getVertex(width, x, y - 1);
                    if(this.values[connectionVertex]) {
                        connections.add(connectionVertex);
                    }
                }
                
                if(y < this.height - 1) {
                    final int connectionVertex = getVertex(width, x, y + 1);
                    if(this.values[connectionVertex]) {
                        connections.add(connectionVertex);
                    }
                }
                
                if(x > 0) {
                    final int connectionVertex = getVertex(width, x - 1, y);
                    if(this.values[connectionVertex]) {
                        connections.add(connectionVertex);
                    }
                }
                
                if(x < this.width - 1) {
                    final int connectionVertex = getVertex(width, x + 1, y);
                    if(this.values[connectionVertex]) {
                        connections.add(connectionVertex);
                    }
                }
                
                mutableEdges.put(vertex, Collections.unmodifiableSet(connections));
            }
        }
        
        this.edges = Collections.unmodifiableMap(mutableEdges);
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
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
    
    public Map<Integer, Set<Integer>> getEdges() {
        return edges;
    }
    
    
}
