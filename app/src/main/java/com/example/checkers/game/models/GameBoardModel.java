package com.example.checkers.game.models;

import com.example.checkers.game.MoveAction;
import com.example.checkers.game.Stone;
import com.example.checkers.utils.AppConstants;

import java.util.List;

/**
 * The model representation of the GameBoard. Handles Stone placement and manages board state.
 *
 * @author Ramiar Odendaal
 */
public class GameBoardModel implements Cloneable{

    private Stone[][] board;

    public GameBoardModel() {
        board = new Stone[8][8];
    }

    public GameBoardModel(GameBoardModel model){
        board = model.cloneBoard();
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
    public void moveStone(Stone stone, int oldPosition, int newPosition) {
        int targetRow = AppConstants.ROW[newPosition];
        int targetCol = AppConstants.COL[newPosition];
        int originRow = AppConstants.ROW[oldPosition];
        int originCol = AppConstants.COL[oldPosition];

        board[originRow][originCol] = null;
        board[targetRow][targetCol] = stone;
        stone.setPosition(newPosition);

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
     * @param move the action to be executed
     * @param stone the stone to be moved
     * @return true if succesful; false otherwise
     */
    public boolean executeMove(MoveAction move, Stone stone) {
        moveStone(stone,
                move.from, move.to);
        //notify UI that changes have been made
        return true; //if valid move
    }
    private void removeStone(Stone stone){
        board[AppConstants.ROW[stone.getPosition()]][AppConstants.COL[stone.getPosition()]] = null;
    }
    public boolean executeJump(JumpAction jump){
        List<Stone> capturedStones = jump.getCapturedStones();
        List<Integer> path = jump.getPositions();

        for(Stone stone : capturedStones){
            removeStone(stone);
        }

        Stone playerStone = jump.getCurrentStone();

        for(int x : path){
            MoveAction move = new MoveAction(playerStone.getPosition(), x, jump.getActingPlayer(), jump.getCurrentStone());
            moveStone(playerStone, move.from, move.to);
            //delay updates to UI??
        }
        //notify UI that changes have been made
        return true;
    }

    private Stone[][] cloneBoard(){
        return board.clone();
    }
    @Override
    public GameBoardModel clone(){
        return new GameBoardModel(this);
    }

    public boolean isPositionOccupied(int position){
        return board[AppConstants.ROW[position]][AppConstants.COL[position]] != null;
    }


}
