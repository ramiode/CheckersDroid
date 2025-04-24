package com.example.checkers.game;

/**
 * A node representation of the current state for use in Monte Carlo Tree Search.
 *
 * @param <GameState> the state of the game at this node
 * @param <Action> the action chosen at this node
 *
 * @author Ramiar Odendaal
 */
public class GameNode<GameState, Action>{
    private final GameState state;
    private final Action action;

    public GameNode(GameState state, Action action){
        this.state = state;
        this.action = action;
    }
}
