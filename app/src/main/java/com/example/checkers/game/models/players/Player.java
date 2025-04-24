package com.example.checkers.game.models.players;

import com.example.checkers.AgentSubject;
import com.example.checkers.Controller;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.utils.AppConstants;

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
    protected List<Controller> controllers;
    private volatile Action selectedAction;
    protected volatile Stone currentlySelectedStone;

    /**
     * Constructor for abstract player.
     *
     * @param isRed Set as true if player should be red
     */
    public Player(boolean isRed, String name) {
        this.color = isRed ? AppConstants.PLAYER_RED : AppConstants.PLAYER_WHITE;
        this.name = name;
        controllers = new LinkedList<>();
    }

    /**
     * Get the assigned color for the player.
     *
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * Retrieves the next move the player has selected
     *
     * @return the move to be executed
     */
    public Action getNextMove() {
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
    public void setNextMove(Action action) {
        selectedAction = action;
    }

    /**
     * Sets the player's currently selected stone.
     *
     * @param stone the stone to be selected
     */
    public void setSelectedStone(Stone stone) {
        currentlySelectedStone = stone;
    }

    /**
     * Retrieves the currently selected stone.
     *
     * @return the stone that the player has selected
     */
    public Stone getSelectedStone() {
        return currentlySelectedStone;
    }

    /**
     * Notifies the controller that a move has been made.
     */
    @Override
    public void notifyMoveMade() {
        controllers.forEach(Controller::updateMoveMade);
    }

    /**
     * Adds an observer which can be notified later.
     *
     * @param c the observer to be added
     */
    @Override
    public void addController(Controller c) {
        controllers.add(c);
    }

    /**
     * Removes an observer from the observers list.
     *
     * @param c the observer to be removed
     */
    @Override
    public void removeController(Controller c) {
        controllers.remove(c);
    }

    /**
     * Getter for the name of this Player.
     *
     * @return the name of this player
     */
    public String getName() {
        return name;
    }
}
