package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.base.Stopwatch;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AtomicDouble;
import edu.nova.chardin.patrol.adversary.AdversaryContext;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.Game;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.event.GameLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import edu.nova.chardin.patrol.experiment.event.MatchLifecycleEvent;
import edu.nova.chardin.patrol.experiment.result.GameResult;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({
  @Inject}))
@Value
@Getter(AccessLevel.NONE)
public class GameRunner implements Function<Game, GameResult> {

  private static <T> T newInstance(@NonNull final Class<T> type) {
    try {
      return type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(String.format("Could not create new instance of %s", type), e);
    }
  }
  
  EventBus eventBus;

  @Override
  public GameResult apply(@NonNull final Game game) {
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final Random random = new Random();
    final Match match = game.getMatch();
    final Scenario scenario = match.getScenario();
    final Experiment experiment = scenario.getExperiment();
    final PatrolGraph graph = scenario.getGraph();
    final Map<AgentStrategy, AgentState> agentStates = new HashMap<>();
    final Map<AdversaryStrategy, AdversaryState> adversaryStates = new HashMap<>();
    final Map<VertexId, AtomicInteger> targetAttackedCounts;
    final Map<VertexId, AtomicInteger> targetCompromisedCounts;
    final Map<VertexId, AtomicInteger> targetThwartedCounts;
    
    eventBus.post(new GameLifecycleEvent(game, Lifecycle.Started));

    // create agents
    IntStream.range(0, scenario.getNumberOfAgents()).forEach(agentNumber -> {
      final AgentStrategy agentStrategy = newInstance(match.getAgentStrategyType());
      final ImmutableList<VertexId> verticesList = ImmutableList.copyOf(graph.getVertices());
      final Set<VertexId> alreadyOccupied = agentStates.values().stream().map(AgentState::getCurrentVertex).collect(Collectors.toSet());
      
      if (graph.getVertices().equals(alreadyOccupied)) {
        throw new IllegalStateException("There are more agents than vertices!");
      }
      
      while (true) {
        final VertexId agentLocation = verticesList.get(random.nextInt(verticesList.size()));
        
        if (!alreadyOccupied.contains(agentLocation)) {
          agentStates.put(agentStrategy, new AgentState(agentLocation));
          break;
        }
      }
    });

    // create adversaries
    IntStream.range(0, scenario.getNumberOfAdversaries()).forEach(adversaryNumber -> {
      final AdversaryStrategy adversaryStrategy = newInstance(match.getAdversaryStrategyType());
      final ImmutableList<VertexId> verticesList = ImmutableList.copyOf(graph.getVertices());
      final Set<VertexId> alreadyTargets = adversaryStates.values().stream().map(AdversaryState::getTarget).collect(Collectors.toSet());
      
      if (graph.getVertices().equals(alreadyTargets)) {
        throw new IllegalStateException("There are more adversaries than vertices!");
      }
      
      while (true) {
        final VertexId adversaryTarget = verticesList.get(random.nextInt(verticesList.size()));

        if (!alreadyTargets.contains(adversaryTarget)) {
          adversaryStates.put(adversaryStrategy, new AdversaryState(adversaryTarget));
          break;
        }
      }
    });
    
    targetAttackedCounts = new HashMap<>(adversaryStates.size());
    targetCompromisedCounts = new HashMap<>(adversaryStates.size());
    targetThwartedCounts = new HashMap<>(adversaryStates.size());
    adversaryStates.values().stream().map(AdversaryState::getTarget).forEach(target -> {
      targetAttackedCounts.put(target, new AtomicInteger());
      targetCompromisedCounts.put(target, new AtomicInteger());
      targetThwartedCounts.put(target, new AtomicInteger());
    });

    //run through the simulation
    IntStream.rangeClosed(1, experiment.getNumberOfTimestepsPerGame()).forEach(timestep -> {

      final Set<VertexId> agentLocations;
      
      //compute agent locations
      agentStates.forEach((agentStrategy, agentState) -> {
        if (agentState.timestep()) {
          final AgentContext agentContext = new AgentContext() {
            @Override
            public VertexId getCurrentVertex() {
              return agentState.getCurrentVertex();
            }

            @Override
            public Set<VertexId> getAdjacentVertices() {
              return graph.adjacentVertices(agentState.getCurrentVertex());
            }
          };
          agentStrategy.arrived(agentContext);
        }
      });
      agentLocations = agentStates.values().stream()
              .filter(AgentState::isAtVertex)
              .map(AgentState::getCurrentVertex)
              .collect(Collectors.toSet());
      
      // determine if any attacks have become thwarted or successful
      adversaryStates.values().stream().filter(AdversaryState::isAttacking).forEach(adversaryState -> {
        adversaryState.timestep();

        if (adversaryState.isAttacking()) {
          if (agentLocations.contains(adversaryState.getTarget())) {
            targetThwartedCounts.get(adversaryState.getTarget()).incrementAndGet();
            adversaryState.endAttack();
          } else {
            if (adversaryState.getAttackingTimeStepCount() == match.getAttackInterval()) {
              targetCompromisedCounts.get(adversaryState.getTarget()).incrementAndGet();
              adversaryState.endAttack();
            }
          }
        }
      });

      //determine if any adversaries start attacking
      adversaryStates.entrySet().stream().filter(e -> !e.getValue().isAttacking()).forEach(e -> {
        final AdversaryStrategy adversaryStrategy = e.getKey();
        final AdversaryState adversaryState = e.getValue();
        final VertexId targetVertex = adversaryState.getTarget();

        final AdversaryContext context = new AdversaryContext() {
          @Override
          public int getAttackInterval() {
            return match.getAttackInterval();
          }

          @Override
          public long getTimestep() {
            return timestep;
          }

          @Override
          public boolean isOccupied() {
            return agentLocations.contains(targetVertex);
          }
        };

        if (adversaryStrategy.attack(context)) {
          adversaryState.beginAttack();
          targetAttackedCounts.get(adversaryState.getTarget()).incrementAndGet();
        }
      });
      
      //determine where agents move to
      agentStates.entrySet().stream().filter(e -> e.getValue().isAtVertex()).forEach(e -> {
        final AgentStrategy agentStrategy = e.getKey();
        final AgentState agentState = e.getValue();
        final AgentContext agentContext = new AgentContext() {
          @Override
          public VertexId getCurrentVertex() {
            return agentState.getCurrentVertex();
          }

          @Override
          public Set<VertexId> getAdjacentVertices() {
            return graph.adjacentVertices(agentState.getCurrentVertex());
          }
        };
        final VertexId nextVertex = agentStrategy.choose(agentContext);
        
        if (!nextVertex.equals(agentState.getCurrentVertex())) {
          final EdgeWeight edgeWeight = graph.edgeWeight(agentState.getCurrentVertex(), nextVertex);
          
          agentState.startMove(nextVertex, edgeWeight.getValue());
        }
      });

    });
    
    //after done
    eventBus.post(new GameLifecycleEvent(game, Lifecycle.Finished));

    {
      final double targetVerticesCount = adversaryStates.keySet().stream().count();
      final double targetNotCompromizedCount = targetCompromisedCounts.values().stream()
              .filter(c -> c.get() == 0)
              .count();
      final double targetNotAttackedCount = targetAttackedCounts.values().stream()
              .filter(c -> c.get() == 0)
              .count();
      final double generalEffectiveness = targetNotCompromizedCount / targetVerticesCount;
      final double deteranceEffectiveness = targetNotAttackedCount / targetVerticesCount;
      final double patrolEffectiveness = 0.0; // TODO
      final double defenseEffectiveness = 0.0; //TODO
    
    return GameResult.builder()
            .game(game)
            .executionTimeNanoSeconds(stopwatch.elapsed(TimeUnit.NANOSECONDS))
            .generalEffectiveness(generalEffectiveness)
            .deterenceEffectiveness(deteranceEffectiveness)
            .patrolEffectiveness(patrolEffectiveness)
            .defenseEffectiveness(defenseEffectiveness)
            .build();
    }
  }
}
