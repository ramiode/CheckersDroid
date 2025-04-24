package com.example.checkers.game;

import com.example.checkers.game.models.GameBoardModel;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.game.models.actions.MoveAction;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.players.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the state of the game, the current player, and the board.
 *
 * @author Ramiar Odendaal
 */
public class GameState implements Cloneable {
    private GameBoardModel board;
    private final Player playerOne;
    private final Player playerTwo;
    private Player currentPlayer;

    /**
     * Clones the given board and initializes all members.
     *
     * @param board         the board model
     * @param playerOne     the first player
     * @param playerTwo     the second player
     * @param currentPlayer the player whose turn it is
     */
    public GameState(GameBoardModel board, Player playerOne, Player playerTwo, Player currentPlayer) {
        this.board = board.clone();

        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentPlayer = currentPlayer;
    }

    /**
     * Generates all legal actions for the current player given the current state.
     *
     * @return a list of all legal actions that can be made
     */
    public List<Action> generateLegalActions() {
        List<Stone> heldStones = currentPlayer.equals(playerOne) ? board.getPlayerOneStones() : board.getPlayerTwoStones();
        List<Action> allLegalActions = new ArrayList<>();

        List<JumpAction> availableJumps = generateJumpActions(heldStones);

        if (availableJumps.size() > 0) {
            allLegalActions.addAll(availableJumps);
        }
        else {
            allLegalActions.addAll(generateMoveActions(heldStones));
        }
        return allLegalActions;
    }

    /**
     * Getter for this state's board model
     *
     * @return the board model
     */
    public GameBoardModel getBoard() {
        return board;
    }

    /**
     * Generates all legal move actions given the current state
     *
     * @param stones the list of the current player's stones
     * @return a list of all possible move actions
     */
    private List<MoveAction> generateMoveActions(List<Stone> stones) {
        LinkedList<MoveAction> possibleMoves = new LinkedList<>();

        for (Stone stone : stones) {
            int from = stone.getPosition();
            int[] directions = stone.getDirections();

            for (int direction : directions) {
                MoveAction move = new MoveAction(from, from + direction, currentPlayer, stone);
                if (RuleEnforcer.isActionValid(move, this)) {
                    possibleMoves.add(move);
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Getter used externally to generate and return a list of possible jump actions given the current state.
     *
     * @return all possible jump actions for the current player
     */
    public List<JumpAction> getJumpActions() {
        return generateJumpActions(currentPlayer.equals(playerOne) ? board.getPlayerOneStones() : board.getPlayerTwoStones());
    }

    /**
     * Generates all legal jump actions for the current player given the current state
     *
     * @param stones the current player's stones
     * @return a list of all possible jump actions
     */
    private List<JumpAction> generateJumpActions(List<Stone> stones) {
        LinkedList<JumpAction> possibleMoves = new LinkedList<>();

        for (Stone stone : stones) {
            int[] directions = stone.getDirections();
            int from = stone.getPosition();

            boolean firstJumpable = canJump(stone, from, directions);

            if (!firstJumpable) {
                continue;
            }

            JumpAction jump = new JumpAction(currentPlayer, stone);
            findJumps(possibleMoves, jump, from, directions);

        }
        return possibleMoves;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Helper method used to recursively search for jumps until all possibilities are exhausted.
     *
     * @param possibleJumps list containing all generated jump actions to this point
     * @param jump          the JumpAction to build on if more jumps are available
     * @param from          the starting position
     * @param directions    possible directions for the given stone
     */
    private void findJumps(List<JumpAction> possibleJumps, JumpAction jump, int from, int[] directions) {
        boolean deadEnd = true;

        for (int direction : directions) {
            //clone the jump so far
            if (canJump(jump.getStone(), from, direction)) {
                int enemyStonePos = from + direction;
                int targetPos = from + direction * 2;

                Stone enemyStone = board.getStoneByPosition(enemyStonePos);
                if (jump.getCapturedStones().stream().noneMatch(s -> s.getId() == enemyStone.getId())) {
                    //needs to add unique sequences of jumps only
                    JumpAction updatedJump = jump.clone();
                    updatedJump.addCapturedStone(enemyStone);
                    updatedJump.addJumpPosition(targetPos);

                    findJumps(possibleJumps, updatedJump, targetPos, directions);

                    deadEnd = false;
                }
            }
        }
        if (deadEnd && !jump.isEmpty() && RuleEnforcer.isActionValid(jump, this.clone())) {
            possibleJumps.add(jump);
        }
    }

    /**
     * Checks if the stone can jump in the given direction.
     *
     * @param stone     the current player's selected stone
     * @param from      the starting position for the stone
     * @param direction the direction to check
     * @return true if a jump is possible in the specific direction
     */
    private boolean canJump(Stone stone, int from, int direction) {
        int target = from + direction;

        if (RuleEnforcer.isOutOfBounds(target) || !RuleEnforcer.isOnDiagonal(from, target)) {
            return false;
        }
        if (RuleEnforcer.isOutOfBounds(target + direction) || !RuleEnforcer.isOnDiagonal(from, target + direction)) {
            return false;
        }
        if (board.isPositionOccupied(target) && !board.getStoneByPosition(target).getPlayerColor().equals(stone.getPlayerColor())) {
            target += direction;
            return !board.isPositionOccupied(target);
        }

        return false;
    }

    /**
     * Helper method to check if the stone can jump in any of the possible directions.
     *
     * @param stone      the current player's selected stone
     * @param from       the starting position for the stone
     * @param directions the directions to check
     * @return true if a jump is possible in any direction
     */
    private boolean canJump(Stone stone, int from, int[] directions) {
        return Arrays.stream(directions).anyMatch(e -> canJump(stone, from, e));
    }

    /**
     * Given an action and a stone, updates this state with that action by performing the move on the board. Switches the current player after the action is executed.
     *
     * @param stone  the selected stone
     * @param action the selected action
     */
    public void updateStateWithAction(Stone stone, Action action) {
        board.executeAction(action, stone);
        switchPlayer();
    }

    /**
     * Switches the current player.
     */
    private void switchPlayer() {
        currentPlayer = currentPlayer.equals(playerOne) ? playerTwo : playerOne;
    }

    /**
     * Checks if this state is terminal, i.e. the game has ended in a win for either player or a draw.
     *
     * @return true if the state is terminal
     */
    public boolean isTerminal() {
        //TODO: Add draw if no captures have been made in x amount of moves?
        if (board.getPlayerOneStones().size() == 0 || board.getPlayerTwoStones().size() == 0) {
            return true;
        }
        else if (generateLegalActions().size() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public GameState clone() {
        try {
            GameState clonedState = (GameState) super.clone();
            clonedState.board = board.clone();
            return clonedState;
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
