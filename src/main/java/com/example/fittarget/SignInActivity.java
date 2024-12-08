package com.example.fittarget;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fittarget.APIRequests.SignInRequest;
import com.example.fittarget.APIResponses.SignInResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button goBtn = findViewById(R.id.signInButton);
        goBtn.setOnClickListener(view -> {
            // Validate inputs
            EditText emailEditText = findViewById(R.id.emailEditText);
            String email = emailEditText.getText().toString().trim();
            EditText passwordEditText = findViewById(R.id.passwordEditText);
            String password = passwordEditText.getText().toString().trim();

            boolean isValid = true;

            if (email.isEmpty()) {
                emailEditText.setError("Email is required!");
                isValid = false;
            }
            if (password.isEmpty()) {
                passwordEditText.setError("Password is required!");
                isValid = false;
            }

            if (isValid) {
                performSignIn(email, password);
            }
        });
    }

    private void performSignIn(String email, String password) {
        SignInRequest signInRequest = new SignInRequest(email, password);

        RetrofitClient.getInstance().getUserService().signIn(signInRequest).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    User user = response.body().getUser();

                    saveUserDetailsLocally(user);

                    Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignInActivity.this, HomePageActivity.class);
                    intent.putExtra("userEmail", user.getEmail());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignInActivity.this, "Sign in failed. Check your credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDetailsLocally(User user) {
        FitTargetDatabaseHelper dbHelper = new FitTargetDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("FIRST_NAME", user.getFirstName());
        values.put("LAST_NAME", user.getLastName());
        values.put("EMAIL", user.getEmail());
        values.put("PASSWORD", user.getPassword());
        values.put("AGE", user.getAge());
        values.put("HEIGHT", user.getHeight());
        values.put("WEIGHT", user.getWeight());
        values.put("GENDER", user.getGender());
        values.put("WEIGHT_TARGET", user.getWeightTarget());
        values.put("PERIOD_TARGET", user.getPeriodTarget());
        values.put("WEIGHT_MEASUREMENT_PREFERENCE", user.getWeightMeasurementPreference());

        // Try updating first in case user exists already
        int rowsUpdated = db.update("USER", values, "EMAIL = ?", new String[]{user.getEmail()});
        if (rowsUpdated == 0) {
            // If no rows were updated, insert a new user row
            db.insert("USER", null, values);
        }

        db.close();
    }
}
