package chardin.nsu.patrol.ant.reporter;

import chardin.nsu.patrol.ant.AntStepReporter;
import chardin.nsu.patrol.graph.GraphData;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.SortedSet;

/**
 *
 * @author Chad
 */
public class CSVReporter extends AbstractBasicReporter implements AutoCloseable {
    private final PrintStream printStream;
    
    public CSVReporter(File file) throws FileNotFoundException {
        this(new PrintStream(file));
    }
    
    public CSVReporter(PrintStream printStream) {
        this.printStream = printStream;
        printStream.println("step,numAnts,cost,cumulativeCost,avg,min,max");
    }

    @Override
    protected void report(BasicReport basicReport) {
//        printStream.printf("%d,%d,%d,%d%d,%d,%d%n", step, numAnts, cost, cumulativeCost, avg, min, max);
    }

    @Override
    public void close() throws Exception {
        printStream.close();
    }
    
    
}
