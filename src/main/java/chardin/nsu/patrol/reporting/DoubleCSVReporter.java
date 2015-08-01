package chardin.nsu.patrol.reporting;

import java.io.PrintStream;

/**
 *
 * @author Chad
 */
public class DoubleCSVReporter implements Reporter<Double> {
    private final PrintStream printStream;

    public DoubleCSVReporter(PrintStream printStream) {
        this.printStream = printStream;
        this.printStream.println("step,min,max,avg,sum");
    }
    
    
    
    @Override
    public void report(Report<Double> report) {
        
        printStream.printf("%d,%f,%f,%f,%f%n", 
                report.getStep(),
                report.getMin(),
                report.getMax(),
                report.getAvg(),
                report.getSum());
    }
    
}
