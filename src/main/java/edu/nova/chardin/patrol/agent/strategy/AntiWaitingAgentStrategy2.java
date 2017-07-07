package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.Map.Entry;
import org.apache.commons.math3.util.Pair;

public class AntiWaitingAgentStrategy2 extends AbstractCoveringAgentStrategy2 {

  private final ChooseLongestUnvisitedEdgeBiasStrategy edgeBiasStrategy = new ChooseLongestUnvisitedEdgeBiasStrategy();
  private final ChooseLongestUnvistedCoveredVertexStrategy coveredVertexStrategy = new ChooseLongestUnvistedCoveredVertexStrategy();

  public AntiWaitingAgentStrategy2(double coveredVertexFactor) {
    super(coveredVertexFactor);
  }

  
  @Override
  protected void edgeChosen(AgentContext context, EdgeId edge) {
    edgeBiasStrategy.edgeChosen(context.getCurrentTimeStep(), edge);
  }
  
  @Override
  protected ImmutableMap<EdgeId, Double> scoreEdgeBias(AgentContext context) {
    return validateScores(
            edgeBiasStrategy.scoreEdgeBias(
                    context.getCurrentTimeStep(), 
                    context.getIncidientEdgeIds()));
  }

  @Override
  protected ImmutableMap<EdgeId, Double> scoreCoveredVertices(
          AgentContext context, 
          ImmutableMap<VertexId, Integer> leftTimes, 
          ImmutableMap<VertexId, Pair<EdgeId, Integer>> edgeAndArrivalTs) {
    
    final int currentTs = context.getCurrentTimeStep();
    final int attackInterval = context.getAttackInterval();
    final ImmutableSet<VertexId> verticesToKeep = leftTimes.keySet().stream()
            .filter(vertex -> {
              final int leftTs = leftTimes.get(vertex);
              final int arriveTs = edgeAndArrivalTs.get(vertex).getSecond();
              final int cutoffTs = leftTs + attackInterval;
              
              return arriveTs <= cutoffTs;
            })
            .collect(ImmutableSet.toImmutableSet());
    final ImmutableMap<VertexId, Integer> filteredLeftTimes = leftTimes.entrySet().stream()
            .filter(entry -> verticesToKeep.contains(entry.getKey()))
            .collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
    final ImmutableMap<VertexId, Pair<EdgeId, Integer>> filteredbestEdgeAndDistanceToCoveredVertices = edgeAndArrivalTs.entrySet().stream()
            .filter(entry -> verticesToKeep.contains(entry.getKey()))
            .collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
    
    return validateScores(
            coveredVertexStrategy.scoreCoveredVertices(
                    context, 
                    filteredLeftTimes, 
                    filteredbestEdgeAndDistanceToCoveredVertices));
  }

  @Override
  protected void leavingVertex(AgentContext context, VertexId vertex) {
  }
}
