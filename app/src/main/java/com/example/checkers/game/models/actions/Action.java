package com.example.checkers.game.models.actions;

import com.example.checkers.game.models.players.Player;
import com.example.checkers.game.models.pieces.Stone;

/**
 * Abstract class representing a generic action.
 *
 * @author Ramiar Odendaal
 */
public abstract class Action {
    private final Player actingPlayer;
    private final Stone currentStone;

    /**
     * Constructor which takes the player who is performing the action and the selected stone.
     *
     * @param player the acting player
     * @param stone the selected stone
     */
    public Action(Player player, Stone stone){
        this.actingPlayer = player;
        this.currentStone = stone;
    }

    /**
     * Retrieves the player who is performing the action.
     *
     * @return the acting player
     */
    public Player getActingPlayer(){
        return actingPlayer;
    }

    /**
     * Retrieves the selected stone.
     *
     * @return the stone that is being used for the action
     */
    public Stone getStone(){
        return currentStone;
    }

    @Override
    public String toString(){
        return "Player " + actingPlayer.getName() + " " + getActionMessage();
    }

    public abstract String getActionMessage();
}
