package com.example.checkers.ai;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;

import java.util.List;

/**
 * A concrete implementation of an Agent using the minimax algorithm.
 *
 * @author Ramiar Odendaal
 */
public class MinimaxAgent extends Agent{
    public final int DEPTH_LIMIT = 2;
    public MinimaxAgent(String name) {
        super(name);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Action getNextMove(GameState state) {
        List<Action> actions = state.clone().generateLegalActions();
        if(actions.size() == 1){
            return actions.get(0);
        }
        state.getBoard().printBoard();
        int bestUtility = Integer.MIN_VALUE;
        Action bestChild = null;

        for(Action a : actions){
            GameState clonedState = state.clone();
            clonedState.updateStateWithAction(a);
            int res = recursiveSearchForMove(clonedState, DEPTH_LIMIT, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if(res > bestUtility){
                bestUtility = res;
                bestChild = a;
            }
        }
        state.getBoard().printBoard();
        return bestChild;
    }

    private int recursiveSearchForMove(GameState state, int depth, int alpha, int beta, boolean isMax){
        if(depth == 0 || state.isTerminal()){
            return evaluate(state);
        }

        List<Action> actions = state.generateLegalActions();
        int currentBest = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for(Action a : actions){
            GameState updatedState = state.clone();
            updatedState.updateStateWithAction(a);
            int evaluation = recursiveSearchForMove(updatedState, depth - 1, alpha, beta, !isMax);

            if (isMax) {
                if (evaluation > currentBest) {
                    currentBest = evaluation;
                }
                alpha = Math.max(alpha, currentBest);
            }
            else {
                if (evaluation < currentBest) {
                    currentBest = evaluation;
                }
                beta = Math.min(beta, currentBest);
            }
            if (beta <= alpha) {
                break;
            }
        }
        return currentBest;
    }
}
