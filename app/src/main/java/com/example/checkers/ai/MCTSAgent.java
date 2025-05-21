package com.example.checkers.ai;

import com.example.checkers.game.GameNode;
import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A concrete implementation of an Agent using the Monte Carlo Tree Search algorithm.
 *
 * @author Ramiar Odendaal
 */
public class MCTSAgent extends Agent{
   private int budget;
    public MCTSAgent(String name, int timeSlice, int budget, boolean isAgentPlayerOne) {
        super(name, timeSlice, isAgentPlayerOne);
        this.budget = budget;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Action getNextMove(GameState state) {

        GameState clonedState = state.clone();
        List<Action> availableActions = clonedState.generateLegalActions();
        if(availableActions.size() == 1){
            return availableActions.get(0);
        }
        long startTime, endTime;
        startTime = System.nanoTime();
        GameNode<GameState, Action> node = monteCarloTreeSearch(clonedState);
        //If not undone stone will remain in its updated position -- bad design
        clonedState.updateStateWithUndoAction(node.action);
        endTime = (System.nanoTime() - startTime)/1_000_000;
        node.action.setTimeTaken(endTime);
        return node.action;
    }

    private GameNode<GameState, Action> monteCarloTreeSearch(GameState state){
        AtomicInteger budget = new AtomicInteger(this.budget);

        GameNode<GameState, Action> root = new GameNode<>(state, null, null, 0);
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(timeSlice);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            budget.set(0);
        });
        t.start();
        while(budget.get() > 0){
            GameNode<GameState, Action> selectedNode = selectionStep(root);
            GameNode<GameState, Action> expandedNode = expansionStep(selectedNode);
            double estimatedUtility = playoutStep(expandedNode);
            backPropagationStep(expandedNode, estimatedUtility);
            budget.getAndDecrement();
        }
        return root.getAllChildNodes().stream()
                .max(Comparator.comparingDouble(node -> node.getReward()))
                .get();
    }


    /**
     * Selects the next node based on the value provided by the UCT formula.
     *
     * @param node the node to start from
     * @return the best child node as defined by the UCT formula
     */
    private GameNode<GameState, Action> selectionStep(GameNode<GameState, Action> node){
        if(!node.isTerminal() && node.isFullyExplored()){
            return selectionStep(node.getAllChildNodes().stream()
                    .max(Comparator.comparingDouble(this::uct))
                    .get());
        }
        return node;
    }

    /**
     * Calculates the UCT value of a node.
     *
     * @param node The node to be evaluated.
     * @return The current UCT value of the node
     */
    private double uct(GameNode<GameState, Action> node) {
        if (node.getVisits() == 0) {
            return Double.POSITIVE_INFINITY;
        }
        double exploitationTerm = node.getReward() / node.getVisits();
        double explorationTerm = Math.sqrt(Math.log(node.parent.getVisits()) / node.getVisits());
        double C = Math.sqrt(2);

        return exploitationTerm + C * explorationTerm;
    }

    /**
     * Expands a selected node by generating its next child node.
     *
     * @param node The node to be expanded
     * @return The new child node
     */
    private GameNode<GameState, Action> expansionStep(GameNode<GameState, Action> node){
        if(!node.isTerminal() && !node.isFullyExplored()){
            return node.generateNextChildNode();
        }
        else{
            return node;
        }
    }
    public Action getBestAction(GameState state, List<Action> unexploredActions, int depth){
        GameState clonedState = state.clone();
        Action bestChild = null;
        if(state.getCurrentPlayer().getName().equals(this.name)){
            int currentBest = Integer.MIN_VALUE;
            for(Action a : unexploredActions){
                clonedState.updateStateWithAction(a);
                int evaluation = evaluate(clonedState, depth+1);
                if(evaluation > currentBest){
                    currentBest = evaluation;
                    bestChild = a;
                }
                clonedState.updateStateWithUndoAction(a);
            }
        }
        else{
            int currentBest = Integer.MAX_VALUE;
            for(Action a : unexploredActions){
                clonedState.updateStateWithAction(a);
                int evaluation = evaluate(clonedState, depth+1);
                if(evaluation < currentBest){
                    currentBest = evaluation;
                    bestChild = a;
                }
                clonedState.updateStateWithUndoAction(a);
            }
        }

        return bestChild;
    }
    /**
     * Simulates a series of moves from the node until a terminal state or a maximum depth is reached.
     * Evaluates the utility of the furthest node reached in the simulation.
     * @param node the start of the simulation
     * @return the estimated utility of the furthest node reached
     */
    private double playoutStep(GameNode<GameState, Action> node) {
        GameNode<GameState, Action> currentNode = node.clone();
        int depth = 6;
        while(!currentNode.isTerminal() && depth > 0){
            Action randomAction = getBestAction(currentNode.state, currentNode.getUnexploredActions(), currentNode.depth);
            currentNode = currentNode.successor(currentNode.state.updateStateWithAction(randomAction), randomAction);
            depth--;
        }
        double utility = 0;
        if(currentNode.isTerminal()) {
            if (currentNode.state.isDraw()) {
                utility = 1500;
            } else {
                utility = currentNode.state.getWinner().getName().equals(this.name) ? 3000 : -3000;
            }
        }
        else{
            double evaluation = evaluate(currentNode.state, currentNode.depth);
            utility = evaluation;
        }
        return utility;
    }

    /**
     * Updates all parent nodes in the path of a node with the result of a simulation from the node
     * @param node the node used in the simulation
     * @param reward the estimated utility returned from the simulation
     */
    private void backPropagationStep(GameNode<GameState, Action> node, double reward){
        GameNode<GameState, Action> currentNode = node;
        while(currentNode != null){
            currentNode.update(reward);
            currentNode = currentNode.parent;
        }
    }
}