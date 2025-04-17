package com.example.checkers.game;

import com.example.checkers.game.models.Action;

/**
 * Represents a move of a Stone in the game.
 *
 * @author Ramiar Odendaal
 */
public class MoveAction extends Action {
    public final int from;
    public final int to;
    public MoveAction(int from, int to, Player player, Stone stone){
       super(player, stone);
       this.from = from;
       this.to = to;
    }
}
