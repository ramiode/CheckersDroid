package com.example.checkers.game.models;

import com.example.checkers.game.MoveAction;
import com.example.checkers.game.Player;
import com.example.checkers.game.Stone;
import com.example.checkers.utils.AppConstants;

import java.util.List;

/**
 * The model representation of the GameBoard. Handles Stone placement and manages board state.
 *
 * @author Ramiar Odendaal
 */
public class GameBoardModel {

    private Stone[][] board = new Stone[8][8];

    public GameBoardModel() {

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
        if (board[targetRow][targetCol] == null) {
            board[targetRow][targetCol] = stone;
        } else {
            //throw InvalidMoveException
        }
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

        if(board[targetRow][targetCol] == null) {
            board[originRow][originCol] = null;
            board[targetRow][targetCol] = stone;
            stone.setPosition(newPosition);
        }
        else{
            //InvalidMoveException
        }
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
        return true; //if valid move
    }


}
