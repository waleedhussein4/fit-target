package com.example.fittarget;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    //    SQLiteDatabase db;
    FitTargetDatabaseHelper DB = new FitTargetDatabaseHelper(this);
    //    Cursor cursor;
    Map<String, String> userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userInfo = DB.getLocalUserInfo();

//        FitTargetDatabaseHelper fitTargetDatabaseHelper = new FitTargetDatabaseHelper(ProfileActivity.this);
//        db = fitTargetDatabaseHelper.getReadableDatabase();
        String userEmail = userInfo.get("email");
//        cursor = db.rawQuery("SELECT FIRST_NAME, LAST_NAME, EMAIL, AGE, HEIGHT, WEIGHT, GENDER, WEIGHT_TARGET, PERIOD_TARGET, WEIGHT_MEASUREMENT_PREFERENCE FROM USER WHERE EMAIL = ?", new String[]{userEmail});
        TextView firstNameText = findViewById(R.id.profileFirstName);
        TextView lastNameText = findViewById(R.id.profileLastName);
        TextView emailText = findViewById(R.id.profileEmail);
        EditText ageEditText = findViewById(R.id.profileAge);
        EditText heightEditText = findViewById(R.id.profileHeight);
        EditText weightEditText = findViewById(R.id.profileWeight);
        TextView genderText = findViewById(R.id.genderTextView);
        EditText weightTargetEditText = findViewById(R.id.profileTargetWeight);
        EditText periodTargetEditText = findViewById(R.id.profileTargetPeriod);
        EditText weightMeasurementPreferenceEditText = findViewById(R.id.profileMeasurementPreference);

        firstNameText.setText(userInfo.get("FIRST_NAME"));
        lastNameText.setText(userInfo.get("LAST_NAME"));
        emailText.setText(userInfo.get("EMAIL"));
        weightTargetEditText.setText(String.valueOf(userInfo.get("WEIGHT_TARGET")));
        periodTargetEditText.setText(String.valueOf(userInfo.get("PERIOD_TARGET")));
        weightMeasurementPreferenceEditText.setText(userInfo.get("WEIGHT_MEASUREMENT_PREFERENCE"));
        ageEditText.setText(String.valueOf(userInfo.get("AGE")));
        ageEditText.setTextColor(getResources().getColor(R.color.black));
        heightEditText.setText(userInfo.get("HEIGHT"));
        heightEditText.setTextColor(getResources().getColor(R.color.black));
        weightEditText.setText(userInfo.get("WEIGHT"));
        weightEditText.setTextColor(getResources().getColor(R.color.black));
        genderText.setText(userInfo.get("GENDER"));
        genderText.setTextColor(getResources().getColor(R.color.black));

        Button logoutButton = findViewById(R.id.signOutButton);
        logoutButton.setOnClickListener(v -> {
            // Clear any stored user session data (if applicable)
            // dbHelper.clearUserSession(); // Uncomment if dbHelper has session clear method
            // Redirect user to SignInActivity
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Finish current activity to prevent going back
        });
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            // Gather updated values from input fields
            String updatedFirstName = firstNameText.getText().toString();
            String updatedLastName = lastNameText.getText().toString();
            String updatedEmail = emailText.getText().toString();
            int updatedAge = Integer.parseInt(ageEditText.getText().toString());
            float updatedHeight = Float.parseFloat(heightEditText.getText().toString());
            float updatedWeight = Float.parseFloat(weightEditText.getText().toString());
            String updatedGender = genderText.getText().toString();
            float updatedWeightTarget = Float.parseFloat(weightTargetEditText.getText().toString());
            int updatedPeriodTarget = Integer.parseInt(periodTargetEditText.getText().toString());
            String updatedMeasurementPreference = weightMeasurementPreferenceEditText.getText().toString();

            // Call the helper function to update the user info
            boolean success = DB.updateLocalUser(
                    updatedFirstName, updatedLastName, updatedEmail, updatedAge,
                    updatedHeight, updatedWeight, updatedGender,
                    updatedWeightTarget, updatedPeriodTarget, updatedMeasurementPreference
            );

            // Show feedback to the user
            if (success) {
                Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Error updating profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Close database and cursor to prevent leaks
//        if (cursor != null && !cursor.isClosed()) {
//            cursor.close();
//        }
//        if (db != null && db.isOpen()) {
//            db.close();
//        }
        super.onDestroy();
    }
}