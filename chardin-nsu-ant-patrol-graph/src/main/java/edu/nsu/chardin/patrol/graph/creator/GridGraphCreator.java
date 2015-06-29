package edu.nsu.chardin.patrol.graph.creator;

import edu.nsu.chardin.patrol.graph.Graph;

/**
 *
 * @author Chad
 */
public class GridGraphCreator {

    public Graph create(final int width, final int height) {
        final boolean[] values = new boolean[width * height];
        
        for(int i=0; i < values.length; i++) {
            values[i] = true;
        }
        
        return new Graph(values, width);
    }
}
