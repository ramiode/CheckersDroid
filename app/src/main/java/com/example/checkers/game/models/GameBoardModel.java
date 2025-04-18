package com.example.checkers.game.models;

import com.example.checkers.EngineSubject;
import com.example.checkers.Observer;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.actions.MoveAction;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.utils.AppConstants;

import java.util.LinkedList;
import java.util.List;

/**
 * The model representation of the GameBoard. Handles Stone placement and manages board state.
 *
 * @author Ramiar Odendaal
 */
public class GameBoardModel implements Cloneable, EngineSubject {

    private Stone[][] board;
    private List<Observer> observers;

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
    }

    /**
     * Places each stone in the provided list of stones.
     *
     * @param stones the list representing a specific Player's stones
     */
    public void initializeStones(List<Stone> stones) {
        for (Stone stone : stones) {
            placeStone(stone, AppConstants.ROW[stone.getPosition()], AppConstants.COL[stone.getPosition()]);
        }
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
        int targetRow = AppConstants.ROW[newPosition];
        int targetCol = AppConstants.COL[newPosition];
        int originRow = AppConstants.ROW[oldPosition];
        int originCol = AppConstants.COL[oldPosition];

        board[originRow][originCol] = null;
        board[targetRow][targetCol] = stone;
        stone.setPosition(newPosition);

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
    }
    //TODO: IMPLEMENT THIS
    /**
     * Executes a jump move.
     * @param jump the jump action to be executed
     */
    private void executeJump(JumpAction jump){
        List<Stone> capturedStones = jump.getCapturedStones();
        List<Integer> path = jump.getPositions();

        Stone playerStone = jump.getStone();
        //Move the stone
        for(int x : path){
            MoveAction move = new MoveAction(playerStone.getPosition(), x, jump.getActingPlayer(), jump.getStone());
            executeMove(playerStone, move.from, move.to);
            System.out.println("MOVING FROM : " +move.from + " TO " + move.to);
            if(observers.size() != 0){
                observers.forEach(e -> e.updateMoveStoneInUI(playerStone));
            }
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
     * Clones the array used for the board.
     *
     * @return the cloned version of the board
     */
    private Stone[][] cloneBoardArray(){
        return board.clone();
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
}
