package chardin.nsu.patrol;

import chardin.nsu.patrol.ant.AntProcessor;
import chardin.nsu.patrol.ant.AntStepReporter;
import chardin.nsu.patrol.ant.AntStrategy;
import chardin.nsu.patrol.ant.StopWhenMinimalChangeToAverage;
import chardin.nsu.patrol.ant.StopWhenNoProgress;
import chardin.nsu.patrol.ant.evap.AntEvapStrategy;
import chardin.nsu.patrol.ant.evap.EvapGraphDataStrategy;
import chardin.nsu.patrol.ant.evap.swarm.AntEvapSwarmStrategy;
import chardin.nsu.patrol.ant.reporter.CSVReporter;
import chardin.nsu.patrol.ant.reporter.HumanReadableReporter;
import chardin.nsu.patrol.graph.Graph;
import chardin.nsu.patrol.graph.GraphData;
import chardin.nsu.patrol.graph.GraphDataStrategy;
import chardin.nsu.patrol.graph.creator.GridGraphCreator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Chad
 */
public class SomeTest {

    private File createReportFile(String name) throws IOException {
        return File.createTempFile(String.format("report-%s", name), ".csv", new File("reports"));
    }
    
    @Test
    @Ignore
    public void testEvap() throws Exception {
        final GridGraphCreator gridGraphCreator = new GridGraphCreator();
        final File reportFile = createReportFile("evap");

        System.out.printf("Report file : %s%n", reportFile.getCanonicalPath());

        try (final CSVReporter antStepReporter = new CSVReporter(reportFile)) {
            for (int numAnts = 1; numAnts <= 100; numAnts++) {
                final Graph graph = gridGraphCreator.create(100, 100);
                final GraphDataStrategy<Double, Object> graphDataStrategy = new EvapGraphDataStrategy(0.9);
                final AntStrategy<Double, Object> antStrategy = new AntEvapStrategy(1.0);
                final GraphData<Double, Object> graphData = new GraphData<>(graph);
                final SortedSet<Integer> locations = new TreeSet<>();
                final Predicate<GraphData<Double, Object>> stopPredicate = new StopWhenMinimalChangeToAverage();
                final AntProcessor<Double, Object> antProcessor;

                for (int i = 0; i < numAnts; i++) {
                    locations.add(i);
                }

                antProcessor = new AntProcessor<>(antStrategy, graphDataStrategy, graphData, locations, stopPredicate, antStepReporter);

                antProcessor.run();
            }

        }
    }

    @Test
    @Ignore
    public void testEvap11Ants() throws Exception {
        final GridGraphCreator gridGraphCreator = new GridGraphCreator();
        final Graph graph = gridGraphCreator.create(100, 100);
        final GraphData<Double, Object> graphData = new GraphData<>(graph);
        final GraphDataStrategy<Double, Object> graphDataStrategy = new EvapGraphDataStrategy(0.9);
        final AntStrategy<Double, Object> antStrategy = new AntEvapStrategy(1.0);
        final Predicate<GraphData<Double, Object>> stopPredicate = new StopWhenMinimalChangeToAverage();
        final File reportFile = createReportFile("evap-11-ants");
        final SortedSet<Integer> locations = new TreeSet<>();
        
        for(int i = 0; i < 11; i++) {
            locations.add(i);
        }
        
        System.out.printf("Report file : %s%n", reportFile.getCanonicalPath());
        
        try(final CSVReporter antStepReporter = new CSVReporter(reportFile)) {
                
            final AntProcessor<Double, Object> antProcessor;
            
            antProcessor = new AntProcessor<>(antStrategy, graphDataStrategy, graphData, locations, stopPredicate, antStepReporter);
        
            antProcessor.run();
            
        }
    }
    
    @Test
    @Ignore
    public void testEvap11AntsNeverStop() throws Exception {
        final GridGraphCreator gridGraphCreator = new GridGraphCreator();
        final Graph graph = gridGraphCreator.create(100, 100);
        final GraphData<Double, Object> graphData = new GraphData<>(graph);
        final GraphDataStrategy<Double, Object> graphDataStrategy = new EvapGraphDataStrategy(0.9);
        final AntStrategy<Double, Object> antStrategy = new AntEvapStrategy(1.0);
        final Predicate<GraphData<Double, Object>> stopPredicate = x -> false;
        final File reportFile = createReportFile("evap-11-ants-never-stop");
        final SortedSet<Integer> locations = new TreeSet<>();
        
        for(int i = 0; i < 11; i++) {
            locations.add(i);
        }
        
        System.out.printf("Report file : %s%n", reportFile.getCanonicalPath());
        
        try(final CSVReporter antStepReporter = new CSVReporter(reportFile)) {
                
            final AntProcessor<Double, Object> antProcessor;
            
            antProcessor = new AntProcessor<>(antStrategy, graphDataStrategy, graphData, locations, stopPredicate, antStepReporter);
        
            antProcessor.run();
            
        }
    }
    
//    @Test
//    public void testSwarm() throws Exception {
//        final GridGraphCreator gridGraphCreator = new GridGraphCreator();
//        final Graph<Integer> graph = gridGraphCreator.create(10, 10);
//        final GraphData<Integer, Double, Object> graphData = new GraphData<>(graph);
//        final GraphDataStrategy<Integer, Double, Object> graphDataStrategy = new EvapGraphDataStrategy(0.9);
//        final AntStrategy<Integer, Double, Object> antStrategy = new AntEvapSwarmStrategy(1.0);
//        final Predicate<GraphData<Integer, Double, Object>> stopPredicate = new StopWhenMinimalChangeToAverage();
//        final File reportFile = createReportFile("swarm");
//        final SortedSet<Integer> locations = new TreeSet<>(Collections.singleton(0));
//        System.out.printf("Report file : %s%n", reportFile.getCanonicalPath());
//        
//        try(final CSVReporter<Integer> antStepReporter = new CSVReporter(reportFile)) {
//            final AntProcessor<Integer, Double, Object> antProcessor;
//            
//            antProcessor = new AntProcessor<>(antStrategy, graphDataStrategy, graphData, locations, stopPredicate, antStepReporter);
//        
//            antProcessor.run();
//        }
//    }
}
