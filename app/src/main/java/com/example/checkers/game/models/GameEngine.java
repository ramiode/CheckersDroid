package com.example.checkers.game.models;

import com.example.checkers.EngineSubject;
import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.players.MachinePlayer;
import com.example.checkers.Observer;
import com.example.checkers.game.models.players.Player;
import com.example.checkers.utils.DataLogger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GameEngine implements EngineSubject {
    //player one is red
    private final Player playerOne;
    //player two is white
    private final Player playerTwo;
    private final DataLogger gameLogger;
    private Player turnToPlay;
    private CountDownLatch latch;
    private volatile boolean isRunning;
    private final GameBoardModel gameBoardModel;
    private final List<Observer> observers;

    /**
     * Constructs the game engine with starting players and an initialized board.
     *
     * @param playerOne The red player
     * @param playerTwo The white player
     * @param gameBoardModel The checkers board
     */
    public GameEngine(Player playerOne, Player playerTwo, GameBoardModel gameBoardModel){
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.gameBoardModel = gameBoardModel;
        observers = new LinkedList<>();
        gameLogger = new DataLogger();
    }

    /**
     * Starts the game in a separate thread. Waits for the current player to make a move, executes it, notifies the controller, then switches to the other player in a loop.
     */
    public void startGame(){
        isRunning = true;
        //player one always starts
        turnToPlay = playerOne;

        Runnable gameLoop = () -> {
            while(isRunning){
                latch = new CountDownLatch(1);
                try {
                    gameLogger.printSystemText(String.format("Waiting for player %s...\n", turnToPlay.getName()), observers.get(0));
                    if(!turnToPlay.isHuman()){
                        MachinePlayer player = (MachinePlayer) turnToPlay;
                        player.generateAction(new GameState(gameBoardModel, playerOne, playerTwo, turnToPlay));
                    }
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                    Action nextMove = turnToPlay.getNextMove();

                    gameBoardModel.executeAction(nextMove, turnToPlay.getSelectedStone());
                    gameLogger.printAction(nextMove, observers.get(0));
                    turnToPlay.setSelectedStone(null);
                    observers.get(0).updateMoveStoneInUI(nextMove.getStone());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                turnToPlay = turnToPlay.equals(playerOne) ? playerTwo : playerOne;


            }
        };

        Thread gameThread = new Thread(gameLoop);
        gameThread.start();

    }

    /**
     * Terminates the game by toggling the isRunning boolean and resetting the latch.
     */
    public void terminate(){
        countDownLatch();
        this.isRunning = false;
    }

    /**
     * Retrieves the player whose turn it currently is
     * @return the current player
     */
    public Player getTurnToPlay(){
        return turnToPlay;
    }

    /**
     * Counts down the latch to allow the game loop to stop waiting for a move.
     */
    public void countDownLatch(){
        latch.countDown();
    }

    /**
     * Checks if a terminal state has been reached.
     */
    private void checkIfGameOver(){
        GameState currentState = new GameState(gameBoardModel, playerOne, playerTwo, turnToPlay);
        currentState.isTerminal();
    }
    //TODO: What to do here??
    @Override
    public void notifyUpdateUI() {

    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }
}
