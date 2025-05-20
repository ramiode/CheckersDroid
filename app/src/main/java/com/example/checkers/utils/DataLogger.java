package com.example.checkers.utils;

import android.graphics.Color;

import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.players.Player;
import com.example.checkers.mvcinterfaces.Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Logs the game results and notifies the controller to update the UI.
 *
 * @author Ramiar Odendaal
 */
public class DataLogger {
    private int currentColor;
    private BufferedWriter bw;
    private String playerOneModel;
    private String playerTwoModel;
    private double averageTimePlayerOne;
    private int playerTwoWinCount;
    private int playerOneWinCount;
    private double averageTimePlayerTwo;
    private int playerOneMoveCount;
    private int playerTwoMoveCount;

    public DataLogger(String playerOneModel, String playerTwoModel){
        this.playerOneModel = playerOneModel;
        this.playerTwoModel = playerTwoModel;
    }

    public void initializeWriterForTest(String testName){
        try {
            bw = new BufferedWriter(new FileWriter(testName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveTestFileData(){
        try {
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
            currentColor = Color.WHITE;
        }
    }

    public void logMatchResult(int cpuPercentage, int batteryPercentage, int ramPercentage, boolean isWin, boolean isPlayerOne){
        /*
        try {
            if(isPlayerOne){

                bw.write(String.format(Locale.ENGLISH,"%s,%s,%d,%d,%d,%d,%f", playerOneModel, win, cpuPercentage,
                        batteryPercentage, ramPercentage, playerOneMoveCount, getAverageTimeForPlayerOne())); //also get vitals data
                bw.newLine();
                playerOneWinCount++;
            }
            else{
                bw.write(String.format(Locale.ENGLISH,"%s,%s,%d,%d,%d,%d,%f", playerTwoModel, win, cpuPercentage,
                        batteryPercentage, ramPercentage, playerTwoMoveCount, getAverageTimeForPlayerTwo())); //also get vitals data
                bw.newLine();
                playerTwoWinCount++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

         */
        if(isPlayerOne && isWin){
            playerOneWinCount++;
        }
        else if(!isPlayerOne && isWin){
            playerTwoWinCount++;
        }
    }

    public int getPlayerOneWinCount(){
        return playerOneWinCount;
    }
    public int getPlayerTwoWinCount(){
        return playerTwoWinCount;
    }

    public void addMoveTime(Player player, double time){
        if(player.getColor().equals(AppConstants.PLAYER_RED)){
            averageTimePlayerOne += time;
            playerOneMoveCount++;
        }
        else{
            averageTimePlayerTwo += time;
            playerTwoMoveCount++;
        }
    }

    public double getAverageTimeForPlayerTwo(){
        return (double) averageTimePlayerTwo/playerTwoMoveCount;
    }
    public double getAverageTimeForPlayerOne(){
        return (double) averageTimePlayerOne/playerOneMoveCount;
    }
    public int getPlayerOneMoveCount(){
        return playerOneMoveCount;
    }
    public int getPlayerTwoMoveCount(){
        return playerTwoMoveCount;
    }

    public void reset(){
        averageTimePlayerTwo = 0;
        averageTimePlayerOne = 0;
        playerOneMoveCount = 0;
        playerTwoMoveCount = 0;
    }


}
