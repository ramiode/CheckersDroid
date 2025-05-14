package com.example.checkers.game.models;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.players.HumanPlayer;
import com.example.checkers.game.models.players.MachinePlayer;
import com.example.checkers.game.models.players.Player;
import com.example.checkers.mvcinterfaces.Controller;
import com.example.checkers.mvcinterfaces.Subject;
import com.example.checkers.utils.AppConfig;
import com.example.checkers.utils.DataLogger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GameEngine implements Subject {
    //player one is red
    private final Player playerOne;
    //player two is white
    private final Player playerTwo;
    private final DataLogger gameLogger;
    private Player turnToPlay;
    private CountDownLatch latch;
    private volatile boolean isRunning;
    private GameState currentState;
    private Thread gameThread;
    private int counter = 100;
    private final List<Controller> controllers;

    /**
     * Constructs the game engine with starting players and an initialized board.
     *
     * @param controller the controller for this model
     */
    public GameEngine(Controller controller) {
        this.playerOne = AppConfig.isPlayerOneHuman ? new HumanPlayer(true, "One") : new MachinePlayer(true, "One", AppConfig.playerOneModel.equals(AppConfig.MINIMAX));
        this.playerTwo = AppConfig.isPlayerTwoHuman ? new HumanPlayer(false, "Two") : new MachinePlayer(false, "Two", AppConfig.playerTwoModel.equals(AppConfig.MINIMAX));
        playerOne.addController(controller);
        playerTwo.addController(controller);
        GameBoardModel model = new GameBoardModel();
        model.initializeStones(true);
        this.currentState = new GameState(model, playerOne, playerTwo, playerOne);
        controllers = new LinkedList<>();
        gameLogger = new DataLogger();
    }

    public void restartGame(){
        GameBoardModel model = new GameBoardModel();
        model.initializeStones(true);
        this.currentState = new GameState(model, playerOne, playerTwo, playerOne);
        controllers.get(0).resetBoard();
        this.currentState.getBoard().addController(controllers.get(0));
        //startGame();
    }
    /**
     * Starts the game in a separate thread. Waits for the current player to make a move, executes it, notifies the controller, then switches to the other player in a loop.
     */
    public void startGame() {
        isRunning = true;
        //player one always starts
        turnToPlay = currentState.getCurrentPlayer();

        Runnable gameLoop = () -> {
            while (isRunning) {
                latch = new CountDownLatch(1);
                try {
                    if (currentState.isTerminal()) {
                        if(currentState.isDraw()){
                            gameLogger.printSystemText("Game ends in draw after no captures and no stone movement for 40 turns.\n", controllers.get(0));
                        }
                        else {
                            gameLogger.printSystemText(String.format("Game over: %s wins.\n", currentState.getWinner().getName()), controllers.get(0));
                        }
                        //Could log the match results here
                        Thread.sleep(1000);
                        gameLogger.printSystemText("Restarting game...", controllers.get(0));
                        Thread.sleep(1000);
                        restartGame();
                        //break;
                    }

                    turnToPlay = currentState.getCurrentPlayer();
                    gameLogger.printSystemText(String.format("Waiting for player %s...\n", turnToPlay.getName()), controllers.get(0));

                    if (!turnToPlay.isHuman()) {
                        MachinePlayer player = (MachinePlayer) turnToPlay;
                        player.generateAction(currentState);
                    }
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                Action nextMove = turnToPlay.getNextMove();

                currentState.updateStateWithAction(nextMove);
                gameLogger.printAction(nextMove, controllers.get(0));

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        gameThread = new Thread(gameLoop);
        gameThread.start();
    }

    /**
     * Terminates the game by toggling the isRunning boolean and resetting the latch.
     */
    public void terminate() {
        countDownLatch();
        this.isRunning = false;
    }

    /**
     * Retrieves the player whose turn it currently is
     *
     * @return the current player
     */
    public Player getTurnToPlay() {
        return currentState.getCurrentPlayer();
    }

    /**
     * Counts down the latch to allow the game loop to stop waiting for a move.
     */
    public void countDownLatch() {
        latch.countDown();
    }

    /**
     * Getter for the board model
     *
     * @return the game board
     */
    public GameBoardModel getModel() {
        return currentState.getBoard();
    }

    /**
     * Getter for the current state of the game
     *
     * @return the current GameState
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * Getter for player one's stones.
     *
     * @return the list for player one's stones on the board
     */
    public List<Stone> getPlayerOneStones() {
        return currentState.getBoard().getPlayerOneStones();
    }

    /**
     * Getter for player two's stones.
     *
     * @return the list for player two's stones on the board
     */
    public List<Stone> getPlayerTwoStones() {
        return currentState.getBoard().getPlayerTwoStones();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void addController(Controller c) {
        controllers.add(c);
        currentState.getBoard().addController(c);
        //playerTwo.addController(c);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void removeController(Controller c) {
        controllers.remove(c);
    }

    /**
     * Prints a text representation of the board. Used for debugging only.
     */
    private void printBoard() {
        GameBoardModel board = currentState.getBoard();
        board.printBoard();
    }
}
