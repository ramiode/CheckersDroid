package activities;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.checkers.R;
import com.example.checkers.game.RuleEnforcer;
import com.example.checkers.game.models.GameEngine;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.game.models.actions.MoveAction;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.players.HumanPlayer;
import com.example.checkers.game.models.players.MachinePlayer;
import com.example.checkers.game.models.players.Player;
import com.example.checkers.game.views.GameBoardViewGroup;
import com.example.checkers.game.views.StoneView;
import com.example.checkers.game.views.TileView;
import com.example.checkers.mvcinterfaces.Controller;
import com.example.checkers.utils.AppConfig;
import com.example.checkers.utils.AppConstants;

import java.util.List;
import java.util.Objects;

/**
 * Activity for the game that also acts as a controller for the models and views.
 *
 * @author Ramiar Odendaal
 */
public class GameActivity extends AppCompatActivity implements Controller {
    private GameBoardViewGroup gameBoardViewGroup;
    private GameEngine engine;
    private TileView previousTile;
    private TextView logText;

    //TODO: Add quitting game thread and disposing of waste when going in and out of GameActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Button backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(e -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
        });


        initializeComponents();

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(e -> {
            recreate();
        });

        engine.startGame();
    }


    /**
     * Helper method that initializes the game-related components.
     */
    private void initializeComponents() {
        //View components
        FrameLayout mainLayout = findViewById(R.id.MainLayout);

        gameBoardViewGroup = new GameBoardViewGroup(this);
        gameBoardViewGroup.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mainLayout.addView(gameBoardViewGroup);
        View.OnClickListener listener = new OnBoardClickListener();
        //Add listener to all TileViews on the board
        gameBoardViewGroup.initializeBoard(listener);

        logText = findViewById(R.id.textView2);
        logText.setText("");

        engine = new GameEngine(this);
        engine.addController(this);

        //Add StoneViews to the board
        gameBoardViewGroup.initializeStones(engine.getPlayerOneStones(), AppConstants.RED_STONE);
        gameBoardViewGroup.initializeStones(engine.getPlayerTwoStones(), AppConstants.WHITE_STONE);
    }

    /**
     * Helper method that handles when a user selects a tile with a stone on it.
     *
     * @param currentPlayer the player whose turn it is
     * @param currentTile   the tile being selected
     * @param currentStone  the stone on the current tile
     * @param previousStone the previously selected stone
     */
    private void handleTileWithStone(Player currentPlayer, TileView currentTile, Stone currentStone, Stone previousStone) {
        //No selection has been made previously
        if (previousTile == null && previousStone == null) {
            currentPlayer.setSelectedStone(currentStone);
            previousTile = currentTile;
            engine.getCurrentState().getJumpActions()
                    .stream()
                    .filter(jump -> jump.getStone().getId() == currentStone.getId())
                    .map(JumpAction::getPositions)
                    .forEach(positionList -> {
                        for (int i = 0; i < positionList.size(); i++) {
                            gameBoardViewGroup.markJumpableTile(positionList.get(i));
                        }
                    });
        }
        //A selection has been made previously but the current tile also contains a stone (invalid move)
        else {
            currentPlayer.setSelectedStone(null);
            resetSelection();
        }
    }

    /**
     * Helper method for handling tiles with no stone on them.
     *
     * @param currentPlayer           the player whose turn it is
     * @param currentTile             the currently selected tile
     * @param previouslySelectedStone the previously selected stone
     */
    private void handleEmptyTile(Player currentPlayer, TileView currentTile, Stone previouslySelectedStone) {
        //This is a new tile, if empty execute move if a stone has been selected previously
        if (previouslySelectedStone != null) {
            //Must set the next move for the player based on their selected action
            int from = previouslySelectedStone.getPosition();
            int to = currentTile.getPosition();

            List<JumpAction> availableJumps = engine.getCurrentState().getJumpActions();
            int stepDistance = Math.abs(to - from);

            Action action = null;
            if (stepDistance > 9 && availableJumps.size() > 0) {
                action = createJumpAction(previouslySelectedStone, availableJumps, to);
            }
            else if(availableJumps.size() == 0){
                action = new MoveAction(previouslySelectedStone.getPosition(), currentTile.getPosition(), currentPlayer, previouslySelectedStone);
            }

            if (Objects.equals(previouslySelectedStone.getPlayerColor(), currentPlayer.getColor()) && RuleEnforcer.isActionValid(action, engine.getCurrentState())) {
                currentPlayer.setNextMove(action);
                //Notify the waiting engine that a move has been made
                engine.countDownLatch();
            } else {
                //alert user that move could not be executed
            }
            resetSelection();
        }
        //No stone selected, so reset the selection of tiles on the board
        else {
            resetSelection();
        }
    }

    /**
     * Helper method that creates a suitable jump action after a user selection.
     *
     * @param stone    The selected stone
     * @param jumps    A list of all possible jumps
     * @param position The position to move to
     * @return a JumpAction matching the user's selected action
     */
    private JumpAction createJumpAction(Stone stone, List<JumpAction> jumps, int position) {
        return jumps.stream()
                .filter(jump -> jump.getPositions().get(jump.getPositions().size() - 1) == position && jump.getStone().getId() == stone.getId())
                .findFirst()
                .orElse(null);
    }

    /**
     * Helper method that resets all changes brought about by selecting tiles on the screen.
     */
    private void resetSelection() {
        gameBoardViewGroup.resetSelection();
        previousTile = null;
        engine.getTurnToPlay().setSelectedStone(null);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void updateRemoveStoneFromUI(Stone stone) {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(() -> {
            StoneView stoneView = gameBoardViewGroup.findStoneById(stone.getId());
            stoneView.postDelayed(() -> {
                stoneView.setVisibility(GONE);
                stoneView.invalidate();
            }, 500);
        });
    }

    /**
     * @inheritDoc
     */
    @Override
    public void updateMoveStoneInUI(Stone stone) {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(() -> {
            StoneView s = gameBoardViewGroup.findStoneById(stone.getId());
            if (stone.getKingStatus()) {
                s.setKing();
                s.invalidate();
            }
            s.setPosition(stone.getPosition());
            gameBoardViewGroup.requestLayout();
        });
    }

    /**
     * @inheritDoc
     */
    @Override
    public void updateText(String s, int color) {
        runOnUiThread(() -> {
            logText.setTextColor(color);
            logText.append(s);
            ScrollView scroll = findViewById(R.id.scrollView);
            scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
        });
    }

    public void resetBoard(){
        runOnUiThread(() -> {
            gameBoardViewGroup.resetStones();
            gameBoardViewGroup.initializeStones(engine.getPlayerOneStones(), AppConstants.RED_STONE);
            gameBoardViewGroup.initializeStones(engine.getPlayerTwoStones(), AppConstants.WHITE_STONE);
        });
    }

    /**
     * @inheritDoc
     */
    @Override
    public void updateMoveMade() {
        engine.countDownLatch();
    }

    /**
     * Inner class that defines a custom click listener for the board.
     */
    private class OnBoardClickListener implements View.OnClickListener {
        /**
         * Handles user selection in the case of a human player. Utilizes helper methods in the outer class.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Player currentPlayer = engine.getTurnToPlay();
            //Do nothing if current player is AI
            if (!currentPlayer.isHuman()) {
                return;
            }

            TileView currentTile = (TileView) v;
            //Highlights selected tile
            gameBoardViewGroup.highlightTile(currentTile, true);
            int position = currentTile.getPosition();

            Stone stoneOnCurrentTile = engine.getModel().getStoneByPosition(position);
            Stone previouslySelectedStone = currentPlayer.getSelectedStone();

            if (stoneOnCurrentTile != null) {
                handleTileWithStone(currentPlayer, currentTile, stoneOnCurrentTile, previouslySelectedStone);
            } else {
                handleEmptyTile(currentPlayer, currentTile, previouslySelectedStone);
            }
        }
    }

}