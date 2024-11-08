package com.example.fittarget;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LogWorkoutActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_EXERCISE = 1;
    private FitTargetDatabaseHelper DB;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private Workout currentWorkout;
    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter exerciseAdapter;

    private TextView durationTextView;
    private long startTimeMillis;
    private TextView totalVolumeText;
    private TextView totalSetsText;


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

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishWorkout();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);

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

        totalVolumeText = findViewById(R.id.value_totalVolume);
        totalSetsText = findViewById(R.id.value_totalSets);
        updateVolumeAndSets();

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
            int selectedExerciseReferenceId = data.getIntExtra("selectedExerciseReferenceId", 0);
            Exercise selectedExercise = new Exercise(selectedExerciseReferenceId, this);

            // initialize with first set, empty
            selectedExercise.addSet(new Exercise.Set());

            // Add directly to currentWorkout's exercise list
            currentWorkout.getExercises().add(selectedExercise);

            // Notify adapter to refresh the view
            exerciseAdapter.notifyDataSetChanged();
            updateVolumeAndSets();
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
        exerciseAdapter = new ExerciseAdapter(this, currentWorkout, new WorkoutChangeListener() {
            @Override
            public void onWorkoutModified() {
                updateVolumeAndSets();
            }
        });
        exerciseRecyclerView.setAdapter(exerciseAdapter);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private int[] getTotalVolumeAndSets() {
        int totalVolume = 0;
        int totalSets = 0;

        for (Exercise exercise : currentWorkout.getExercises()) {
            for (Exercise.Set set : exercise.getSets()) {
                totalVolume += set.getWeight();
                totalSets++;
            }
        }

        return new int[]{totalVolume, totalSets};
    }

    public void updateVolumeAndSets() {
        int[] values = getTotalVolumeAndSets();

        totalVolumeText.setText(values[0] + "kg");
        totalSetsText.setText(String.valueOf(values[1]));
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
                    if (validateFields()) {
                        currentWorkout.setEndDate(new Date());
                        DB.insertCompletedWorkout(currentWorkout);
                        DB.deleteCurrentWorkout();
                        finish();
                    } else {
                        showError("Please fill in all fields before saving.");
                    }
                })
                .setNegativeButton("Discard", (dialog, which) -> {
                    DB.deleteCurrentWorkout();
                    finish();
                })
                .setCancelable(true)
                .show();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateFields() {
        for (Exercise exercise : currentWorkout.getExercises()) {
            for (Exercise.Set set : exercise.getSets()) {
                if (set.getWeight() <= 0 || set.getReps() <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public interface WorkoutChangeListener {
        void onWorkoutModified();
    }
}
