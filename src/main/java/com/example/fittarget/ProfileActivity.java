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

public class ProfileActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Cursor cursor;

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
        String userEmail = getIntent().getStringExtra("userEmail");
        FitTargetDatabaseHelper fitTargetDatabaseHelper = new FitTargetDatabaseHelper(ProfileActivity.this);
        db = fitTargetDatabaseHelper.getReadableDatabase();
         cursor = db.rawQuery("SELECT FIRST_NAME, LAST_NAME, EMAIL, AGE, HEIGHT, WEIGHT, GENDER, WEIGHT_TARGET, PERIOD_TARGET, WEIGHT_MEASUREMENT_PREFERENCE FROM USER WHERE EMAIL = ?", new String[]{userEmail});
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

        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("EMAIL"));
            int age = cursor.getInt(cursor.getColumnIndexOrThrow("AGE"));
            float height = cursor.getFloat(cursor.getColumnIndexOrThrow("HEIGHT"));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("WEIGHT"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("GENDER"));
            String weightTarget = cursor.getString(cursor.getColumnIndexOrThrow("WEIGHT_TARGET"));
            int periodTarget = cursor.getInt(cursor.getColumnIndexOrThrow("PERIOD_TARGET"));
            String weightMeasurementPreference = cursor.getString(cursor.getColumnIndexOrThrow("WEIGHT_MEASUREMENT_PREFERENCE"));

            firstNameText.setText(firstName);
            lastNameText.setText(lastName);
            emailText.setText(email);
            weightTargetEditText.setText(String.valueOf(weightTarget));
            periodTargetEditText.setText(String.valueOf(periodTarget));
            weightMeasurementPreferenceEditText.setText(weightMeasurementPreference);
            ageEditText.setText(String.valueOf(age));
            ageEditText.setTextColor(getResources().getColor(R.color.black));
            heightEditText.setText(String.format("%.1f", height));
            heightEditText.setTextColor(getResources().getColor(R.color.black));
            weightEditText.setText(String.format("%.1f", weight));
            weightEditText.setTextColor(getResources().getColor(R.color.black));
            genderText.setText(gender);
            genderText.setTextColor(getResources().getColor(R.color.black));

        }
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

            // Update the database
            ContentValues contentValues = new ContentValues();
            contentValues.put("FIRST_NAME", updatedFirstName);
            contentValues.put("LAST_NAME", updatedLastName);
            contentValues.put("EMAIL", updatedEmail);
            contentValues.put("AGE", updatedAge);
            contentValues.put("HEIGHT", updatedHeight);
            contentValues.put("WEIGHT", updatedWeight);
            contentValues.put("GENDER", updatedGender);
            contentValues.put("WEIGHT_TARGET", updatedWeightTarget);
            contentValues.put("PERIOD_TARGET", updatedPeriodTarget);
            contentValues.put("WEIGHT_MEASUREMENT_PREFERENCE", updatedMeasurementPreference);

            int rowsUpdated = db.update("USER", contentValues, "EMAIL = ?", new String[]{userEmail});
            if (rowsUpdated > 0) {
                Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Error updating profile.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onDestroy() {
        // Close database and cursor to prevent leaks
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        super.onDestroy();
    }
}