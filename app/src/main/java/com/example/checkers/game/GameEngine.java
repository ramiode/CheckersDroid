package com.example.checkers.game;

import com.example.checkers.game.models.GameBoardModel;
import com.example.checkers.utils.AppConstants;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class GameEngine {
    //player one is red
    private Player playerOne;
    //player two is white
    private Player playerTwo;
    private Player turnToPlay;
    private CountDownLatch latch;
    private volatile boolean isRunning;
    private GameBoardModel gameBoardModel;

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
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MoveAction nextMove = turnToPlay.getNextMove();

                //TODO: check if move is valid
                /*
                Two methods, one for stone and one for king
                First, check if there are jumps available (player must jump if jump is available)
                Then check if the move is even valid (player must move to black, stone can only move one step and only forwards)

                 */
                if(isStoneMoveValid(nextMove, turnToPlay.getSelectedStone())){
                    gameBoardModel.executeMove(nextMove, turnToPlay.getSelectedStone());
                    turnToPlay.setSelectedStone(null);
                    //change players if time has elapsed or if a move has been made
                    turnToPlay = turnToPlay.equals(playerOne) ? playerTwo : playerOne;
                }

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
    //TODO: Add to rule enforcer class instead and handle logic in GameActivity
    private boolean isStoneMoveValid(MoveAction action, Stone stone){
        if(!AppConstants.BOARD_COLOR[action.to].equals( "BLACK")){
            System.out.println("COLOR: " + AppConstants.BOARD_COLOR[action.to]);
            System.out.println("POS: " + action.to);
            return false;
        }
        else{
            return true;
        }
    }
}
