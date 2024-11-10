package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fittarget.R;

import java.util.HashMap;
import java.util.Map;

public class ViewAnalyticsActivity extends AppCompatActivity {

    private FitTargetDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_analytics);

        // Initialize database helper
        dbHelper = new FitTargetDatabaseHelper(this);

        // Generate Report button
        findViewById(R.id.submitAllButton).setOnClickListener(v -> generateReport());
    }

    private void generateReport() {
        // Fetch muscle group frequency and pass to SummaryActivity
        Map<String, Integer> muscleGroupFrequency = dbHelper.getMuscleGroupFrequency();

        Intent intent = new Intent(ViewAnalyticsActivity.this, SummaryActivity.class);
        intent.putExtra("muscleGroupFrequency", (HashMap<String, Integer>) muscleGroupFrequency);
        startActivity(intent);
    }
}

