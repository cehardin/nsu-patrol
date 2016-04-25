package edu.nova.chardin.patrol.engine;

/**
 *
 * @author cehar
 */
public class SimulationResult {
    private final String identifier;
    private final long totalAttacks;
    private final long succesfulAttacks;

    public SimulationResult(String identifier, long totalAttacks, long succesfulAttacks) {
        this.identifier = identifier;
        this.totalAttacks = totalAttacks;
        this.succesfulAttacks = succesfulAttacks;
    }

    public String getIdentifier() {
        return identifier;
    }

    public long getTotalAttacks() {
        return totalAttacks;
    }

    public long getSuccesfulAttacks() {
        return succesfulAttacks;
    }
    
    
}
