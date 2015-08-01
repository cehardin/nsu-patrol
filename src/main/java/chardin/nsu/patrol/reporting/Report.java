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
public class Report<T> {
    private final int step;
    private final T min, max, avg, sum;
    private final Collection<Location> locations;

    public Report(int step, T min, T max, T avg, T sum, Collection<Location> locations) {
        this.step = step;
        this.min = min;
        this.max = max;
        this.avg = avg;
        this.sum = sum;
        this.locations = locations;
    }

    public int getStep() {
        return step;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public T getAvg() {
        return avg;
    }

    public T getSum() {
        return sum;
    }

    public Collection<Location> getLocations() {
        return locations;
    }
}
