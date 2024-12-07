package com.example.fittarget;

import android.content.Intent;
import android.database.Cursor;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Cursor cursor;

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
                    Boolean isValid = true;
                    EditText emailEditText = findViewById(R.id.emailEditText);
                    String email = emailEditText.getText().toString();
                    if (email.isEmpty()) {
                        emailEditText.setError("Email is required!");
                        isValid=false;
                    }
                    EditText passwordEditText = findViewById(R.id.passwordEditText);
                    String password = passwordEditText.getText().toString();
                    if(password.isEmpty()){
                        passwordEditText.setError("Password is required!");
                        isValid=false;
                    }
                    FitTargetDatabaseHelper fitTargetDatabaseHelper = new FitTargetDatabaseHelper(SignInActivity.this);
                    try {
                        db = fitTargetDatabaseHelper.getReadableDatabase();
                        String[] columns = {"EMAIL", "PASSWORD"};


                        String selection = "EMAIL = ?";



                        String[] selectionArgs = {email};


                        cursor = db.query("USER", columns, selection, selectionArgs, null, null, null);

                        if (cursor != null && cursor.moveToFirst()) {
                            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("PASSWORD"));

                            if (storedPassword.equals(password)) {
                                isValid = true;

                            } else {
                                isValid = false;
                                passwordEditText.setError("Incorrect Password. Please try again!");
                            }
                        } else {
                                 isValid = false;
                            emailEditText.setError("Incorrect Email! Please try again!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        isValid= false;
                        Toast.makeText(SignInActivity.this, "Error accessing database.", Toast.LENGTH_SHORT).show();
                    } finally {

                        if (cursor != null) {
                            cursor.close();
                        }
                        if (db != null) {
                            db.close();
                        }
                    }
                    performSignIn(email, password);
                    if(isValid){
                        Intent intent = new Intent(SignInActivity.this, HomePageActivity.class);
                        intent.putExtra("userEmail",email);
                        startActivity(intent);
                    }
                });

    }
    private void performSignIn(String email, String password) {
        SignInRequest signInRequest = new SignInRequest(email, password);

        RetrofitClient.getInstance().getUserService().signIn(signInRequest).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Access the message from the JSON response
                    String message = response.body().getMessage();
                    Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
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
}