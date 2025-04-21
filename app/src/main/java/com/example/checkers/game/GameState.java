package com.example.checkers.game;

import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.GameBoardModel;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.game.models.actions.MoveAction;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameState implements Cloneable{
    private GameBoardModel board;
    private final Player playerOne;
    private final Player playerTwo;
    private Player currentPlayer;
    //TODO: Fix the memory leak in findJumps
    public GameState(GameBoardModel board, Player playerOne, Player playerTwo, Player currentPlayer){
        this.board = board.clone();
        //have to clone players too
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentPlayer = currentPlayer;
    }

    public List<Action> generateLegalActions(){
        List<Stone> heldStones = currentPlayer.equals(playerOne) ? board.getPlayerOneStones() : board.getPlayerTwoStones();
        List<Action> allLegalActions = new ArrayList<>();

        List<JumpAction> availableJumps = generateJumpActions(heldStones);
        if(availableJumps.size() > 0){
            allLegalActions.addAll(availableJumps);
        }
        else{
            allLegalActions.addAll(generateMoveActions(heldStones));
        }

        return allLegalActions;

    }
    public GameBoardModel getBoard(){
        return board;
    }
    private List<MoveAction> generateMoveActions(List<Stone> stones){
        LinkedList<MoveAction> possibleMoves = new LinkedList<>();
        Iterator itr = stones.iterator();

        while(itr.hasNext()){
            Stone stone = (Stone) itr.next();
            int from = stone.getPosition();
            int[] directions = stone.getDirections();

            for(int i = 0; i < directions.length; i++){
                MoveAction move = new MoveAction(from, from + directions[i], currentPlayer, stone);
                if(RuleEnforcer.isMoveValid(move, this)){
                    possibleMoves.add(move);
                }
            }
        }
        return possibleMoves;
    }
    //TODO: LOOK INTO THIS -USEFUL?
    public List<JumpAction> getJumpActions(){
        return generateJumpActions(currentPlayer.equals(playerOne) ? board.getPlayerOneStones() : board.getPlayerTwoStones());
    }
    private List<JumpAction> generateJumpActions(List<Stone> stones){
        LinkedList<JumpAction> possibleMoves = new LinkedList<>();
        Iterator<Stone> itr = stones.iterator();

        while(itr.hasNext()){
            Stone stone = itr.next();
            int[] directions = stone.getDirections();
            int from = stone.getPosition();

            boolean firstJumpable = canJump(stone, from, directions);

            if(!firstJumpable){
                continue;
            }

            JumpAction jump = new JumpAction(currentPlayer, stone);
            findJumps(possibleMoves, jump, from, directions);

        }
        return possibleMoves;
    }
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    //Stone is only used for the color
    private void findJumps(List<JumpAction> possibleJumps, JumpAction jump, int from, int[] directions){
        boolean deadEnd = true;
        //TODO: KING MIGHT GET STUCK IN A LOOP HERE SINCE IT CAN BACKTRACK
        for(int direction : directions){
            //clone the jump so far
            if(canJump(jump.getStone(), from, direction)){
                int enemyStonePos = from + direction;
                int targetPos = from + direction * 2;
                //needs to add unique sequences of jumps only
                JumpAction updatedJump = jump.clone();
                updatedJump.addCapturedStone(board.getStoneByPosition(enemyStonePos));
                updatedJump.addJumpPosition(targetPos);

                findJumps(possibleJumps, updatedJump, targetPos, directions);

                deadEnd = false;
            }
        }
        if(deadEnd && !jump.isEmpty() && RuleEnforcer.isMoveValid(jump, this)){
            possibleJumps.add(jump);
        }
    }

    private boolean canJump(Stone stone, int from, int direction){
        int target = from + direction;
        if(RuleEnforcer.isOutOfBounds(target) || !RuleEnforcer.isOnDiagonal(from, target)){
            return false;
        }
        if(RuleEnforcer.isOutOfBounds(target+direction) || !RuleEnforcer.isOnDiagonal(from, target + direction)){
            return false;
        }
        if(board.isPositionOccupied(target) && !board.getStoneByPosition(target).getPlayerColor().equals(stone.getPlayerColor())){
            target += direction;
            if(board.isPositionOccupied(target)){
                return false;
            }
            else{
                return true;
            }
        }
        return false;
    }
    private boolean canJump(Stone stone, int from, int[] directions){
        return Arrays.stream(directions).anyMatch(e -> canJump(stone, from, e));
    }
    public void updateStateWithAction(Stone stone, Action action){
        board.executeAction(action, stone);
        switchPlayer();
    }

    public void updateStateWithPartialMove(MoveAction move){
        board.executeAction(move, move.getStone().clone());
    }

    private void switchPlayer(){
        currentPlayer = currentPlayer.equals(playerOne) ? playerTwo : playerOne;
    }

    public boolean isTerminal(){
        return true;
    }

    public GameState clone(){
        try {
            GameState clonedState = (GameState) super.clone();
            clonedState.board = board.clone();
            return clonedState;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
