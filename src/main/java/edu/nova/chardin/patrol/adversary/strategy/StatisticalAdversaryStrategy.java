package edu.nova.chardin.patrol.adversary.strategy;

import com.google.common.collect.Iterables;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticalAdversaryStrategy implements AdversaryStrategy {

  private final ArrayList<Integer> unoccupiedSamples = new ArrayList<>();
  private final AtomicBoolean wasOccupied = new AtomicBoolean(false);
  private final AtomicInteger timestepsUnoccupied = new AtomicInteger();

  // main entry point for api to determine whether to attack or not
  @Override
  public boolean attack(int attackInterval, long timestep, boolean occupied) {
    final boolean attack;

    if (occupied) {
      attack = false;
      if (wasOccupied.compareAndSet(false, true)) {
        unoccupiedSamples.add(timestepsUnoccupied.getAndSet(0));
      }
    } else if (wasOccupied.compareAndSet(true, false)) {
      attack = decide(attackInterval);
      timestepsUnoccupied.set(1);
    } else {
      attack = false;
      timestepsUnoccupied.incrementAndGet();
    }

    return attack;
  }

  private boolean decide(final int attackInterval) {
    final Iterator<Integer> samples = unoccupiedSamples.iterator();
    final boolean attack;

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

        // need at least four samples and over 50% of them must be over the attack interval
        attack = totalCount < 2.0 ? false : (overAttackIntervalCount / totalCount) > 0.50;
      } else {
        attack = false;
      }
    } else {
      attack = false;
    }

    return attack;
  }
}
