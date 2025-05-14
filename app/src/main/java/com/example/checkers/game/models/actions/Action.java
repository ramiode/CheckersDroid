package com.example.checkers.game.models.actions;

import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.players.Player;

/**
 * Abstract class representing a generic action.
 *
 * @author Ramiar Odendaal
 */
public abstract class Action{
    private final Player actingPlayer;
    private final Stone currentStone;

    /**
     * Constructor which takes the player who is performing the action and the selected stone.
     *
     * @param player the acting player
     * @param stone  the selected stone
     */
    public Action(Player player, Stone stone) {
        this.actingPlayer = player;
        this.currentStone = stone;
    }

    /**
     * Retrieves the player who is performing the action.
     *
     * @return the acting player
     */
    public Player getActingPlayer() {
        return actingPlayer;
    }

    /**
     * Retrieves the selected stone.
     *
     * @return the stone that is being used for the action
     */
    public Stone getStone() {
        return currentStone;
    }

    /**
     * Generates a message to describe the next action taken by the player.
     *
     * @return a description of the action
     */
    @Override
    public String toString() {
        return "Player " + actingPlayer.getName() + " " + getActionMessage();
    }

    /**
     * Implemented in concrete subclasses to define their custom action messages.
     *
     * @return a string representing the action
     */
    public abstract String getActionMessage();
}
