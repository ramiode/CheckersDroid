package com.example.checkers.utils;

import android.graphics.Color;

import com.example.checkers.game.models.actions.Action;
import com.example.checkers.mvcinterfaces.Controller;

/**
 * Logs the game results and notifies the controller to update the UI.
 *
 * @author Ramiar Odendaal
 */
public class DataLogger {
    private int currentColor;

    /**
     * Used to print the action description to the log.
     *
     * @param action the selected action
     * @param c the controller
     */
    public void printAction(Action action, Controller c){
        switchColor(action);
        c.updateText(action.toString(), currentColor);
    }

    /**
     * Used to print system text to the log
     * @param s the string to be printed
     * @param c the controller
     */
    public void printSystemText(String s, Controller c){
        c.updateText(s, currentColor);
    }

    /**
     * Switches color based on who the current player is.
     * @param action the selected action
     */
    private void switchColor(Action action){
        if(action.getActingPlayer().getColor() == AppConstants.PLAYER_RED){
            currentColor = Color.RED;
        }
        else{
            currentColor = Color.BLUE;
        }
    }
}
