package edu.nova.chardin.patrol.adversary.strategy;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.nova.chardin.patrol.adversary.AdversaryContext;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticalAdversaryStrategy implements AdversaryStrategy {

  private static final double MINIMUM_PROBABILITY_OF_SUCCESS = 0.75;
  private Optional<Integer> timestepsOccupied = Optional.empty();
  private Optional<Integer> timestepsPreviouslyOccupied = Optional.empty();
  private final Table<Integer, Integer, AtomicInteger> occupiedCounts = HashBasedTable.create();

  @Override
  public boolean attack(AdversaryContext context) {

    final boolean isOccupied = context.isOccupied();
    final boolean attack;

    if (isOccupied) {
      if (timestepsOccupied.isPresent()) {
        timestepsOccupied = timestepsOccupied.map(ts -> ts + 1);
      } else {
        timestepsOccupied = Optional.of(1);
      }
      attack = false;
    } else {
      if (timestepsOccupied.isPresent()) {
        if (timestepsPreviouslyOccupied.isPresent()) {
          final int attackInterval = context.getAttackInterval();
          final AtomicInteger trialCount = new AtomicInteger();
          final AtomicInteger successCount = new AtomicInteger();
          final double probabilityOfSuccess;

          if (occupiedCounts.contains(timestepsPreviouslyOccupied.get(), timestepsOccupied.get())) {
            occupiedCounts.get(timestepsPreviouslyOccupied.get(), timestepsOccupied.get()).incrementAndGet();
          } else {
            occupiedCounts.put(timestepsPreviouslyOccupied.get(), timestepsOccupied.get(), new AtomicInteger(1));
          }
          
          occupiedCounts.cellSet().forEach(cell -> {
            final int prev = cell.getRowKey();
            
            if (prev == timestepsPreviouslyOccupied.get()) {
              final int next = cell.getColumnKey();
              final int count = cell.getValue().get();
              
              trialCount.addAndGet(count);
              
              if (next >= attackInterval) {
                successCount.addAndGet(count);
              }
            }
          });

          probabilityOfSuccess = trialCount.doubleValue() == 0.0 ? 0.0 : successCount.doubleValue() / trialCount.doubleValue();
          attack = probabilityOfSuccess >= MINIMUM_PROBABILITY_OF_SUCCESS;

        } else {
          attack = false;
        }

        timestepsPreviouslyOccupied = timestepsOccupied;
        timestepsOccupied = Optional.empty();
      } else {
        attack = false;
      }
    }

    return attack;
  }

}
