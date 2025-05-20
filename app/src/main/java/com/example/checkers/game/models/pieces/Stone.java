package com.example.checkers.game.models.pieces;

import com.example.checkers.utils.AppConstants;

/**
 * A model representation of a Stone in the game.
 *
 * @author Ramiar Odendaal
 */
public class Stone implements Cloneable {
    private int position;
    private String player;
    private int[] directions;
    private volatile boolean isKing;
    private final int id;

    /**
     * Instantiates the member variables and generates a unique id for the stone.
     *
     * @param position    the stone's position on the board
     * @param playerColor the player that owns the stone
     */
    public Stone(int position, String playerColor) {
        this.position = position;
        this.player = playerColor;
        int code = hashCode();
        id = (int) (code ^ (System.nanoTime()));
        isKing = false;

        directions = playerColor.equals(AppConstants.PLAYER_RED) ? AppConstants.RED_DIRECTIONS : AppConstants.WHITE_DIRECTIONS;

    }

    /**
     * Returns the stone's position on the board (0-63)
     *
     * @return the position of the stone
     */
    public int getPosition() {
        return position;
    }

    /**
     * Updates the stone's position on the board (0-63)
     *
     * @param position the new position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Retrieves the unique identifier for this stone.
     *
     * @return the unique id
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the color of the player who owns this stone.
     *
     * @return the stone's color
     */
    public String getPlayerColor() {
        return player;
    }

    /**
     * Reverts king status, used for undoing moves.
     */
    public void undoKing(){
        isKing = false;
        directions = this.player.equals(AppConstants.PLAYER_RED) ? AppConstants.RED_DIRECTIONS : AppConstants.WHITE_DIRECTIONS;
    }

    /**
     * Upgrades this stone to be a King. Kings can move backwards as well as forwards.
     */
    public void upgradeToKing() {
        isKing = true;
        directions = AppConstants.KING_DIRECTIONS;
    }

    /**
     * Used to check if this Stone is a king.
     *
     * @return true only if this stone is a king
     */
    public boolean getKingStatus() {
        return isKing;
    }

    /**
     * Return the valid directions available to this stone. A stone can only move forward from its starting position, unless it has been upgraded to a king.
     *
     * @return the valid directions for this stone
     */
    public int[] getDirections() {
        return directions;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Stone clone() {
        try {
            Stone clonedStone = (Stone) super.clone();
            clonedStone.directions = this.directions.clone();
            return clonedStone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
