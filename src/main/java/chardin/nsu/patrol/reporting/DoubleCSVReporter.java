package chardin.nsu.patrol.reporting;

import chardin.nsu.patrol.DoubleLand;
import chardin.nsu.patrol.Land;
import chardin.nsu.patrol.Location;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Chad
 */
public class DoubleCSVReporter implements Reporter<Double, DoubleLand> {
    private final PrintStream printStream;

    public DoubleCSVReporter(PrintStream printStream) {
        this.printStream = printStream;
        this.printStream.println("step,width,height,numAgents,numValues,min,max,avg,sum");
    }
    
    
    
    @Override
    public void report(Report<Double, DoubleLand> report) {
        final int step = report.getStep();
        final Collection<Location> locations = report.getLocations();
        final int numAgents = locations.size();
        final DoubleLand land = report.getLand();
        final int width = land.getWidth();
        final int height = land.getHeight();
        final long numValues = land.getValues().count();
        final double min = land.getMinValue();
        final double max = land.getMaxValue();
        final double avg = land.average();
        final double sum = land.sum();
        
        printStream.printf("%d,%d,%d,%d,%d,%f,%f,%f,%f%n", step,width,height,numAgents,numValues,min,max,avg,sum);
    }
    
}
