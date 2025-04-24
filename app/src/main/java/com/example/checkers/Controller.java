package com.example.checkers;

import com.example.checkers.game.models.pieces.Stone;

/**
 * Used as an interface for controllers to offer relevant models.
 *
 * @author Ramiar Odendaal
 */
public interface Controller {
    /**
     * Used by the board to remove a captured stone from view.
     *
     * @param stone The stone to be removed
     */
    void updateRemoveStoneFromUI(Stone stone);

    /**
     * Used by models to move a stone in the view
     *
     * @param stone the stone to be moved
     */
    void updateMoveStoneInUI(Stone stone);

    /**
     * Used by models to update the log text with game information.
     *
     * @param s the text to be appended to the TextView
     * @param color the color of the text
     */
    void updateText(String s, int color);

    /**
     * Used by agents to notify the controller so it can unblock the GameEngine when a move has been selected.
     */
    void updateMoveMade();
}
