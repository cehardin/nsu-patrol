package edu.nova.chardin.patrol;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.experiment.result.CombinedGameResult;
import lombok.NonNull;

import java.io.IOException;
import java.io.PrintWriter;

public final class CombinedGameResultsCsvWriter extends AbstractCsvWriter<CombinedGameResult> {

  private static final ImmutableList<String> FIELDS = ImmutableList.<String>builder()
          .add("numberOfGamesPerMatch")
          .add("tspLengthFactor")
          .add("attackInterval")
          .add("graphName")
          .add("agentToVertexCountRatio")
          .add("adversaryToVertexCountRatio")
          .add("numberOfAgents")
          .add("numberOfAdversaries")
          .add("agentStrategy")
          .add("adversaryStrategy")
          .add("gameExecutionTimeMilliSeconds")
          .add("timestepExecutionTimeMicroseconds")
          .add("numberOfTimesteps")
          .add("generalEffectiveness")
          .add("deteranceEffectiveness")
          .add("patrolEffectiveness")
          .add("defenseEffectiveness")
          .add("attackCount")
          .add("compromisedCount")
          .add("thwartedCount")
          .add("criticalVerticesCount")
          .add("targetVerticesCount")
          .add("agentMoveCount")
          .add("agentTimestepsSpentMoving")
          .add("ratioVerticesVisited")
          .build();

  public CombinedGameResultsCsvWriter(@NonNull final PrintWriter printWriter) throws IOException {
    super(printWriter, FIELDS);
  }

  @Override
  protected ImmutableList<String> toFields(@NonNull final CombinedGameResult combinedResult) {
    return ImmutableList.<String>builder()
            .add(toString(combinedResult.getNumberOfGamesPerMatch()))
            .add(toString(combinedResult.getTspLengthFactor()))
            .add(toString(combinedResult.getAttackInterval()))
            .add(combinedResult.getGraph().getName())
            .add(toString(combinedResult.getAgentToVertexCountRatio()))
            .add(toString(combinedResult.getAdversaryToVertexCountRatio()))
            .add(toString(combinedResult.getNumberOfAgents()))
            .add(toString(combinedResult.getNumberOfAdversaries()))
            .add(combinedResult.getAgentStrategyFactory().getName())
            .add(combinedResult.getAdversaryStrategyFactory().getName())
            .add(toString(combinedResult.getExecutionTimeMilliSeconds()))
            .add(toString(combinedResult.getTimeStepExecutionTimeMicroseconds()))
            .add(toString(combinedResult.getNumberOfTimesteps()))
            .add(toString(combinedResult.getGeneralEffectiveness()))
            .add(toString(combinedResult.getDeterenceEffectiveness()))
            .add(toString(combinedResult.getPatrolEffectiveness()))
            .add(toString(combinedResult.getDefenseEffectiveness()))
            .add(toString(combinedResult.getAttackCount()))
            .add(toString(combinedResult.getCompromisedCount()))
            .add(toString(combinedResult.getThwartedCount()))
            .add(toString(combinedResult.getCriticalVerticesCount()))
            .add(toString(combinedResult.getTargetVerticesCount()))
            .add(toString(combinedResult.getAgentMoveCount()))
            .add(toString(combinedResult.getAgentTimestepsSpentMoving()))
            .add(toString(combinedResult.getRatioVerticesVisited()))
            .build();
  }
}
