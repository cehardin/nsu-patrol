package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.util.DoubleArray;
import org.apache.commons.math3.util.ResizableDoubleArray;

/**
 *
 * @author cehar
 */
public class StatisticalAdversaryStrategy implements AdversaryStrategy {

    private final SortedMap<Integer, Integer> occupiedTimeStepsWithDuration = new TreeMap<>();
    private final int minimumObservations;
    private int timeStep = 0;

    public StatisticalAdversaryStrategy(final int minimumObservations) {
        this.minimumObservations = minimumObservations;
    }

    @Override
    public boolean attack(final int k, final boolean occupied) {
        final boolean attack;

        if (occupied) {
            if (occupiedTimeStepsWithDuration.isEmpty()) {
                occupiedTimeStepsWithDuration.put(timeStep, 0);
            } else {
                final int lastTimeStep = occupiedTimeStepsWithDuration.lastKey();
                final int duration = timeStep - lastTimeStep;

                occupiedTimeStepsWithDuration.put(timeStep, duration);
            }
            attack = false;
        } else {
            final Iterator<Integer> durations = occupiedTimeStepsWithDuration.values().iterator();
            final DoubleArray data1 = new ResizableDoubleArray();
            final DoubleArray data2 = new ResizableDoubleArray();
            double lastDuration;
            
            if(durations.hasNext()) {
                lastDuration = durations.next();
                
                if(durations.hasNext()) {
                    final PearsonsCorrelation correlation;
                    
                    data1.addElement(lastDuration);
                    lastDuration = durations.next();
                    data2.addElement(lastDuration);
                    
                    while(durations.hasNext()) {
                        data1.addElement(lastDuration);
                        lastDuration = durations.next();
                        data2.addElement(lastDuration);
                    }
                    
                    correlation = new PearsonsCorrelation(new double[][] {data1.getElements(), data2.getElements()});
                    
                    correlation.correlation(xArray, yArray)
                }
                    
                }
                else {
                    attack = false;
                }
            }
            else {
                attack = false;
            }
        }

        timeStep++;

        return attack;
    }

}
