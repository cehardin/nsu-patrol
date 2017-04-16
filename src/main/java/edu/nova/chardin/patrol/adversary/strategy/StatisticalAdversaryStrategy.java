package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import org.apache.commons.math3.util.DoubleArray;
import org.apache.commons.math3.util.ResizableDoubleArray;

/**
 * The statistical adversary strategy.
 */
public final class StatisticalAdversaryStrategy implements AdversaryStrategy {

  private final DoubleArray unoccupiedDurations = new ResizableDoubleArray(1024);
  private final DoubleArray occuupiedDurations = new ResizableDoubleArray(1024);
  private final double minimumError;
  private boolean wasOccupied = false;
  private int timestepStart = 0;

  public StatisticalAdversaryStrategy(final int minimumError) {
    this.minimumError = minimumError;
  }

  @Override
  public boolean attack(final int k, final boolean occupied, final int timestep) {

    if (wasOccupied) {
      if (!occupied) {
        occuupiedDurations.addElement(timestep - timestepStart);
        timestepStart = timestep;
      }
    } else {
      if (occupied) {
        unoccupiedDurations.addElement(timestep - timestepStart);
        timestepStart = timestep;
      }
    }

    wasOccupied = occupied;

//    if (!occupied) {
//      final long duration = timestep - timestepStart;
//      final PearsonsCorrelation correlation = new PearsonsCorrelation();
//      final int size = Math.min(unoccupiedDurations.getNumElements(), occuupiedDurations.getNumElements());
//      final double[] x = Arrays.copyOf(unoccupiedDurations.getElements(), size);
//      final double[] y = Arrays.copyOf(occuupiedDurations.getElements(), size);
//      final double c = correlation.correlation(y, y);
//
//      if (c > k) {
//        final double error = correlation.getCorrelationStandardErrors().getEntry(index, index);
//
//        if (Math.abs(error) < minimumError) {
//          return true;
//        }
//      }
//    }
//  }
//}
//}
//    }

    return false;
  }

}
