package com.example.checkers.ai;

import com.example.checkers.game.GameNode;
import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;

import java.util.Comparator;
import java.util.List;

/**
 * A concrete implementation of an Agent using the Monte Carlo Tree Search algorithm.
 *
 * @author Ramiar Odendaal
 */
public class MCTSAgent extends Agent{
    //TODO: Implement timeout, tree reuse?
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

        int budget = 8000;
        GameNode<GameState, Action> root = new GameNode<>(clonedState, null, null, 0);
        while(budget>0){
            GameNode<GameState, Action> selectedNode = selectionStep(root);
            GameNode<GameState, Action> expandedNode = expansionStep(selectedNode);
            int estimatedUtility = playoutStep(expandedNode);
            backPropagationStep(expandedNode, estimatedUtility);
            budget--;
        }
        return root.getAllChildNodes().stream()
                .max(Comparator.comparingInt(GameNode::getReward))
                .map(node -> node.action)
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
                    .max(Comparator.comparingInt(this::uct))
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
    private int uct(GameNode<GameState, Action> node){
        return (int) ((node.getReward()/node.getVisits()) + Math.sqrt((Math.log(node.parent.getVisits())/Math.log(2)/node.getVisits())));
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
    //TODO: Save all actions in a list and undo them?

    /**
     * Simulates a series of moves from the node until a terminal state or a maximum depth is reached.
     * Evaluates the utility of the furthest node reached in the simulation.
     * @param node the start of the simulation
     * @return the estimated utility of the furthest node reached
     */
    private int playoutStep(GameNode<GameState, Action> node){

        GameNode<GameState, Action> currentNode = new GameNode<>(node.state.clone(), node.action, node.parent, node.depth);
        int depth = 10;
        while(!currentNode.isTerminal() && depth > 0){
            Action randomAction = currentNode.getRandomAction();
            currentNode = currentNode.successor(currentNode.state.updateStateWithAction(randomAction), randomAction);
            depth--;
        }

        /*
        GameNode<GameState, Action> currentNode = new GameNode<>(node.state, null, null, node.depth);
        int depth = 10;
        while(!currentNode.isTerminal() && depth > 0){
            Action randomAction = currentNode.getRandomAction();
            GameState updatedState = currentNode.state.updateStateWithAction(randomAction);
            currentNode = currentNode.successor(updatedState, randomAction);
            depth--;
        }
        int estimatedUtility = evaluate(currentNode.state, currentNode.depth);
        List<Action> executedActions = currentNode.getActions();
        for(int i = executedActions.size() - 1; i >= 0; i--){
            node.state.updateStateWithUndoAction(executedActions.get(i));
        }

         */
        return evaluate(currentNode.state, currentNode.depth);
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