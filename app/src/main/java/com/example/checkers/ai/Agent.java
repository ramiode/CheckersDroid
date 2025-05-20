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
    protected final int DEPTH_LIMIT = 10;
    protected int counter = 0;
    protected Random r = new Random();
    private final int[] RED_POSITIONAL_SCORES = new int[] {
            0, 8, 0, 8, 0, 8, 0, 8,
            7, 0, 7, 0, 7, 0, 7, 0,
            0, 6, 0, 6, 0, 6, 0, 0,
            0, 0, 6, 0, 6, 0, 6, 0,
            0, 6, 0, 6, 0, 6, 0, 0,
            0, 0, 4, 0, 4, 0, 4, 0,
            0, 4, 0, 4, 0, 4, 0, 0,
            8, 0, 8, 0, 8, 0, 8, 0
    };
    private final int[] WHITE_POSITIONAL_SCORES = new int[] {
            0, 8, 0, 8, 0, 8, 0, 8,
            0, 0, 4, 0, 4, 0, 4, 0,
            0, 4, 0, 4, 0, 4, 0, 0,
            0, 0, 6, 0, 6, 0, 6, 0,
            0, 6, 0, 6, 0, 6, 0, 0,
            0, 0, 6, 0, 6, 0, 6, 0,
            0, 7, 0, 7, 0, 7, 0, 7,
            8, 0, 8, 0, 8, 0, 8, 0
    };

    protected String name;
    protected int timeSlice;
    protected ExecutorService executor;
    protected final boolean isAgentPlayerOne;
    private int stoneWeight = 100, kingWeight = 150, movementWeight = 1, triangleWeight = 1;

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
        depth = depth == 0 ? 1 : depth;
        if(state.isTerminal()){
            if(state.isDraw()){
               return Integer.MAX_VALUE/(2*depth);
            }
            else if(state.getWinner().getName().equals(this.name)){
                return Integer.MAX_VALUE/depth;
            }
            else{
                return Integer.MIN_VALUE/depth;
            }
        }

        int random = r.nextInt(3);
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

        estimatedUtility += stoneWeight * (noMyStones - noEnemyStones); //10, 20, 30, 40...
        estimatedUtility += triangleWeight * triangleFormationEvaluation;
        estimatedUtility += movementWeight * (myMovementEvaluation); //30
        estimatedUtility += kingWeight * (countKings(myStones) - countKings(enemyStones));

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

    public int getNumberOfMoves(){
       return counter;
    }
}
