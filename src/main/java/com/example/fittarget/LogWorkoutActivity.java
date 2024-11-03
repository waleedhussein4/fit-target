package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LogWorkoutActivity extends AppCompatActivity {
    private FitTargetDatabaseHelper DB;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private TextView durationTextView;
    private long startTimeMillis;
    private Workout currentWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_workout);

        // Setting up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initializing database and views
        DB = new FitTargetDatabaseHelper(this);
        durationTextView = findViewById(R.id.value_duration);
        Button button_addExercise = findViewById(R.id.button_addExercise);
        Button button_finish = findViewById(R.id.button_finish);

        button_addExercise.setOnClickListener(view -> addExercise());
        button_finish.setOnClickListener(view -> finishWorkout());

        // Load current workout and start duration counter if exists
        Workout importedWorkout = DB.getUserCurrentWorkout();
        if (importedWorkout != null) {
            startTimeMillis = importedWorkout.getStartDate().getTime();
            currentWorkout = importedWorkout;

            // Hide the placeholder prompt if workout exists
            TextView placeholder = findViewById(R.id.placeholderPrompt);
            placeholder.setVisibility(TextView.INVISIBLE);
        } else {
            startTimeMillis = System.currentTimeMillis();
            currentWorkout = new Workout();
        }
        startDurationCounter();
        displayWorkout(currentWorkout);
    }

    private void startDurationCounter() {
        // Initialize handler and runnable for the timer
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                // Calculate the elapsed time since start time
                long currentTimeMillis = System.currentTimeMillis();
                long elapsedMillis = currentTimeMillis - startTimeMillis;

                // Calculate hours, minutes, and seconds
                long hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60;

                // Format the time as hh:mm:ss
                String timeText = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                durationTextView.setText(timeText);  // Update the duration display

                // Repeat every second
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the timer when activity is destroyed
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        DB.insertWorkout(currentWorkout);
    }

    private void displayWorkout(Workout workout) {
        // Implement workout display logic here if needed
    }

    private void addExercise() {
        // Implement add exercise logic here if needed
    }

    private void finishWorkout() {
        // todo confirmation prompt
        // discard or save

        currentWorkout.setEndDate(new Date());
        // todo save workout to database

        DB.deleteCurrentWorkout();

        // todo go to homepage
        startActivity(new Intent(LogWorkoutActivity.this, HomePageActivity.class));
        finish();
    }
}
