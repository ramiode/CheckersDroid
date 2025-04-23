package com.example.checkers.game.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;

/**
 * An extension of ComponentView that represents a Stone visually on the board.
 *
 * @author Ramiar Odendaal
 */
public class StoneView extends ComponentView {


    private final Paint paint;
    private final int stoneColor;
    private boolean isKing;

    /**
     * Initializes the StoneView and sets the color to be drawn.
     *
     * @param context the context of the view
     * @param color the color of the stone
     * @param position the stone's position on the board
     */
    public StoneView(Context context, int color, int position) {
        super(context, position);
        this.paint = new Paint();
        stoneColor = color;
    }

    public void setKing(boolean isKing){
        this.isKing = isKing;
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas){
        if(getVisibility() == VISIBLE) {
            super.onDraw(canvas);
            paint.setColor(stoneColor);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, paint);
            if(isKing){
                paint.setColor(Color.BLACK);
                canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/10f, paint);
            }

        }
    }
}
