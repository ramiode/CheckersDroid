package com.example.checkers.game.models;

import com.example.checkers.game.RuleEnforcer;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.game.models.actions.MoveAction;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.mvcinterfaces.Controller;
import com.example.checkers.mvcinterfaces.Subject;
import com.example.checkers.utils.AppConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The model representation of the GameBoard. Handles Stone placement and manages board state.
 *
 * @author Ramiar Odendaal
 */
public class GameBoardModel implements Cloneable, Subject {

    private Stone[][] board;
    private List<Controller> controllers;
    private List<Stone> playerOneStones;
    private List<Stone> playerTwoStones;
    private boolean isPlayerOneRed;

    /**
     * Constructor that initializes an empty board array to be filled later.
     */
    public GameBoardModel() {
        board = new Stone[8][8];
        controllers = new LinkedList<>();
    }

    /**
     * Constructor that initializes a duplicate board.
     *
     * @param model the model which will have its board duplicated
     */
    public GameBoardModel(GameBoardModel model) {
        board = new Stone[8][8];
        playerOneStones = model.getPlayerOneStones().stream().map(Stone::clone).collect(Collectors.toList());
        playerTwoStones = model.getPlayerTwoStones().stream().map(Stone::clone).collect(Collectors.toList());

        playerOneStones.forEach(s -> placeStone(s, s.getPosition()));
        playerTwoStones.forEach(s -> placeStone(s, s.getPosition()));
        controllers = new LinkedList<>();
    }

    /**
     * Initializes each player's stones, places them in a list, and places them on the board in the starting configuration.
     *
     * @param isPlayerOneRed true if player one plays as red
     */
    public void initializeStones(boolean isPlayerOneRed) {
        this.isPlayerOneRed = isPlayerOneRed;
        playerOneStones = initializePieces(isPlayerOneRed);
        playerTwoStones = initializePieces(!isPlayerOneRed);
    }

    /**
     * Getter for the list containing player one's held stones.
     *
     * @return the list containing all stones belonging to player one that are currently on the board
     */
    public List<Stone> getPlayerOneStones() {
        return playerOneStones;
    }

    /**
     * Getter for the list containing player two's held stones.
     *
     * @return the list containing all stones belonging to player two that are currently on the board
     */
    public List<Stone> getPlayerTwoStones() {
        return playerTwoStones;
    }

    /**
     * Helper method that initializes the starting stones for a player. Used at the beginning of the game.
     *
     * @param isRed true if the player is playing as red
     * @return A list containing all initialized and placed stones for the player
     */
    private List<Stone> initializePieces(boolean isRed) {
        List<Stone> stones = new ArrayList<>();
        int start = isRed ? 40 : 0;
        int end = isRed ? 63 : 24;
        String color = isRed ? AppConstants.PLAYER_RED : AppConstants.PLAYER_WHITE;
        for (int i = start; i < end; i++) {
            //if even row then place in odd columns
            Stone stone;
            if ((i & 1) % 2 == 1 && (i >> 3) % 2 == 0) {
                stone = new Stone(i, color);
                stones.add(stone);
                placeStone(stone, stone.getPosition());
            }
            //if odd row then place in even columns
            else if ((i >> 3) % 2 == 1 && (i & 1) % 2 == 0) {
                stone = new Stone(i, color);
                stones.add(stone);
                placeStone(stone, stone.getPosition());
            }
        }
        return stones;
    }

    /**
     * Places a new stone in the indicated row and column.
     *
     * @param stone     the stone to be placed
     */
    public void placeStone(Stone stone, int position) {
        int targetRow = AppConstants.ROW[position];
        int targetCol = AppConstants.COL[position];
        board[targetRow][targetCol] = stone;
    }

    /**
     * Moves an existing stone from one position to a new one.
     * Checks if the stone can be upgraded to a king after moving.
     *
     * @param move the MoveAction to be executed
     */
    private void executeMove(MoveAction move) {
        int newPosition = move.to;
        int oldPosition = move.from;
        Stone stone = move.getStone();
        boolean isPlayerOne = move.getActingPlayer().getName().equals("One");
        List<Stone> stones = isPlayerOne ? playerOneStones : playerTwoStones;

        int targetRow = AppConstants.ROW[newPosition];
        int targetCol = AppConstants.COL[newPosition];
        int originRow = AppConstants.ROW[oldPosition];
        int originCol = AppConstants.COL[oldPosition];

        board[originRow][originCol] = null;
        board[targetRow][targetCol] = stone;
        stone.setPosition(newPosition);

        //Checks if the opponent's back row has been reached after the move
        boolean upgradeToKing = move.isKingingMove();

        stones.stream()
                .filter(element -> element.getId() == stone.getId())
                .findFirst()
                .ifPresent(element -> {
                    element.setPosition(newPosition);
                    if (upgradeToKing) {
                        element.upgradeToKing();
                        stone.upgradeToKing();
                    }
                });

        controllers.forEach(e -> e.updateMoveStoneInUI(stone));
    }

    /**
     * Retrieves the stone at the specified position
     *
     * @param position the position of the stone
     * @return the desired stone (if it exists)
     */
    public Stone getStoneByPosition(int position) {
        return board[AppConstants.ROW[position]][AppConstants.COL[position]];
    }

    /**
     * Executes a MoveAction on the board.
     *
     * @param action the action to be executed
     */
    public void executeAction(Action action) {
        if (action instanceof MoveAction) {
            MoveAction move = (MoveAction) action;
            executeMove(move);
        } else if (action instanceof JumpAction) {
            executeJump((JumpAction) action);
        }
    }

    /**
     * Removes the specified stone form the board.
     *
     * @param stone the stone to be removed
     */
    private void removeStone(Stone stone, boolean isPlayerOne) {
        board[AppConstants.ROW[stone.getPosition()]][AppConstants.COL[stone.getPosition()]] = null;
        if (isPlayerOne) {
            playerTwoStones.removeIf(element -> element.getId() == stone.getId());
        } else {
            playerOneStones.removeIf(element -> element.getId() == stone.getId());
        }
    }
    public int checkAdjacentFriendlyStones(Stone stone){
        int[] directions = AppConstants.KING_DIRECTIONS;
        int countAdjacent = 0;
        for(int direction : directions){
            int adjacentPosition = stone.getPosition() + direction;
            if(!RuleEnforcer.isOutOfBounds(adjacentPosition)) {
                Stone adjacentStone = getStoneByPosition(adjacentPosition);
                if (adjacentStone != null && adjacentStone.getPlayerColor().equals(stone.getPlayerColor())) {
                    countAdjacent++;
                }
            }
        }
        return countAdjacent;
    }
    /**
     * Reverses a previous action to undo changes to the board.
     *
     * @param action the action to be reversed
     */
    public void undoAction(Action action){
        if (action instanceof MoveAction) {
            MoveAction move = (MoveAction) action;
            undoMove(move);
        } else if (action instanceof JumpAction) {
            undoJump((JumpAction) action);
        }
    }

    /**
     * Helper method that handles undoing of MoveActions.
     * @param move the move action to be reversed
     */
    private void undoMove(MoveAction move){
        if(move.isKingingMove()){
            move.getStone().undoKing();
            boolean isPlayerOne = move.getActingPlayer().getName().equals("One");
            List<Stone> stones = isPlayerOne ? playerOneStones : playerTwoStones;
            stones.stream()
                    .filter(element -> element.getId() == move.getStone().getId())
                    .findFirst()
                    .ifPresent(Stone::undoKing);
        }

        MoveAction undoMove = new MoveAction(move.to, move.from, move.getActingPlayer(), move.getStone());
        executeMove(undoMove);
    }

    /**
     * Helper method that undoes JumpActions
     * @param jump the JumpAction to be reversed
     */
    private void undoJump(JumpAction jump){

        jump.getCapturedStones().forEach(stone -> {
            //not the exact same stone objects
            placeStone(stone, stone.getPosition());
            boolean b =  jump.getActingPlayer().getName().equals("One") ? playerTwoStones.add(stone) : playerOneStones.add(stone);
        });
        int originPos = jump.getStartingPos();
        Stone playerStone = jump.getStone();
        MoveAction move = new MoveAction(playerStone.getPosition(), originPos, jump.getActingPlayer(), playerStone);
        executeMove(move);
    }

    /**
     * Executes a jump move by moving the stone to each new position and removing the captured enemy stones.
     *
     * @param jump the jump action to be executed
     */
    private void executeJump(JumpAction jump) {
        List<Stone> capturedStones = jump.getCapturedStones();
        List<Integer> path = jump.getPositions();

        Stone playerStone = jump.getStone();

        for (int x : path) {
            MoveAction move = new MoveAction(playerStone.getPosition(), x, jump.getActingPlayer(), playerStone);
            executeMove(move);
        }

        for (Stone stone : capturedStones) {
            removeStone(stone, jump.getActingPlayer().getName().equals("One"));
            if (controllers.size() != 0) {
                controllers.forEach(e -> e.updateRemoveStoneFromUI(stone));
            }
        }
    }

    /**
     * Used to clone the board model to simulate different states without affecting the actual game board.
     *
     * @return the cloned model
     */
    @Override
    public GameBoardModel clone() {
        try {
            GameBoardModel clonedBoard = (GameBoardModel) super.clone();
            clonedBoard.board = new Stone[8][8];
            clonedBoard.playerOneStones = this.getPlayerOneStones().stream().map(Stone::clone).collect(Collectors.toList());
            clonedBoard.playerTwoStones = this.getPlayerTwoStones().stream().map(Stone::clone).collect(Collectors.toList());
            clonedBoard.controllers = new LinkedList<>();

            clonedBoard.playerOneStones.forEach(s -> clonedBoard.placeStone(s, s.getPosition()));
            clonedBoard.playerTwoStones.forEach(s -> clonedBoard.placeStone(s, s.getPosition()));

            return clonedBoard;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Checks if the position on the board contains a stone.
     *
     * @param position the position on the board
     * @return true if the position is occupied; false otherwise
     */
    public boolean isPositionOccupied(int position) {
        if (position > 63 || position < 0) {
            return true;
        } else {
            return board[AppConstants.ROW[position]][AppConstants.COL[position]] != null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void addController(Controller c) {
        controllers.add(c);
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
    public String printBoard() {
        //System.out.println("+++++++++++++++++++");
        //System.out.println("  1 2 3 4 5 6 7 8");
        AtomicInteger i = new AtomicInteger();
        String textBoard = Arrays.stream(board).map(stoneRow -> Arrays.stream(stoneRow).map(stone -> stone == null ? "-" : stone.getPlayerColor().substring(0, 1)).collect(Collectors.joining("|")))
                .map(string -> String.format("%c|%s\n", 'A' + (i.getAndIncrement()), string))
                .collect(Collectors.joining("\n"));
        //System.out.print(textBoard);
        //System.out.println("+++++++++++++++++++");
        return textBoard;
    }

    public boolean equals(GameBoardModel model){
        for(int i = 0; i < AppConstants.NO_TILES; i++){
            if(model.getStoneByPosition(i) == null){
                if(!(this.getStoneByPosition(i) == model.getStoneByPosition(i))){
                    return false;
                }
            }
            else{
                if(this.getStoneByPosition(i) == null){
                    return false;
                }
                if(!(this.getStoneByPosition(i).getId() == model.getStoneByPosition(i).getId())){
                    return false;
                }
            }
        }
        if(model.getPlayerOneStones().size() != this.playerOneStones.size()){
            return false;
        }
        else if(model.getPlayerTwoStones().size() != this.playerTwoStones.size()){
            return false;
        }
        boolean playerOneMatch = IntStream.range(0, model.getPlayerOneStones().size()).allMatch(i -> model.getPlayerOneStones().get(i).getId() == this.playerOneStones.get(i).getId());
        boolean playerTwoMatch = IntStream.range(0, model.getPlayerTwoStones().size()).allMatch(i -> model.getPlayerTwoStones().get(i).getId() == this.playerTwoStones.get(i).getId());
        return playerOneMatch && playerTwoMatch;
    }
}
