package com.example.checkers.ai;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.utils.AppConstants;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Interface implemented by the concrete AI agents in the game.
 *
 * @author Ramiar Odendaal
 */
public abstract class Agent {
    protected final int DEPTH_LIMIT = 20;

    private final int[] RED_POSITIONAL_SCORES = new int[] {
            4, 0, 4, 0, 4, 0, 4, 0,
            3, 0, 3, 0, 3, 0, 3, 0,
            2, 0, 2, 0, 2, 0, 2, 0,
            1, 0, 1, 0, 1, 0, 1, 0,
            1, 0, 1, 0, 1, 0, 1, 0,
            2, 0, 2, 0, 2, 0, 2, 0,
            3, 0, 3, 0, 3, 0, 3, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    };
    private final int[] WHITE_POSITIONAL_SCORES = new int[] {
            0, 0, 0, 0, 0, 0, 0, 0,
            3, 0, 3, 0, 3, 0, 3, 0,
            2, 0, 2, 0, 2, 0, 2, 0,
            1, 0, 1, 0, 1, 0, 1, 0,
            1, 0, 1, 0, 1, 0, 1, 0,
            2, 0, 2, 0, 2, 0, 2, 0,
            3, 0, 3, 0, 3, 0, 3, 0,
            4, 0, 4, 0, 4, 0, 4, 0
    };

    protected String name;
    protected int timeSlice;
    protected ExecutorService executor;
    private final int stoneWeight = 50, kingWeight = 250, movementWeight = 5;

    public Agent(String name, int timeSlice){
        this.name = name;
        this.timeSlice = timeSlice;
        executor = Executors.newSingleThreadExecutor();
    }
    /**
     * Generates a move based on the current state of the game.
     *
     * @param state the current state
     * @return a selected action
     */
    public abstract Action getNextMove(GameState state);
    /**
     * Used to evaluate the utility of a state in the tree.
     *
     * @param state The current state of the game
     * @return An integer representing the estimated utility of the state.
     */
    protected int evaluate(GameState state, int depth){
        Random r = new Random();
        int random = r.nextInt(3);
        int estimatedUtility = 0;

        boolean isPlayerOne = state.getPlayerOneName().equals(this.name);
        boolean isMyTurn = state.getCurrentPlayer().getName().equals(this.name);

        List<Stone> myStones = isPlayerOne ? state.getBoard().getPlayerOneStones() : state.getBoard().getPlayerTwoStones();
        List<Stone> enemyStones = isPlayerOne ? state.getBoard().getPlayerTwoStones() : state.getBoard().getPlayerOneStones();

        int noMyStones = (int) myStones.stream().filter(stone -> stone.getKingStatus() == false).count();
        int noEnemyStones = (int) enemyStones.stream().filter(stone -> stone.getKingStatus() == false).count();

        int myMovementEvaluation = myStones.stream()
                .map(Stone::getPosition)
                .map(pos -> evaluatePosition(pos, isPlayerOne))
                .reduce(Integer::sum)
                .orElse(0);

        int enemyMovementEvaluation = enemyStones.stream()
                .map(Stone::getPosition)
                .map(pos -> evaluatePosition(pos, !isPlayerOne))
                .reduce(Integer::sum)
                .orElse(0);

        int noJumps = state.getJumpActions().size();

        if(state.isTerminal()){
            if(state.isDraw()){
                estimatedUtility += 5000/depth;
            }
            else if(state.getWinner().getName().equals(this.name)){
                estimatedUtility += 10000/depth;
            }
            else{
                estimatedUtility -= 10000/depth;
            }
        }
        estimatedUtility += stoneWeight * (noMyStones - noEnemyStones); //10, 20, 30, 40...
        estimatedUtility += kingWeight * (countKings(myStones) - countKings(enemyStones)); //50, 100, 150...
        estimatedUtility += (noMyStones + countKings(myStones) * 25); //<20
        estimatedUtility += movementWeight * (myMovementEvaluation); //-10 to 10
        //estimatedUtility += jumpWeight * (isMyTurn ? noJumps : -noJumps); // 10, 20, 30, -10, -20, -30

        return estimatedUtility + (isPlayerOne ? random : -random);
    }
    private int maxEvaluate(GameState state, int depth){
        int estimatedUtility = 0;
        boolean isPlayerOne = state.getPlayerOneName().equals(this.name);
        List<Stone> myStones = isPlayerOne ? state.getBoard().getPlayerOneStones() : state.getBoard().getPlayerTwoStones();
        int noMyStones = (int) myStones.stream().filter(stone -> stone.getKingStatus() == false).count();
        int noKings = countKings(myStones);

        int myMovementEvaluation = myStones.stream()
                .map(Stone::getPosition)
                .map(pos -> evaluatePosition(pos, isPlayerOne))
                .reduce(Integer::sum)
                .orElse(0);

        estimatedUtility = stoneWeight * noMyStones + kingWeight * noKings + movementWeight * myMovementEvaluation;

        if(state.isTerminal()){
            if(state.isDraw()){
                estimatedUtility += 5000/depth;
            }
            else if(state.getWinner().getName().equals(this.name)){
                estimatedUtility += 10000/depth;
            }
            else{
                estimatedUtility -= 10000/depth;
            }
        }

        return estimatedUtility;

    }
    private int evaluatePosition(int position, boolean isPlayerOne){
        return isPlayerOne ? RED_POSITIONAL_SCORES[position] : WHITE_POSITIONAL_SCORES[position];
    }
    private int countKings(List<Stone> stones){
        return (int) stones.stream()
                .filter(stone -> stone.getKingStatus() == true)
                .count();
    }
}
