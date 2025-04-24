package com.example.checkers.game.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.checkers.utils.AppConstants;

/**
 * A concrete ComponentView that graphically represents a Tile on the board.
 *
 * @author Ramiar Odendaal
 */
public class TileView extends ComponentView {
    private final int pos;
    private boolean selected;
    private boolean showJumpable;
    private final Paint paint;

    /**
     * Instantiates a new TileView with the specified position on the board.
     *
     * @param context the context of this view
     * @param pos     the position of this view
     */
    public TileView(Context context, int pos) {
        super(context, pos);
        this.pos = pos;
        this.selected = false;
        this.paint = new Paint();

    }

    @Override
    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
        }
    }

    /**
     * Marks this tile as jumpable.
     */
    public void setJumpable() {
        showJumpable = true;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Tile color is decided based on the tile's position in the board. On even rows, even columns are white. On odd rows, even columns are black.
        final int tileColour = (pos & 1) == 0 ? (((pos >> 3) & 1) == 0 ? AppConstants.WHITE : AppConstants.BLACK) : (((pos >> 3) & 1) == 0 ? AppConstants.BLACK : AppConstants.WHITE);

        if (selected) {
            paint.setColor(AppConstants.MAGENTA);
        } else {
            paint.setColor(tileColour);
        }
        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);

        if (showJumpable) {
            paint.setColor(AppConstants.MAGENTA);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 10f, paint);
        }
    }

    /**
     * Resets this tile so it has no additional parameters set.
     */
    public void reset() {
        showJumpable = false;
        selected = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(width, height);

        setMeasuredDimension(size, size);
    }
}
