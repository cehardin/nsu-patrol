package edu.nova.chardin.patrol;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.experiment.result.CombinedMatchResult;
import lombok.NonNull;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public final class CombinedMatchResultsCsvWriter extends AbstractCsvWriter<CombinedMatchResult> {

  private static final ImmutableList<String> FIELDS = ImmutableList.<String>builder()
                    .add("numberOfGamedPerMatch")
                    .add("numberOfTimestepsPerGame")
                    .add("graphName")
                    .add("numberOdAgents")
                    .add("numberOfAdversaries")
                    .add("agentStrategy")
                    .add("adversaryStrategy")
                    .add("attackInterval")
                    .addAll(toStatisicalSummaryHeaderList("gameExecutionTimeMilliSeconds"))
                    .addAll(toStatisicalSummaryHeaderList("generalEffectiveness"))
                    .addAll(toStatisicalSummaryHeaderList("deteranceEffectiveness"))
                    .addAll(toStatisicalSummaryHeaderList("patrolEffectiveness"))
                    .addAll(toStatisicalSummaryHeaderList("defenseEffectiveness"))
                    .build();
  
  private static ImmutableList<String> toStatisicalSummaryHeaderList(@NonNull final String name) {
    return ImmutableList.<String>builder()
            .add(String.format("%s_max", name))
            .add(String.format("%s_mean", name))
            .add(String.format("%s_min", name))
            .add(String.format("%s_n", name))
            .add(String.format("%s_stddev", name))
            .add(String.format("%s_sum", name))
            .add(String.format("%s_var", name))
            .build();
  }

  public CombinedMatchResultsCsvWriter(@NonNull final PrintWriter printWriter) throws IOException {
    super(printWriter, FIELDS);
  }

  @Override
  protected ImmutableList<String> toFields(@NonNull final CombinedMatchResult combinedResult) {
    return ImmutableList.<String>builder()
                    .add(toString(combinedResult.getNumberOfGamesPerMatch()))
                    .add(toString(combinedResult.getNumberOfTimestepsPerGame()))
                    .add(combinedResult.getGraph().getName())
                    .add(toString(combinedResult.getNumberOfAgents()))
                    .add(toString(combinedResult.getNumberOfAdversaries()))
                    .add(combinedResult.getAgentStrategyFactory().getName())
                    .add(combinedResult.getAdversaryStrategyFactory().getName())
                    .add(toString(combinedResult.getAttackInterval()))
                    .addAll(toStringList(combinedResult.getExecutionTimeMilliSeconds()))
                    .addAll(toStringList(combinedResult.getGeneralEffectiveness()))
                    .addAll(toStringList(combinedResult.getDeterenceEffectiveness()))
                    .addAll(toStringList(combinedResult.getPatrolEffectiveness()))
                    .addAll(toStringList(combinedResult.getDefenseEffectiveness()))
                    .build();
  }

  private List<String> toStringList(@NonNull final StatisticalSummary statisticalSummary) {
    return ImmutableList.<String>builder()
            .add(toString(statisticalSummary.getMax()))
            .add(toString(statisticalSummary.getMean()))
            .add(toString(statisticalSummary.getMin()))
            .add(toString(statisticalSummary.getN()))
            .add(toString(statisticalSummary.getStandardDeviation()))
            .add(toString(statisticalSummary.getSum()))
            .add(toString(statisticalSummary.getVariance()))
            .build();
  }
  
}
