package chardin.nsu.patrol;

import chardin.nsu.patrol.ant.AntProcessor;
import chardin.nsu.patrol.ant.AntStepReporter;
import chardin.nsu.patrol.ant.AntStrategy;
import chardin.nsu.patrol.ant.evap.AntEvapStrategy;
import chardin.nsu.patrol.ant.evap.EvapGraphDataStrategy;
import chardin.nsu.patrol.ant.reporter.HumanReadableReporter;
import chardin.nsu.patrol.graph.Graph;
import chardin.nsu.patrol.graph.GraphData;
import chardin.nsu.patrol.graph.GraphDataStrategy;
import chardin.nsu.patrol.graph.creator.GridGraphCreator;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import org.junit.Test;

/**
 *
 * @author Chad
 */
public class SomeTest {
    
    @Test
    public void someTest1() {
        final GridGraphCreator gridGraphCreator = new GridGraphCreator();
        final Graph<Integer> graph = gridGraphCreator.create(10, 10);
        final GraphData<Integer, Double, Object> graphData = new GraphData<>(graph);
        final GraphDataStrategy<Integer, Double, Object> graphDataStrategy = new EvapGraphDataStrategy(0.1);
        final AntStepReporter antStepReporter = new HumanReadableReporter(System.out);
        final AntStrategy<Integer, Double, Object> antStrategy = new AntEvapStrategy(1.0);
        final SortedSet<Integer> locations = new TreeSet(Collections.singleton(graph.getVertices().iterator().next()));
        final Predicate<GraphData<Integer, Double, Object>> stopPredicate = x -> x.getVertexData().stream().filter(y -> y.isPresent()).count() >= 2;
        final AntProcessor<Integer, Double, Object> antProcessor = new AntProcessor<>(antStrategy, graphDataStrategy, graphData, locations, stopPredicate, antStepReporter);
        
        antProcessor.run();
    }
}
