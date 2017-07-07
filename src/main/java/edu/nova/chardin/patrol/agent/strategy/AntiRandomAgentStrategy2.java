package edu.nova.chardin.patrol.agent.strategy;

import com.google.common.collect.ImmutableMap;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.VertexId;
import org.apache.commons.math3.util.Pair;

public class AntiRandomAgentStrategy2 extends AbstractCoveringAgentStrategy2 {

  private final ChooseLongestUnvisitedEdgeBiasStrategy edgeBiasStrategy = new ChooseLongestUnvisitedEdgeBiasStrategy();
  private final ChooseLongestUnvistedCoveredVertexStrategy coveredVertexStrategy = new ChooseLongestUnvistedCoveredVertexStrategy();

  public AntiRandomAgentStrategy2(double coveredVertexFactor) {
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
          final AgentContext context,
          final ImmutableMap<VertexId, Integer> leftTimes,
          final ImmutableMap<VertexId, Pair<EdgeId, Integer>> bestEdgeAndArrivalTsToCoveredVertices) {

    return validateScores(
            coveredVertexStrategy.scoreCoveredVertices(
              context, 
              leftTimes, 
              bestEdgeAndArrivalTsToCoveredVertices));
  }

  @Override
  protected void leavingVertex(AgentContext context, VertexId vertex) {
  }

}
