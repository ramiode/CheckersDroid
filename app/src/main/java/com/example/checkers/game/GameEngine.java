package com.example.checkers.game;

import java.util.concurrent.CountDownLatch;

public class GameEngine {
    //player one is red
    private Player playerOne;
    //player two is white
    private Player playerTwo;
    private Player turnToPlay;
    private CountDownLatch latch;
    private boolean isRunning;

    public GameEngine(Player playerOne, Player playerTwo){
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public void startGame(){
        isRunning = true;

        //player one always starts
        turnToPlay = playerOne;
        //separate thread
        while(isRunning){
            latch = new CountDownLatch(1);
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            turnToPlay.getNextMove();
            //get next move from turnToPlay
            //player.awaitMove
            //executeMove
            //change players if time has elapsed or if a move has been made
            turnToPlay = turnToPlay.equals(playerOne) ? playerTwo : playerOne;
        }
    }

    public void toggleRunning(){
        countDownLatch();
        isRunning = !isRunning;
    }

    public void countDownLatch(){
        latch.countDown();
    }
}
