package chardin.nsu.patrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Chad
 */
public final class Land<T> implements Cloneable {

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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isNavigable(Location location) {
        return !blocked.contains(location);
    }

    private int offset(Location location) {
        return width * location.getY() + location.getX();
    }

    public T getValue(Location location) {
        return values.get(offset(location));
    }

    public void setValue(Location location, T value) {
        values.set(offset(location), value);
    }

    @Override
    public Land<T> clone() {
        final Land<T> clone = new Land<>(width, height, blocked);

        clone.values.clear();
        clone.values.addAll(values);

        return clone;
    }

    @Override
    public String toString() {
        return "Land{" + "width=" + width + ", height=" + height + ", values=" + values + ", blocked=" + blocked + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.width;
        hash = 23 * hash + this.height;
        hash = 23 * hash + Objects.hashCode(this.values);
        hash = 23 * hash + Objects.hashCode(this.blocked);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Land<?> other = (Land<?>) obj;
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (!Objects.equals(this.values, other.values)) {
            return false;
        }
        return true;
    }

    

}
