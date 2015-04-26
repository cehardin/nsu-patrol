package chardin.nsu.patrol.graph;

import java.util.Optional;

/**
 *
 * @author Chad
 * @param <T>
 */
public final class SimpleVertexData<T> extends VertexData<T> {

    public SimpleVertexData(final Optional<T> object) {
        super(object);
    }

    @Override
    public SimpleVertexData<T> clone() {
        return new SimpleVertexData<>(get());
    }
}
