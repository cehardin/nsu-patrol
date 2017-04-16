package edu.nova.chardin.patrol.experiment.event;

import edu.nova.chardin.patrol.experiment.Match;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class MatchLifecycleEvent extends AbstractLifecycleEvent<Match> {

  public MatchLifecycleEvent(Match subject, Lifecycle lifecycle) {
    super(subject, lifecycle);
  }
  
}
