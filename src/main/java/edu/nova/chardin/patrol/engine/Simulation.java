package edu.nova.chardin.patrol.engine;

import edu.nova.chardin.patrol.adversary.Adversary;
import edu.nova.chardin.patrol.adversary.AdversaryStrategy;
import edu.nova.chardin.patrol.agent.Agent;
import edu.nova.chardin.patrol.agent.AgentStrategy;
import edu.nova.chardin.patrol.agent.Context;
import edu.nova.chardin.patrol.graph.PatrolGraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 *
 * @author cehar
 */
public class Simulation {

    private final PatrolGraph graph;
    private final Set<Agent> agents;
    private final Set<Adversary> adversaries;
    private final int k;
    private final Map<Integer, Integer> criticalVertices = new HashMap<>();
    private final Set<Integer> succesfullyAttacked = new HashSet<>();
    private long totalAttacks = 0;
    private long successfulAttacks = 0;

    public Simulation(PatrolGraph graph, Set<Agent> agents, Set<Adversary> adversaries, int k) {
        this.graph = graph;
        this.agents = agents;
        this.adversaries = adversaries;
        this.k = k;
    }

    public long getTotalAttacks() {
        return totalAttacks;
    }

    public long getSuccessfulAttacks() {
        return successfulAttacks;
    }
    
    public void step() {
        final Set<Integer> occupiedVertices;

        //increment the time that critical vertices have gone unvisited
        for(final Map.Entry<Integer, Integer> criticalVertex : criticalVertices.entrySet()) {
            criticalVertex.setValue(criticalVertex.getValue() + 1);
        }
        
        //transition agents that are on edges
        for (final Agent agent : agents) {
            if (agent.isOnEdge()) {
                if(agent.incrementOnEdge()) {
                    //moved off of edge onto vertex
                    if(succesfullyAttacked.contains(agent.getVertex())) {
                        criticalVertices.put(agent.getVertex(), 0);
                    }
                }
            }
        }
        
        //we already noted these, we can remove
        succesfullyAttacked.removeAll(criticalVertices.keySet());

        //figure out all of the vertices that are occupied by agents
        occupiedVertices = agents.stream().filter(a -> !a.isOnEdge()).map(a -> a.getVertex()).collect(Collectors.toSet());

        //all occupied vertices have been unvisited for zero time steps
        for(final int occupiedVertex : occupiedVertices) {
            if(criticalVertices.containsKey(occupiedVertex)) {
                criticalVertices.put(occupiedVertex, 0);
            }
        }
        
        //proccess each adversary
        for (final Adversary adversary : adversaries) {
            final int vertex = adversary.getTargetVertex();
            final boolean occupied = occupiedVertices.contains(vertex);

            if (adversary.isAttacking()) {
                if (occupied) {
                    //attack has been thwarted
                    adversary.endAttack();
                    criticalVertices.put(vertex, 0); //agents know about this vertex now
                } else {
                    //keep up the attack
                    adversary.incrementAttack();

                    //has the attack been succesful?
                    if (k == adversary.getTimeStepsAttacting()) {
                        successfulAttacks++; //yup, it has
                        adversary.endAttack();
                        succesfullyAttacked.add(vertex); //mark the ertex as having been sucesfully attacked
                    }
                }
            } else {
                final AdversaryStrategy strategy = adversary.getStrategy();

                //not attacking, determine if adversary wants to attack
                if (strategy.attack(k, occupied)) {
                    totalAttacks++;
                    
                    if(occupied) { //attack will be immediately thwarted
                        adversary.endAttack();
                        criticalVertices.put(vertex, 0); //agents now now ths is a critical vertex
                    }
                }
            }
        }
        
        //process each agent that is not on an edge
        for(final Agent agent : agents) {
            if(!agent.isOnEdge()) {
                final AgentStrategy strategy = agent.getStrategy();
                final DefaultWeightedEdge chosenEdge;
                final int targetVertex;
                
                //agent strategy must pick an edge
                chosenEdge = strategy.process(new Context() {
                    @Override
                    public int getK() {
                        return k;
                    }

                    @Override
                    public Integer getVertex() {
                        return agent.getVertex();
                    }

                    @Override
                    public Set<DefaultWeightedEdge> getEdges() {
                        return graph.edgesOf(agent.getVertex());
                    }

                    @Override
                    public Set<Integer> getCriticalVertices() {
                        return criticalVertices.keySet();
                    }

                    @Override
                    public int getDistance(DefaultWeightedEdge edge, Integer criticalVertex) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public int getTimestepsUnoccupied(Integer criticalVertex) {
                        return criticalVertices.get(criticalVertex);
                    }
                });
                
                //determine the destination vertex of the chosen edge
                if(graph.getEdgeSource(chosenEdge) != agent.getVertex()) {
                    targetVertex = graph.getEdgeSource(chosenEdge);
                }
                else {
                    targetVertex = graph.getEdgeTarget(chosenEdge);
                }
                
                //the agent begins moving
                agent.moveToEdge(chosenEdge, (int)graph.getEdgeWeight(chosenEdge), targetVertex);
                
            }
        }
    }
}
