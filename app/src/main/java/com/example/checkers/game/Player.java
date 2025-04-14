package com.example.checkers.game;

import com.example.checkers.ai.Agent;
import com.example.checkers.ai.MCTSAgent;
import com.example.checkers.ai.MinimaxAgent;
import com.example.checkers.utils.AppConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a player in the game. Can be either AI or human.
 *
 * @author Ramiar Odendaal
 */
public abstract class Player {
    private String name;
    private String color;
    private List<Stone> stones;

    /**
     * Constructor for abstract player.
     *
     * @param isRed Set as true if player should be red
     */
    public Player(boolean isRed, String name){
        this.color = isRed ? AppConstants.PLAYER_RED : AppConstants.PLAYER_WHITE;
        this.name = name;
        stones = new ArrayList<>();
        initializePieces(isRed);
    }

    /**
     * Initializes the player's stones. Uses bitwise operations to position stones in the traditional checkers pattern.
     *
     * @param isRed True if player is red; false otherwise
     */
    private void initializePieces(boolean isRed){
        int start = isRed? 40 : 0;
        int end = isRed ? 64 : 24;
        for(int i = start; i < end; i++){
            //if even row then place in odd columns
            if((i & 1) % 2 == 1 && (i >> 3) % 2 == 0){
                stones.add(new Stone(i, color));
            }
            //if odd row then place in even columns
            else if((i >> 3) % 2 == 1 && (i & 1) % 2 == 0){
                stones.add(new Stone(i, color));
            }
        }

    }

    /**
     * Get the assigned color for the player.
     * @return the color
     */
    public String getColor(){
        return color;
    }

    /**
     * Get the list of stones the player currently possesses.
     * @return the list of stones
     */
    public List<Stone> getStones(){
        return stones;
    }

    /**
     * Retrieves the next move the player has selected
     * @return the move to be executed
     */
    public abstract MoveAction getNextMove();

    public abstract boolean isHuman();
    public void setNextMove(MoveAction move){
        //Concrete classes must implement this
    }
}
