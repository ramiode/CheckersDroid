package com.example.checkers.game.models.players;

/**
 * A concrete implementation of player representing a human player that interacts with the UI directly.
 *
 * @author Ramiar Odendaal
 */
public class HumanPlayer extends Player {
    /**
     * Constructor for human player.
     *
     * @param isRed Set as true if player should be red
     */
    public HumanPlayer(boolean isRed, String name) {
        super(isRed, name);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isHuman() {
        return true;
    }


}
