package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

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
      final ImmutableSet<VertexId> agentLocations = agentStates.values().stream().map(AgentState::getCurrentVertex).collect(ImmutableSet.toImmutableSet());
      
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
      
      final ImmutableSet<VertexId> agentLocations;
      
      //compute agent locations
      agentStates.forEach((agentStrategy, agentState) -> {
        final boolean wasMoving = agentState.isMoving();
        
        agentState.timestep();
        
        if (wasMoving && agentState.isAtVertex()) {
          final VertexId currentVertex = agentState.getCurrentVertex();

          agentStrategy.arrived(
                  new AgentContext(
                          scenario.getAttackInterval(),
                          currentVertex, 
                          graph.adjacentVertices(currentVertex), 
                          ImmutableSet.copyOf(criticalVertices),
                          targetVertices.contains(currentVertex),
                          timestep,
                          graph));
        }
      });
      
      //get all of the agent locations into a set
      agentLocations = agentStates.values().stream()
              .filter(AgentState::isAtVertex)
              .map(AgentState::getCurrentVertex)
              .collect(ImmutableSet.toImmutableSet());
      
      // simulate adversary behavior
      adversaryStates.forEach((adversaryStrategy, adversaryState) -> {
        final VertexId targetVertex = adversaryState.getTarget();
        
        adversaryState.timestep();
        
        if (adversaryState.isAttacking()) {  // determine if any attacks have become thwarted or successful
          if (adversaryState.getAttackingTimeStepCount() > scenario.getAttackInterval()) {
              throw new IllegalStateException(
                      String.format(
                              "Attacking time step count of %d is above the attack interval %d", 
                              adversaryState.getAttackingTimeStepCount(), 
                              scenario.getAttackInterval()));
          }
          
          //check if the attack succeeded first, because we moved the agent before this
          if (adversaryState.getAttackingTimeStepCount() == scenario.getAttackInterval()) {
            adversaryState.endAttack(true);
          } else if (agentLocations.contains(targetVertex)) {
            criticalVertices.add(targetVertex);
            adversaryState.endAttack(false);
          }
        } else { //determine if the adversary starts attacking
          final AdversaryContext adversaryContext = new AdversaryContext(
                  scenario.getAttackInterval(), 
                  timestep, 
                  agentLocations.contains(targetVertex));

          if (adversaryStrategy.attack(adversaryContext)) {
            adversaryState.beginAttack();
            
            //if an agent is currently at the vertex, the attack immediately fails
            if (agentLocations.contains(targetVertex)) {
                criticalVertices.add(targetVertex);
                adversaryState.endAttack(false);
            }
          }
        }
      });
      
      //determine where agents move to
      agentStates.forEach((agentStrategy, agentState) -> {
        if (agentState.isAtVertex()) {
          final VertexId currentVertex = agentState.getCurrentVertex();
          final VertexId nextVertex = agentStrategy.choose(
                  new AgentContext(
                          scenario.getAttackInterval(),
                          currentVertex, 
                          graph.adjacentVertices(currentVertex), 
                          ImmutableSet.copyOf(criticalVertices),
                          targetVertices.contains(currentVertex),
                          timestep,
                          graph));
        
          if (!graph.adjacentVertices(currentVertex).contains(nextVertex) && !currentVertex.equals(nextVertex)) {
            throw new IllegalStateException("Agent chose an invalid vertex to move to");
          }
        
          if (!nextVertex.equals(agentState.getCurrentVertex())) {
            final EdgeWeight edgeWeight = graph.edgeWeight(currentVertex, nextVertex);
          
            agentState.startMove(nextVertex, edgeWeight.getValue());
          } else {
            agentState.stay();
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
      final int agentChoseToMoveCount = agentStates.values().stream().mapToInt(AgentState::getChoseToMoveCount).sum();
      final int agentChoseToStayCount = agentStates.values().stream().mapToInt(AgentState::getChoseToStayCount).sum();
      final int agentTimeStepsSpentMoving = agentStates.values().stream().mapToInt(AgentState::getTimestepsSpentMoving).sum();
      
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
            .agentChoseToMoveCount(agentChoseToMoveCount)
            .agentChoseToStayCount(agentChoseToStayCount)
            .agentTimestepsSpentMoving(agentTimeStepsSpentMoving)
            .build();
    }
  }
}
