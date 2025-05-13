package com.example.checkers.game.models.players;

import com.example.checkers.ai.Agent;
import com.example.checkers.ai.MCTSAgent;
import com.example.checkers.ai.MinimaxAgent;
import com.example.checkers.ai.RandomAgent;
import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.utils.AppConfig;

import java.util.List;
import java.util.Random;

/**
 * A concrete implementation of player representing an AI player. Uses an AI model to generate actions.
 *
 * @author Ramiar Odendaal
 */
//TODO: Implement the AI here
public class MachinePlayer extends Player {
    private final Agent aiModel;

    /**
     * Sets the AI model and constructs the player.
     *
     * @param isRed     true if player is red, false otherwise
     * @param name      the name of the player
     * @param isMinimax true if player should use minimax model, false otherwise
     */
    public MachinePlayer(boolean isRed, String name, boolean isMinimax) {
        super(isRed, name);
        aiModel = isMinimax ? new MinimaxAgent(name, AppConfig.timeSlice, isRed) : new MCTSAgent(name, AppConfig.timeSlice, isRed);
    }

    /**
     * Generates an action using this machine player's model.
     *
     * @param state the current state of the game
     */
    public void generateAction(GameState state) {
        Action action = aiModel.getNextMove(state);
        setSelectedStone(action.getStone());
        setNextMove(action);
        notifyMoveMade();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isHuman() {
        return false;
    }
}
