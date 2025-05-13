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
    private Random random = new Random();
    private AtomicInteger budget = new AtomicInteger();
    private Runnable timeOutTask = () -> {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(timeSlice);
            long end = System.currentTimeMillis() - start;
            budget.set(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };
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

        counter++;
        return monteCarloTreeSearch(clonedState).action;
    }
    //TODO: Run in separate thread
    private GameNode<GameState, Action> monteCarloTreeSearch(GameState state){
        int budget = 8000;
        GameNode<GameState, Action> root = new GameNode<>(state, null, null, 0);
        long searchStartTime = System.currentTimeMillis();
        while(budget > 0){
            GameNode<GameState, Action> selectedNode = selectionStep(root);
            GameNode<GameState, Action> expandedNode = expansionStep(selectedNode);
            long playoutStartTime = System.currentTimeMillis();
            double estimatedUtility = playoutStep(expandedNode);
            long playoutEndTime = System.currentTimeMillis() - playoutStartTime;
            backPropagationStep(expandedNode, estimatedUtility);
            budget--;
        }
        long end = System.currentTimeMillis() - searchStartTime;
        return root.getAllChildNodes().stream()
                .max(Comparator.comparingDouble(node -> node.getReward()/node.getVisits()))
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
        //TODO: Check if selection is working properly
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
        //TODO: Check if UCT is calculated properly
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

    /**
     * Simulates a series of moves from the node until a terminal state or a maximum depth is reached.
     * Evaluates the utility of the furthest node reached in the simulation.
     * @param node the start of the simulation
     * @return the estimated utility of the furthest node reached
     */
    private double playoutStep(GameNode<GameState, Action> node){
        GameState state = node.state;
        int depth = 10;
        LinkedList<Action> undoList = new LinkedList<>();
        //TODO: Implement successor instead, maybe guided moves not random
        while(!state.isTerminal() && depth > 0){
            List<Action> availableActions = state.generateLegalActions();
            Action randomAction = availableActions.get(random.nextInt(availableActions.size()));
            state.updateStateWithAction(randomAction);
            undoList.push(randomAction);
            depth--;
        }
        double estimatedUtility = 0;
        if (state.isTerminal()) {
            if(!state.isDraw()){
                estimatedUtility = state.getWinner().getName().equals(this.name) ? 1 : -1;
            }
            else{
                estimatedUtility = 0.5;
            }
        }
        else{
            estimatedUtility = evaluate(state, node.depth + 10 - depth)/3000f; //divide by max evaluation return
        }

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
    private void backPropagationStep(GameNode<GameState, Action> node, double reward){
        GameNode<GameState, Action> currentNode = node;
        while(currentNode != null){
            currentNode.update(reward);
            currentNode = currentNode.parent;
        }
    }
}