package edu.nova.chardin.patrol.experiment;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.nova.chardin.patrol.adversary.AdversaryStrategyFactory;
import edu.nova.chardin.patrol.agent.AgentStrategyFactory;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Getter
@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Log
public class Match {

  @NonNull
  Scenario scenario;

  @NonNull
  AgentStrategyFactory agentStrategyFactory;

  @NonNull
  AdversaryStrategyFactory adversaryStrategyFactory;

  @NonNull
  Integer attackInterval;

  @Getter(lazy = true)
  ImmutableSet<Game> games = createGames();

  private ImmutableSet<Game> createGames() {
    final Experiment experiment = scenario.getExperiment();
    final PatrolGraph graph = scenario.getGraph();
    final ImmutableList<VertexId> vertices = ImmutableList.copyOf(graph.getVertices());
    final int numberOfAgents = scenario.getNumberOfAgents();
    final int numberOfAdversaries = scenario.getNumberOfAdversaries();
    final int numberOfGames = experiment.getNumberOfGamesPerMatch();
    final int numberOfTimesteps = scenario.getNumberOfTimestepsPerGame();
    final Set<Game> createdGames = ConcurrentHashMap.newKeySet(numberOfGames);
    

    IntStream.range(0, numberOfGames).parallel().forEach(gameNumber -> {
      final ImmutableSet<VertexId> agentStartingVertices = pickRandomVertices(vertices, numberOfAgents);
      final ImmutableSet<VertexId> targetVertices = pickRandomVertices(vertices, numberOfAdversaries);

      createdGames.add(
              Game.builder()
                      .match(this)
                      .number(gameNumber)
                      .agentStartingPositions(agentStartingVertices)
                      .targets(targetVertices)
                      .timesteps(numberOfTimesteps)
                      .build());

    });

    return ImmutableSet.copyOf(createdGames);
  }

  private ImmutableSet<VertexId> pickRandomVertices(
          @NonNull final ImmutableList<VertexId> vertices,
          final int countToPick) {

    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final Set<VertexId> picked = new HashSet<>(countToPick);

    Preconditions.checkState(
            vertices.size() > countToPick,
            "Cannot pick %s vertices out of a set of only %s",
            countToPick,
            vertices.size());

    while (picked.size() < countToPick) {
      picked.add(vertices.get(random.nextInt(vertices.size())));
    }

    return ImmutableSet.copyOf(picked);
  }

}
