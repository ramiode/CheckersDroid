package com.example.checkers;

import com.example.checkers.game.Stone;
import com.example.checkers.game.views.ComponentView;

public interface Observer {
    void updateUI(Stone stone);
    void updateMoveMade();
}
