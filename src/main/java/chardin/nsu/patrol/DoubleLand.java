package chardin.nsu.patrol;

import java.util.Collection;
import java.util.stream.DoubleStream;

/**
 *
 * @author Chad
 */
public class DoubleLand extends ComparableLand<Double> {

    public DoubleLand(int width, int height, Collection<Location> blocked, Double defaultValue) {
        super(width, height, blocked, defaultValue, (d1,d2) -> d1.compareTo(d2));
    }
    
    public final DoubleStream getDoubleValues() {
        return getValues().mapToDouble(d -> d);
    }
    
    public final Double average() {
        return getDoubleValues().average().getAsDouble();
    }
    
    public final Double sum() {
        return getDoubleValues().sum();
    }
}
