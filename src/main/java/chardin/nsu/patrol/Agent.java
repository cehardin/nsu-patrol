package chardin.nsu.patrol;

import java.util.Set;

/**
 *
 * @author Chad
 */
public interface Agent<T> {
    Location process(Land<T> land, Set<Location> agentLocations, Location currentLocation);
}
