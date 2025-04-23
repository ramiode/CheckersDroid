package com.example.checkers.game;

import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.GameBoardModel;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.game.models.actions.MoveAction;
import com.example.checkers.utils.AppConstants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


public class RuleEnforcer {
    private static final int LEFT_DIAGONAL = 7;
    private static final int RIGHT_DIAGONAL = 9;
    public static boolean isMoveValid(final Action action, final GameState state) {
        //List<JumpAction> jumps = state.getJumpActions();

        if(action instanceof MoveAction){
            return isMoveValid((MoveAction) action, state.getBoard(), false);
        }
        else if(action instanceof JumpAction){
            return isJumpValid((JumpAction) action, state);
        }
        else{
            return false;
        }
    }
    //Question: does updating the state in the stream actually update the object or only a copy of it?
    private static boolean isJumpValid(JumpAction jump, GameState state){
        List<Integer> positions = jump.getPositions();

        MoveAction firstJump = new MoveAction(jump.getStone().getPosition(), positions.get(0), jump.getActingPlayer(), jump.getStone());
        boolean isMoveValid = isMoveValid(firstJump, state.getBoard(), true);

        if(positions.size() == 1){
            return isMoveValid;
        }
        else{
            //TODO: Rework
            //change to not update state?
            //state.updateStateWithPartialMove(firstJump);
            boolean areAllMovesValid = IntStream.range(1, positions.size())
                    .mapToObj(i -> new MoveAction(positions.get(i - 1), positions.get(i), jump.getActingPlayer(), jump.getStone()))
                    .allMatch(move -> {
                        boolean valid = isMoveValid(move, state.getBoard(), true);
                        if (valid) {
                            //state.updateStateWithPartialMove(move);
                        }
                        return valid;
                    });
            //boolean noJumpsRemaining = state.getJumpActions().size() == 0;

            return isMoveValid && areAllMovesValid;
        }
    }
    private static boolean isMoveValid(MoveAction move, GameBoardModel model, boolean isJump) {
        int stepDistance = move.to - move.from;
        int multiplier = isJump ? 2 : 1;

        if(isOutOfBounds(move.to)){
            return false;
        } else if (!AppConstants.BOARD_COLOR[move.to].equals("BLACK")) {
            return false;
        } else if(model.isPositionOccupied(move.to)){
            return false;
        } else if(!isOnDiagonal(move.from, move.to)){
            return false;
        } else if (!(Math.abs(stepDistance) == LEFT_DIAGONAL * multiplier || Math.abs(stepDistance) == RIGHT_DIAGONAL * multiplier)) {
            return false;
        } else if(Arrays.stream(move.getStone().getDirections()).noneMatch(e -> e * multiplier == stepDistance)){
            return false;
        }
        return true;
    }

    public static boolean isOnDiagonal(int from, int to) {
        int fromRow = AppConstants.ROW[from];
        int toRow = AppConstants.ROW[to];

        int fromCol = AppConstants.COL[from];
        int toCol = AppConstants.COL[to];

        // For diagonal moves, row and col must each change by exactly 1 (for move) or 2 (for jump)
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        return rowDiff == colDiff; // true if still on a diagonal
    }

    public static boolean isOutOfBounds(int position){
        if(position > 63){
            return true;
        }
        else if(position < 0){
            return true;
        }
        else{
            return false;
        }
    }
}
