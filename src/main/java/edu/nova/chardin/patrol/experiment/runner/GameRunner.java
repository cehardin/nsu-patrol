package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.eventbus.EventBus;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.Game;
import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.event.GameLifecycleEvent;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import edu.nova.chardin.patrol.experiment.result.GameResult;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
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
    
    final Match match = game.getMatch();
    final Scenario scenario = match.getScenario();
    final Experiment experiment = scenario.getExperiment();
    final Map<AgentStrategy, VertexId> agentLocations = new HashMap<>();
//    final ImmutableMap<Adversary, VertexId> adversaryTargets;
    
    eventBus.post(new GameLifecycleEvent(game, Lifecycle.Started));
    
//    game.getAgentStartingPositions().forEach(agentStartingVertexId -> {
//      final AgentStrategy agentStrategy = null;
//      agentLocations.put(agentStrategy, agentStartingVertexId);
//    });
//    
//    adversaryTargets = game.getTargets()
//            .stream()
//            .collect(ImmutableMap.toImmutableMap(
//                    t -> new Adversary(null, null, 0),
//                    Function.identity()));
//    
//    IntStream.rangeClosed(1, experiment.getNumberOfTimestepsPerGame()).forEach(timestep -> {
//      final ImmutableSet<VertexId> occupiedVertices;
//      
//      agentLocations.entrySet().forEach(agentLocation -> {
//        final AgentContext context = null;
//        final AgentStrategy strategy = agentLocation.getKey();
//        
//        strategy.arrived(context);
//        agentLocation.setValue(strategy.choose(context));
//      });
//      
//      occupiedVertices = agentLocations.values().stream().collect(ImmutableSet.toImmutableSet());
//      
//      adversaryTargets.entrySet().forEach(adversaryTarget -> {
//        final Adversary adversary = adversaryTarget.getKey();
//        final VertexId target = adversaryTarget.getValue();
//        
//        adversary.decide(occupiedVertices.contains(target), timestep);
//      });
//    });

    try {
      Thread.sleep(game.getMatch().getScenario().getExperiment().getNumberOfTimestepsPerGame() / 10);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    //after done
    eventBus.post(new GameLifecycleEvent(game, Lifecycle.Finished));
    
    return GameResult.builder()
            .game(game)
            .idlenessAllVerticesStatistics(IntStream.empty().summaryStatistics())
            .idlenessNonTargetVerticesStatistics(IntStream.empty().summaryStatistics())
            .idlenessTargetVerticesStatistics(IntStream.empty().summaryStatistics())
            .numberOfTargetVerticesCompromised(0)
            .numberOfTargetVerticesDiscoveredCritical(0)
            .numberOfTargetVerticesNotAttacked(0)
            .numberOfTargetVerticesThwartedThenCompromised(0)
            .build();
  }
  
}
