package edu.nova.chardin.patrol.experiment.result;

import edu.nova.chardin.patrol.adversary.AdversaryStrategyFactory;
import edu.nova.chardin.patrol.agent.AgentStrategyFactory;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

@Value
@Builder
public class CombinedMatchResult {

  @NonNull
  Integer numberOfGamesPerMatch;

  @NonNull
  Integer numberOfTimestepsPerGame;

  @NonNull
  PatrolGraph graph;

  @NonNull
  Integer numberOfAgents;

  @NonNull
  Integer numberOfAdversaries;

  @NonNull
  AgentStrategyFactory agentStrategyFactory;

  @NonNull
  AdversaryStrategyFactory adversaryStrategyFactory;

  @NonNull
  Integer attackInterval;

  @NonNull
  StatisticalSummary executionTimeMilliSeconds;

  @NonNull
  StatisticalSummary generalEffectiveness;
  
  @NonNull
  StatisticalSummary deterenceEffectiveness;
  
  @NonNull
  StatisticalSummary patrolEffectiveness;
  
  @NonNull
  StatisticalSummary defenseEffectiveness;
  
}
