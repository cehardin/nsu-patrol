package chardin.nsu.patrol.agents;

import chardin.nsu.patrol.Agent;
import chardin.nsu.patrol.Land;
import chardin.nsu.patrol.Location;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Chad
 */
public abstract class AbstractAgent<T> implements Agent<T> {

    @Override
    public final Location process(Land<T> land, Set<Location> agentLocations, Location location) {
        final Set<Location> possibleLocations = new HashSet<>();
        final Location newLocation;

        if (location.getX() == 0) {
            possibleLocations.add(new Location(location.getX() + 1, location.getY()));
        } 
        else if (location.getX() == land.getWidth() - 1) {
            possibleLocations.add(new Location(location.getX() - 1, location.getY()));
        } 
        else {
            possibleLocations.add(new Location(location.getX() - 1, location.getY()));
            possibleLocations.add(new Location(location.getX() + 1, location.getY()));
        }

        if (location.getY() == 0) {
            possibleLocations.add(new Location(location.getX(), location.getY() + 1));
        } 
        else if (location.getY() == land.getHeight() - 1) {
            possibleLocations.add(new Location(location.getX(), location.getY() - 1));
        } 
        else {
            possibleLocations.add(new Location(location.getX(), location.getY() - 1));
            possibleLocations.add(new Location(location.getX(), location.getY() + 1));
        }

        possibleLocations.removeAll(agentLocations);

        newLocation = processPossible(land, possibleLocations, location);

        return newLocation;

    }

    protected abstract Location processPossible(Land<T> land, Set<Location> possibleLocations, Location currentLocation);

}
