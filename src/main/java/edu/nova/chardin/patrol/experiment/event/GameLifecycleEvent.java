package edu.nova.chardin.patrol.experiment.event;

import edu.nova.chardin.patrol.experiment.Game;
import lombok.Value;

@Value
public class GameLifecycleEvent extends AbstractLifecycleEvent<Game> {

  public GameLifecycleEvent(Game subject, Lifecycle lifecycle) {
    super(subject, lifecycle);
  }
  
}
