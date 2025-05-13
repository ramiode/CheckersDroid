package com.example.checkers.ai;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;

import java.util.List;
import java.util.Random;

public class RandomAgent extends Agent{
    private Random random = new Random();
    public RandomAgent(String name, int timeSlice, boolean isAgentPlayerOne) {
        super(name, timeSlice, isAgentPlayerOne);
    }

    @Override
    public Action getNextMove(GameState state){
        List<Action> availableActions = state.generateLegalActions();
        return availableActions.get(random.nextInt(availableActions.size()));
    }
}
