package com.example.fittarget;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        EditText emailEditText = findViewById(R.id.emailEditText);
        String email = emailEditText.getText().toString();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required!");
        }
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();
        if(password.isEmpty()){
            passwordEditText.setError("Password is required!");
        }



    }
}