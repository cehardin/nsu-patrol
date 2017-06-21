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
import java.util.LinkedList;
import java.util.List;
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

  @Getter(lazy = true)
  ImmutableSet<Game> games = createGames();

  private ImmutableSet<Game> createGames() {
    final Experiment experiment = getScenario().getExperiment();
    final PatrolGraph graph = getScenario().getGraph();
    final ImmutableList<VertexId> vertices = graph.getVertices().asList();
    final int numberOfAgents = getScenario().getNumberOfAgents();
    final int numberOfAdversaries = getScenario().getNumberOfAdversaries();
    final int numberOfGames = experiment.getNumberOfGamesPerMatch();
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
                      .build());

    });

    return ImmutableSet.copyOf(createdGames);
  }

  private ImmutableSet<VertexId> pickRandomVertices(
          @NonNull final ImmutableList<VertexId> vertices,
          final int countToPick) {

    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final List<VertexId> toPickFrom = new LinkedList<>(vertices);
    final Set<VertexId> picked = new HashSet<>(countToPick);

    Preconditions.checkState(
            vertices.size() >= countToPick,
            "Cannot pick %s vertices out of a set of only %s",
            countToPick,
            vertices.size());

    IntStream.range(0, countToPick).forEach(pickNumber -> {
      picked.add(toPickFrom.remove(random.nextInt(toPickFrom.size())));
    });
    
    if (picked.size() != countToPick) {
      throw new IllegalStateException(
              String.format(
                      "Was supposed to pick %d vertices but only %d were picked", 
                      countToPick, 
                      picked.size()));
    }

    return ImmutableSet.copyOf(picked);
  }

}
