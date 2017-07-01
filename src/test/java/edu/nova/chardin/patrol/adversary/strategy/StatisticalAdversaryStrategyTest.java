package edu.nova.chardin.patrol.adversary.strategy;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class StatisticalAdversaryStrategyTest {

  @Test
  public void startOccupied() {
    final StatisticalAdversaryStrategy strategy = new StatisticalAdversaryStrategy();
    final int attackInterval = 10;
    int timestep = 1;
    
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
  }
  
  @Test
  public void startUnoccupied() {
    final StatisticalAdversaryStrategy strategy = new StatisticalAdversaryStrategy();
    final int attackInterval = 10;
    int timestep = 1;
    
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
  }
  
  @Test
  public void startOccupiedThenAttack() {
    final StatisticalAdversaryStrategy strategy = new StatisticalAdversaryStrategy();
    final int attackInterval = 2;
    int timestep = 1;
    
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
    timestep++;
    Assert.assertTrue(strategy.attack(attackInterval, timestep, false));
  }
  
  @Test
  public void startUnoccupiedThenAttack() {
    final StatisticalAdversaryStrategy strategy = new StatisticalAdversaryStrategy();
    final int attackInterval = 2;
    int timestep = 1;
    
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
    timestep++;
    Assert.assertTrue(strategy.attack(attackInterval, timestep, false));
  }
  
  @Test
  public void startUnoccupiedThenAttack50_50() {
    final StatisticalAdversaryStrategy strategy = new StatisticalAdversaryStrategy();
    final int attackInterval = 2;
    int timestep = 1;
    
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
    timestep++;
    Assert.assertTrue(strategy.attack(attackInterval, timestep, false));
  }
  
  @Test
  @Ignore
  public void neverOccupied() {
    final StatisticalAdversaryStrategy strategy = new StatisticalAdversaryStrategy();
    final int attackInterval = 2;
    int timestep = 1;
    
    while (timestep < (attackInterval * 4)) {
      Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
      timestep++;
    }
    
    
    Assert.assertTrue(strategy.attack(attackInterval, timestep, false));
  }
  
  @Test
  @Ignore
  public void occupiedOnceThenNeverAgain() {
    final StatisticalAdversaryStrategy strategy = new StatisticalAdversaryStrategy();
    final int attackInterval = 2;
    int timestep = 1;
    
    Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
    timestep++;
    Assert.assertFalse(strategy.attack(attackInterval, timestep, true));
    timestep++;
    
    while (timestep < (attackInterval * 4) + 2) {
      Assert.assertFalse(strategy.attack(attackInterval, timestep, false));
      timestep++;
    }
    
    
    Assert.assertTrue(strategy.attack(attackInterval, timestep, false));
  }
  
 
}
