package com.example.checkers.utils;

import android.graphics.Color;

/**
 * Utility class for constants that are used throughout the game.
 *
 * @author Ramiar Odendaal
 */
public final class AppConstants {
    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }

    //Even tile
    public final static int WHITE = Color.GRAY;
    //Odd tile
    public final static int BLACK = Color.BLACK;
    //Selected tile
    public final static int MAGENTA = Color.MAGENTA;

    public final static int RED_STONE = Color.RED;
    public final static int WHITE_STONE = Color.WHITE;

    public final static String PLAYER_WHITE = "WHITE";
    public final static String PLAYER_RED = "RED";

    public final static int NO_TILES = 64;

    //Position-to-row converter
    public static final int[] ROW = {
            0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 1, 1, 1, 1, 1, 1,
            2, 2, 2, 2, 2, 2, 2, 2,
            3, 3, 3, 3, 3, 3, 3, 3,
            4, 4, 4, 4, 4, 4, 4, 4,
            5, 5, 5, 5, 5, 5, 5, 5,
            6, 6, 6, 6, 6, 6, 6, 6,
            7, 7, 7, 7, 7, 7, 7, 7};

    //Position-to-column converter
    public static final int[] COL = {
            0, 1, 2, 3, 4, 5, 6, 7,
            0, 1, 2, 3, 4, 5, 6, 7,
            0, 1, 2, 3, 4, 5, 6, 7,
            0, 1, 2, 3, 4, 5, 6, 7,
            0, 1, 2, 3, 4, 5, 6, 7,
            0, 1, 2, 3, 4, 5, 6, 7,
            0, 1, 2, 3, 4, 5, 6, 7,
            0, 1, 2, 3, 4, 5, 6, 7};
    public static final String[] BOARD_COLOR = {
            "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK",
            "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE",
            "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK",
            "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE",
            "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK",
            "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE",
            "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK",
            "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE", "BLACK", "WHITE",
    };
}

