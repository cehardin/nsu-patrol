package edu.nova.chardin.patrol.adversary;

/**
 * An adversary strategy.
 */
public interface AdversaryStrategy {

  boolean attack(int k, boolean occupied);
}
