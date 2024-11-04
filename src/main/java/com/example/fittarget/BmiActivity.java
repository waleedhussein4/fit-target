package com.example.fittarget;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BmiActivity extends AppCompatActivity {
SQLiteDatabase db;
Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bmi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String userEmail = getIntent().getStringExtra("userEmail");
        FitTargetDatabaseHelper fitTargetDatabaseHelper = new FitTargetDatabaseHelper(BmiActivity.this);
        db = fitTargetDatabaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT AGE, HEIGHT, WEIGHT, WEIGHT_TARGET FROM USER_INFO WHERE EMAIL = ?", new String[]{userEmail});
        EditText ageEditText = findViewById(R.id.ageInput);
        EditText heightEditText = findViewById(R.id.heightInput);
        EditText weightEditText = findViewById(R.id.weightInput);
        EditText goalWeightEditText = findViewById(R.id.goalWeightInput);

        if (cursor.moveToFirst()) {

            int age = cursor.getInt(cursor.getColumnIndexOrThrow("AGE"));
            float height = cursor.getFloat(cursor.getColumnIndexOrThrow("HEIGHT"));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow("WEIGHT"));
            float targetWeight = cursor.getFloat(cursor.getColumnIndexOrThrow("WEIGHT_TARGET"));

            ageEditText.setText(String.valueOf(age));
            ageEditText.setTextColor(getResources().getColor(R.color.black));
            heightEditText.setText(String.format("%.1f", height));
            heightEditText.setTextColor(getResources().getColor(R.color.black));
            weightEditText.setText(String.format("%.1f", weight));
            weightEditText.setTextColor(getResources().getColor(R.color.black));
            goalWeightEditText.setText(String.format("%.1f", targetWeight));
            goalWeightEditText.setTextColor(getResources().getColor(R.color.black));
            cursor.close();
            db.close();
        }
        Button decrementHeight = findViewById(R.id.decrementHeight);
        Button incrementHeight = findViewById(R.id.incrementHeight);
        final float[] height = {Float.parseFloat(heightEditText.getText().toString())};
        decrementHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                height[0] = Math.max(0, height[0] -0.5f);
                heightEditText.setText(String.format("%.1f", height[0]));
            }
        });
        incrementHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                height[0] = Math.min(250, height[0] + 0.5f);
                heightEditText.setText(String.format("%.1f", height[0]));
            }
        });

        Button incrementWeight = findViewById(R.id.incrementWeight);
        Button decrementWeight = findViewById(R.id.decrementWeight);
        final float[] weightValue = {Float.parseFloat(weightEditText.getText().toString())};

        decrementWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weightValue[0] = Math.max(0, weightValue[0] - 0.5f);
                weightEditText.setText(String.format("%.1f", weightValue[0]));
            }
        });
        incrementWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weightValue[0] = Math.min(250, weightValue[0] + 0.5f);
                weightEditText.setText(String.format("%.1f", weightValue[0]));
            }
        });
        Button calculateBmi = findViewById(R.id.calculateBmiButton);
        BMIGaugeView bmiGaugeView = findViewById(R.id.bmiGaugeView);
        bmiGaugeView.setBMIValue(0);
        calculateBmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateAndDisplayBMI(heightEditText,weightEditText,bmiGaugeView);
            }
        });
    }
        private void calculateAndDisplayBMI(EditText heightEditText, EditText weightEditText, BMIGaugeView bmiGaugeView) {

            // Retrieve height and weight from input fields
            String heightText = heightEditText.getText().toString();
            String weightText = weightEditText.getText().toString();

            if (heightText.isEmpty() || weightText.isEmpty()) {
                Toast.makeText(this, "Please enter both height and weight.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float heightV = Float.parseFloat(heightText) / 100; // Convert cm to meters
                float weightV = Float.parseFloat(weightText);

                if (heightV <= 0 || weightV <= 0) {
                    Toast.makeText(this, "Height and weight must be greater than 0.", Toast.LENGTH_SHORT).show();
                    return;
                }


                float bmi = weightV / (heightV * heightV);


                bmiGaugeView.setBMIValue(bmi);



            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers.", Toast.LENGTH_SHORT).show();
            }
        }




    }

