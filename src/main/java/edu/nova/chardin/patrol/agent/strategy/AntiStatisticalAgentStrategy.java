package edu.nova.chardin.patrol.agent.strategy;

import edu.nova.chardin.patrol.agent.AgentContext;
import java.util.concurrent.ThreadLocalRandom;

public final class AntiStatisticalAgentStrategy extends AntiRandomAgentStrategy {

  @Override
  protected int calculateReturnTime(final AgentContext agentContext) {

    return agentContext.getCurrentTimeStep()
            + (int) Math.ceil(
                    ThreadLocalRandom.current().nextDouble(1.0)
                    * (double) agentContext.getAttackInterval());
  }  
  
}
