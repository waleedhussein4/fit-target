package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private FitTargetDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        dbHelper = new FitTargetDatabaseHelper(this);

        // Attempt to retrieve stored user credentials from the database
        String[] userCredentials = dbHelper.getStoredUserCredentials();

        if (userCredentials != null) {
            String email = userCredentials[0];
            String password = userCredentials[1];

            if (attemptAutoSignIn(email, password)) {
                // Navigate to HomePageActivity if sign-in is successful
                Intent intent = new Intent(this, HomePageActivity.class);
                intent.putExtra("userEmail",email);
                startActivity(intent);
                finish();
                return;  // Exit onCreate to avoid showing the sign-in/sign-up buttons
            }
        }

        // Set up the main view if automatic sign-in was not successful
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button signInButton = findViewById(R.id.btnSignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        Button signUpButton = findViewById(R.id.btnSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    // Attempts to sign in using stored credentials
    private boolean attemptAutoSignIn(String email, String password) {
        // Replace with actual authentication logic
        return dbHelper.validateUser(email, password);  // Ensure validateUser is implemented
    }
}
