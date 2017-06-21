package edu.nova.chardin.patrol.experiment.runner;

import edu.nova.chardin.patrol.graph.VertexId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public class AdversaryStateTest {

  @Test
  public void allAttacksSuccessful() {
    final AdversaryState state = new AdversaryState(new VertexId("a"));
    final int expectedAttackCount = 1000;
    
    for (int attack=0; attack < expectedAttackCount; attack++) {
      state.beginAttack();
      state.endAttack(true);
    }
    
    Assert.assertEquals(expectedAttackCount, state.getAttackCount());
    Assert.assertEquals(expectedAttackCount, state.getAttackSuccessfulCount());
    Assert.assertEquals(0, state.getAttackThwartedCount());
  }
  
  @Test
  public void allAttacksThwarted() {
    final AdversaryState state = new AdversaryState(new VertexId("a"));
    final int expectedAttackCount = 1000;
    
    for (int attack=0; attack < expectedAttackCount; attack++) {
      state.beginAttack();
      state.endAttack(false);
    }
    
    Assert.assertEquals(expectedAttackCount, state.getAttackCount());
    Assert.assertEquals(0, state.getAttackSuccessfulCount());
    Assert.assertEquals(expectedAttackCount, state.getAttackThwartedCount());
  }
  
  @Test
  public void tenPercentAttacksSuccesful() {
    final AdversaryState state = new AdversaryState(new VertexId("a"));
    final int expectedAttackCount = 1000;
    
    for (int attack=0; attack < expectedAttackCount; attack++) {
      state.beginAttack();
      state.endAttack(attack % 10 == 0);
    }
    
    Assert.assertEquals(expectedAttackCount, state.getAttackCount());
    Assert.assertEquals(expectedAttackCount / 10, state.getAttackSuccessfulCount());
    Assert.assertEquals(expectedAttackCount * 9 / 10, state.getAttackThwartedCount());
//    Assert.assertEquals((double)expectedAttackCount * 0.90, state.getAttackThwartedCount());
  }
  
  public void streamTest() {
    final int stateCount = 1_000_000;
    final List<AdversaryState> states = new ArrayList<>(stateCount);
    final int expectedAttackCount = 1000;
    final int totalAttackSum;
    final int totalAttackSuccessfulSum;
    final int totalAttackThwartedSum;
    final double totalThwartedRatio;
    
    IntStream.rangeClosed(1, stateCount).forEach(stateNumber -> {
      states.add(new AdversaryState(new VertexId(Integer.toString(stateNumber))));
    });
    
    states.forEach(state -> {
      for (int attack=0; attack < expectedAttackCount; attack++) {
        state.beginAttack();
        state.endAttack(attack % 10 == 0);
      }
      
      Assert.assertEquals(expectedAttackCount, state.getAttackCount());
      Assert.assertEquals(expectedAttackCount / 10, state.getAttackSuccessfulCount());
      Assert.assertEquals(expectedAttackCount * 9 / 10, state.getAttackThwartedCount());
    });
    
    totalAttackSum = states.stream().mapToInt(AdversaryState::getAttackCount).sum();
    totalAttackSuccessfulSum = states.stream().mapToInt(AdversaryState::getAttackSuccessfulCount).sum();
    totalAttackThwartedSum = states.stream().mapToInt(AdversaryState::getAttackThwartedCount).sum();
    totalThwartedRatio = (double)totalAttackThwartedSum / (double)totalAttackSum;
    
    Assert.assertEquals(stateCount * expectedAttackCount, totalAttackSum);
    Assert.assertEquals(stateCount * expectedAttackCount / 10, totalAttackSuccessfulSum);
    Assert.assertEquals(stateCount * expectedAttackCount * 9 / 10, totalAttackThwartedSum);
    Assert.assertEquals(0.10, totalThwartedRatio);
  }
}
