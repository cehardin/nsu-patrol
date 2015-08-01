package chardin.nsu.patrol.reporting;

import chardin.nsu.patrol.Land;

/**
 *
 * @author Chad
 */
public interface Reporter<T> {
    void report(Report<T> report);
}
