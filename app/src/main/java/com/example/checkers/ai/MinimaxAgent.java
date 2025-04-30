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
    private int counter;
    public MinimaxAgent(String name, int timeSlice) {
        super(name, timeSlice);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Action getNextMove(GameState state) {
        GameState clonedState = state.clone();
        List<Action> actions = clonedState.generateLegalActions();

        if(actions.size() == 1){
            return actions.get(0);
        }
        //TODO: Check if clonedstate and state are equal in the end to see if undo works
        return iterativeDeepeningSearch(state, DEPTH_LIMIT, actions);
    }

    /**
     * Searches successively deeper through iteration. Each search is executed as a Callable and interrupts if the time limit is exceeded.
     *
     * @param state the initial state of the game
     * @param depth the depth limit to progress towards
     * @param actions a list of the possible actions for the agent
     * @return the optimal move based on the search results
     */
    //TODO: FIX TIME OUT
    private Action iterativeDeepeningSearch(GameState state, int depth, List<Action> actions){
        executor = Executors.newSingleThreadExecutor();
        AtomicReference<Action> bestChildSoFar = new AtomicReference<>();
        Action generatedAction = null;
        Callable<Action> iterativeSearcher = () -> {
            for(int i = 1; i <= depth; i++){
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("Reached depth " + i);
                    return bestChildSoFar.get();
                }
                Action result = alphaBetaSearch(state, i);
                bestChildSoFar.set(result);
            }
            return bestChildSoFar.get();
        };

        Future<Action> res = executor.submit(iterativeSearcher);

        try{
            generatedAction = res.get(timeSlice, TimeUnit.MILLISECONDS);
        }
        catch(ExecutionException | InterruptedException e){
            //Thread.currentThread().interrupt();
        }
        catch (TimeoutException e) {
            //res.cancel(true);
            generatedAction = bestChildSoFar.get();
        }
        finally{
            executor.shutdown();
        }

        if(generatedAction == null){
            Random r = new Random();
            int random = r.nextInt(actions.size());
            generatedAction = actions.get(random);
        }
        System.out.println("EVALUATED " + counter + " TIMES");
        counter = 0;
        return generatedAction;
    }

    private Action alphaBetaSearch(GameState state, int depth){
        int bestUtility = Integer.MIN_VALUE;
        Action bestChild = null;
        GameState clonedState = state.clone();
        List<Action> actions = clonedState.generateLegalActions();

        for(Action a : actions){
            clonedState.updateStateWithAction(a);
            int res = recursiveSearchForMove(state, depth, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if(res > bestUtility){
                bestUtility = res;
                bestChild = a;
            }
            clonedState.updateStateWithUndoAction(a);
            if(Thread.currentThread().isInterrupted()){
                return null;
            }
        }
        return bestChild;
    }


    private int recursiveSearchForMove(GameState state, int depth, int limit, int alpha, int beta, boolean isMax){
        if(Thread.currentThread().isInterrupted()){
            return 0;
        }
        if(depth == 0 || state.isTerminal()){
            counter++;
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
            if (beta <= alpha) {
                break;
            }
            updatedState.updateStateWithUndoAction(a);
        }
        return currentBest;
    }
}
