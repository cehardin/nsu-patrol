package edu.nova.chardin.patrol.experiment.runner;

import edu.nova.chardin.patrol.experiment.Match;
import edu.nova.chardin.patrol.experiment.result.GameResult;
import edu.nova.chardin.patrol.experiment.result.MatchResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({@Inject}))
@Value
@Getter(AccessLevel.NONE)
public class GameResultToMatchResultConverter implements Function<GameResult, MatchResult> {

  @NonNull
  Match match;

  @Override
  public MatchResult apply(@NonNull final GameResult gr) {
    return MatchResult.builder()
            .match(match)
            .idlenessTargetVerticesStatistics(gr.getIdlenessTargetVerticesStatistics())
            .idlenessNonTargetVerticesStatistics(gr.getIdlenessNonTargetVerticesStatistics())
            .idlenessAllVerticesStatistics(gr.getIdlenessAllVerticesStatistics())
            .numberOfTargetVerticesThwartedThenCompromised(IntStream.of(gr.getNumberOfTargetVerticesThwartedThenCompromised()).summaryStatistics())
            .numberOfTargetVerticesNotAttacked(IntStream.of(gr.getNumberOfTargetVerticesNotAttacked()).summaryStatistics())
            .numberOfTargetVerticesDiscoveredCritical(IntStream.of(gr.getNumberOfTargetVerticesDiscoveredCritical()).summaryStatistics())
            .numberOfTargetVerticesCompromised(IntStream.of(gr.getNumberOfTargetVerticesCompromised()).summaryStatistics())
            .build();
  }

}
