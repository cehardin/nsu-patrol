package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class StatisticalAdversaryStrategy implements AdversaryStrategy {

  private final AtomicBoolean wasOccupied = new AtomicBoolean();
  private final List<Integer> unoccupiedSamples = new ArrayList<>();
  private int timestepsUnoccupied = 0;

  // main entry point for api to determine whether to attack or not
  @Override
  public boolean attack(int attackInterval, long timestep, boolean occupied) {
    final boolean attack;

    if (occupied) {
      occupied();
      attack = false; //never attack if occupied
    } else {
      attack = unoccupied(attackInterval);
    }

    return attack;
  }

  // if newly occupied, record the interval
  // previously unnoccipied and record it
  // with an incremeting count.
  // if staying occupied, do nothing.
  private void occupied() {
    wasOccupied.set(true);
    unoccupiedSamples.add(timestepsUnoccupied);
    timestepsUnoccupied = 0;
  }

  // if newly unoccupied, update stats and 
  // decide wether to attack or not.
  // When staying unoccupied, keep track of how many
  // timesteps.
  private boolean unoccupied(final int attackInterval) {

    final boolean attack;

    timestepsUnoccupied++;

    //if newly unoccupied...
    if (wasOccupied.compareAndSet(true, false)) {
      attack = decide(attackInterval);
    } else {
      attack = false;
    }

    return attack;
  }

  private boolean decide(final int attackInterval) {
    final boolean attack;

    if (unoccupiedSamples.size() < 2) {
      attack = false;
    } else {
      final int lastUnoccupied = unoccupiedSamples.get(unoccupiedSamples.size() - 1);
      final Iterator<Integer> samples = unoccupiedSamples.iterator();
      Integer pre = samples.next();
      int totalCount = 0;
      int overAttackIntervalCount = 0;
      final double ratio;

      while (samples.hasNext()) {
        final Integer post = samples.next();

        if (pre == lastUnoccupied) {
          if (post >= attackInterval) {
            overAttackIntervalCount++;
          }
          totalCount++;
        }
        pre = post;
      }
      
      ratio = (double) overAttackIntervalCount / (double) totalCount;
      
      attack = ratio >= 0.90;
    }

    return attack;
  }
}
