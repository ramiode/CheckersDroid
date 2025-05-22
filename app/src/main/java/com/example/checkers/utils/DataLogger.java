package com.example.checkers.utils;

import android.content.Context;
import android.graphics.Color;

import com.example.checkers.game.models.ResourceMonitor;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.players.Player;
import com.example.checkers.mvcinterfaces.Controller;

import java.io.BufferedWriter;
import java.io.File;
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
    private float playerOneCpu;
    private long playerOneRam;
    private int playerOneBattery;
    private float playerTwoCpu;
    private long playerTwoRam;
    private int playerTwoBattery;

    public DataLogger(String playerOneModel, String playerTwoModel){
        this.playerOneModel = playerOneModel;
        this.playerTwoModel = playerTwoModel;
    }

    public void initializeWriterForTest(String testName, Context context){
        try {
            File file = new File(context.getFilesDir(), testName);
            bw = new BufferedWriter(new FileWriter(file));
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

    public void logMatchResult(boolean isPlayerOne, String outcome){

        try {
            if(isPlayerOne){
                bw.write(String.format(Locale.ENGLISH,"%s,%s,%f,%d,%d,%d,%f", playerOneModel, outcome, playerOneCpu/playerOneMoveCount,
                        playerOneBattery/playerOneMoveCount, playerOneRam/playerOneMoveCount, playerOneMoveCount, getAverageTimeForPlayerOne())); //also get vitals data
                bw.newLine();
                if(outcome.equals("Win")){
                    playerOneWinCount++;
                }
            }
            else{
                bw.write(String.format(Locale.ENGLISH,"%s,%s,%f,%d,%d,%d,%f", playerTwoModel, outcome, playerTwoCpu/playerOneMoveCount,
                        playerTwoBattery/playerTwoMoveCount, playerTwoRam/playerTwoMoveCount, playerTwoMoveCount, getAverageTimeForPlayerTwo())); //also get vitals data
                bw.newLine();
                if(outcome.equals("Win")){
                    playerTwoWinCount++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPlayerOneWinCount(){
        return playerOneWinCount;
    }
    public int getPlayerTwoWinCount(){
        return playerTwoWinCount;
    }

    public void addMoveData(Player player, double time, ResourceMonitor.Result res){
        if(player.getName().equals("One")){
            averageTimePlayerOne += time;
            playerOneMoveCount++;
            playerOneCpu += (float) res.avgCpu;
            playerOneBattery += res.avgBattery;
            playerOneRam += res.avgRam;
        }
        else{
            averageTimePlayerTwo += time;
            playerTwoMoveCount++;
            playerTwoCpu += (float) res.avgCpu;
            playerTwoBattery += res.avgBattery;
            playerTwoRam += res.avgRam;
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
        playerOneCpu = 0;
        playerOneRam = 0;
        playerOneBattery = 0;
        playerTwoCpu = 0;
        playerTwoRam = 0;
        playerTwoBattery = 0;
    }


}
