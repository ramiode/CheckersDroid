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

    public final static int[] WHITE_DIRECTIONS = {7, 9};

    public final static int[] RED_DIRECTIONS = {-7, -9};

    public final static int[] KING_DIRECTIONS = {-7, -9, 7, 9};

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

    public static final String[] TILE_NAMES = {
            "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8",
            "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8",
            "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8",
            "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8",
            "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8",
            "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8",
            "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8",
            "H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8",
    };

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

