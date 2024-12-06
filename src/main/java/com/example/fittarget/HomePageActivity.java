package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.database.Cursor;


import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Map;
import java.util.HashMap;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.RegionKt;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fittarget.objects.Workout;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    FitTargetDatabaseHelper DB = new FitTargetDatabaseHelper(this);
    private TextView usernameText;
    private TextView currentWeightText;
    private TextView targetWeightText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        usernameText = findViewById(R.id.usernameText);

        // Retrieve user ID (if stored in session or passed via Intent)
        int userId = getUserId();
        // Fetch username from database and update UI
        String username = DB.getUsername(userId);
        usernameText.setText(username != null ? username : "User");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadTotalWorkoutTime();
        loadMostActiveDay();
        loadTotalExercises();
        loadMostRecentWorkout();

        Workout importedWorkout = DB.getUserCurrentWorkout();
        if (importedWorkout != null) {
            startActivity(new Intent(this, LogWorkoutActivity.class));
        }

        TextView startWorkoutButton = findViewById(R.id.button_startWorkout);

        // Set up the OnClickListener to navigate to LogWorkoutActivity
        startWorkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, LogWorkoutActivity.class);
            startActivity(intent);
        });

        String email = getIntent().getStringExtra("userEmail");

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
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
                intent.putExtra("userEmail", email);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    private void loadMostRecentWorkout() {
        Map<String, String> recentWorkout = DB.getMostRecentWorkout();
        TextView recentWorkoutsText = findViewById(R.id.recentWorkoutsText);

        if (recentWorkout.isEmpty()) {
            recentWorkoutsText.setText("No recent workouts available.");
        } else {
            StringBuilder workoutDetails = new StringBuilder();
            workoutDetails.append("Most Recent Workout:\n");
            workoutDetails.append("Sets: ").append(recentWorkout.get("sets")).append("\n");
            workoutDetails.append("Volume: ").append(recentWorkout.get("volume") + "kg");
            recentWorkoutsText.setText(workoutDetails.toString());
        }
    }


    private void loadMostActiveDay() {
        String mostActiveDay = DB.getMostActiveDay() ;
        TextView activeDayText = findViewById(R.id.activeDayText);
        activeDayText.setText(mostActiveDay+"");
    }


    private void loadTotalWorkoutTime() {
        int totalMinutes = DB.getTotalWorkoutTime();
        TextView workoutTimeText = findViewById(R.id.workoutTimeText);
        workoutTimeText.setText(totalMinutes +"min");
    }

    private void loadTotalExercises() {
        int totalExercises = DB.getTotalExercises();
        TextView exercisesText = findViewById(R.id.exercisesText);
        exercisesText.setText(totalExercises+" exercises");
    }

    private int getUserId() {
        // Retrieve the user ID from SharedPreferences, Intent, or session data
        return 1; // Replace with actual user ID fetching logic
    }


}

