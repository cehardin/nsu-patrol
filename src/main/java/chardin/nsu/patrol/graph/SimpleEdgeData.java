package chardin.nsu.patrol.graph;

import java.util.Optional;

/**
 *
 * @author Chad
 */
public final class SimpleEdgeData<T> extends EdgeData<T> {

    public SimpleEdgeData(Optional<T> object) {
        super(object);
    }

    @Override
    public SimpleEdgeData<T> clone() {
        return new SimpleEdgeData<>(get());
    }
}
