package com.example.checkers.ai;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;

public abstract class Agent {
    public abstract Action getNextMove(GameState state);
}
