package com.example.checkers.game.models.actions;

import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.players.Player;
import com.example.checkers.utils.AppConstants;

/**
 * Represents a move of a Stone in the game.
 *
 * @author Ramiar Odendaal
 */
public class MoveAction extends Action {
    public final int from;
    public final int to;
    private final boolean isKingingMove;

    /**
     * Instantiates a MoveAction with origin and target positions.
     *
     * @param from   the starting position
     * @param to     the target position
     * @param player the acting player
     * @param stone  the selected stone
     */
    public MoveAction(int from, int to, Player player, Stone stone) {
        super(player, stone);
        this.from = from;
        this.to = to;
        int[] opponentRow = player.getColor().equals(AppConstants.PLAYER_RED) ? AppConstants.WHITE_ROW : AppConstants.RED_ROW;
        boolean movingToOpponentRow = to >= opponentRow[0] && to <= opponentRow[1];
        this.isKingingMove = stone.getKingStatus() ? false : movingToOpponentRow;
    }

    /**
     * Generates a message describing this MoveAction
     *
     * @return a string describing this move
     */
    @Override
    public String getActionMessage() {
        return String.format("moves from position %s to %s.\n", AppConstants.TILE_NAMES[from], AppConstants.TILE_NAMES[to]);
    }

    public boolean isKingingMove(){
        return isKingingMove;
    }
}
