package edu.nova.chardin.patrol.experiment.runner;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import edu.nova.chardin.patrol.experiment.Experiment;
import edu.nova.chardin.patrol.experiment.Scenario;
import edu.nova.chardin.patrol.experiment.result.ExperimentResult;
import edu.nova.chardin.patrol.graph.TspLengthCalculator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({@Inject}))
@Value
@Getter(AccessLevel.NONE)
public class ExperimentRunner implements Function<Experiment, ExperimentResult> {

  static ImmutableSet<Double> TSP_LENGTH_FACTORS = ImmutableSet.of(1.0 / 8.0, 1.0 / 4.0, 1.0 / 2.0, 1.0, 2.0);
  
  EventBus eventBus;
  ScenarioRunner scenarioRunner;
  TspLengthCalculator tspLengthCalculator;

  @Override
  public ExperimentResult apply(@NonNull final Experiment experiment) {
    
    return ExperimentResult.builder()
            .experiment(experiment)
            .scenarioResults(
                    createScenarios(experiment)
                            .parallelStream()
                            .map(scenarioRunner)
                            .collect(Collectors.toList()))
            .build();
  }
  
   private ImmutableSet<Scenario> createScenarios(@NonNull final Experiment experiment) {

    final ImmutableSet.Builder<Scenario> scenarios = ImmutableSet.builder();

    experiment.getGraphs().values().forEach(g -> {
      experiment.getNumbersOfAgents().forEach(numberOfAgents -> {
        final ImmutableSet<Integer> attackIntervals =
                ImmutableSet.copyOf(
                        TSP_LENGTH_FACTORS.stream()
                                .map(factor -> (int) (factor * ((double) numberOfAgents / tspLengthCalculator.apply(g))))
                                .collect(Collectors.toSet()));
        
        experiment.getNumbersOfAdversaries().forEach(numberOfAdversaries -> {
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

    return scenarios.build();
  }

}
