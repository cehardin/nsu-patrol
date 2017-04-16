package edu.nova.chardin.patrol.experiment.event;

import edu.nova.chardin.patrol.experiment.Scenario;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ScenarioLifecycleEvent extends AbstractLifecycleEvent<Scenario> {

  public ScenarioLifecycleEvent(Scenario subject, Lifecycle lifecycle) {
    super(subject, lifecycle);
  }
  
}
