package com.example.checkers;

import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.views.TileView;

public interface Observer {
    void updateRemoveStoneFromUI(Stone stone);
    void updateMoveStoneInUI(Stone stone);
    void updateTileInUI(TileView tile);
    void updateText(String s, int color);
    void updateMoveMade();
}
