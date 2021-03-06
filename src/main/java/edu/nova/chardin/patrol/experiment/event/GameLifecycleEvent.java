package edu.nova.chardin.patrol.experiment.event;

import edu.nova.chardin.patrol.experiment.Game;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class GameLifecycleEvent extends AbstractLifecycleEvent<Game> {

  public GameLifecycleEvent(Game subject, Lifecycle lifecycle) {
    super(subject, lifecycle);
  }
  
}
