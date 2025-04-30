package com.example.checkers.game.models;

import com.example.checkers.game.GameState;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.players.HumanPlayer;
import com.example.checkers.game.models.players.MachinePlayer;
import com.example.checkers.game.models.players.Player;
import com.example.checkers.mvcinterfaces.Controller;
import com.example.checkers.mvcinterfaces.Subject;
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
    private final List<Controller> controllers;

    /**
     * Constructs the game engine with starting players and an initialized board.
     *
     * @param playerOne The red player
     * @param playerTwo The white player
     */
    public GameEngine(Player playerOne, Player playerTwo) {
        //TODO: Game engine should define the players
        this.playerOne = new HumanPlayer(true, "Chad");
        this.playerTwo = new MachinePlayer(false, "Brad", true);
        GameBoardModel model = new GameBoardModel();
        model.initializeStones(true);
        this.currentState = new GameState(model, playerOne, playerTwo, playerOne);
        controllers = new LinkedList<>();
        gameLogger = new DataLogger();
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
                        gameLogger.printSystemText(String.format("Game over: %s wins.", turnToPlay.getName()), controllers.get(0));
                        break;
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
                }
                Action nextMove = turnToPlay.getNextMove();

                //gameBoardModel.executeAction(nextMove, turnToPlay.getSelectedStone());

                currentState.updateStateWithAction(nextMove);
                gameLogger.printAction(nextMove, controllers.get(0));
                //controllers.get(0).updateMoveStoneInUI(turnToPlay.getSelectedStone());
                //turnToPlay.setSelectedStone(null);
                //printBoard();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        Thread gameThread = new Thread(gameLoop);
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
        playerTwo.addController(c);
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
