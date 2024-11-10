package com.example.fittarget;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryActivity extends AppCompatActivity {

    private PieChart muscleGroupPieChart;
    private LineChart weightLineChart;
    private FitTargetDatabaseHelper dbHelper;
    private Spinner bodyPartSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        dbHelper = new FitTargetDatabaseHelper(this);
        muscleGroupPieChart = findViewById(R.id.muscleGroupPieChart);
        weightLineChart = findViewById(R.id.weightLineChart);
        bodyPartSpinner = findViewById(R.id.bodyPartSpinner); // Assuming bodyPartSpinner is in activity_summary.xml

        // Retrieve muscle group frequency from intent
        Map<String, Integer> muscleGroupFrequency = (HashMap<String, Integer>) getIntent().getSerializableExtra("muscleGroupFrequency");

        if (muscleGroupFrequency != null) {
            displayPieChart(muscleGroupFrequency);
        }

        // Populate the spinner with distinct body parts
        populateBodyPartSpinner();

        // Set listener for body part selection
        bodyPartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBodyPart = parent.getItemAtPosition(position).toString();
                Map<String, Integer> weightData = dbHelper.getWeightOverTime(selectedBodyPart);
                displayLineChart(weightData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Method to populate spinner with body parts worked out
    private void populateBodyPartSpinner() {
        List<String> bodyParts = new ArrayList<>(dbHelper.getDistinctBodyParts());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bodyParts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyPartSpinner.setAdapter(adapter);
    }


    private void displayPieChart(Map<String, Integer> data) {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Muscle Group Frequency");

        // Set colors for each muscle group
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        dataSet.setColors(colors);

        // Set text size and color for the frequency numbers
        dataSet.setValueTextSize(16f); // Increase the size as needed
        dataSet.setValueTextColor(Color.WHITE); // Set the text color to white

        PieData pieData = new PieData(dataSet);
        muscleGroupPieChart.setData(pieData);
        muscleGroupPieChart.invalidate(); // Refresh chart with new data and styles
    }



    private void displayLineChart(Map<String, Integer> data) {
        List<Entry> entries = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new Entry(index++, entry.getValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Weight Over Time in KG");

        // Set text size for the values displayed on the line chart
        dataSet.setValueTextSize(12f); // Adjust the size as needed
        dataSet.setValueTextColor(Color.BLACK); // Optional: Set to black or another contrasting color

        LineData lineData = new LineData(dataSet);
        weightLineChart.setData(lineData);
        weightLineChart.invalidate(); // Refresh chart with updated value sizes
    }


}
