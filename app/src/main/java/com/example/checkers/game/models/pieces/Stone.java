package com.example.checkers.game.models.pieces;

import com.example.checkers.utils.AppConstants;

/**
 * A model representation of a Stone in the game.
 *
 * @author Ramiar Odendaal
 */
public class Stone implements Cloneable{
    private int position;
    private String player;
    private int[] directions;
    private volatile boolean isKing;
    private int id;

    /**
     * Instantiates the member variables and generates a unique id for the stone.
     *
     * @param position the stone's position on the board
     * @param playerColor the player that owns the stone
     */
    public Stone(int position, String playerColor){
        this.position = position;
        this.player = playerColor;
        int code = hashCode();
        id = (int) (code ^ (System.nanoTime()));
        isKing = false;

        directions = playerColor.equals(AppConstants.PLAYER_RED) ? AppConstants.RED_DIRECTIONS : AppConstants.WHITE_DIRECTIONS;

    }

    /**
     * Returns the stone's position on the board (0-63)
     * @return the position of the stone
     */
    public int getPosition(){
        return position;
    }

    /**
     * Updates the stone's position on the board (0-63)
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
    public int getId(){
        return id;
    }

    /**
     * Retrieves the color of the player who owns this stone.
     *
     * @return the stone's color
     */
    public String getPlayerColor(){
        return player;
    }

    /**
     * Upgrades this stone to be a King. Kings can move backwards as well as forwards.
     */
    public void upgradeToKing(){
        isKing = true;
        directions = AppConstants.KING_DIRECTIONS;
    }

    public boolean getKingStatus(){
        return isKing;
    }
    public int[] getDirections(){
        return directions;
    }
    @Override
    public Stone clone(){
        try {
            Stone clonedStone = (Stone) super.clone();
            return clonedStone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
