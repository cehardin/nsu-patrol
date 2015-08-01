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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Chad
 */
public class EvapTool {
    private final Engine<Double, DoubleLand> engine;
    
    public EvapTool(int width, int height, int numAgents) {
        final DoubleLand land = new DoubleLand(width, height, Collections.emptyList(), 0.0);
        final Map<EvapAgent, Location> agents = new HashMap<>();
        final Random random = new Random();
        
        for(int n = 0; n < numAgents; n++) {
            final EvapAgent agent = new EvapAgent(random, 1);
            Location location;
            
            do {
                location = new Location(random.nextInt(width), random.nextInt(height));
            } while(agents.values().contains(location));
            
            agents.put(agent, location);
        }
        engine = new Engine(land, agents);
    }
    
    public void run() throws IOException {
        final File file = Files.createTempFile("evap", ".csv").toFile();
        try(FileOutputStream fos = new FileOutputStream(file); PrintStream ps = new PrintStream(fos)) {
            final DoubleCSVReporter reporter = new DoubleCSVReporter(ps);
            
            System.out.printf("File is at %s%n", file.getCanonicalPath());
            
            for(int n=0; n < 1000; n++) {
                engine.getLand().changeValues(v -> v * 0.99);
                engine.step();
                reporter.report(new Report<>(n, engine.getLand(), engine.getAgentLocations().values()));
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        final EvapTool tool = new EvapTool(10, 10, 10);
        
        tool.run();
    }
}
