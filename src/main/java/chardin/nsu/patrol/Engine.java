package chardin.nsu.patrol;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Chad
 */
public class Engine<T, L extends Land<T>> {
    private final L land;
    private final Map<Agent<T>, Location> agentLocations;

    public Engine(L land, Map<Agent<T>, Location> agentLocations) {
        this.land = land;
        this.agentLocations = agentLocations;
    }
    
    public void step() {
        
        for(final Map.Entry<Agent<T>, Location> agentEntry : agentLocations.entrySet()) {
            final Agent<T> agent = agentEntry.getKey();
            final Location startingLocation = agentEntry.getValue();
            final Set<Location> allAgentLocations = new HashSet<>(agentLocations.values());
            final Location endingLocation = agent.process(land, allAgentLocations, startingLocation);
            
            if(allAgentLocations.contains(endingLocation)) {
                throw new IllegalStateException();
            }
            
            agentEntry.setValue(endingLocation);
        }
    }
    
    public L getLand() {
        return land;
    }
    
    public Map<Agent<T>, Location> getAgentLocations() {
        return agentLocations;
    }
}
