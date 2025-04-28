package com.example.checkers.ai;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.utils.AppConstants;

import java.util.List;

/**
 * Interface implemented by the concrete AI agents in the game.
 *
 * @author Ramiar Odendaal
 */
public abstract class Agent {
    private String name;

    public Agent(String name){
        this.name = name;
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
    protected int evaluate(GameState state){
        int estimatedUtility = 0;
        //change to id instead of name?
        boolean isMyTurn = state.getCurrentPlayer().getName().equals(this.name);
        boolean isPlayerOne = state.getPlayerOneName().equals(this.name);

        List<Stone> myStones = isPlayerOne ? state.getBoard().getPlayerOneStones() : state.getBoard().getPlayerTwoStones();
        List<Stone> enemyStones = isPlayerOne ? state.getBoard().getPlayerTwoStones() : state.getBoard().getPlayerOneStones();

        int noMyStones = myStones.size();
        int noEnemyStones = enemyStones.size();
        estimatedUtility += noMyStones - noEnemyStones;

        estimatedUtility += countKings(myStones) - countKings(enemyStones);

        GameState clonedState = state.clone();
        int noJumps = clonedState.getJumpActions().size();
        int noMoves = clonedState.generateMoveActions(isMyTurn ? myStones : enemyStones).size();
        estimatedUtility += isMyTurn ? noJumps : -noJumps;
        estimatedUtility += isMyTurn ? noMoves : -noMoves;

        if(state.isTerminal()){
            if(state.isDraw()){
                estimatedUtility = Integer.MAX_VALUE/2;
            }
            else if(state.getWinner().getName().equals(this.name)){
                //TODO: Look into if winner is correct
                estimatedUtility = Integer.MAX_VALUE;
            }
            else{
                estimatedUtility = Integer.MAX_VALUE;
            }
        }
        return estimatedUtility;
    }

    private int countKings(List<Stone> stones){
        return (int) stones.stream()
                .filter(stone -> stone.getKingStatus() == true)
                .count();
    }
}
