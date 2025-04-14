package com.example.checkers.game;

/**
 * Represents a move of a Stone in the game.
 *
 * @author Ramiar Odendaal
 */
public class MoveAction {
    public final int from;
    public final int to;
    public MoveAction(int from, int to){
       this.from = from;
       this.to = to;
    }
}
