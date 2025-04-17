package com.example.checkers.game;

import com.example.checkers.game.models.Action;
import com.example.checkers.game.models.GameBoardModel;
import com.example.checkers.game.models.King;
import com.example.checkers.utils.AppConstants;

public class RuleEnforcer {
    public static boolean isMoveValid(final Action action, final Stone stone, final GameBoardModel model) {
        if (stone.getClass().equals(Stone.class)) {
            if(action.getClass().equals(MoveAction.class)){
                return isStoneMoveValid((MoveAction) action, stone, model);
            }
            else{
                //return isStoneJumpValid();
                return false;
            }

        } else {
            if(action.getClass().equals(MoveAction.class)){
                return isKingMoveValid((MoveAction) action, (King) stone, model);
            }
            else{
                //return isStoneJumpValid();
                return false;
            }
        }
    }

    private static boolean isStoneMoveValid(MoveAction move, Stone stone, GameBoardModel model) {
        //TODO: split up into readable helper functions
                /*
                Two methods, one for stone and one for king
                First, check if there are jumps available (player must jump if jump is available)
                Then check if the move is even valid (player must move to black, stone can only move one step and only forwards)
                 */

        //Can only move to black, another way to do this is to check if the step count is odd
        int stepDistance = Math.abs(move.from - move.to);
        if (!AppConstants.BOARD_COLOR[move.to].equals("BLACK")) {
            return false;
        } else if(model.isPositionOccupied(move.to)){
            return false;
        } else if(move.to > 63 || move.to < 0){
            return false;
        } else if (!(stepDistance == 7 || stepDistance == 9)) {
            return false;
        } else if (stone.getPlayerColor().equals(AppConstants.PLAYER_WHITE) && !(move.to > move.from)) {
            return false;
        } else if (stone.getPlayerColor().equals(AppConstants.PLAYER_RED) && !(move.to < move.from)) {
            return false;
        }
        return true;
    }

    private static boolean isKingMoveValid(MoveAction action, King king, GameBoardModel model) {
        if (!AppConstants.BOARD_COLOR[action.to].equals("BLACK")) {
            System.out.println("COLOR: " + AppConstants.BOARD_COLOR[action.to]);
            System.out.println("POS: " + action.to);
            return false;
        } else {
            return true;
        }
    }
}
