package edu.nova.chardin.patrol.engine;

import java.util.concurrent.Callable;

/**
 *
 * @author cehar
 */
public class SimulationRunner implements Callable<SimulationResult> {
    private final String identifier;
    private final Simulation simulation;
    private final long timeSteps;

    public SimulationRunner(String identifier, Simulation simulation, long timeSteps) {
        this.identifier = identifier;
        this.simulation = simulation;
        this.timeSteps = timeSteps;
    }

    @Override
    public SimulationResult call() throws Exception {
        for(long timeStep = 0; timeStep < timeSteps; timeStep++) {
            simulation.step();
        }
        
        return new SimulationResult(identifier, simulation.getTotalAttacks(), simulation.getSuccessfulAttacks());
    }
}
