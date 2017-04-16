package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.graph.ImmutableValueGraph;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.event.Lifecycle;
import edu.nova.chardin.patrol.experiment.event.ScenarioLifecycleEvent;
import edu.nova.chardin.patrol.experiment.result.ExperimentResult;
import edu.nova.chardin.patrol.graph.EdgeWeight;
import edu.nova.chardin.patrol.graph.TspLengthCalculator;
import edu.nova.chardin.patrol.graph.VertexId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({@Inject}))
@Value
@Getter(AccessLevel.NONE)
public class ExperimentRunner implements Function<Experiment, ExperimentResult> {
  
  EventBus eventBus;
  ScenarioRunner scenarioRunner;
  TspLengthCalculator tspLengthCalculator;

  @Override
  public ExperimentResult apply(@NonNull final Experiment experiment) {
    final ImmutableSet<Scenario> scenarios = createScenarios(experiment);
    
    scenarios.parallelStream()
            .map(s -> new ScenarioLifecycleEvent(s, Lifecycle.Created))
            .forEach(e -> eventBus.post(e));
    
    return ExperimentResult.builder()
            .experiment(experiment)
            .scenarioResults(
                    scenarios.parallelStream()
                            .map(scenarioRunner)
                            .collect(Collectors.toList()))
            .build();
  }
  
   private ImmutableSet<Scenario> createScenarios(@NonNull final Experiment experiment) {

    final ImmutableMap<ImmutableValueGraph<VertexId, EdgeWeight>, Integer> tspLengths;
    final Set<Scenario> scenarios = ConcurrentHashMap.newKeySet(
            experiment.getGraphs().size() 
                    * experiment.getAgentToVertexCountRatios().size() 
                    * experiment.getAdversaryToVertexCountRatios().size());
    
    tspLengths = ImmutableMap.copyOf(
            experiment.getGraphs()
                    .values()
                    .parallelStream()
                    .collect(
                            Collectors.toMap(
                                    Function.identity(), 
                                    tspLengthCalculator)));
    
    experiment.getGraphs().values().parallelStream().forEach(g -> {
      experiment.getAgentToVertexCountRatios().parallelStream().forEach(agentToVertexCountRatio -> {
        final int numberOfAgents = (int)Math.ceil(g.nodes().size() * agentToVertexCountRatio);
        final ImmutableSet<Integer> attackIntervals =
                ImmutableSet.copyOf(
                        experiment.getTspLengthFactors().parallelStream()
                                .map(factor -> (int) (factor * ((double) numberOfAgents / tspLengths.get(g))))
                                .collect(Collectors.toSet()));
        
        experiment.getAdversaryToVertexCountRatios().parallelStream().forEach(adversaryToVertexCountRatio -> {
          final int numberOfAdversaries = (int)Math.ceil(g.nodes().size() * adversaryToVertexCountRatio);
          scenarios.add(
                  Scenario.builder()
                          .experiment(experiment)
                          .graph(g)
                          .numberOfAgents(numberOfAgents)
                          .numberOfAdversaries(numberOfAdversaries)
                          .attackIntervals(attackIntervals)
                          .build());
        });
      });
    });

    return ImmutableSet.copyOf(scenarios);
  }

}
