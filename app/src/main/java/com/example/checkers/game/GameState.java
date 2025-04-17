package com.example.checkers.game;

import com.example.checkers.game.models.Action;
import com.example.checkers.game.models.GameBoardModel;
import com.example.checkers.game.models.JumpAction;
import com.example.checkers.game.models.King;
import com.example.checkers.utils.AppConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameState {
    private GameBoardModel board;
    private final Player playerOne;
    private final Player playerTwo;
    private final int[] RED_DIRECTIONS = {-7, -9};
    private final int[] WHITE_DIRECTIONS = {7, 9};
    private final int[] KING_DIRECTIONS = {-7, -9, 7, 9};
    private Player currentPlayer;

    public GameState(GameBoardModel board, Player playerOne, Player playerTwo, Player currentPlayer){
        this.board = board.clone();
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentPlayer = currentPlayer;
    }

    public List<Action> generateLegalActions(){
        List<Stone> heldStones = currentPlayer.getStones();
        List<Action> allLegalActions = new ArrayList<>();
        /*
        List<JumpAction> availableJumps = generateJumpActions(heldStones);

        if(availableJumps.size() > 0){
            allLegalActions.addAll(availableJumps);
        }
        else{

         */
        allLegalActions.addAll(generateMoveActions(heldStones));


        return allLegalActions;

    }

    private List<MoveAction> generateMoveActions(List<Stone> stones){
        LinkedList<MoveAction> possibleMoves = new LinkedList<>();
        Iterator itr = stones.iterator();
        int[] directions = currentPlayer.getColor().equals(AppConstants.PLAYER_RED) ? RED_DIRECTIONS : WHITE_DIRECTIONS;
        while(itr.hasNext()){
            Stone stone = (Stone) itr.next();
            int from = stone.getPosition();

            for(int i = 0; i < directions.length; i++){
                MoveAction move = new MoveAction(from, from + directions[i], currentPlayer, stone);
                if(RuleEnforcer.isMoveValid(move, stone, board)){
                    possibleMoves.add(move);
                }
            }
        }
        return possibleMoves;
    }
    //TODO: Refactor to use all four directions for King, don't hardcode this
    private List<JumpAction> generateJumpActions(List<Stone> stones){
        LinkedList<JumpAction> possibleMoves = new LinkedList<>();
        Iterator itr = stones.iterator();

        int[] directions = currentPlayer.getSelectedStone().getClass().equals(King.class) ? KING_DIRECTIONS : (currentPlayer.getColor().equals(AppConstants.PLAYER_RED) ? RED_DIRECTIONS : WHITE_DIRECTIONS);

        while(itr.hasNext()){
            Stone stone = (Stone) itr.next();
            int from = stone.getPosition();
            int target = from;

            boolean firstJumpable = (canJump(stone, target+directions[0], directions[0])
                                    || canJump(stone, target+directions[0], directions[0]));
            if(!firstJumpable){
                continue;
            }
            JumpAction jump = new JumpAction(currentPlayer, stone);
            while(true){
                if(canJump(stone, target+directions[0], directions[0])){
                    jump.addCapturedStone(board.getStoneByPosition(target+directions[0]));
                    target += directions[0] * 2;
                    jump.addJumpPosition(target);
                }
                else if(canJump(stone, target+directions[1], directions[1])){
                    jump.addCapturedStone(board.getStoneByPosition(target+directions[1]));
                    target += directions[1] * 2;
                    jump.addJumpPosition(target);
                }
                else{
                    break;
                }
            }
        }
        return possibleMoves;
    }

    private boolean canJump(Stone stone, int target, int direction){
        if(board.isPositionOccupied(target) && !board.getStoneByPosition(target).getPlayerColor().equals(stone.getPlayerColor())){
            target += direction;
            if(!board.isPositionOccupied(target)){
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }
    public void updateStateWithAction(Stone stone, MoveAction move){
        board.moveStone(stone, move.from, move.to);
        switchPlayer();
    }

    private void switchPlayer(){
        currentPlayer = currentPlayer.equals(playerOne) ? playerTwo : playerOne;
    }

}
