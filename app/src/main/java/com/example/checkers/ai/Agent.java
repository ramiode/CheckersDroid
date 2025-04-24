package com.example.checkers.ai;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;

/**
 * Interface implemented by the concrete AI agents in the game.
 *
 * @author Ramiar Odendaal
 */
public interface Agent {

    /**
     * Generates a move based on the current state of the game.
     *
     * @param state the current state
     * @return a selected action
     */
    Action getNextMove(GameState state);
}
