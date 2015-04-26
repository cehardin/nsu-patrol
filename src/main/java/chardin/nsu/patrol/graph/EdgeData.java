package chardin.nsu.patrol.graph;

import java.util.Optional;


/**
 *
 * @author Chad
 * @param <T>
 */
public abstract class EdgeData<T> extends UnitData<T> {

    protected EdgeData(final Optional<T> object) {
        super(object);
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public abstract EdgeData<T> clone();
}
