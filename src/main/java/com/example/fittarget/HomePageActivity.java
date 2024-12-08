package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.fittarget.objects.Workout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomePageActivity extends AppCompatActivity {

    FitTargetDatabaseHelper DB = new FitTargetDatabaseHelper(this);
    private TextView usernameText;
    private TextView currentWeightText;
    private TextView targetWeightText;
    private Map<String, String> userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        scheduleWeightReminder();

        userInfo = DB.getLocalUserInfo();
        handleWeightTrackingPopup();

        loadTotalWorkoutTime();
        loadMostActiveDay();
        loadTotalExercises();
        loadMostRecentWorkout();
        generateNavBar();

        String username = userInfo.get("FIRST_NAME");
        usernameText = findViewById(R.id.usernameText);
        usernameText.setText(username != null ? username : "User");
    }

    private void generateNavBar() {
        Workout importedWorkout = DB.getUserCurrentWorkout();
        if (importedWorkout != null) {
            startActivity(new Intent(this, LogWorkoutActivity.class));
        }
        String email = userInfo.get("EMAIL");
        TextView homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, HomePageActivity.class);
            intent.putExtra("userEmail", email);
            startActivity(intent);
            startActivity(intent);
        });

        TextView startWorkoutButton = findViewById(R.id.button_startWorkout);

        startWorkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, LogWorkoutActivity.class);
            startActivity(intent);
        });



        TextView bmiBtn = findViewById(R.id.BmiButton);
        bmiBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomePageActivity.this, BmiActivity.class);
            intent.putExtra("userEmail", email);
            startActivity(intent);
        });

        TextView viewAnalyticsBtn = findViewById(R.id.ViewAnalyticsButton);
        viewAnalyticsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomePageActivity.this, ViewAnalyticsActivity.class);
            startActivity(intent);
        });

        ImageView profile = findViewById(R.id.smallIcon);
        profile.setOnClickListener(view -> {
            Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
            intent.putExtra("userEmail", email);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private void loadMostRecentWorkout() {
        Pair<Integer, Integer> recentWorkoutDetails = DB.getMostRecentWorkoutDetails();
        TextView recentWorkoutsText = findViewById(R.id.recentWorkoutsText);
        if (recentWorkoutDetails== null) {
            recentWorkoutsText.setText("No recent workouts available.");
            Log.d("RecentWorkout", "No workout data available.");
        } else
         {
            int sets = recentWorkoutDetails.first; // Number of sets
            int volume = recentWorkoutDetails.second; // Volume (weight lifted)
            StringBuilder workoutDetails = new StringBuilder();
            workoutDetails.append("Most Recent Workout:\n");
            workoutDetails.append("Sets: ").append(sets).append("\n");
            workoutDetails.append("Volume: ").append(volume+ "kg");
            recentWorkoutsText.setText(workoutDetails.toString());
            // Display or use the details
            Log.d("RecentWorkout", "Sets: " + sets + ", Volume: " + volume);

        }
    }

    private void loadMostActiveDay() {
        String mostActiveDay = DB.getMostActiveDay();
        TextView activeDayText = findViewById(R.id.activeDayText);
        activeDayText.setText(mostActiveDay);
    }

    private void loadTotalWorkoutTime() {
        long totalWorkoutTimeMillis = DB.calculateTotalWorkoutTime();
        long totalMinutes = totalWorkoutTimeMillis / (1000 * 60);
        TextView workoutTimeText = findViewById(R.id.workoutTimeText);
        workoutTimeText.setText(totalMinutes + "min");
    }

    private void loadTotalExercises() {
        int totalExercises = DB.getTotalExercises();
        TextView exercisesText = findViewById(R.id.exercisesText);
        exercisesText.setText(totalExercises + " exercises");
    }

    private void handleWeightTrackingPopup() {
        if (DB.isWeightEntryRequired()) {
            showWeightEntryPopup();
        }
    }

    private void showWeightEntryPopup() {
        // Create and display a popup dialog for weight entry
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_weight_entry, null);
        builder.setView(dialogView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();

        dialog.setCancelable(false);

        TextView title = dialogView.findViewById(R.id.dialogTitle);
        title.setText("Please enter your weight");

        TextView weightInput = dialogView.findViewById(R.id.weightInput);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        TextView settingsLink = dialogView.findViewById(R.id.settingsLink);

        saveButton.setOnClickListener(v -> {
            double weight = Double.parseDouble(weightInput.getText().toString());
            DB.addWeightEntry(weight);
            dialog.dismiss();
        });

        settingsLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        dialog.show();
    }

    private void scheduleWeightReminder() {
        PeriodicWorkRequest weightReminderRequest = new PeriodicWorkRequest.Builder(
                WeightReminderWorker.class, 1, TimeUnit.DAYS) // Runs every day
                .build();

        // Enqueue the work request
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "WeightReminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                weightReminderRequest
        );
    }
}
