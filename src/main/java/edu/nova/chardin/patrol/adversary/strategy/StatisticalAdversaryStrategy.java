package edu.nova.chardin.patrol.adversary.strategy;

import com.google.common.collect.ImmutableMap;
import edu.nova.chardin.patrol.adversary.AdversaryContext;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.math3.util.Pair;

public class StatisticalAdversaryStrategy implements AdversaryStrategy {

  // if there are at least 3 samples and 2 / 3 are anough of an interval of time....
  private static final double MINIMUM_SAMPLES = 3;
  private static final double MINIMUM_AVERAGE = 0.666; //close enough and more accurate
  private final AtomicBoolean wasOccupied = new AtomicBoolean();
  private int timestepsUnoccupied = 0;
  private int timestepsPreviouslyUnoccupied = 0;

  // key is a pair of previous duration unnociped with a future duration unnoccupied.
  // Value is the count of that pair occuring
  private final Map<Pair<Integer, Integer>, Integer> unoccupiedCounts = new HashMap<>();

  // main entry point for api to determine whether to attack or not
  @Override
  public boolean attack(AdversaryContext context) {
    final boolean attack;

    if (context.isOccupied()) {
      occupied();
      attack = false; //never attack if occupied
    } else {
      attack = unoccupied(context.getAttackInterval()); 
    }

    return attack;
  }

  // if newly occupied, record the interval
  // previously unnoccipied and record it
  // with an incremeting count.
  // if staying occupied, do nothing.
  private void occupied() {
    if (wasOccupied.compareAndSet(false, true)) {
        unoccupiedCounts.merge(
                Pair.create(timestepsPreviouslyUnoccupied, timestepsUnoccupied),
                1,
                Integer::sum);
      
      timestepsPreviouslyUnoccupied = timestepsUnoccupied;
    }
  }

  // if newly unoccupied, update stats and 
  // decide wether to attack or not.
  // When staying unoccupied, keep track of how many
  // timesteps.
  private boolean unoccupied(final int attackInterval) {
    final boolean attack;

    //if newly unoccupied...
    if (wasOccupied.compareAndSet(true, false)) {
      attack = decide(attackInterval);
      timestepsUnoccupied = 0;
    } else {
      timestepsUnoccupied++; //keep track for recording when occupied again
      attack = false;
    }

    return attack;
  }

  //creates a list of 1s and 0s where 1 indicates 
  //that the interval was at least as long as the attack interval
  //and 0 otherwise.
  //Then, the mean average is taken of those 1s and 0s.
  private boolean decide(final int attackInterval) {
    final boolean attack;
    
    //create a submap for the previous unoccupied interval
    final ImmutableMap<Integer, Integer> nextUnoccupiedCounts = unoccupiedCounts.entrySet().stream()
            .filter(e -> e.getKey().getFirst() == timestepsPreviouslyUnoccupied)
            .collect(
                    ImmutableMap.toImmutableMap(
                            e -> e.getKey().getSecond(),
                            e -> e.getValue()));
    //create the list of 1s and 0s
    final DoubleSummaryStatistics summaryStatistics = nextUnoccupiedCounts.entrySet().stream()
            .map(e -> e.getKey() < attackInterval ? new SimpleImmutableEntry<>(e.getValue(), 0.0) : new SimpleImmutableEntry<>(e.getValue(), 1.0))
            .flatMap(e -> Collections.nCopies(e.getKey(), e.getValue()).stream())
            .mapToDouble(Double::doubleValue)
            .sorted() //supposedly this gives a more accurate average
            .summaryStatistics();
    final double numSamples = summaryStatistics.getCount();

    // only consider attacking if we have enough samples
    if (numSamples >= MINIMUM_SAMPLES) {
      final double average = summaryStatistics.getAverage();

      //did something bad happen?
      if (average == Double.NaN) {
        throw new IllegalStateException(String.format("Found NaN in summary stats : %s", summaryStatistics));
      }

      //attack if we hit the threshold
      attack = average >= MINIMUM_AVERAGE;
    } else {
      attack = false;
    }

    return attack;
  }
}
