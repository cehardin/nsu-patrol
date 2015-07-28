package chardin.nsu.patrol;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Chad
 */
public class Engine<T> {
    private final Land<T> land;
    private final Map<Agent<T>, Location> agentLocations;

    public Engine(Land<T> land, Map<Agent<T>, Location> agentLocations) {
        this.land = land;
        this.agentLocations = agentLocations;
    }
    
    public void step() {
        
        for(final Map.Entry<Agent<T>, Location> agentEntry : agentLocations.entrySet()) {
            final Agent<T> agent = agentEntry.getKey();
            final Location startingLocation = agentEntry.getValue();
            final Set<Location> startingAgentLocations = new HashSet<>(agentLocations.values());
            final Location endingLocation = agent.process(land, startingAgentLocations, startingLocation);
            
            if(startingAgentLocations.contains(endingLocation)) {
                throw new IllegalStateException();
            }
            
            agentEntry.setValue(endingLocation);
        }
    }
}
