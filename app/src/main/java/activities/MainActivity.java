package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.checkers.R;

/**
 * Activity for the main menu of the game.
 *
 * @author Ramiar Odendaal
 */
public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private Button settingsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.StartButton);
        settingsButton = findViewById(R.id.SettingsButton);
        startButton.setOnClickListener(e -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });
        settingsButton.setOnClickListener(e -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

    }
}