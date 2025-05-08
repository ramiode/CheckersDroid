package com.example.checkers.ai;

import com.example.checkers.game.GameNode;
import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A concrete implementation of an Agent using the Monte Carlo Tree Search algorithm.
 *
 * @author Ramiar Odendaal
 */
public class MCTSAgent extends Agent{
    private GameNode<GameState, Action> lastNode;
    public MCTSAgent(String name, int timeSlice, boolean isAgentPlayerOne) {
        super(name, timeSlice, isAgentPlayerOne);
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

        return monteCarloTreeSearch(clonedState).action;
    }

    private GameNode<GameState, Action> monteCarloTreeSearch(GameState state){
        AtomicInteger budget = new AtomicInteger(50000);
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(timeSlice);
                budget.set(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();

        GameNode<GameState, Action> root = new GameNode<>(state, null, null, 0);
        //Do I need to reset rewards? Use node.equals
        if(lastNode != null) {
            lastNode = lastNode.getAllChildNodes().stream()
                    .filter(node -> node.state.equals(state))
                    .findFirst()
                    .orElse(root)
                    .clone();
        }
        else{
            lastNode = root;
        }
        while(budget.get() > 0){
            GameNode<GameState, Action> selectedNode = selectionStep(lastNode);
            GameNode<GameState, Action> expandedNode = expansionStep(selectedNode);
            int estimatedUtility = playoutStep(expandedNode);
            backPropagationStep(expandedNode, estimatedUtility);
            budget.getAndDecrement();
        }
        lastNode = lastNode.getAllChildNodes().stream()
                .max(Comparator.comparingInt(GameNode::getReward))
                .get();
        return lastNode;
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
                    .orElse(node));
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
        double exploitationTerm = (double) node.getReward() / node.getVisits();
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
        try{
            return node.generateNextChildNode();
        }
        catch(IllegalStateException e){
            return node;
        }
    }

    /**
     * Simulates a series of moves from the node until a terminal state or a maximum depth is reached.
     * Evaluates the utility of the furthest node reached in the simulation.
     * @param node the start of the simulation
     * @return the estimated utility of the furthest node reached
     */
    private int playoutStep(GameNode<GameState, Action> node){
        GameState state = node.state;
        int depth = 100;
        LinkedList<Action> undoList = new LinkedList<>();
        Random random = new Random();
        while(!state.isTerminal() && depth > 0){
            List<Action> availableActions = state.generateLegalActions();
            //Action randomAction = availableActions.get(random.nextInt(availableActions.size()));
            int maxUtility = Integer.MIN_VALUE;
            Action randomAction = null;
            for(Action action : availableActions){
                state.updateStateWithAction(action);
                int utility = evaluate(state, node.depth + (100 - depth));
                if(utility > maxUtility){
                    randomAction = action;
                    maxUtility = utility;
                }
                state.updateStateWithUndoAction(action);
            }
            state.updateStateWithAction(randomAction);
            undoList.push(randomAction);
            depth--;
        }
        int estimatedUtility = evaluate(state, node.depth + (100-depth));
        for(int i = 0; i < undoList.size(); i++){
            state.updateStateWithUndoAction(undoList.pop());
        }
        return estimatedUtility;
    }

    /**
     * Updates all parent nodes in the path of a node with the result of a simulation from the node
     * @param node the node used in the simulation
     * @param reward the estimated utility returned from the simulation
     */
    private void backPropagationStep(GameNode<GameState, Action> node, int reward){
        GameNode<GameState, Action> currentNode = node;
        while(currentNode != null){
            currentNode.update(reward);
            currentNode = currentNode.parent;
        }
    }
}