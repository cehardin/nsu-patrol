package edu.nova.chardin.patrol.experiment.result;

import com.google.common.collect.ImmutableList;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Value
@Builder
public class SuperResult {

  public static final ImmutableList<String> CSV_HEAD = ImmutableList.<String>builder()
          .add("numberOfGamesPerMatch")
          .add("numberOfTimestepsPerGame")
          .add("graph")
          .add("numberOfAgents")
          .add("numberOfAdversaries")
          .add("agentStrategy")
          .add("adversaryStrategy")
          .add("attackInterval")
          .add("executionTimeNanoSeconds")
          .add("attackCount")
          .add("attackSuccessfulCount")
          .add("attackThwartedCount")
          .build();

  public static final Function<SuperResult, ImmutableList<String>> TO_CSV_LINE = r -> {
    return ImmutableList.<String>builder()
            .add(r.numberOfGamesPerMatch.toString())
            .add(r.numberOfTimestepsPerGame.toString())
            .add(r.graph.toString())
            .add(r.numberOfAgents.toString())
            .add(r.numberOfAdversaries.toString())
            .add(r.agentStrategyType.getSimpleName())
            .add(r.adversaryStrategyType.getSimpleName())
            .add(r.attackInterval.toString())
            .add(r.executionTimeNanoSeconds.toString())
            .add(r.attackCount.toString())
            .add(r.attackSuccessfulCount.toString())
            .add(r.attackThwartedCount.toString())
            .build();
  };

  public static final Function<ImmutableList<String>, String> COMMA_JOINER = list -> {
    return list.stream().map(s -> s.replace(',', '_')).collect(Collectors.joining(","));
  };

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
  Class<? extends AgentStrategy> agentStrategyType;

  @NonNull
  Class<? extends AdversaryStrategy> adversaryStrategyType;

  @NonNull
  Integer attackInterval;

  @NonNull
  Long executionTimeNanoSeconds;

  @NonNull
  Integer attackCount;

  @NonNull
  Integer attackSuccessfulCount;

  @NonNull
  Integer attackThwartedCount;

}
