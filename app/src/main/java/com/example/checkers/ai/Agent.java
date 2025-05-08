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
    //TODO: Tweak weights, optimize late game play
    protected final int DEPTH_LIMIT = 20;

    private final int[] RED_POSITIONAL_SCORES = new int[] {
            8, 0, 8, 0, 8, 0, 8, 0,
            3, 0, 3, 0, 3, 0, 3, 0,
            2, 0, 2, 0, 2, 0, 2, 0,
            1, 0, 1, 0, 1, 0, 1, 0,
            1, 0, 1, 0, 1, 0, 1, 0,
            2, 0, 2, 0, 2, 0, 2, 0,
            3, 0, 3, 0, 3, 0, 3, 0,
            8, 0, 8, 0, 8, 0, 8, 0
    };
    private final int[] WHITE_POSITIONAL_SCORES = new int[] {
            8, 0, 8, 0, 8, 0, 8, 0,
            3, 0, 3, 0, 3, 0, 3, 0,
            2, 0, 2, 0, 2, 0, 2, 0,
            1, 0, 1, 0, 1, 0, 1, 0,
            1, 0, 1, 0, 1, 0, 1, 0,
            2, 0, 2, 0, 2, 0, 2, 0,
            3, 0, 3, 0, 3, 0, 3, 0,
            8, 0, 8, 0, 8, 0, 8, 0
    };

    protected String name;
    protected int timeSlice;
    protected ExecutorService executor;
    protected final boolean isAgentPlayerOne;
    private int stoneWeight = 50, kingWeight = 100, movementWeight = 1, triangleWeight = 1;

    public Agent(String name, int timeSlice, boolean isAgentPlayerOne){
        this.name = name;
        this.timeSlice = timeSlice;
        executor = Executors.newSingleThreadExecutor();
        this.isAgentPlayerOne = isAgentPlayerOne;
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
        int random = r.nextInt(2);
        int estimatedUtility = 0;

        List<Stone> myStones = isAgentPlayerOne ? state.getBoard().getPlayerOneStones() : state.getBoard().getPlayerTwoStones();
        List<Stone> enemyStones = isAgentPlayerOne ? state.getBoard().getPlayerTwoStones() : state.getBoard().getPlayerOneStones();

        int noMyStones = myStones.size();
        int noEnemyStones = enemyStones.size();

        int myMovementEvaluation = myStones.stream()
                .map(Stone::getPosition)
                .map(pos -> evaluatePosition(pos, isAgentPlayerOne))
                .reduce(Integer::sum)
                .orElse(0);
        int triangleFormationEvaluation = myStones.stream()
                .map(stone -> state.getBoard().checkAdjacentFriendlyStones(stone))
                .reduce(Integer::sum)
                .orElse(0);

        estimatedUtility += stoneWeight * Math.pow(state.gameStage, 2) * (noMyStones - noEnemyStones); //10, 20, 30, 40...
        //estimatedUtility += kingWeight / state.gameStage * (countKings(myStones) - countKings(enemyStones)); //50, 100, 150...
        estimatedUtility += triangleWeight * triangleFormationEvaluation; //30
        estimatedUtility += movementWeight * (myMovementEvaluation); //30
        estimatedUtility += kingWeight * (countKings(myStones) - countKings(enemyStones));
        estimatedUtility -= noEnemyStones;

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

        return estimatedUtility + (estimatedUtility % 2 == 0 ? random : -random);
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
