package activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.checkers.R;
import com.example.checkers.utils.AppConfig;

/**
 * Activity for the settings menu of the game.
 *
 * @author Ramiar Odendaal
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button backButton = findViewById(R.id.BackButton2);
        backButton.setOnClickListener(e -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
        });
        Button playerTwoButton = findViewById(R.id.playerTwoSetting);
        playerTwoButton.setOnClickListener(e -> {
            String currentText = playerTwoButton.getText().toString();
            switch(currentText){
                case AppConfig.MINIMAX:
                    currentText = AppConfig.MCTS;
                    break;
                case AppConfig.MCTS:
                    currentText = AppConfig.HUMAN;
                    break;
                case AppConfig.HUMAN:
                    currentText = AppConfig.MINIMAX;
                    break;
            }
            AppConfig.setPlayerModel(false, currentText);
            playerTwoButton.setText(currentText);
        });
        Button playerOneButton = findViewById(R.id.playerOneSetting);
        playerOneButton.setOnClickListener(e -> {
            String currentText = playerOneButton.getText().toString();
            switch(currentText){
                case AppConfig.MINIMAX:
                    currentText = AppConfig.MCTS;
                    break;
                case AppConfig.MCTS:
                    currentText = AppConfig.HUMAN;
                    break;
                case AppConfig.HUMAN:
                    currentText = AppConfig.MINIMAX;
                    break;
            }
            AppConfig.setPlayerModel(true, currentText);
            playerOneButton.setText(currentText);
        });

        Button difficultyButton = findViewById(R.id.difficultySetting);
        difficultyButton.setOnClickListener(e -> {
            String currentText = difficultyButton.getText().toString();
            switch(currentText){
                case AppConfig.EASY:
                    currentText = AppConfig.MEDIUM;
                    break;
                case AppConfig.MEDIUM:
                    currentText = AppConfig.HARD;
                    break;
                case AppConfig.HARD:
                    currentText = AppConfig.VERY_HARD;
                    break;
                case AppConfig.VERY_HARD:
                    currentText = AppConfig.EASY;
                    break;
            }
            AppConfig.setDifficulty(currentText);
            difficultyButton.setText(currentText);
        });
    }
}