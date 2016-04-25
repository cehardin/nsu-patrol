package edu.nova.chardin.patrol.adversary;

import com.google.common.base.Preconditions;

/**
 *
 * @author cehar
 */
public class Adversary {
    private final AdversaryStrategy strategy;
    private final int targetVertex;
    private boolean attacking = false;
    private int timeStepsAttacting = 0;

    public Adversary(AdversaryStrategy strategy, int targetVertex) {
        this.strategy = strategy;
        this.targetVertex = targetVertex;
    }

    public AdversaryStrategy getStrategy() {
        return strategy;
    }

    public int getTargetVertex() {
        return targetVertex;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public int getTimeStepsAttacting() {
        return timeStepsAttacting;
    }

    public void beginAttack() {
        Preconditions.checkState(!attacking, "already attacking");
        attacking = true;
        timeStepsAttacting = 0;
    }
    
    public void incrementAttack() {
        Preconditions.checkState(attacking, "not attacking");
        timeStepsAttacting++;
    }
    public void endAttack() {
        Preconditions.checkState(attacking, "not attacking");
        attacking = false;
        timeStepsAttacting = 0;
    }
    
}
