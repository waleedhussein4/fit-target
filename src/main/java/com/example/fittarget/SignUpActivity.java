package com.example.fittarget;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
             return insets;
        });
        Button readyBtn = findViewById(R.id.readyButton);
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                EditText firstNameEdit = findViewById(R.id.firstNameEditText);
                EditText lastNameEdit = findViewById(R.id.lastNameEditText);
                EditText emailEdit = findViewById(R.id.emailEditText);
                EditText passwordEdit = findViewById(R.id.passwordEditText);
                EditText ageEdit = findViewById(R.id.ageEditText);
                EditText heightEdit = findViewById(R.id.heightEditText);
                EditText weightEdit = findViewById(R.id.weightEditText);
                RadioGroup genderGroup = findViewById(R.id.genderRadioGroup);
                RadioGroup weightMGroup = findViewById(R.id.preferenceGroup);
                EditText weightTarget = findViewById(R.id.targetEditText);
                EditText weightPeriod = findViewById(R.id.periodTargetEditText);
                TextView genderText = findViewById(R.id.genderTextView);
                TextView measText = findViewById(R.id.measurementPrefTextView);



                final String[] genderHolder = {""};
                final String[] measHolder ={""};
                boolean isValid = true;

                String firstName = firstNameEdit.getText().toString().trim();
                if (firstName.isEmpty()) {
                    firstNameEdit.setError("First name is required");
                    isValid = false;
                }

                String lastName = lastNameEdit.getText().toString().trim();
                if (lastName.isEmpty()) {
                    lastNameEdit.setError("Last name is required");
                    isValid = false;
                }

                String email = emailEdit.getText().toString().trim();
                if (email.isEmpty()) {
                    emailEdit.setError("Email is required");
                    isValid = false;
                }

                String password = passwordEdit.getText().toString().trim();
                if (password.isEmpty()) {
                    passwordEdit.setError("Password is required");
                    isValid = false;
                }

                String age = ageEdit.getText().toString().trim();
                if (age.isEmpty()) {
                    ageEdit.setError("Age is required");
                    isValid = false;
                }

                String height = heightEdit.getText().toString().trim();
                if (height.isEmpty()) {
                    heightEdit.setError("Height is required");
                    isValid = false;
                }

                String weight = weightEdit.getText().toString().trim();
                if (weight.isEmpty()) {
                    weightEdit.setError("Weight is required");
                    isValid = false;
                }
                genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId != -1) {
                        genderText.setError(null);
                    }
                });
                int selectedGenderId = genderGroup.getCheckedRadioButtonId();
                if (selectedGenderId == -1) { // No option selected
                    genderText.setError("Gender selection is required");
                    isValid = false;
                }
                else {
                    int genderButtonId = genderGroup.getCheckedRadioButtonId();
                    RadioButton genderButton = findViewById(genderButtonId);
                    genderHolder[0]= genderButton.getText().toString();
                }
                String gender = genderHolder[0];
                weightMGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId != -1) {
                        measText.setError(null);
                    }

                });
                int selectedMeasurement = weightMGroup.getCheckedRadioButtonId();
                if (selectedMeasurement == -1) {
                    measText.setError("Measurement preference is required");
                    isValid = false;
                }
                else {
                    int measButtonId = weightMGroup.getCheckedRadioButtonId();
                    RadioButton measButton = findViewById(measButtonId);
                   measHolder[0] = measButton.getText().toString();
                }
                String measurement = measHolder[0];

                String weight_target = weightTarget.getText().toString().trim();
                if (weight_target.isEmpty()) {
                    weightTarget.setError("Weight target is required");
                    isValid = false;

                }

                String weight_period = weightPeriod.getText().toString().trim();
                if (weight_period.isEmpty()) {
                    weightPeriod.setError("Weight period is required");
                    isValid = false;

                }
                createUser(email,password,firstName,lastName,Integer.parseInt(age),Float.parseFloat(height), Float.parseFloat(weight),
                        gender, measurement, Float.parseFloat(weight_target), Integer.parseInt(weight_period));
                FitTargetDatabaseHelper fitTargetDatabaseHelper = new FitTargetDatabaseHelper(SignUpActivity.this);
                if (fitTargetDatabaseHelper.isEmailUsed(email)) {
                    emailEdit.setError("Email is already in use. Please choose another email.");
                }
                if (isValid){

                    db = fitTargetDatabaseHelper.getWritableDatabase();
                    fitTargetDatabaseHelper.insertUser(db,firstName,lastName,email,password,Integer.parseInt(age),Integer.parseInt(weight),
                            Integer.parseInt(height),gender, measurement, Integer.parseInt(weight_target),
                            Integer.parseInt(weight_period)
                            );
                    fitTargetDatabaseHelper.insertWeightRecord(Integer.parseInt(weight));
                    db.close();
                    Intent intent = new Intent(SignUpActivity.this, HomePageActivity.class);
                    intent.putExtra("userEmail",email);
                    startActivity(intent);
                }
            }
        });
    }
    private void createUser(String email, String password, String firstName, String lastName,
                            int age, float height, float weight, String gender, String measurementPreference,
                            float weightTarget, int weightPeriod) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setHeight(height);
        user.setWeight(weight);
        user.setGender(gender);
        user.setWeightMeasurementPreference(measurementPreference);
        user.setWeightTarget(weightTarget);
        user.setPeriodTarget(weightPeriod);
        RetrofitClient.getInstance().getUserService().createUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

