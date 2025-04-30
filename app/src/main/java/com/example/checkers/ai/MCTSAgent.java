package com.example.checkers.ai;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;

/**
 * A concrete implementation of an Agent using the Monte Carlo Tree Search algorithm.
 *
 * @author Ramiar Odendaal
 */
public class MCTSAgent extends Agent{
    public MCTSAgent(String name, int timeSlice) {
        super(name, timeSlice);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Action getNextMove(GameState state) {
        return null;
    }
}
