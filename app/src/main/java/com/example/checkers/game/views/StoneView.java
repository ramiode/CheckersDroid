package com.example.checkers.game.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * An extension of ComponentView that represents a Stone visually on the board.
 *
 * @author Ramiar Odendaal
 */
public class StoneView extends ComponentView {


    private final Paint paint;

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
        paint.setColor(color);
    }

    public void setColor(int color){
        paint.setColor(color);
    }
    @Override
    protected void onDraw(Canvas canvas){
        if(getVisibility() == VISIBLE) {
            super.onDraw(canvas);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, paint);
        }
    }
}
