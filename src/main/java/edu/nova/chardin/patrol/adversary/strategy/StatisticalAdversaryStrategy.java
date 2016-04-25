package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.DoubleArray;
import org.apache.commons.math3.util.ResizableDoubleArray;

/**
 *
 * @author cehar
 */
public class StatisticalAdversaryStrategy implements AdversaryStrategy {

    private final SortedMap<Integer, Integer> occupiedTimeStepsWithDuration = new TreeMap<>();
    private final double minimumError;
    private int timeStep = 0;

    public StatisticalAdversaryStrategy(final int minimumError) {
        this.minimumError = minimumError;
    }

    @Override
    public boolean attack(final int k, final boolean occupied) {
        final int duration;

        if (occupiedTimeStepsWithDuration.isEmpty()) {
            duration = 0;
        } else {
            duration = timeStep - occupiedTimeStepsWithDuration.lastKey() - 
        }

        timeStep++;

        if (occupied) {
            occupiedTimeStepsWithDuration.put(timeStep - 1, duration);
        } else {
            final Iterator<Integer> durations = occupiedTimeStepsWithDuration.values().iterator();
            final DoubleArray data1 = new ResizableDoubleArray();
            final DoubleArray data2 = new ResizableDoubleArray();
            double lastDuration;

            if (durations.hasNext()) {
                lastDuration = durations.next();

                if (durations.hasNext()) {
                    final PearsonsCorrelation correlation;

                    data1.addElement(lastDuration);
                    lastDuration = durations.next();
                    data2.addElement(lastDuration);

                    while (durations.hasNext()) {
                        data1.addElement(lastDuration);
                        lastDuration = durations.next();
                        data2.addElement(lastDuration);
                    }

                    correlation = new PearsonsCorrelation(new double[][]{data1.getElements(), data2.getElements()});

                    for (int index = 0; index < data1.getNumElements(); index++) {
                        final double d1 = data1.getElement(index);

                        if (duration == d1) {
                            final double d2 = data2.getElement(index);
                            
                            if (d2 > k) {
                                final double error = correlation.getCorrelationStandardErrors().getEntry(index, index);
                                
                                if(Math.abs(error) < minimumError) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

}
