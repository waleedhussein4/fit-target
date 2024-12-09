package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fittarget.APIRequests.UserUpdateRequest;
import com.example.fittarget.APIRequests.SyncCheckRequest;
import com.example.fittarget.APIRequests.SyncRequest;
import com.example.fittarget.APIResponses.SyncResponse;
import com.example.fittarget.APIResponses.SyncStatusResponse;
import com.example.fittarget.APIServices.SyncService;
import com.example.fittarget.objects.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    FitTargetDatabaseHelper DB = new FitTargetDatabaseHelper(this);
    Map<String, String> userInfo;
    private TextView syncStatus;
    private Button syncButton;

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
        syncStatus = findViewById(R.id.sync_status);
        syncButton = findViewById(R.id.sync_button);
        syncButton.setOnClickListener(v -> sync());

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
                int updatedAge = Integer.parseInt(ageEditText.getText().toString());
                float updatedHeight = Float.parseFloat(heightEditText.getText().toString());
                float updatedWeight = Float.parseFloat(weightEditText.getText().toString());
                float updatedWeightTarget = Float.parseFloat(weightTargetEditText.getText().toString());
                int updatedPeriodTarget = Integer.parseInt(periodTargetEditText.getText().toString());
                String updatedMeasurementPreference = measurementPreferenceSpinner.getSelectedItem().toString();

                // Create a UserUpdateRequest object
                UserUpdateRequest updateRequest = new UserUpdateRequest();
                updateRequest.setAge(updatedAge);
                updateRequest.setHeight(updatedHeight);
                updateRequest.setWeight(updatedWeight);
                updateRequest.setTargetWeight(updatedWeightTarget);
                updateRequest.setTargetPeriod(updatedPeriodTarget);
                updateRequest.setWeightMeasurementPreference(updatedMeasurementPreference);

                // Make API call to update the profile
                String userEmail = userInfo.get("EMAIL");
                RetrofitClient.getInstance().getUserService().updateUserProfile(userEmail, updateRequest).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Update local database with new values
                            DB.updateLocalUser(
                                    userInfo.get("FIRST_NAME"), userInfo.get("LAST_NAME"), userEmail,
                                    updatedAge, updatedHeight, updatedWeight,
                                    userInfo.get("GENDER"), updatedWeightTarget,
                                    updatedPeriodTarget, updatedMeasurementPreference
                            );

                            Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(ProfileActivity.this, "Invalid input. Please check your entries.", Toast.LENGTH_SHORT).show();
            }
        });

        fetchSyncStatus();
    }

    private void fetchSyncStatus() {
        SyncCheckRequest syncRequest = new SyncCheckRequest(DB);

        RetrofitClient.getInstance().getSyncService().checkSyncStatus(syncRequest).enqueue(new retrofit2.Callback<SyncStatusResponse>() {
            @Override
            public void onResponse(Call<SyncStatusResponse> call, retrofit2.Response<SyncStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SyncStatusResponse syncStatusResponse = response.body();
                    if (syncStatusResponse.isSynced()) {
                        syncStatus.setText("You are up to date.");
                        syncButton.setEnabled(false); // Disable button if already synced
                    }
                    else {
                        syncStatus.setText("You have unsynced data.");
                        syncButton.setEnabled(true); // Enable button if unsynced data
                    }
                } else {
                    handleSyncError("Failed to fetch sync status.");
                }
            }

            @Override
            public void onFailure(Call<SyncStatusResponse> call, Throwable t) {
                handleSyncError("Error: Unable to fetch sync status.");
            }
        });
    }

    private void handleSyncError(String message) {
        syncStatus.setText(message);
        syncButton.setEnabled(false); // Disable button on failure
    }

    private void sync() {
        Log.d("Sync", "Syncing data...");
        SyncRequest syncRequest = new SyncRequest(DB);

        syncButton.setEnabled(false);

        RetrofitClient.getInstance().getSyncService().syncAllData(syncRequest).enqueue(new retrofit2.Callback<SyncResponse>() {
            @Override
            public void onResponse(Call<SyncResponse> call, retrofit2.Response<SyncResponse> response) {
                if (response.isSuccessful()) {
                    syncStatus.setText("Sync successful.");
                    syncButton.setEnabled(false);

                    DB.updateLastLocalSync();
                    DB.markPendingWorkoutsAsUploaded();
                } else {
                    handleSyncError("Failed to sync data.");
                }
            }

            @Override
            public void onFailure(Call<SyncResponse> call, Throwable t) {
                syncButton.setEnabled(true);
                handleSyncError("Error: Unable to sync data.");
            }
        });
    }
}
