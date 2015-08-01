package chardin.nsu.patrol.reporting;

import chardin.nsu.patrol.Land;

/**
 *
 * @author Chad
 */
public interface Reporter<T, L extends Land<T>> {
    void report(Report<T,L> report);
}
