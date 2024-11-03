package com.example.fittarget;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fittarget.adapters.ExerciseAdapter;
import com.example.fittarget.objects.Exercise;
import com.example.fittarget.objects.Workout;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LogWorkoutActivity extends AppCompatActivity {
    private FitTargetDatabaseHelper DB;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private TextView durationTextView;
    private long startTimeMillis;
    private Workout currentWorkout;
    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private static final int REQUEST_SELECT_EXERCISE = 1;

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

        exerciseRecyclerView = findViewById(R.id.exerciseList);

        Workout importedWorkout = DB.getUserCurrentWorkout();
        if (importedWorkout != null) {
            startTimeMillis = importedWorkout.getStartDate().getTime();
            currentWorkout = importedWorkout;

            TextView placeholder = findViewById(R.id.placeholderPrompt);
            placeholder.setVisibility(TextView.INVISIBLE);
        } else {
            startTimeMillis = System.currentTimeMillis();
            currentWorkout = new Workout();
        }
        startDurationCounter();
        displayWorkout();
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
        DB.insertCurrentWorkout(currentWorkout); // Ensure ongoing workout is saved
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_EXERCISE && resultCode == RESULT_OK) {
            String selectedExerciseName = data.getStringExtra("selectedExerciseName");
            int selectedExerciseId = data.getIntExtra("selectedExerciseId", 0);
            String selectedExerciseMuscle = data.getStringExtra("selectedExerciseMuscle");
            Exercise selectedExercise = new Exercise(selectedExerciseName, selectedExerciseId, selectedExerciseMuscle);

            // Add directly to currentWorkout's exercise list
            currentWorkout.getExercises().add(selectedExercise);

            // Notify adapter to refresh the view
            exerciseAdapter.notifyDataSetChanged();
        }
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

    private void displayWorkout() {
        // Initialize the adapter with currentWorkout's exercise list
        exerciseAdapter = new ExerciseAdapter(this, currentWorkout);
        exerciseRecyclerView.setAdapter(exerciseAdapter);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addExercise() {
        Intent intent = new Intent(this, SelectExerciseActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_EXERCISE);
    }

    private void finishWorkout() {
        new AlertDialog.Builder(this)
                .setTitle("Finish Workout")
                .setMessage("Do you want to save or discard this workout?")
                .setPositiveButton("Save", (dialog, which) -> {
                    currentWorkout.setEndDate(new Date());
                    DB.insertCompletedWorkout(currentWorkout);  // Move to completed workouts
                    DB.deleteCurrentWorkout();  // Clear current workout
                    finish(); // Close the activity
                })
                .setNegativeButton("Discard", (dialog, which) -> {
                    DB.deleteCurrentWorkout();  // Just delete the current workout
                    finish(); // Close the activity
                })
                .setCancelable(true)
                .show();
    }
}
