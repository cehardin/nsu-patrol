package chardin.nsu.patrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 *
 * @author Chad
 */
public class Land<T> implements Cloneable {

    private final int width, height;
    private final List<T> values;
    private final Set<Location> blocked;

    public Land(int width, int height, Collection<Location> blocked, T defaultValue) {
        this(width, height, blocked);
        
        for (int element = 0; element < width * height; element++) {
            values.add(defaultValue);
        }
    }
    
    private Land(int width, int height, Collection<Location> blocked) {
        this.width = width;
        this.height = height;
        this.values = new ArrayList<>(width * height);
        this.blocked = new HashSet<>(blocked);
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final boolean isNavigable(Location location) {
        return !blocked.contains(location);
    }

    private int offset(Location location) {
        return width * location.getY() + location.getX();
    }

    public final T getValue(Location location) {
        return values.get(offset(location));
    }

    public final void setValue(Location location, T value) {
        values.set(offset(location), value);
    }
    
    public final T changeValue(Location location, UnaryOperator<T> operator) {
        final T currentValue = getValue(location);
        final T newValue = operator.apply(currentValue);
        
        setValue(location, newValue);
        return newValue;
    }
    
    public final void changeValues(UnaryOperator<T> operator) {
        values.replaceAll(operator);
    }
    
    public final Stream<T> getValues() {
        return values.stream();
    }
}
