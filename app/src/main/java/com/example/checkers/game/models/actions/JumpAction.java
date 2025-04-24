package com.example.checkers.game.models.actions;

import com.example.checkers.game.models.players.Player;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.utils.AppConstants;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete action representing a jump or series of jumps in the game.
 *
 * @author Ramiar Odendaal
 */
public class JumpAction extends Action implements Cloneable {
    private final LinkedList<Integer> path;
    private final LinkedList<Stone> captured;
    private final int startingPos;

    /**
     * Instantiates the JumpAction with a path and a list of captured stones.
     *
     * @param player   the acting player
     * @param stone    the selected stone
     * @param path     the jump path
     * @param captured the captured opponent stones
     */
    public JumpAction(Player player, Stone stone, LinkedList<Integer> path, LinkedList<Stone> captured) {
        super(player, stone);
        this.path = path;
        this.captured = captured;
        startingPos = stone.getPosition();
    }

    /**
     * Instantiates an empty jump action with no path and no captured stones.
     *
     * @param player the acting player
     * @param stone  the selected stone
     */
    public JumpAction(Player player, Stone stone) {
        super(player, stone);
        path = new LinkedList<>();
        captured = new LinkedList<>();
        startingPos = stone.getPosition();
    }

    /**
     * Generates a string describing each jump contained in this JumpAction instance.
     *
     * @return A string describing the JumpAction
     */
    @Override
    public String getActionMessage() {
        String output = "";
        String currentPos = AppConstants.TILE_NAMES[startingPos];
        for (int i = 0; i < path.size(); i++) {
            output += String.format("...moves to %s from %s, capturing enemy at %s.\n", AppConstants.TILE_NAMES[path.get(i)], currentPos,
                    AppConstants.TILE_NAMES[captured.get(i).getPosition()]);
            currentPos = AppConstants.TILE_NAMES[path.get(i)];
        }
        return output;
    }

    /**
     * Adds a position in the jump path.
     *
     * @param position the position to be jumped to
     */
    public void addJumpPosition(int position) {
        if (path != null) {
            path.add(position);
        }
    }

    /**
     * Add a captured stone to the list of captured stones on the path.
     *
     * @param stone the stone to be captured
     */
    public void addCapturedStone(Stone stone) {
        if (captured != null) {
            captured.add(stone);
        }
    }

    /**
     * Returns the jump path.
     *
     * @return the list containing all jump positions
     */
    public List<Integer> getPositions() {
        return path;
    }

    /**
     * Returns the list of captured stones
     *
     * @return the list of captured stones
     */
    public List<Stone> getCapturedStones() {
        return captured;
    }

    /**
     * @inheritDoc
     */
    @Override
    public JumpAction clone() {
        //TODO: clone getstone?
        return new JumpAction(getActingPlayer(), getStone(), new LinkedList<>(path), captured.stream().map(Stone::clone).collect(Collectors.toCollection(LinkedList::new)));
    }

    /**
     * Checks if the JumpAction contains no jumps.
     *
     * @return true only if no jumps have been added
     */
    public boolean isEmpty() {
        if (path.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
