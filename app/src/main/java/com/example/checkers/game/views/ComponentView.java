package com.example.checkers.game.views;

import android.content.Context;
import android.view.View;

/**
 * Abstract class representing a visual Component in the game (i.e. Tile). Extended by concrete classes such as TileView.
 *
 * @author Ramiar Odendaal
 */
public abstract class ComponentView extends View {
    private int position;

    public ComponentView(Context context, int position) {
        super(context);
        this.position = position;
    }

    /**
     * Getter for the position of the component in the board.
     *
     * @return the component's position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Setter for the position of the component in the board.
     *
     * @param position the new position
     */
    public void setPosition(int position) {
        this.position = position;
    }
}
