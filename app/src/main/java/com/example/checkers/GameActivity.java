package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

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

    private TileView currentTile;
    private Stone currentStone;
    private Player turnToPlay;
    private Player playerOne;
    private Player playerTwo;
    private CountDownLatch latch;
    private volatile boolean isRunning;

    /**
     * Initializes the activity and manages the models and views.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    //TODO: Manage versions with Git
    //TODO: Decouple the game engine from the GameActivity. Clean up the listener code.
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

        gameBoardViewGroup = new GameBoardViewGroup(this);
        gameBoardViewGroup.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        gameBoardModel = new GameBoardModel();


        FrameLayout mainLayout = findViewById(R.id.MainLayout);
        mainLayout.addView(gameBoardViewGroup);

        //TODO: Needs to be cleaned up, but logic works
        View.OnClickListener listener = v -> {
            if(turnToPlay.isHuman()) {
                TileView tile = (TileView) v;

                toggleTile(tile, true);

                Stone stone = gameBoardModel.getStoneByPosition(tile.getPosition());
                /*
                System.out.println("-------------------------");
                if(stone!= null)
                    System.out.println("Stone position: " + stone.getPosition());
                System.out.println("Tile position: " + tile.getPosition());
                System.out.println("Current stone: " + currentStone);
                if(currentTile != null) {
                    System.out.println("Previous tile " + currentTile.getPosition());
                    Stone stone2 = gameBoardModel.getStoneByPosition(currentTile.getPosition());
                    if(stone2 != null && stone != null){
                        System.out.println("Stones match: " + (stone2.getId() == stone.getId()));
                    }
                }
                System.out.println("-------------------------");
                */

                //Must check if turnToPlay is human or not and if so what stones they can play with first

                //There is a stone in the selected tile
                if (stone != null) {
                    //No tile has been clicked before and no stone has been selected before
                    if (currentTile == null && currentStone == null) {
                        //select stone
                        currentStone = stone;
                        currentTile = tile;
                    }
                    //A tile has been clicked before and a stone is already selected
                    //however this new tile has a stone in it so no move can be made
                    else {
                        toggleTile(tile, false);
                        toggleTile(currentTile, false);

                        currentTile = null;
                        currentStone = null;
                    }
                }
                //No stone in the selected tile
                else {
                    //this is a new tile, if empty execute move if a stone has been selected previously
                    if (currentStone != null) {
                        //must set the next move for the player based on their selected action
                        if(currentStone.getPlayerColor() == turnToPlay.getColor()){
                            turnToPlay.setNextMove(new MoveAction(currentStone.getPosition(), tile.getPosition()));
                            toggleTile(tile, false);
                            gameBoardViewGroup.findStoneById(currentStone.getId()).setPosition(tile.getPosition());
                            gameBoardViewGroup.requestLayout();
                            toggleTile(currentTile, false);

                            countDownLatch();
                        }
                        else{
                          //allert user
                          toggleTile(currentTile, false);
                          toggleTile(tile, false);
                          currentStone = null;
                          currentTile = null;
                        }


                        //executeMove(tile);
                    }
                    //No stone selected, so reset the selection of tiles on the board
                    else {
                        //TODO: Consider adding a timer here to show the selection for a short time
                        toggleTile(tile, false);
                        if (currentTile != null) {
                            toggleTile(currentTile, false);
                        }
                    }
                }
            }
        };
        gameBoardViewGroup.initializeBoard(listener);

        //Type of player to be decided through settings
        playerOne = new HumanPlayer(true, "One");
        playerTwo = new HumanPlayer(false, "Two");
        turnToPlay = playerOne;
        gameBoardModel.initializeStones(playerOne.getStones());
        gameBoardModel.initializeStones(playerTwo.getStones());

        gameBoardViewGroup.initializeStones(playerOne.getStones(), AppConstants.RED_STONE);
        gameBoardViewGroup.initializeStones(playerTwo.getStones(), AppConstants.WHITE_STONE);

        startGame();


    }

    /**
     * Runs when a player has clicked on a tile in the game board.
     * Toggles the selected boolean for the tile and prompts a redraw.
     * A selected tile has a different color.
     * @param tile the TileView that was clicked
     */
    private void toggleTile(TileView tile, boolean toggle){
        tile.setSelected(toggle);
        tile.invalidate();
    }

    public void startGame(){
        isRunning = true;
        //player one always starts
        turnToPlay = playerOne;

        Runnable gameLoop = () -> {
            while(isRunning){
                latch = new CountDownLatch(1);
                try {
                    System.out.println("Awaiting player...");
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                turnToPlay.getNextMove();
                //get next move from turnToPlay
                //player.awaitMove
                executeMove(turnToPlay.getNextMove());
                //change players if time has elapsed or if a move has been made
                turnToPlay = turnToPlay.equals(playerOne) ? playerTwo : playerOne;
                System.out.println("Turn to play: " + turnToPlay.getColor());

            }
        };
        //separate thread
        Thread gameThread = new Thread(gameLoop);
        gameThread.start();

    }

    public void toggleRunning(){
        countDownLatch();
        isRunning = !isRunning;
    }

    public void countDownLatch(){
        latch.countDown();
    }

    private void executeMove(MoveAction move){
        gameBoardModel.executeMove(move, currentStone);
        currentStone = null;
        currentTile = null;
    }
}