package com.example.checkers.game.models;

import com.example.checkers.game.Stone;

public class King extends Stone {
    /**
     * Instantiates the member variables and generates a unique id for the stone.
     *
     * @param position the stone's position on the board
     * @param player   the player that owns the stone
     */
    public King(int position, String player) {
        super(position, player);
    }
}
