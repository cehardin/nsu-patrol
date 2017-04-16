package edu.nova.chardin.patrol.experiment.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public abstract class AbstractLifecycleEvent<T> {
  
  @NonNull
  T subject;
  
  @NonNull
  Lifecycle lifecycle;
}
