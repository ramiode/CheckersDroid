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

public class GameState {
    private final GameBoardModel board;
    private final Player playerOne;
    private final Player playerTwo;
    private Player currentPlayer;
    private List<JumpAction> availableJumpsForCurrentPlayer;

    //TODO: LOOK INTO AVAILABLE JUMPS FOR CURRENT AS A SOURCE FOR STACKOVERFLOW

    public GameState(GameBoardModel board, Player playerOne, Player playerTwo, Player currentPlayer){
        this.board = board.clone();
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentPlayer = currentPlayer;
        this.availableJumpsForCurrentPlayer = new LinkedList<>();
    }

    public List<Action> generateLegalActions(){
        List<Stone> heldStones = currentPlayer.getStones();
        List<Action> allLegalActions = new ArrayList<>();

        List<JumpAction> availableJumps = generateJumpActions(heldStones);
        if(availableJumps.size() > 0){
            allLegalActions.addAll(availableJumps);
        }
        else{
            allLegalActions.addAll(generateMoveActions(heldStones));
        }
        //TODO: REMOVE DEBUG TEXT
        System.out.println("+++++++++++++++++++++++++++++++++");
        currentPlayer.getStones().forEach(e -> System.out.println(e.getPosition()));
        System.out.println("+++++++++++++++++++++++++++++++++");
        System.out.println("--------------------------------");
        System.out.println("SIMULATION TO FIND ALL POSSIBLE MOVES");
        allLegalActions.forEach(e -> System.out.println("STONE " + e.getStone().getPosition() + " " + e));
        System.out.println("--------------------------------");


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
        if(availableJumpsForCurrentPlayer == null){
            availableJumpsForCurrentPlayer = generateJumpActions(currentPlayer.getStones());
        }
        return availableJumpsForCurrentPlayer;
    }
    private List<JumpAction> generateJumpActions(List<Stone> stones){
        LinkedList<JumpAction> possibleMoves = new LinkedList<>();
        Iterator<Stone> itr = stones.iterator();


        while(itr.hasNext()){
            Stone stone = (Stone) itr.next();
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
    //Stone is only used for the color
    private void findJumps(List<JumpAction> possibleJumps, JumpAction jump, int from, int[] directions){
        boolean notDeadEnd = false;
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

                notDeadEnd = true;
            }
            if(notDeadEnd && !jump.isEmpty() && RuleEnforcer.isMoveValid(jump, this)){
                possibleJumps.add(jump);
            }

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
        availableJumpsForCurrentPlayer = null;
        switchPlayer();
    }

    public void updateStateWithPartialMove(MoveAction move){
        availableJumpsForCurrentPlayer = null;
        board.executeAction(move, move.getStone());
    }

    private void switchPlayer(){
        currentPlayer = currentPlayer.equals(playerOne) ? playerTwo : playerOne;
    }

    public boolean isTerminal(){
        return true;
    }

}
