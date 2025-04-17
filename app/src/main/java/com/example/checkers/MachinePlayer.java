package com.example.checkers;

import com.example.checkers.ai.Agent;
import com.example.checkers.ai.MCTSAgent;
import com.example.checkers.ai.MinimaxAgent;
import com.example.checkers.game.GameState;
import com.example.checkers.game.MoveAction;
import com.example.checkers.game.Player;
import com.example.checkers.game.models.Action;

import java.util.List;
import java.util.Random;

public class MachinePlayer extends Player{
    private MoveAction selectedAction;
    private Agent aiModel;

    public MachinePlayer(boolean isRed, String name, boolean isMinimax){
        super(isRed, name);
        aiModel = isMinimax ? new MinimaxAgent() : new MCTSAgent();
    }

    @Override
    public MoveAction getNextMove(GameState state) {
        generateAction(state);
        return selectedAction;
    }
    @Override
    public void setNextMove(MoveAction move){
        selectedAction = move;
    }
    public void generateAction(GameState state){
        Random r = new Random();
        System.out.println("REACHED 1");
        List<Action> actions = state.generateLegalActions();
        System.out.println("REACHED 2");
        int random = Math.abs(r.nextInt() % actions.size());
        System.out.println("RANDOM: " + random + " ACTION_SIZE" + actions.size());
        MoveAction action = (MoveAction) actions.get(random);
        setSelectedStone(action.getCurrentStone());
        setNextMove(action);
        System.out.println("REACHED 3");
        controller.updateMoveMade();

    }


    @Override
    public boolean isHuman() {
        return false;
    }
}
