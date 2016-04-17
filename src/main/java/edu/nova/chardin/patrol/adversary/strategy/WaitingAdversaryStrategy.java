package edu.nova.chardin.patrol.adversary.strategy;

import edu.nova.chardin.patrol.adversary.AdversaryStrategy;

/**
 *
 * @author cehar
 */
public final class WaitingAdversaryStrategy implements AdversaryStrategy {

    private boolean wasOccupied = false;

    public WaitingAdversaryStrategy() {
    }

    @Override
    public boolean attack(final int k, final boolean occupied) {
        if (wasOccupied) {
            return !occupied;
        } else {
            wasOccupied = occupied;
            return false;
        }
    }

}
