package com.example.checkers.game;

import com.example.checkers.game.models.actions.Action;

import java.util.List;

/**
 * A node representation of the current state for use in Monte Carlo Tree Search.
 *
 * @param <S> the state of the game at this node
 * @param <A> the action chosen at this node
 *
 * @author Ramiar Odendaal
 */
public class GameNode<S,A>{
    public final GameState state;
    public final GameNode<S, A> parent;
    public final Action action;
    public final int depth;
    private int reward;
    private int visits;
    private List<Action> unexploredActions;

    public GameNode(S state, A action, GameNode<S, A> parent, int depth){
        this.state = (GameState) state;
        this.action = (Action) action;
        this.parent = parent;
        this.depth = depth;
        this.reward = 0;
        this.visits = 0;
        this.unexploredActions = this.state.generateLegalActions();
    }

    public List<Action> getAllLegalActions(){
        return state.generateLegalActions();
    }
    public Action getAction(){
        return action;
    }
    public void setReward(int reward){
        this.reward += reward;
    }
    public int getReward(){
        return reward;
    }

}
