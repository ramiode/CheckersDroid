package com.example.checkers.game;

/**
 * A model representation of a Stone in the game.
 *
 * @author Ramiar Odendaal
 */
public class Stone {
    private int position;
    private String player;
    private int id;

    /**
     * Instantiates the member variables and generates a unique id for the stone.
     *
     * @param position the stone's position on the board
     * @param player the player that owns the stone
     */
    public Stone(int position, String player){
        this.position = position;
        this.player = player;

        int code = hashCode();
        id = (int) (code ^ (System.nanoTime()));
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
     * @return the unique id
     */
    public int getId(){
        return id;
    }

    public String getPlayerColor(){
        return player;
    }

}
