package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    final Set<VertexId> thwartedVertices = new HashSet<>();
    
    eventBus.post(new GameLifecycleEvent(game, Lifecycle.Started));

    // create agents
    IntStream.range(0, scenario.getNumberOfAgents()).forEach(agentNumber -> {
      final AgentStrategy agentStrategy = match.getAgentStrategyFactory().get();
      final ImmutableList<VertexId> verticesList = graph.getVertices().asList();
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
      final AdversaryStrategy adversaryStrategy = match.getAdversaryStrategyFactory().get();
      final ImmutableList<VertexId> verticesList = graph.getVertices().asList();
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
    IntStream.rangeClosed(1, scenario.getNumberOfTimestepsPerGame()).forEach(timestep -> {
      
      final Set<VertexId> agentLocations;
      
      //compute agent locations
      agentStates.forEach((agentStrategy, agentState) -> {
        if (agentState.timestep()) {
          final VertexId currentVertex = agentState.getCurrentVertex();

          agentStrategy.arrived(
                  new AgentContext(
                          match.getAttackInterval(),
                          currentVertex, 
                          graph.adjacentVertices(currentVertex), 
                          ImmutableSet.copyOf(thwartedVertices),
                          adversaryStates.values().stream().map(AdversaryState::getTarget).anyMatch(Predicate.isEqual(currentVertex)),
                          timestep,
                          graph));
        }
      });
      
      agentLocations = agentStates.values().stream()
              .filter(AgentState::isAtVertex)
              .map(AgentState::getCurrentVertex)
              .collect(Collectors.toSet());
      
      // simulate adversary behavior
      adversaryStates.entrySet().forEach(e -> {
        final AdversaryState adversaryState = e.getValue();
        
        adversaryState.timestep();
        
        if (adversaryState.isAttacking()) {  // determine if any attacks have become thwarted or successful
          if (agentLocations.contains(adversaryState.getTarget())) {
            targetThwartedCounts.get(adversaryState.getTarget()).incrementAndGet();
            thwartedVertices.add(adversaryState.getTarget());
            adversaryState.endAttack();
          } else {
            if (adversaryState.getAttackingTimeStepCount() == match.getAttackInterval()) {
              targetCompromisedCounts.get(adversaryState.getTarget()).incrementAndGet();
              adversaryState.endAttack();
            }
          }
        } else { //determine if any adversaries start attacking
          final AdversaryStrategy adversaryStrategy = e.getKey();
          final VertexId targetVertex = adversaryState.getTarget();
          final AdversaryContext adversaryContext = new AdversaryContext(
                  match.getAttackInterval(), 
                  timestep, 
                  agentLocations.contains(targetVertex));

          if (adversaryStrategy.attack(adversaryContext)) {
            adversaryState.beginAttack();
            targetAttackedCounts.get(adversaryState.getTarget()).incrementAndGet();
          }
        }
      });
      
      //determine where agents move to
      agentStates.entrySet().stream().filter(e -> e.getValue().isAtVertex()).forEach(e -> {
        final AgentStrategy agentStrategy = e.getKey();
        final AgentState agentState = e.getValue();
        final VertexId currentVertex = agentState.getCurrentVertex();
        final VertexId nextVertex = agentStrategy.choose(
                new AgentContext(
                        match.getAttackInterval(),
                        currentVertex, 
                        graph.adjacentVertices(currentVertex), 
                        ImmutableSet.copyOf(thwartedVertices),
                        adversaryStates.values().stream().map(AdversaryState::getTarget).anyMatch(Predicate.isEqual(currentVertex)),
                        timestep,
                        graph));
        
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
      final double criticalVerticesCount = thwartedVertices.size();
      final double generalEffectiveness = criticalVerticesCount == 0.0 ? 0.0 : targetNotCompromizedCount / criticalVerticesCount;
      final double deteranceEffectiveness = criticalVerticesCount == 0.0 ? 0.0 : targetNotAttackedCount / criticalVerticesCount;
      final double patrolEffectiveness = criticalVerticesCount == 0.0 ? 0.0 : criticalVerticesCount / criticalVerticesCount;
      final double defenseEffectiveness = criticalVerticesCount == 0.0 ? 0.0 : targetNotCompromizedCount / criticalVerticesCount;
      final int attackCount = targetAttackedCounts.values().stream().mapToInt(AtomicInteger::get).sum();
      final int thwartedCount = targetThwartedCounts.values().stream().mapToInt(AtomicInteger::get).sum();
      final int compromizedCount = targetCompromisedCounts.values().stream().mapToInt(AtomicInteger::get).sum();
      final double succesfullAttackRatio = attackCount == 0 ? 0.0 : (double)compromizedCount / (double)attackCount;
      final double thwartedAttackRatio = attackCount == 0 ? 0.0 : (double)thwartedCount / (double)attackCount;
      
      return GameResult.builder()
            .game(game)
            .executionTimeMilliSeconds((double)stopwatch.elapsed(TimeUnit.MICROSECONDS) / 1000.0)
            .timeStepExecutionTimeMicroSeconds((double)stopwatch.elapsed(TimeUnit.NANOSECONDS) / 1000.0 / scenario.getNumberOfTimestepsPerGame())
            .generalEffectiveness(generalEffectiveness)
            .deterenceEffectiveness(deteranceEffectiveness)
            .patrolEffectiveness(patrolEffectiveness)
            .defenseEffectiveness(defenseEffectiveness)
            .attackCount(attackCount)
            .compromisedCount(compromizedCount)
            .twartedCount(thwartedCount)
            .criticalVerticesCount((int)criticalVerticesCount)
            .succesfulAttackRatio(succesfullAttackRatio)
            .thwartedAttackRatio(thwartedAttackRatio)
            .build();
    }
  }
}
