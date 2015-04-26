package chardin.nsu.patrol.graph;

import java.util.Optional;


/**
 *
 * @author Chad
 * @param <T>
 */
public abstract class VertexData<T> extends UnitData<T> {

    protected VertexData(final Optional<T> object) {
        super(object);
    }
    
    
    
    /**
     * 
     * @return 
     */
    @Override
    public abstract VertexData<T> clone();
}
