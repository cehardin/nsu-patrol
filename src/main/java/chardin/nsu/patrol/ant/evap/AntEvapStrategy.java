package chardin.nsu.patrol.ant.evap;

import chardin.nsu.patrol.ant.AntStrategy;
import chardin.nsu.patrol.graph.GraphData;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author Chad
 */
public class AntEvapStrategy implements AntStrategy<Object, Double, Object> {

    @Override
    public Object calculate(Object currentVertex, Set<Object> occupiedVertices, GraphData<Object, Double, Object> graphData) {
        final TreeMap<Double, SortedSet<Object>> data = new TreeMap<>();
        
        for(final Object vertex : occupiedVertices) {
            if(!currentVertex.equals(vertex)) {
                final Double value = graphData.getVertexData(vertex).orElse(0.0);
                    
                data.put(value, new TreeSet<>());
                data.get(value).add(vertex);
                
            }
        }
        
        if(data.isEmpty()) {
            return currentVertex;
        }
        else {
            return data.firstEntry().getValue().first();
        }
    }
    
}
