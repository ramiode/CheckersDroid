package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.checkers.game.GameEngine;
import com.example.checkers.game.HumanPlayer;
import com.example.checkers.game.MoveAction;
import com.example.checkers.game.Player;
import com.example.checkers.game.Stone;
import com.example.checkers.game.models.GameBoardModel;
import com.example.checkers.game.views.GameBoardViewGroup;
import com.example.checkers.game.views.TileView;
import com.example.checkers.utils.AppConstants;

import java.util.concurrent.CountDownLatch;

/**
 * Activity for the game that acts as a controller for the models and the views.
 *
 * @author Ramiar Odendaal
 */
public class GameActivity extends AppCompatActivity {
    private GameBoardViewGroup gameBoardViewGroup;
    private GameBoardModel gameBoardModel;
    private GameEngine engine;
    private Player playerOne;
    private Player playerTwo;
    private TileView previousTile;

    /**
     * Initializes the activity and manages the models and views.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    //TODO: Consider further cleaning up the listener code by creating a new listener type.
    //TODO: Implement rules for checkers
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

        initializeComponents();

        View.OnClickListener listener = v -> {
            Player currentPlayer = engine.getTurnToPlay();
            //Do nothing if current player is AI
            if (!currentPlayer.isHuman()) {
                return;
            }

            TileView currentTile = (TileView) v;
            //Highlights selected tile
            gameBoardViewGroup.toggleTile(currentTile, true);
            int position = currentTile.getPosition();
            Stone stoneOnCurrentTile = gameBoardModel.getStoneByPosition(position);
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
        gameBoardViewGroup.initializeStones(playerOne.getStones(), AppConstants.RED_STONE);
        gameBoardViewGroup.initializeStones(playerTwo.getStones(), AppConstants.WHITE_STONE);

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
        gameBoardModel = new GameBoardModel();
        playerOne = new HumanPlayer(true, "One");
        playerTwo = new HumanPlayer(false, "Two");
        gameBoardModel.initializeStones(playerOne.getStones());
        gameBoardModel.initializeStones(playerTwo.getStones());

        engine = new GameEngine(playerOne, playerTwo, gameBoardModel);
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
            if (previouslySelectedStone.getPlayerColor() == currentPlayer.getColor()) {
                currentPlayer.setNextMove(new MoveAction(previouslySelectedStone.getPosition(), currentTile.getPosition()));
                //Notify the waiting engine that a move has been made
                engine.countDownLatch();
                gameBoardViewGroup.findStoneById(previouslySelectedStone.getId()).setPosition(currentTile.getPosition());
                gameBoardViewGroup.requestLayout();
            } else {
                //alert user that move could not be executed
            }
            resetSelection(currentTile);
        }
        //No stone selected, so reset the selection of tiles on the board
        else {
            //TODO: Consider adding a timer here to show the selection for a short time
            resetSelection(currentTile);
        }
    }

    /**
     * Helper method that resets all changes brought about by selecting tiles on the screen.
     *
     * @param currentTile the currently selected tile
     */
    private void resetSelection(TileView currentTile) {
        gameBoardViewGroup.toggleTile(currentTile, false);
        if (previousTile != null) {
            gameBoardViewGroup.toggleTile(previousTile, false);
        }
        previousTile = null;
    }
}