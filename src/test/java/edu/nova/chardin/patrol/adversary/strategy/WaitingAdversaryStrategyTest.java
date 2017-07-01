package edu.nova.chardin.patrol.adversary.strategy;

import org.junit.Assert;
import org.junit.Test;

public class WaitingAdversaryStrategyTest {
  
  @Test
  public void startOccupied() {
    final WaitingAdversaryStrategy strategy = new WaitingAdversaryStrategy();
    
    Assert.assertFalse(strategy.attack(10, 1, true));
    Assert.assertTrue(strategy.attack(10, 2, false));
    Assert.assertFalse(strategy.attack(10, 3, true));
    Assert.assertTrue(strategy.attack(10, 4, false));
  }
  
  
  @Test
  public void startUnoccupied() {
    final WaitingAdversaryStrategy strategy = new WaitingAdversaryStrategy();
    
    Assert.assertFalse(strategy.attack(10, 1, false));
    Assert.assertFalse(strategy.attack(10, 2, true));
    Assert.assertTrue(strategy.attack(10, 3, false));
    Assert.assertFalse(strategy.attack(10, 4, true));
  }
}
