package com.example.checkers.game;

import com.example.checkers.game.models.actions.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A node representation of the current state for use in Monte Carlo Tree Search.
 *
 * @param <S> the state of the game at this node
 * @param <A> the action chosen at this node
 *
 * @author Ramiar Odendaal
 */
public class GameNode<S,A>{
    public final GameState state;
    public GameNode<S, A> parent;
    public final Action action;
    public final int depth;
    private Set<GameNode<S,A>> childNodes;
    private Random random = new Random();
    private double reward;
    private int visits;
    private List<Action> unexploredActions;

    public GameNode(S state, A action, GameNode<S, A> parent, int depth){
        this.state = (GameState) state;
        this.action = (Action) action;
        this.parent = parent;
        this.depth = depth;
        this.reward = 0;
        this.visits = 0;
        this.unexploredActions = this.state.generateLegalActions();
        childNodes = new HashSet<>();
    }

    /**
     * Returns the set of all expanded child nodes of this node
     *
     * @return the set of child nodes
     */
    public Set<GameNode<S,A>> getAllChildNodes(){
        return childNodes;
    }

    /**
     * Returns the action which led to this node.
     *
     * @return The action that led to this node
     */
    public Action getAction(){
        return action;
    }

    public void resetChildren(){
        childNodes = null;
    }

    public void resetParent(){
        if(this.parent != null){
            parent.resetChildren();
        }
        this.parent = null;
    }

    /**
     * Returns true if and only if the node is fully explored.
     *
     * @return true if no more unexplored actions, false otherwise
     */
    public boolean isFullyExplored(){
        return unexploredActions.isEmpty();
    }

    /**
     * Gets a random action from the list of unexplored actions.
     *
     * @return A random action that can be made from this node
     */
    public Action getRandomAction(){
        return unexploredActions.get(random.nextInt(unexploredActions.size()));
    }

    public List<Action> getUnexploredActions(){
        return unexploredActions;
    }

    /**
     * Gets the current reward (potential) of this node.
     * @return the reward
     */
    public double getReward(){
        return reward;
    }

    /**
     * Gets the number of visits for this node (updated during back-propagation in MCTS).
     *
     * @return the number of visits to this node
     */
    public int getVisits(){
        return visits;
    }

    /**
     * Updates the reward and the number of visits during back-propagation in MCTS.
     *
     * @param value the estimated utility to be added to the reward
     */
    public void update(double value){
        visits++;
        this.reward += value;
    }

    /**
     * Generates the next child node if unexplored actions remain for this node.
     *
     * @return a random child node based on the remaining unexplored actions
     * @throws IllegalStateException Throws exception if no unexplored actions remain
     */
    public GameNode<S, A> generateNextChildNode() {
        if (unexploredActions.isEmpty()) {
            throw new IllegalStateException("No more unexplored actions available");
        } else {
            int r = random.nextInt(unexploredActions.size());
            Action a = unexploredActions.get(r);
            unexploredActions.remove(a);
            GameNode<S, A> child = new GameNode(state.clone().updateStateWithAction(a), a, this, depth + 1);
            childNodes.add(child);
            return child;
        }
    }

    /**
     * Checks if this node is a root node.
     *
     * @return true if this node has no parent
     */
    public boolean isRoot(){
        return this.parent == null;
    }

    /**
     * Checks if this node's state is terminal.
     *
     * @return True if the state is terminal (game is over)
     */
    public boolean isTerminal(){
        return state.isTerminal();
    }

    /**
     * Generates a list of all actions that led to this node from the root.
     *
     * @return An array list representing all actions taken to reach this node
     */
    public List<Action> getActionPathToThisNode() {
        if (this.isRoot()) {
            return new ArrayList();
        } else {
            List actions = this.parent.getActionPathToThisNode();
            actions.add(this.action);
            return actions;
        }
    }

    /**
     * Used to create successor nodes for use in simulated playouts.
     *
     * @param state the state of the target node
     * @param action the action taken to progress toward the target node
     * @return a GameNode representing the next node in a playout
     */
    public GameNode<S, A> successor(S state, A action) {
        if (state == null) {
            throw new IllegalArgumentException("Null states not allowed.");
        } else if (action == null) {
            throw new IllegalArgumentException("Null actions not allowed.");
        } else {
            return new GameNode<>(state, action, this, depth + 1);
        }
    }
    public boolean equals(GameNode<S, A> node) {
        if (this == node) {
            return true;
        }
        else if (node != null) {
            if (this.depth == node.depth && this.visits == node.visits) {
                if (Double.compare(node.reward, this.reward) != 0) {
                    return false;
                }
                else if (!this.state.equals(node.state)) {
                    return false;
                }
                else {
                    if (this.action != null) {
                        if (!this.action.equals(node.action)) {
                            return false;
                        }
                    }
                    else if (node.action != null) {
                        return false;
                    }
                    return this.childNodes.equals(node.childNodes);
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    public GameNode<S, A> clone(){
        return new GameNode(this.state.clone(), action, null, this.depth);
    }

}
