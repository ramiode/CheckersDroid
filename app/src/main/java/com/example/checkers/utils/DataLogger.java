package com.example.checkers.utils;

import android.graphics.Color;

import com.example.checkers.Observer;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.actions.MoveAction;

/**
 * Logs the game results.
 *
 * @author Ramiar Odendaal
 */
public class DataLogger {
    private int currentColor;
    public void printAction(Action action, Observer o){
        switchColor(action);
        o.updateText(action.toString(), currentColor);
    }

    public void printSystemText(String s, Observer o){
        o.updateText(s, currentColor);
    }

    private void switchColor(Action action){
        if(action.getActingPlayer().getColor() == AppConstants.PLAYER_RED){
            currentColor = Color.RED;
        }
        else{
            currentColor = Color.BLUE;
        }
    }
}
