package activities;

import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.checkers.Observer;
import com.example.checkers.R;
import com.example.checkers.game.GameState;
import com.example.checkers.game.models.GameEngine;
import com.example.checkers.game.models.actions.Action;
import com.example.checkers.game.models.actions.JumpAction;
import com.example.checkers.game.models.players.HumanPlayer;
import com.example.checkers.game.models.actions.MoveAction;
import com.example.checkers.game.models.players.MachinePlayer;
import com.example.checkers.game.models.players.Player;
import com.example.checkers.game.RuleEnforcer;
import com.example.checkers.game.models.pieces.Stone;
import com.example.checkers.game.models.GameBoardModel;
import com.example.checkers.game.views.GameBoardViewGroup;
import com.example.checkers.game.views.StoneView;
import com.example.checkers.game.views.TileView;
import com.example.checkers.utils.AppConstants;

import java.util.List;

/**
 * Activity for the game that acts as a controller for the models and the views.
 *
 * @author Ramiar Odendaal
 */

public class GameActivity extends AppCompatActivity implements Observer {
    private GameBoardViewGroup gameBoardViewGroup;
    private GameEngine engine;
    private Player playerOne;
    private Player playerTwo;
    private TileView previousTile;
    private TextView logText;

    /**
     * Initializes the activity and manages the models and views.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

    //TODO: Controller thread?
    //TODO: Clean up the listener code by creating a new listener type.
    //TODO: Clean up this whole class
    //TODO: Go through all classes and make them not rely on hard-coded variables, especially GameState class, GameActivity, and GameEngine
    //TODO: Fix memory leak issues related to cloning.
    //TODO: Start on the AI models and logging data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        Button backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(e -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
        });

        logText = findViewById(R.id.textView2);
        logText.setText("");

        initializeComponents();

        View.OnClickListener listener = v -> {
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
        };
        //Add listener to all TileViews on the board
        gameBoardViewGroup.initializeBoard(listener);
        //Add StoneViews to the board
        gameBoardViewGroup.initializeStones(engine.getPlayerOneStones(), AppConstants.RED_STONE);
        gameBoardViewGroup.initializeStones(engine.getPlayerTwoStones(), AppConstants.WHITE_STONE);

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
        //Model components

        playerOne = new HumanPlayer(true, "One");
        playerTwo = new MachinePlayer(false, "Two", true);
        playerTwo.addObserver(this);

        engine = new GameEngine(playerOne, playerTwo);
        engine.addObserver(this);
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
            //add highlighting of available jumps
        }
        //A selection has been made previously but the current tile also contains a stone (invalid move)
        else {
            //TODO: Find alternative solution
            currentPlayer.setSelectedStone(null);
            resetSelection(currentTile);
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
               Action move = Math.abs(to - from) > 9 ? createJumpAction(previouslySelectedStone, to) : new MoveAction(previouslySelectedStone.getPosition(), currentTile.getPosition(), currentPlayer, previouslySelectedStone);
            if (previouslySelectedStone.getPlayerColor() == currentPlayer.getColor() && RuleEnforcer.isMoveValid(move, engine.getCurrentState())) {
                currentPlayer.setNextMove(move);
                //Notify the waiting engine that a move has been made
                engine.countDownLatch();
                //gameBoardViewGroup.findStoneById(previouslySelectedStone.getId()).setPosition(currentTile.getPosition());
                //gameBoardViewGroup.requestLayout();
            } else {
                //alert user that move could not be executed
            }
            resetSelection(currentTile);
        }
        //No stone selected, so reset the selection of tiles on the board
        else {
            resetSelection(currentTile);
        }
    }

    public JumpAction createJumpAction(Stone stone, int position){
        List<JumpAction> availableJumps = engine.getCurrentState().getJumpActions();
        return availableJumps.stream()
                .filter(jump -> jump.getPositions().get(jump.getPositions().size()-1) == position && jump.getStone().getId() == stone.getId())
                .findFirst()
                .orElse(null);
    }
    /**
     * Helper method that resets all changes brought about by selecting tiles on the screen.
     *
     * @param currentTile the currently selected tile
     */
    private void resetSelection(TileView currentTile) {
        gameBoardViewGroup.highlightTile(currentTile, false);
        if (previousTile != null) {
            gameBoardViewGroup.highlightTile(previousTile, false);
        }
        previousTile = null;
    }

    @Override
    public void updateRemoveStoneFromUI(Stone stone) {
        runOnUiThread(() -> {
            StoneView stoneView = gameBoardViewGroup.findStoneById(stone.getId());
            stoneView.postDelayed(() -> {
                stoneView.setVisibility(GONE);
                stoneView.invalidate();
            }, 1000);
        });
    }
    @Override
    public void updateMoveStoneInUI(Stone stone){
        runOnUiThread(() -> {
            StoneView s = gameBoardViewGroup.findStoneById(stone.getId());
            if(stone.getKingStatus()){
                s.setColor(AppConstants.KING_COLOR);
                s.invalidate();
            }
            s.setPosition(stone.getPosition());
            gameBoardViewGroup.requestLayout();
        });
    }
    @Override
    public void updateTileInUI(TileView tile) {
        runOnUiThread(() -> {
            //gameBoardViewGroup.toggleTile(tile);
        });
    }

    @Override
    public void updateText(String s, int color) {
        runOnUiThread(() -> {
            logText.setTextColor(color);
            logText.append(s);
        });
    }

    @Override
    public void updateMoveMade() {
        engine.countDownLatch();
    }

    private class OnBoardClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Player currentPlayer = engine.getTurnToPlay();
            //Do nothing if current player is AI
            if (!currentPlayer.isHuman()) {
                return;
            }

            TileView currentTile = (TileView) v;
            gameBoardViewGroup.highlightTile(currentTile, true);

            int position = currentTile.getPosition();
            Stone stoneOnCurrentTile = engine.getModel().getStoneByPosition(position);
            Stone previouslySelectedStone = currentPlayer.getSelectedStone();

            if (stoneOnCurrentTile != null) {
                handleTileWithStone(currentPlayer, currentTile, stoneOnCurrentTile, previouslySelectedStone);
            } else {
                handleEmptyTile(currentPlayer, currentTile, previouslySelectedStone);
            }
        };
        }

}