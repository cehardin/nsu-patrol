package chardin.nsu.patrol;

import java.util.Collection;
import java.util.Comparator;

/**
 *
 * @author Chad
 */
public class ComparableLand<T> extends Land<T> {

    private final Comparator<T> valueComparator;
    
    public ComparableLand(int width, int height, Collection<Location> blocked, T defaultValue, Comparator<T> valueComparator) {
        super(width, height, blocked, defaultValue);
        
        this.valueComparator = valueComparator;
    }

    public final T getMaxValue() {
        return getValues().max(valueComparator).get();
    }
    
    public final T getMinValue() {
        return getValues().min(valueComparator).get();
    }
}
