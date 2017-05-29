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
          .add("graphName")
          .add("agentToVertexCountRatio")
          .add("adversaryToVertexCountRatio")
          .add("agentStrategy")
          .add("adversaryStrategy")
          .add("gameExecutionTimeMilliSeconds")
          .add("timestepExecutionTimeMicroseconds")
          .add("generalEffectiveness")
          .add("deteranceEffectiveness")
          .add("patrolEffectiveness")
          .add("defenseEffectiveness")
          .add("attackCount")
          .add("compromisedCount")
          .add("thwartedCount")
          .add("criticalVerticesCount")
          .add("succesfulAttackRatio")
          .add("thwartedAttackRatio")
          .build();

  public CombinedGameResultsCsvWriter(@NonNull final PrintWriter printWriter) throws IOException {
    super(printWriter, FIELDS);
  }

  @Override
  protected ImmutableList<String> toFields(@NonNull final CombinedGameResult combinedResult) {
    return ImmutableList.<String>builder()
            .add(toString(combinedResult.getNumberOfGamesPerMatch()))
            .add(toString(combinedResult.getTspLengthFactor()))
            .add(combinedResult.getGraph().getName())
            .add(toString(combinedResult.getAgentToVertexCountRatio()))
            .add(toString(combinedResult.getAdversaryToVertexCountRatio()))
            .add(combinedResult.getAgentStrategyFactory().getName())
            .add(combinedResult.getAdversaryStrategyFactory().getName())
            .add(toString(combinedResult.getExecutionTimeMilliSeconds()))
            .add(toString(combinedResult.getTimeStepExecutionTimeMicroseconds()))
            .add(toString(combinedResult.getGeneralEffectiveness()))
            .add(toString(combinedResult.getDeterenceEffectiveness()))
            .add(toString(combinedResult.getPatrolEffectiveness()))
            .add(toString(combinedResult.getDefenseEffectiveness()))
            .add(toString(combinedResult.getAttackCount()))
            .add(toString(combinedResult.getCompromisedCount()))
            .add(toString(combinedResult.getThwartedCount()))
            .add(toString(combinedResult.getCriticalVerticesCount()))
            .add(toString(combinedResult.getSuccesfulAttackRatio()))
            .add(toString(combinedResult.getThwartedAttackRatio()))
            .build();
  }
}
