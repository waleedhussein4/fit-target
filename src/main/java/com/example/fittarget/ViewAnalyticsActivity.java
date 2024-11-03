package com.example.fittarget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class ViewAnalyticsActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private EditText weightInput;
    private EditText repsInput;
    private Spinner muscleGroupSpinner;
    private Button submitButton, submitAllButton;
    private TableLayout tableLayout;

    // Map to store total weight lifted by date and muscle group
    private Map<String, Integer> weightByDate = new HashMap<>();
    private Map<String, String> muscleGroupByDate = new HashMap<>();

    // Map to store total weight by muscle group
    private Map<String, Integer> totalByMuscleGroup = new HashMap<>();

    // Map to track the frequency of each muscle group worked out
    private Map<String, Integer> workoutFrequencyByMuscleGroup = new HashMap<>();

    // Map to store weight entries for each muscle group over time
    private Map<String, List<Entry>> bodyPartWeightData = new HashMap<>();

    // Map to store date labels (formatted as month/year) for each entry by muscle group
    private Map<String, List<String>> dateLabelsByMuscleGroup = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_analytics);

        // Initialize views
        datePicker = findViewById(R.id.datePicker);
        weightInput = findViewById(R.id.weightInput);
        repsInput = findViewById(R.id.repsInput);
        muscleGroupSpinner = findViewById(R.id.muscleGroupSpinner);
        submitButton = findViewById(R.id.submitButton);
        submitAllButton = findViewById(R.id.submitAllButton);
        tableLayout = findViewById(R.id.tableLayout);

        // Set up muscle group options in the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.muscle_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        muscleGroupSpinner.setAdapter(adapter);

        // Set up button click listener for adding entries
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                String dateKey = day + "/" + month + "/" + year;

                String weightStr = weightInput.getText().toString();
                String repsStr = repsInput.getText().toString();
                String muscleGroup = muscleGroupSpinner.getSelectedItem().toString();

                if (weightStr.isEmpty() || repsStr.isEmpty()) {
                    Toast.makeText(ViewAnalyticsActivity.this, "Please enter both weight and reps", Toast.LENGTH_SHORT).show();
                    return;
                }

                int weight = Integer.parseInt(weightStr);
                int reps = Integer.parseInt(repsStr);
                int totalWeightLifted = weight * reps;

                // Update weight by date and muscle group
                weightByDate.put(dateKey, weightByDate.getOrDefault(dateKey, 0) + totalWeightLifted);
                muscleGroupByDate.put(dateKey, muscleGroup);

                // Update total weight by muscle group
                totalByMuscleGroup.put(muscleGroup, totalByMuscleGroup.getOrDefault(muscleGroup, 0) + totalWeightLifted);

                // Update workout frequency
                workoutFrequencyByMuscleGroup.put(muscleGroup, workoutFrequencyByMuscleGroup.getOrDefault(muscleGroup, 0) + 1);

                // Track weight over time for each muscle group
                List<Entry> entries = bodyPartWeightData.getOrDefault(muscleGroup, new ArrayList<>());
                entries.add(new Entry(entries.size(), totalWeightLifted)); // Use index for x-axis
                bodyPartWeightData.put(muscleGroup, entries);

                // Format the date as month/year for the x-axis label
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                String formattedDate = monthFormat.format(new Date(year - 1900, month - 1, day)); // Adjust for Date constructor

                // Add the formatted date to the date labels list for the selected muscle group
                List<String> dateLabels = dateLabelsByMuscleGroup.getOrDefault(muscleGroup, new ArrayList<>());
                dateLabels.add(formattedDate);
                dateLabelsByMuscleGroup.put(muscleGroup, dateLabels);

                updateTable();
                weightInput.setText("");
                repsInput.setText("");
            }
        });

        // Set up click listener for "Submit all data" button
        submitAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAnalyticsActivity.this, SummaryActivity.class);

                // Pass total weight and frequency data
                for (Map.Entry<String, Integer> entry : totalByMuscleGroup.entrySet()) {
                    intent.putExtra("total_" + entry.getKey(), entry.getValue());
                }
                int combinedTotal = 0;
                for (int value : totalByMuscleGroup.values()) {
                    combinedTotal += value;
                }
                intent.putExtra("total_combined", combinedTotal);

                // Pass workout frequency for each muscle group
                for (Map.Entry<String, Integer> entry : workoutFrequencyByMuscleGroup.entrySet()) {
                    intent.putExtra("frequency_" + entry.getKey(), entry.getValue());
                }

                // Pass weight data for each muscle group
                for (Map.Entry<String, List<Entry>> entry : bodyPartWeightData.entrySet()) {
                    intent.putExtra("data_" + entry.getKey(), new ArrayList<>(entry.getValue()));
                }

                // Pass date labels for each muscle group to SummaryActivity
                for (Map.Entry<String, List<String>> entry : dateLabelsByMuscleGroup.entrySet()) {
                    intent.putStringArrayListExtra("dateLabels_" + entry.getKey(), new ArrayList<>(entry.getValue()));
                }

                startActivity(intent);
            }
        });
    }

    private void updateTable() {
        // Code for updating the table as previously provided
    }
}
