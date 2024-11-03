package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.RegionKt;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomePageActivity extends AppCompatActivity {

    FitTargetDatabaseHelper DB = new FitTargetDatabaseHelper(this);

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


        Workout importedWorkout = DB.getUserCurrentWorkout();
        if (importedWorkout != null) {
            startActivity(new Intent(this, LogWorkoutActivity.class));
        }

        Button startWorkoutButton = findViewById(R.id.button_startWorkout);

        // Set up the OnClickListener to navigate to LogWorkoutActivity
        startWorkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, LogWorkoutActivity.class);
            startActivity(intent);
        });

        String email = getIntent().getStringExtra("userEmail");
        Button bmiBtn = findViewById(R.id.BmiButton);
        bmiBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomePageActivity.this, BmiActivity.class);
            intent.putExtra("userEmail", email);
            startActivity(intent);
        });
        Button viewAnalyticsBtn = findViewById(R.id.ViewAnalyticsButton);
        viewAnalyticsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomePageActivity.this, ViewAnalyticsActivity.class);
            startActivity(intent);
        });
    }
}

