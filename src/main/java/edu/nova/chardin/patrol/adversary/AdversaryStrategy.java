package edu.nova.chardin.patrol.adversary;

/**
 * An adversary strategy.
 */
public interface AdversaryStrategy {

  boolean attack(int attackInterval, long timestep, boolean occupied);
}
