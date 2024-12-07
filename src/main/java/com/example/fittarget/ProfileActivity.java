package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    FitTargetDatabaseHelper DB = new FitTargetDatabaseHelper(this);
    Map<String, String> userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get user data from database
        userInfo = DB.getLocalUserInfo();

        // UI Components
        TextView firstNameText = findViewById(R.id.profileFirstName);
        TextView lastNameText = findViewById(R.id.profileLastName);
        TextView emailText = findViewById(R.id.profileEmail);
        EditText ageEditText = findViewById(R.id.profileAge);
        EditText heightEditText = findViewById(R.id.profileHeight);
        EditText weightEditText = findViewById(R.id.profileWeight);
        TextView genderText = findViewById(R.id.genderTextView);
        EditText weightTargetEditText = findViewById(R.id.profileTargetWeight);
        EditText periodTargetEditText = findViewById(R.id.profileTargetPeriod);
        Spinner measurementPreferenceSpinner = findViewById(R.id.profileMeasurementPreference);

        // Set values for text fields
        firstNameText.setText(userInfo.get("FIRST_NAME"));
        lastNameText.setText(userInfo.get("LAST_NAME"));
        emailText.setText(userInfo.get("EMAIL"));
        weightTargetEditText.setText(String.valueOf(userInfo.get("WEIGHT_TARGET")));
        periodTargetEditText.setText(String.valueOf(userInfo.get("PERIOD_TARGET")));
        ageEditText.setText(String.valueOf(userInfo.get("AGE")));
        heightEditText.setText(userInfo.get("HEIGHT"));
        weightEditText.setText(userInfo.get("WEIGHT"));
        genderText.setText(userInfo.get("GENDER"));

        // Populate Spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.measurement_preferences, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measurementPreferenceSpinner.setAdapter(adapter);

        // Retrieve and set current preference
        String savedPreference = userInfo.get("WEIGHT_MEASUREMENT_PREFERENCE");
        if (savedPreference != null) {
            int position = adapter.getPosition(savedPreference);
            if (position >= 0) {
                measurementPreferenceSpinner.setSelection(position);
            }
        }

        // Logout button functionality
        Button logoutButton = findViewById(R.id.signOutButton);
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Save button functionality
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            try {
                // Retrieve updated values
                String updatedFirstName = firstNameText.getText().toString();
                String updatedLastName = lastNameText.getText().toString();
                String updatedEmail = emailText.getText().toString();
                int updatedAge = Integer.parseInt(ageEditText.getText().toString());
                float updatedHeight = Float.parseFloat(heightEditText.getText().toString());
                float updatedWeight = Float.parseFloat(weightEditText.getText().toString());
                String updatedGender = genderText.getText().toString();
                float updatedWeightTarget = Float.parseFloat(weightTargetEditText.getText().toString());
                int updatedPeriodTarget = Integer.parseInt(periodTargetEditText.getText().toString());
                String updatedMeasurementPreference = measurementPreferenceSpinner.getSelectedItem().toString();

                // Update database
                boolean success = DB.updateLocalUser(
                        updatedFirstName, updatedLastName, updatedEmail, updatedAge,
                        updatedHeight, updatedWeight, updatedGender,
                        updatedWeightTarget, updatedPeriodTarget, updatedMeasurementPreference
                );

                if (success) {
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error updating profile.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(ProfileActivity.this, "Invalid input. Please check your entries.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
