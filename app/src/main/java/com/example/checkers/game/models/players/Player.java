package com.example.checkers.game.models.players;

import com.example.checkers.AgentSubject;
import com.example.checkers.Observer;
import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.game.models.actions.MoveAction;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.utils.AppConstants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a player in the game. Can be either AI or human.
 *
 * @author Ramiar Odendaal
 */
public abstract class Player implements AgentSubject {
    private final String name;
    private final String color;
    private List<Stone> stones;
    protected List<Observer> controllers;
    private volatile Action selectedAction;
    protected volatile Stone currentlySelectedStone;
    private volatile List<JumpAction> availableJumps;

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
        controllers = new LinkedList<>();
    }

    /**
     * Initializes the player's stones. Uses bitwise operations to position stones in the traditional checkers pattern.
     *
     * @param isRed True if player is red; false otherwise
     */
    private void initializePieces(boolean isRed){
        int start = isRed? 40 : 0;
        int end = isRed ? 63 : 24;
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
    public Action getNextMove(){
        return selectedAction;
    }

    /**
     * Returns true if this is a human player, otherwise returns false.
     *
     * @return true or false depending on if player is human
     */
    public abstract boolean isHuman();

    /**
     * Sets the next action that the player has chosen.
     *
     * @param action the action to be set
     */
    public void setNextMove(Action action){
        selectedAction = action;
    }

    /**
     * Sets the player's currently selected stone.
     *
     * @param stone the stone to be selected
     */
    public void setSelectedStone(Stone stone){
       currentlySelectedStone = stone;
    }

    /**
     * Retrieves the currently selected stone.
     *
     * @return the stone that the player has selected
     */
    public Stone getSelectedStone(){
        return currentlySelectedStone;
    }

    /**
     * Notifies the controller that a move has been made.
     */
    @Override
    public void notifyMoveMade() {
        controllers.forEach(Observer::updateMoveMade);
    }

    /**
     * Adds an observer which can be notified later.
     *
     * @param o the observer to be added
     */
    @Override
    public void addObserver(Observer o) {
       controllers.add(o);
    }

    /**
     * Removes an observer from the observers list.
     *
     * @param o the observer to be removed
     */
    @Override
    public void removeObserver(Observer o) {
        controllers.remove(o);
    }

    public String getName(){
        return name;
    }

    public List<JumpAction> getJumpPositions(){
        return availableJumps;
    }
    public void setJumpPositions(List<JumpAction> jumps){
        availableJumps = jumps;
    }
}
