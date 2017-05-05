package edu.nova.chardin.patrol;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.experiment.result.CombinedGameResult;
import lombok.NonNull;

import java.io.IOException;
import java.io.PrintWriter;

public final class CombinedGameResultsCsvWriter extends AbstractCsvWriter<CombinedGameResult> {

  private static final ImmutableList<String> FIELDS = ImmutableList.<String>builder()
          .add("numberOfGamedPerMatch")
          .add("numberOfTimestepsPerGame")
          .add("graphName")
          .add("numberOdAgents")
          .add("numberOfAdversaries")
          .add("agentStrategy")
          .add("adversaryStrategy")
          .add("attackInterval")
          .add("gameExecutionTimeNanoSeconds")
          .add("generalEffectiveness")
          .add("deteranceEffectiveness")
          .add("patrolEffectiveness")
          .add("defenseEffectiveness")
          .build();

  public CombinedGameResultsCsvWriter(@NonNull final PrintWriter printWriter) throws IOException {
    super(printWriter, FIELDS);
  }

  @Override
  protected ImmutableList<String> toFields(@NonNull final CombinedGameResult combinedResult) {
    return ImmutableList.<String>builder()
            .add(toString(combinedResult.getNumberOfGamesPerMatch()))
            .add(toString(combinedResult.getNumberOfTimestepsPerGame()))
            .add(combinedResult.getGraph().getName())
            .add(toString(combinedResult.getNumberOfAgents()))
            .add(toString(combinedResult.getNumberOfAdversaries()))
            .add(combinedResult.getAgentStrategyFactory().getName())
            .add(combinedResult.getAdversaryStrategyFactory().getName())
            .add(toString(combinedResult.getAttackInterval()))
            .add(toString(combinedResult.getExecutionTimeNanoSeconds()))
            .add(toString(combinedResult.getGeneralEffectiveness()))
            .add(toString(combinedResult.getDeterenceEffectiveness()))
            .add(toString(combinedResult.getPatrolEffectiveness()))
            .add(toString(combinedResult.getDefenseEffectiveness()))
            .build();
  }
}
