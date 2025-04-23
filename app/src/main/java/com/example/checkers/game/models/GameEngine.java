package com.example.checkers.game.models;

import com.example.checkers.EngineSubject;
import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.players.HumanPlayer;
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
    private volatile GameBoardModel gameBoardModel;
    private GameState currentState;
    private final List<Observer> observers;

    /**
     * Constructs the game engine with starting players and an initialized board.
     *
     * @param playerOne The red player
     * @param playerTwo The white player
     */
    public GameEngine(Player playerOne, Player playerTwo){
        this.playerOne = new HumanPlayer(true, "Chad");
        this.playerTwo = new MachinePlayer(false, "Brad", true);
        GameBoardModel model = new GameBoardModel();
        model.initializeStones(true);
        this.currentState = new GameState(model, playerOne, playerTwo, playerOne);
        observers = new LinkedList<>();
        gameLogger = new DataLogger();
    }

    /**
     * Starts the game in a separate thread. Waits for the current player to make a move, executes it, notifies the controller, then switches to the other player in a loop.
     */
    public void startGame(){
        isRunning = true;
        //player one always starts
        turnToPlay = currentState.getCurrentPlayer();

        Runnable gameLoop = () -> {
            while(isRunning){
                latch = new CountDownLatch(1);
                try {
                    if(currentState.isTerminal()){
                        gameLogger.printSystemText(String.format("Game over: %s wins.", turnToPlay.getName()), observers.get(0));
                        break;
                    }
                    turnToPlay = currentState.getCurrentPlayer();
                    gameLogger.printSystemText(String.format("Waiting for player %s...\n", turnToPlay.getName()), observers.get(0));

                    if(!turnToPlay.isHuman()){
                        MachinePlayer player = (MachinePlayer) turnToPlay;
                        player.generateAction(currentState);
                    }

                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                    Action nextMove = turnToPlay.getNextMove();

                    //gameBoardModel.executeAction(nextMove, turnToPlay.getSelectedStone());

                    currentState.updateStateWithAction(turnToPlay.getSelectedStone(), nextMove);
                    gameLogger.printAction(nextMove, observers.get(0));
                    observers.get(0).updateMoveStoneInUI(turnToPlay.getSelectedStone());
                    turnToPlay.setSelectedStone(null);
                    printBoard();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //currentState.switchPlayer();


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
        return currentState.getCurrentPlayer();
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
    private boolean checkIfGameOver(){
        return currentState.isTerminal();
    }
    //TODO: What to do here??
    @Override
    public void notifyUpdateUI() {

    }
    public GameBoardModel getModel(){
        return currentState.getBoard();
    }

    public GameState getCurrentState(){
        return currentState;
    }
    public List<Stone> getPlayerOneStones(){
        return currentState.getBoard().getPlayerOneStones();
    }
    public List<Stone> getPlayerTwoStones(){
        return currentState.getBoard().getPlayerTwoStones();
    }
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
        currentState.getBoard().addObserver(o);
        playerTwo.addObserver(o);
    }

    private void printBoard(){
        GameBoardModel board = currentState.getBoard();
        board.printBoard();
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }
}
