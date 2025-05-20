package com.example.checkers.ai;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A concrete implementation of an Agent using the minimax algorithm with alpha-beta pruning and iterative deepening.
 *
 * @author Ramiar Odendaal
 */
public class MinimaxAgent extends Agent{
    private int depthLimit;
    public MinimaxAgent(String name, int timeSlice, int depth, boolean isPlayerAgentOne) {
        super(name, timeSlice, isPlayerAgentOne);
        this.depthLimit = depth;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Action getNextMove(GameState state) {
        /*
        Late game moves are fast since there are fewer options
        Could add random depth limits and multiply by game stage
        Agent should press the advantage if there is a big material difference.
        Otherwise, repeating moves until a draw can be considered the most optimal path.
        Check average move time for each difficulty level.
        Adjust so that they correspond to 100 ms, 500 ms, and 1000 ms.
         */
        GameState clonedState = state.clone();
        List<Action> actions = clonedState.generateLegalActions();

        if(actions.size() == 1){
            return actions.get(0);
        }
        Action generatedAction = alphaBetaSearch(clonedState, depthLimit + (state.gameStage == 3 ? 1 : 0));
        if(generatedAction == null){
            return actions.get(r.nextInt(actions.size()));
        }
        else{
            return generatedAction;
        }
    }

    private Action alphaBetaSearch(GameState state, int depth){
        int bestUtility = Integer.MIN_VALUE;
        Action bestChild = null;
        GameState clonedState = state.clone();
        List<Action> actions = clonedState.generateLegalActions();

        for(Action a : actions){
            clonedState.updateStateWithAction(a);
            int res = recursiveSearchForMove(clonedState, depth, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if(res > bestUtility){
                bestUtility = res;
                bestChild = a;
            }
            clonedState.updateStateWithUndoAction(a);
        }
        return bestChild;
    }


    private int recursiveSearchForMove(GameState state, int depth, int limit, int alpha, int beta, boolean isMax){
        if(depth == 0 || state.isTerminal()){
            return evaluate(state, limit-depth);
        }
        GameState updatedState = state.clone();
        List<Action> actions = updatedState.generateLegalActions();
        int currentBest = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for(Action a : actions){
            updatedState.updateStateWithAction(a);
            int evaluation = recursiveSearchForMove(updatedState, depth - 1, limit, alpha, beta, !isMax);

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
            updatedState.updateStateWithUndoAction(a);
            if (beta <= alpha) {
                break;
            }
        }
        return currentBest;
    }
}
