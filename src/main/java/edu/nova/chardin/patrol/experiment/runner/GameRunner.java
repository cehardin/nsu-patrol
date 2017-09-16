package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import edu.nova.chardin.patrol.agent.AgentContext;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.experiment.Game;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.event.GameLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import edu.nova.chardin.patrol.experiment.result.GameResult;
import edu.nova.chardin.patrol.graph.EdgeId;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import edu.nova.chardin.patrol.graph.VertexId;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({@Inject}))
@Value
@Getter(AccessLevel.NONE)
public class GameRunner implements Function<Game, GameResult> {
 
  EventBus eventBus;

  @Override
  public GameResult apply(@NonNull final Game game) {
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    final Match match = game.getMatch();
    final Scenario scenario = match.getScenario();
    final PatrolGraph graph = scenario.getGraph();
    final Map<AgentStrategy, AgentState> agentStates = new HashMap<>();
    final Map<AdversaryStrategy, AdversaryState> adversaryStates = new HashMap<>();
    final Set<VertexId> criticalVertices = new HashSet<>();
    final ImmutableSet<VertexId> targetVertices;
    final Set<VertexId> visitedVertices = new HashSet<>();
    
    //notifiy the monitor that a game has started
    eventBus.post(new GameLifecycleEvent(game, Lifecycle.Started));

    //some sanity checking
    if (scenario.getNumberOfAgents() > graph.getVertices().size()) {
      throw new IllegalStateException("There are more agents than vertices");
    }
    
    //some sanity checking
    if (scenario.getNumberOfAdversaries() > graph.getVertices().size()) {
      throw new IllegalStateException("There are more adversaries than vertices");
    }
    
    // create agents
    IntStream.range(0, scenario.getNumberOfAgents()).forEach(agentNumber -> {
      final AgentStrategy agentStrategy = match.getAgentStrategyFactory().get();
      final ImmutableList<VertexId> verticesList = ImmutableSet.copyOf(
              Sets.difference(
                      graph.getVertices(),
                      agentStates.values().stream().map(AgentState::getCurrentVertex).collect(Collectors.toSet())))
              .asList();
      final VertexId agentLocation = verticesList.get(random.nextInt(verticesList.size()));
      
      agentStates.put(agentStrategy, new AgentState(agentLocation));
      visitedVertices.add(agentLocation);
    });
    
    //sanity check
    if (agentStates.size() != scenario.getNumberOfAgents()) {
      throw new IllegalStateException(
              String.format(
                      "The actual count of %d agents does not match the expected count of %d agents", 
                      agentStates.size(), 
                      scenario.getNumberOfAgents()));
    }
    
    //sanity check
    {
      final ImmutableSet<VertexId> agentLocations = agentStates.values().stream()
              .map(AgentState::getCurrentVertex)
              .collect(ImmutableSet.toImmutableSet());
      
      if (agentLocations.size() != scenario.getNumberOfAgents()) {
      throw new IllegalStateException(
              String.format(
                      "The count of %d agent locations does not match the count of %d agents", 
                      agentLocations.size(), 
                      scenario.getNumberOfAgents()));
      }
    }
    
    // create adversaries
    IntStream.range(0, scenario.getNumberOfAdversaries()).forEach(adversaryNumber -> {
      final AdversaryStrategy adversaryStrategy = match.getAdversaryStrategyFactory().get();
      final ImmutableList<VertexId> verticesList = ImmutableSet.copyOf(
              Sets.difference(
                      graph.getVertices(),
                      adversaryStates.values().stream().map(AdversaryState::getTarget).collect(Collectors.toSet())))
              .asList();
      final VertexId adversaryTarget = verticesList.get(random.nextInt(verticesList.size()));
      
      adversaryStates.put(adversaryStrategy, new AdversaryState(adversaryTarget));
    });
    
    targetVertices = adversaryStates.values().stream()
            .map(AdversaryState::getTarget)
            .collect(ImmutableSet.toImmutableSet());
    
    //sanity check
    if (adversaryStates.size() != scenario.getNumberOfAdversaries()) {
      throw new IllegalStateException(
              String.format(
                      "The actual count of %d adversaries does not match the expected count of %d adversaries", 
                      targetVertices.size(), 
                      scenario.getNumberOfAdversaries()));
    }
    
    //sanity check
    if (targetVertices.size() != scenario.getNumberOfAdversaries()) {
      throw new IllegalStateException(
              String.format(
                      "The count of %d target vertices does not match the count of %d adversaries", 
                      targetVertices.size(), 
                      scenario.getNumberOfAdversaries()));
    }

    //run through the simulation
    IntStream.rangeClosed(1, scenario.getNumberOfTimestepsPerGame()).forEach(timestep -> {
      final ImmutableSet<AdversaryState> attackingAdversariesSnapshot = adversaryStates.values().stream()
              .filter(AdversaryState::isAttacking)
              .collect(ImmutableSet.toImmutableSet());
      final ImmutableMap<AdversaryStrategy, AdversaryState> nonAttackingAdversariesSnapshot = adversaryStates.entrySet().stream()
              .filter(e -> !e.getValue().isAttacking())
              .collect(ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue));
      final ImmutableSet<VertexId> verticesUnderAttackSnapshot = attackingAdversariesSnapshot.stream()
              .map(AdversaryState::getTarget)
              .collect(ImmutableSet.toImmutableSet());
      final ImmutableMultimap<VertexId, AgentStrategy> agentLocationsSnapshot;
      
      //compute new agent locations
      agentStates.values().forEach(agentState -> {
        agentState.timestep();
      
        if (agentState.isAtVertex()) {
          visitedVertices.add(agentState.getCurrentVertex());
        }
      });
      
      agentLocationsSnapshot = ImmutableMultimap.copyOf(
              agentStates.entrySet().stream()
                      .filter(e -> e.getValue().isAtVertex())
                      .map(e -> new SimpleEntry<>(e.getValue().getCurrentVertex(), e.getKey()))
                      .collect(ImmutableList.toImmutableList()));
      
      //determine if any attacks suceeded or were thwarted
      attackingAdversariesSnapshot.forEach(adversaryState -> {
        final VertexId targetVertex = adversaryState.getTarget();
      
        adversaryState.timestep();
      
        if (adversaryState.getAttackingTimeStepCount() == scenario.getAttackInterval()) {
          adversaryState.endAttack(true);
        } else if (agentLocationsSnapshot.keySet().contains(targetVertex)) {
          adversaryState.endAttack(false);
          agentLocationsSnapshot.get(targetVertex).forEach(agentStrategy -> {
            agentStrategy.thwarted(
                    targetVertex, 
                    ImmutableSet.copyOf(criticalVertices),
                    timestep,
                    scenario.getAttackInterval());
            criticalVertices.add(targetVertex);
          });
        } else if (adversaryState.getAttackingTimeStepCount() > scenario.getAttackInterval()) {
          throw new IllegalStateException("adverasry attacked for more than the attack interval");
        }
      });
      
      //determine if to start an attack
      nonAttackingAdversariesSnapshot.forEach((adversaryStrategy, adversaryState) -> {
        final VertexId targetVertex = adversaryState.getTarget();
        final boolean agentPresent = agentLocationsSnapshot.keySet().contains(targetVertex);
        
        adversaryState.timestep();
        
        if (adversaryStrategy.attack(scenario.getAttackInterval(), timestep, agentPresent)) {
          adversaryState.beginAttack();
          
          if (agentPresent) {
            adversaryState.endAttack(false);
            agentLocationsSnapshot.get(targetVertex).forEach(agentStrategy -> {
              agentStrategy.thwarted(
                    targetVertex, 
                    ImmutableSet.copyOf(criticalVertices),
                    timestep,
                    scenario.getAttackInterval());
              criticalVertices.add(targetVertex);
            });
          }
        }
      });
      
      //determine where agents move to
      agentStates.forEach((agentStrategy, agentState) -> {
        if (agentState.isAtVertex()) {
          final VertexId currentVertex = agentState.getCurrentVertex();
          final ImmutableMap<EdgeId, VertexId> incidentEdgeIdToVertex = graph.adjacentVertices(currentVertex).stream()
                  .collect(
                          ImmutableMap.toImmutableMap(
                                  v -> new EdgeId(currentVertex, v), 
                                  Function.identity()));
          final EdgeId nextEdge = agentStrategy.choose(
                  new AgentContext(
                          scenario.getAttackInterval(),
                          currentVertex, 
                          ImmutableSet.copyOf(criticalVertices),
                          incidentEdgeIdToVertex, 
                          timestep,
                          graph));
          final VertexId nextVertex = incidentEdgeIdToVertex.get(nextEdge);
          
          if (graph.adjacentVertices(currentVertex).contains(nextVertex)) {
            final EdgeWeight edgeWeight = graph.edgeWeight(currentVertex, nextVertex);
          
            agentState.startMove(nextVertex, edgeWeight.getValue());
          } else {
            throw new IllegalStateException(
                    String.format("Agent %s chose an invalid vertex to move to", agentStrategy));
          }
        }
      });
    });
    
    //after done, let the monitor know
    eventBus.post(new GameLifecycleEvent(game, Lifecycle.Finished));

    //create the result object and return it
    {
      final double targetNotCompromizedCount = adversaryStates.values().stream().mapToInt(AdversaryState::getAttackSuccessfulCount)
              .filter(c -> c == 0)
              .count();
      final double targetNotAttackedCount = adversaryStates.values().stream().mapToInt(AdversaryState::getAttackCount)
              .filter(c -> c == 0)
              .count();
      final int attackCount = adversaryStates.values().stream().mapToInt(AdversaryState::getAttackCount).sum();
      final int thwartedCount = adversaryStates.values().stream().mapToInt(AdversaryState::getAttackThwartedCount).sum();
      final int compromizedCount = adversaryStates.values().stream().mapToInt(AdversaryState::getAttackSuccessfulCount).sum();
      final int criticalVerticesCount = criticalVertices.size();
      final int targetVerticesCount = targetVertices.size();
      final double generalEffectiveness = targetNotCompromizedCount / (double)targetVerticesCount;
      final double deteranceEffectiveness = targetNotAttackedCount / (double)targetVerticesCount;
      final double patrolEffectiveness = (double)criticalVerticesCount / (double)targetVerticesCount;
      final double defenseEffectiveness = attackCount == 0 ? 1.0 : (double)thwartedCount / (double)attackCount; 
      final int agentMoveCount = agentStates.values().stream().mapToInt(AgentState::getMoveCount).sum();
      final int agentTimeStepsSpentMoving = agentStates.values().stream().mapToInt(AgentState::getTimestepsSpentMoving).sum();
      final double ratioVerticesVisited = (double)visitedVertices.size() / (double)graph.getVertices().size();
      
      stopwatch.stop();
      
      return GameResult.builder()
            .game(game)
            .executionTimeMilliSeconds(stopwatch.elapsed(TimeUnit.MILLISECONDS))
            .timeStepExecutionTimeMicroSeconds(stopwatch.elapsed(TimeUnit.MICROSECONDS) / scenario.getNumberOfTimestepsPerGame())
            .generalEffectiveness(generalEffectiveness)
            .deterenceEffectiveness(deteranceEffectiveness)
            .patrolEffectiveness(patrolEffectiveness)
            .defenseEffectiveness(defenseEffectiveness)
            .attackCount(attackCount)
            .compromisedCount(compromizedCount)
            .twartedCount(thwartedCount)
            .criticalVerticesCount(criticalVerticesCount)
            .targetVerticesCount(targetVerticesCount)
            .agentMoveCount(agentMoveCount)
            .agentTimestepsSpentMoving(agentTimeStepsSpentMoving)
            .ratioVerticesVisited(ratioVerticesVisited)
            .build();
    }
  }
}
