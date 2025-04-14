package com.example.checkers.game.views;

import android.content.Context;
import android.view.ViewGroup;

import com.example.checkers.game.Stone;
import com.example.checkers.utils.AppConstants;

import java.util.List;

/**
 * Custom ViewGroup that manages the various views included in the game, such as TileView and StoneView.
 * Responsible for the graphical representation of the checkers game.
 *
 * @author Ramiar Odendaal
 */
public class GameBoardViewGroup extends ViewGroup {
    public GameBoardViewGroup(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
    }
    @Override
    public boolean shouldDelayChildPressedState(){
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(width, height);

        setMeasuredDimension(size, size);
    }

    /**
     * Instantiates the game tiles which constitute the board. Assigns a listener to each tile.
     * @param listener a listener provided by GameActivity to manage player interaction
     */
    public void initializeBoard(OnClickListener listener){
        for(int i = 0; i < AppConstants.NO_TILES; i++){
            TileView tile = new TileView(this.getContext(), i);
            addView(tile);
            tile.setOnClickListener(listener);
        }
    }

    /**
     * Instantiates a StoneView for each stone in the provided list
     * @param stones the list of stones a certain player has
     * @param color the color of the player's stones
     */
    public void initializeStones(final List<Stone> stones, int color){
        for(Stone stone : stones){
            StoneView stoneView = new StoneView(this.getContext(), color, stone.getPosition());
            stoneView.setId(stone.getId());
            addView(stoneView);
        }
    }

    /**
     * Finds the StoneView through its unique identifier.
     * @param id the unique identifier for the stone
     * @return the StoneView instance
     */
    public StoneView findStoneById(int id){
        return findViewById(id);
    }

    /**
     * Positions all children in this ViewGroup.
     */
    private void layoutChildren(){
        final int noChildren = getChildCount();
        final int width = getWidth() / 8;
        for(int i = 0; i < noChildren; i++){
            ComponentView child = (ComponentView) getChildAt(i);
            if(child.getVisibility() != GONE){
                int pos = child.getPosition();
                int xPos = AppConstants.ROW[pos];
                int yPos = AppConstants.COL[pos];
                child.layout(yPos * width, xPos*width, yPos * width + width, xPos * width + width);
            }
        }
    }
}
