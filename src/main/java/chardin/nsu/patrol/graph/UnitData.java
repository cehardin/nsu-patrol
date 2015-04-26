package chardin.nsu.patrol.graph;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Chad
 * @param <T>
 */
public abstract class UnitData<T> implements Cloneable {
    private T object;
    
    protected UnitData(final Optional<T> object) {
        this.object = object.orElse(null);
    }
    
    /**
     * 
     * @param o
     * @return 
     */
    private Optional<T> swap(T o) {
        final Optional<T> previous = Optional.ofNullable(object);
        object = o;
        return previous;
    }
    
    /**
     * 
     * @return 
     */
    public final Optional<T> get() {
        return Optional.ofNullable(object);
    }
    
    /**
     * 
     * @param o
     * @return 
     */
    public final Optional<T> set(T o) {
        return swap(Objects.requireNonNull(o));
    }
            
    
    /**
     * 
     * @return 
     */
    public final Optional<T> clear() {
        return swap(null);
    }
    
    /**
     * 
     * @return 
     */
    public abstract UnitData<T> clone();
}
