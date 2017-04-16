package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.graph.ImmutableValueGraph;
import edu.nova.chardin.patrol.experiment.Game;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.event.GameLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import edu.nova.chardin.patrol.experiment.result.MatchResult;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
public class MatchRunner implements Function<Match, MatchResult> {

  EventBus eventBus;
  GameRunner gameRunner;
  MatchResultCombiner matchResultCombiner;

  @Override
  public MatchResult apply(@NonNull final Match match) {

    final ImmutableSet<Game> games = createGames(match);

    games
            .parallelStream()
            .map(g -> new GameLifecycleEvent(g, Lifecycle.Created))
            .forEach(e -> eventBus.post(e));

    return games
            .parallelStream()
            .map(gameRunner)
            .map(new GameResultToMatchResultConverter(match))
            .reduce(matchResultCombiner)
            .get();
  }

  private ImmutableSet<Game> createGames(@NonNull final Match match) {

    final Scenario scenario = match.getScenario();
    final ImmutableSet.Builder<Game> games = ImmutableSet.builder();

    IntStream.range(0, scenario.getExperiment().getNumberOfGamesPerMatch()).forEach(i -> {
      final ImmutableSet<VertexId> agentStartingVertices = pickRandomVertices(scenario.getGraph(), scenario.getNumberOfAgents());
      final ImmutableSet<VertexId> targetVertices = pickRandomVertices(scenario.getGraph(), scenario.getNumberOfAdversaries());

      games.add(
              Game.builder()
                      .match(match)
                      .agentStartingPositions(agentStartingVertices)
                      .targets(targetVertices)
                      .timesteps(scenario.getExperiment().getNumberOfTimestepsPerGame())
                      .build());

    });

    return games.build();
  }

  private ImmutableSet<VertexId> pickRandomVertices(
          @NonNull final ImmutableValueGraph<VertexId, EdgeWeight> graph, 
          final int countToPick) {
    
    final Random random = new Random();
    final ImmutableList<VertexId> allVertices = ImmutableList.copyOf(graph.nodes());
    final Set<VertexId> picked = new HashSet<>(countToPick);

    Preconditions.checkState(
            allVertices.size() <= countToPick,
            "Cannot pick %d vertices out of a set of only %d",
            countToPick,
            allVertices.size());

    while (picked.size() < countToPick) {
      picked.add(allVertices.get(random.nextInt(allVertices.size())));
    }

    return ImmutableSet.copyOf(picked);
  }
}
