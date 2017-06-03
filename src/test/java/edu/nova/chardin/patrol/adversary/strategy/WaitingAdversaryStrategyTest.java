package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryContext;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;

public class WaitingAdversaryStrategyTest {
  
  @Test
  public void startOccupied() {
    final WaitingAdversaryStrategy strategy = new WaitingAdversaryStrategy();
    
    Assert.assertFalse(strategy.attack(new AdversaryContext(10, 1, true)));
    Assert.assertTrue(strategy.attack(new AdversaryContext(10, 2, false)));
    Assert.assertFalse(strategy.attack(new AdversaryContext(10, 3, true)));
    Assert.assertTrue(strategy.attack(new AdversaryContext(10, 4, false)));
  }
  
  
  @Test
  public void startUnoccupied() {
    final WaitingAdversaryStrategy strategy = new WaitingAdversaryStrategy();
    
    Assert.assertFalse(strategy.attack(new AdversaryContext(10, 1, false)));
    Assert.assertFalse(strategy.attack(new AdversaryContext(10, 2, true)));
    Assert.assertTrue(strategy.attack(new AdversaryContext(10, 3, false)));
    Assert.assertFalse(strategy.attack(new AdversaryContext(10, 4, true)));
  }
}
