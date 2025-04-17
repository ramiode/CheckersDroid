package com.example.checkers.game;

import com.example.checkers.EngineSubject;
import com.example.checkers.MachinePlayer;
import com.example.checkers.Observer;
import com.example.checkers.game.models.GameBoardModel;
import com.example.checkers.utils.AppConstants;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class GameEngine implements EngineSubject {
    //player one is red
    private Player playerOne;
    //player two is white
    private Player playerTwo;
    private Player turnToPlay;
    private CountDownLatch latch;
    private volatile boolean isRunning;
    private GameBoardModel gameBoardModel;
    private Observer controller;

    public GameEngine(Player playerOne, Player playerTwo, GameBoardModel gameBoardModel){
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.gameBoardModel = gameBoardModel;
    }

    public void startGame(){
        isRunning = true;
        //player one always starts
        turnToPlay = playerOne;

        Runnable gameLoop = () -> {
            while(isRunning){
                latch = new CountDownLatch(1);
                try {
                    System.out.println("Awaiting player...");
                    if(!turnToPlay.isHuman()){
                        MachinePlayer player = (MachinePlayer) turnToPlay;
                        player.generateAction(new GameState(gameBoardModel, playerOne, playerTwo, turnToPlay));
                    }
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MoveAction nextMove = turnToPlay.getNextMove(new GameState(gameBoardModel, playerOne, playerTwo, turnToPlay));

                    gameBoardModel.executeMove(nextMove, turnToPlay.getSelectedStone());
                    controller.updateUI(turnToPlay.getSelectedStone());
                    turnToPlay.setSelectedStone(null);
                    //change players if time has elapsed or if a move has been made
                    turnToPlay = turnToPlay.equals(playerOne) ? playerTwo : playerOne;


            }
        };
        //separate thread
        Thread gameThread = new Thread(gameLoop);
        gameThread.start();

    }

    public void setRunning(boolean isRunning){
        countDownLatch();
        this.isRunning = isRunning;
    }

    public Player getTurnToPlay(){
        return turnToPlay;
    }

    public void countDownLatch(){
        latch.countDown();
    }

    private void checkIfGameOver(){

    }

    @Override
    public void notifyUpdateUI() {

    }

    @Override
    public void addObserver(Observer o) {
        this.controller = o;
    }

    @Override
    public void removeObserver(Observer o) {
        this.controller = null;
    }
}
