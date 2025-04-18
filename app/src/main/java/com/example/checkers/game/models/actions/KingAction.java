package com.example.checkers.game.models.actions;

import com.example.checkers.game.models.players.Player;
import com.example.checkers.game.models.pieces.Stone;

/**
 * Action that turns the selected Stone into a King.
 *
 * @author Ramiar Odendaal
 */
public class KingAction extends Action {
    /**
     * The player and the stone to be upgraded are selected.
     *
     * @param player the acting player
     * @param stone the selected stone
     */
    public KingAction(Player player, Stone stone) {
        super(player, stone);
    }

    @Override
    public String getActionMessage() {
        return null;
    }
}
