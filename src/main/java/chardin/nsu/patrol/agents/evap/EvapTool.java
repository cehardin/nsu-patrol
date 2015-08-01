package chardin.nsu.patrol.agents.evap;

import chardin.nsu.patrol.DoubleLand;
import chardin.nsu.patrol.Engine;
import chardin.nsu.patrol.Land;
import chardin.nsu.patrol.Location;
import chardin.nsu.patrol.reporting.DoubleCSVReporter;
import chardin.nsu.patrol.reporting.Report;
import chardin.nsu.patrol.reporting.Reporter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.StreamSupport;

/**
 *
 * @author Chad
 */
public class EvapTool {
    final File file;
    private final List<Engine<Double, DoubleLand>> engines = new ArrayList<>();

    public EvapTool(int width, int height, int numAgents, int numRuns) throws IOException {

        for (int run = 0; run < numRuns; run++) {
            final DoubleLand land = new DoubleLand(width, height, Collections.emptyList(), 0.0);
            final Map<EvapAgent, Location> agents = new HashMap<>();
            final Random random = new Random();

            for (int n = 0; n < numAgents; n++) {
                final EvapAgent agent = new EvapAgent(random, 1);
                Location location;

                do {
                    location = new Location(random.nextInt(width), random.nextInt(height));
                } while (agents.values().contains(location));

                agents.put(agent, location);
            }
            engines.add(new Engine(land, agents));
        }

        file = Files.createTempFile(String.format("evap-%d-%d-%d-%d--", width, height, numAgents, numRuns), ".csv").toFile();
        
        System.out.printf("File is at %s%n", file.getCanonicalPath());
    }

    public void run() throws IOException {
        final int numSteps = 1000;
        
        final SortedMap<Integer, List<Report<Double>>> runs = new TreeMap<>();
        final SortedMap<Integer, Report<Double>> reducedRuns = new TreeMap<>();
        
        //set up the map with empty lists
        for(int step = 1; step <= numSteps; step++) {
            runs.put(step, new ArrayList<>(engines.size()));
        }
        
        //execute each engine
        for(final Engine<Double, DoubleLand> engine : engines) {
            final DoubleLand land = engine.getLand();
            
            for (int step = 1; step <= numSteps; step++) {
                
                land.changeValues(v -> v * 0.99);
                engine.step();
                runs.get(step).add(new Report<>(step, land.getMinValue(), land.getMaxValue(), land.average(), land.sum(), engine.getAgentLocations().values()));
            }
        }
        
        //reduce the runs
        for(final Map.Entry<Integer, List<Report<Double>>> stepRuns : runs.entrySet()) {
            final int step = stepRuns.getKey();
            final List<Report<Double>> reports = stepRuns.getValue();
            final double min = reports.stream().mapToDouble(r -> r.getMin()).average().getAsDouble();
            final double max = reports.stream().mapToDouble(r -> r.getMax()).average().getAsDouble();
            final double avg = reports.stream().mapToDouble(r -> r.getAvg()).average().getAsDouble();
            final double sum = reports.stream().mapToDouble(r -> r.getSum()).average().getAsDouble();
            final Collection<Location> locations = reports.stream().map(r -> r.getLocations()).reduce((a,b) -> {
                List<Location> c = new ArrayList<>(); 
                c.addAll(a); 
                c.addAll(b); 
                return c;}).get();
            
          
            reducedRuns.put(step, new Report<>(step, min, max, avg, sum, locations));
            
        }
        try (FileOutputStream fos = new FileOutputStream(file); PrintStream ps = new PrintStream(fos)) {
            
            final DoubleCSVReporter reporter = new DoubleCSVReporter(ps);
            
            for(final Report<Double> report : reducedRuns.values()) {
                reporter.report(report);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final EvapTool tool = new EvapTool(10, 10, 1, 1000);

        tool.run();
    }
}
