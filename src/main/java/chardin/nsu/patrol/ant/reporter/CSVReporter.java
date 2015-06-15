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
public class CSVReporter<T> extends AbstractBasicReporter<T> implements AutoCloseable {
    private final PrintStream printStream;
    
    public CSVReporter(File file) throws FileNotFoundException {
        this(new PrintStream(file));
    }
    
    public CSVReporter(PrintStream printStream) {
        this.printStream = printStream;
        printStream.println("numAnts,step,numVerticesVisisted,cost,cumulativeCost,avg,min,max");
    }

    @Override
    protected void report(BasicReport basicReport) {
        final double verticesVisited = 100.0 * (double)basicReport.visitedVertices / (double)basicReport.numVertices;
        printStream.printf("%d,%d,%f,%d,%d,%f,%f,%f%n", 
                basicReport.numAnts, 
                basicReport.step, 
                verticesVisited, 
                basicReport.cost, 
                basicReport.cumulativeCost, 
                basicReport.average, 
                basicReport.min, 
                basicReport.max);
    }

    @Override
    public void close() throws Exception {
        printStream.close();
    }
    
    
}
