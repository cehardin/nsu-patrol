package chardin.nsu.patrol.reporting;

import chardin.nsu.patrol.Land;
import chardin.nsu.patrol.Location;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Chad
 */
public class Report<T, L extends Land<T>> {
    private final int step;
    private final L land;
    private final Collection<Location> locations;

    public Report(int step, L land, Collection<Location> locations) {
        this.step = step;
        this.land = land;
        this.locations = locations;
    }

    public int getStep() {
        return step;
    }

    public L getLand() {
        return land;
    }

    public Collection<Location> getLocations() {
        return locations;
    }
}
