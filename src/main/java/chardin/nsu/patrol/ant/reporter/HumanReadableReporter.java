/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chardin.nsu.patrol.ant.reporter;

import java.io.Console;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author Chad
 */
public class HumanReadableReporter extends AbstractBasicReporter implements AutoCloseable, Flushable {
    private final PrintStream printStream;

    public HumanReadableReporter(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    protected void report(BasicReport basicReport) {
        printStream.printf("START STEP %d%n", basicReport.step);
        printStream.printf("  Num Ants               : %d%n", basicReport.numAnts);
        printStream.printf("  CumulativeCost         : %d%n", basicReport.cumulativeCost);
        printStream.printf("  Num Vertices           : %d%n", basicReport.numVertices);
        printStream.printf("  Num Visited Vertices   : %d%n", basicReport.visitedVertices);
        printStream.printf("  Num Unvisited Vertices : %d%n", basicReport.unvisitedVertices);
        printStream.printf("  Average                : %f%n", basicReport.average);
        printStream.printf("  Minimum                : %f%n", basicReport.min);
        printStream.printf("  Maximum                : %f%n", basicReport.max);
        printStream.printf("END STEP  %d%n%n", basicReport.step);
        printStream.flush();
    }

    @Override
    public void close() throws Exception {
        printStream.close();
    }
    
    @Override
    public void flush() throws IOException {
        printStream.flush();
    }
}
