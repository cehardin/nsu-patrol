package chardin.nsu.patrol.agents.evap;

import chardin.nsu.patrol.Land;
import chardin.nsu.patrol.Location;
import chardin.nsu.patrol.agents.AbstractAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Chad
 */
public class EvapAgent extends AbstractAgent<Double> {

    private final Random random;
    private final double delta;

    public EvapAgent(Random random, double delta) {
        this.random = random;
        this.delta = delta;
    }

    @Override
    protected Location processPossible(Land<Double> land, Set<Location> possibleLocations, Location currentLocation) {
        final Location chosenLocation;

        if (possibleLocations.isEmpty()) {
            chosenLocation = currentLocation;
        } 
        else {
            final SortedMap<Double, List<Location>> sortedPossibleLocations = new TreeMap<>();
            final List<Location> bestPossibleLocations;
            final int chosenLocationIndex;

            for (final Location possibleLocation : possibleLocations) {
                final Double value = land.getValue(possibleLocation);

                sortedPossibleLocations.putIfAbsent(value, new ArrayList<>(1));
                sortedPossibleLocations.get(value).add(possibleLocation);
            }

            bestPossibleLocations = sortedPossibleLocations.get(sortedPossibleLocations.firstKey());

            if (bestPossibleLocations.size() == 1) {
                chosenLocationIndex = 0;
            } else {
                chosenLocationIndex = random.nextInt(bestPossibleLocations.size());
            }

            chosenLocation = bestPossibleLocations.get(chosenLocationIndex);
        }

        land.changeValue(chosenLocation, v -> v + delta);

        return chosenLocation;

    }

}
