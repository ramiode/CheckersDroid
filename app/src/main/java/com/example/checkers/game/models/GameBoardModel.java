package com.example.checkers.game.models;

import com.example.checkers.EngineSubject;
import com.example.checkers.Observer;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.actions.MoveAction;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.utils.AppConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * The model representation of the GameBoard. Handles Stone placement and manages board state.
 *
 * @author Ramiar Odendaal
 */
public class GameBoardModel implements Cloneable, EngineSubject {

    private Stone[][] board;
    private List<Observer> observers;
    private List<Stone> playerOneStones;
    private List<Stone> playerTwoStones;

    /**
     * Constructor that initializes an empty board to be filled later.
     */
    public GameBoardModel() {
        board = new Stone[8][8];
        observers = new LinkedList<>();
    }

    /**
     * Constructor that initializes a duplicate board.
     *
     * @param model the model which will have its board duplicated
     */
    public GameBoardModel(GameBoardModel model){
        board = new Stone[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board[i][j] = model.board[i][j] != null ? model.board[i][j].clone() : null;
            }
        }
        observers = new LinkedList<>();
        //TODO: Does it matter what type of list?
        playerOneStones = model.getPlayerOneStones().stream().map(Stone::clone).collect(Collectors.toList());
        playerTwoStones = model.getPlayerTwoStones().stream().map(Stone::clone).collect(Collectors.toList());
    }

    /**
     * Places each stone in the provided list of stones.
     *
     * @param isPlayerOneRed true if player one plays as red
     */
    public void initializeStones(boolean isPlayerOneRed) {
        playerOneStones = initializePieces(isPlayerOneRed);
        playerTwoStones = initializePieces(!isPlayerOneRed);

    }

    public List<Stone> getPlayerOneStones(){
        return playerOneStones;
    }

    public List<Stone> getPlayerTwoStones(){
        return playerTwoStones;
    }
    private List<Stone> initializePieces(boolean isRed){
        List<Stone> stones = new ArrayList<>();
        int start = isRed ? 40 : 0;
        int end = isRed ? 63 : 24;
        String color = isRed ? AppConstants.PLAYER_RED : AppConstants.PLAYER_WHITE;
        for(int i = start; i < end; i++){
            //if even row then place in odd columns
            Stone stone;
            if((i & 1) % 2 == 1 && (i >> 3) % 2 == 0){
                stone = new Stone(i, color);
                stones.add(stone);
                placeStone(stone, AppConstants.ROW[stone.getPosition()], AppConstants.COL[stone.getPosition()]);
            }
            //if odd row then place in even columns
            else if((i >> 3) % 2 == 1 && (i & 1) % 2 == 0){
                stone = new Stone(i, color);
                stones.add(stone);
                placeStone(stone, AppConstants.ROW[stone.getPosition()], AppConstants.COL[stone.getPosition()]);
            }
        }
        return stones;
    }

    /**
     * Places a new stone in the indicated row and column.
     *
     * @param stone     the stone to be placed
     * @param targetRow the new row
     * @param targetCol the new column
     */
    public void placeStone(Stone stone, int targetRow, int targetCol) {
        board[targetRow][targetCol] = stone;
    }

    /**
     * Moves an existing stone from one position to a new one.
     *
     * @param stone     the stone to be moved
     * @param oldPosition the previous position of the stone on the board
     * @param newPosition the new position of the stone on the board
     */
    private void executeMove(Stone stone, int oldPosition, int newPosition) {
        //TODO: Fix weirdness
        boolean isRed = stone.getPlayerColor().equals(AppConstants.PLAYER_RED);
        List<Stone> stones = isRed ? playerOneStones : playerTwoStones;

        int targetRow = AppConstants.ROW[newPosition];
        int targetCol = AppConstants.COL[newPosition];
        int originRow = AppConstants.ROW[oldPosition];
        int originCol = AppConstants.COL[oldPosition];

        board[originRow][originCol] = null;
        board[targetRow][targetCol] = stone;
        stone.setPosition(newPosition);

        int[] opponentRow = isRed ? AppConstants.WHITE_ROW : AppConstants.RED_ROW;
        boolean upgradeToKing = newPosition <= opponentRow[1] && newPosition >= opponentRow[0];

        stones.stream()
                .filter(element -> element.getId() == stone.getId())
                .findFirst()
                .ifPresent(element -> {
                    element.setPosition(newPosition);
                    if(upgradeToKing){
                        element.upgradeToKing();
                        stone.upgradeToKing();
                    }
                });

        observers.forEach(e -> e.updateMoveStoneInUI(stone));
    }

    /**
     * Retrieves the stone at the specified position
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
     * @param stone the stone to be moved
     */
    public void executeAction(Action action, Stone stone) {
        if(action instanceof MoveAction){
            MoveAction move = (MoveAction) action;
            executeMove(stone, move.from, move.to);
        }
        else if(action instanceof JumpAction){
            executeJump((JumpAction) action);
        }
    }

    /**
     * Removes the specified stone form the board.
     *
     * @param stone the stone to be removed
     */
    private void removeStone(Stone stone){
        board[AppConstants.ROW[stone.getPosition()]][AppConstants.COL[stone.getPosition()]] = null;
        if(stone.getPlayerColor().equals(AppConstants.PLAYER_RED)){
            playerOneStones.removeIf(element -> element.getId() == stone.getId());
        }
        else{
            playerTwoStones.removeIf(element -> element.getId() == stone.getId());
        }
    }
    /**
     * Executes a jump move.
     * @param jump the jump action to be executed
     */
    private void executeJump(JumpAction jump){
        List<Stone> capturedStones = jump.getCapturedStones();
        List<Integer> path = jump.getPositions();

        Stone playerStone = getStoneByPosition(jump.getStone().getPosition());
        System.out.println("STONE ON BOARD: " + playerStone);
        System.out.println("STONE IN JUMP: " + jump.getStone());
        System.out.println("POSITION: " + jump.getPositions());
        //Move the stone
        for(int x : path){
            MoveAction move = new MoveAction(playerStone.getPosition(), x, jump.getActingPlayer(), playerStone);
            executeMove(playerStone, move.from, move.to);
            System.out.println("MOVING FROM : " +move.from + " TO " + move.to);

            //delay updates to UI??
        }

        for(Stone stone : capturedStones){
            removeStone(stone);
            if(observers.size() != 0){
                observers.forEach(e -> e.updateRemoveStoneFromUI(stone));
            }
        }



        //notify UI that changes have been made
    }

    /**
     * Used to clone the board model to simulate different states without affecting the actual game board.
     *
     * @return the cloned model
     */
    @Override
    public GameBoardModel clone(){
        return new GameBoardModel(this);
    }

    /**
     * Checks if the position on the board contains a stone.
     *
     * @param position the position on the board
     * @return true if the position is occupied; false otherwise
     */
    public boolean isPositionOccupied(int position){
        if(position > 63 || position < 0){
            return true;
        }
        else{
            return board[AppConstants.ROW[position]][AppConstants.COL[position]] != null;
        }
    }
    //TODO: DO SOMETHING HERE
    @Override
    public void notifyUpdateUI() {
        //
    }

    /**
     * @inheritDoc
     */
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void printBoard(){
        System.out.println("+++++++++++++++++++");
        System.out.println("  1 2 3 4 5 6 7 8");
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(board).map(stoneRow -> Arrays.stream(stoneRow).map(stone -> stone == null ? "-" : stone.getPlayerColor().substring(0, 1)).collect(Collectors.joining("|")))
                .map(string -> String.format("%c|%s", 'A' + (i.getAndIncrement()), string))
                .forEach(System.out::println);
        System.out.println("+++++++++++++++++++");
    }
}
