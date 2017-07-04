package edu.nova.chardin.patrol.adversary.strategy;

import com.google.common.collect.Iterables;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import java.util.ArrayList;
import java.util.Iterator;

public class StatisticalAdversaryStrategy implements AdversaryStrategy {

  private final ArrayList<Integer> unoccupiedSamples = new ArrayList<>();
  private boolean decide = false;
  private int timestepsUnoccupied = 0;

  // main entry point for api to determine whether to attack or not
  @Override
  public boolean attack(int attackInterval, long timestep, boolean occupied) {
    final boolean attack;

    // if occupied, reset.
    // if just became unnocupied decide.
    // If unnocpied for at least the attack interval, reset
    if (occupied) {
      reset();
      attack = false; //never attack if occupied
    } else {
      timestepsUnoccupied++;

      if (decide) {
        attack = decide(attackInterval);
      } else {
        attack = false;

        if (timestepsUnoccupied >= attackInterval) {
          reset();
        }
      }
    }

    return attack;
  }

  private void reset() {
    unoccupiedSamples.add(timestepsUnoccupied);
    timestepsUnoccupied = 0;
    decide = true;
  }

  private boolean decide(final int attackInterval) {
    final Iterator<Integer> samples = unoccupiedSamples.iterator();
    final boolean attack;

    decide = false;

    if (samples.hasNext()) {
      int pre = samples.next();

      if (samples.hasNext()) {
        final int lastUnoccupied = Iterables.getLast(unoccupiedSamples);
        double totalCount = 0.0;
        double overAttackIntervalCount = 0.0;

        while (samples.hasNext()) {
          final int post = samples.next();

          if (pre == lastUnoccupied) {
            if (post >= attackInterval) {
              overAttackIntervalCount++;
            }
            totalCount++;
          }

          pre = post;
        }

        // need at least ten samples and 90% or more of them must be over the attack interval
        // this results in a stdev of 0.3 or less
        attack = totalCount < 10.0 ? false : (overAttackIntervalCount / totalCount) >= 0.90;
      } else {
        attack = false;
      }
    } else {
      attack = false;
    }

    return attack;
  }
}
