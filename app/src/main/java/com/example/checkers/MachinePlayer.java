package com.example.checkers;

import com.example.checkers.ai.Agent;
import com.example.checkers.ai.MCTSAgent;
import com.example.checkers.ai.MinimaxAgent;
import com.example.checkers.game.MoveAction;
import com.example.checkers.game.Player;

public class MachinePlayer extends Player {
    private Agent aiModel;

    public MachinePlayer(boolean isRed, String name, boolean isMinimax){
        super(isRed, name);
        aiModel = isMinimax ? new MinimaxAgent() : new MCTSAgent();
    }

    @Override
    public MoveAction getNextMove() {
        //aiModel.getMove;
        return null;
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}
